/*
 * Created on Wed Sep 30 2020
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Optional;
import java.util.ServiceLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import sh.pancake.launcher.classloader.DynamicURLClassLoader;
import sh.pancake.launcher.classloader.ModdedClassLoader;
import sh.pancake.launcher.classloader.ServerClassLoader;
import sh.pancake.launcher.mod.MixinClassModder;
import sh.pancake.sauce.RemapInfo;

public class PancakeLauncher {

    private static final Logger LOGGER = LogManager.getLogger("PancakeLauncher");

    private static PancakeLauncher launcher;

    public static PancakeLauncher getLauncher() {
        return launcher;
    }

    private IPancakeServer server;

    private ServerClassLoader serverClassLoader;

    private PancakeLauncher(IPancakeServer server, ServerClassLoader serverClassLoader) {
        this.server = server;
        this.serverClassLoader = serverClassLoader;
    }

    public ServerClassLoader getServerClassLoader() {
        return serverClassLoader;
    }

    public IPancakeServer getServer() {
        return server;
    }

    private void finishMixin() {
        try {
            Method m = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
            m.setAccessible(true);
            m.invoke(null, MixinEnvironment.Phase.INIT);
            m.invoke(null, MixinEnvironment.Phase.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String findTargetVersion(ClassLoader loader) throws IOException {
        try (InputStream stream = loader.getResourceAsStream("target_version")) {
            return new String(stream.readAllBytes());
        }
    }

    private static DynamicURLClassLoader prepareServerClassLoader(File serverFile) throws Exception {
        DynamicURLClassLoader loader = new DynamicURLClassLoader(new URL[] { serverFile.toURI().toURL() }, PancakeLauncher.class.getClassLoader());
    
        String version;
        try {
            version = findTargetVersion(loader);
        } catch (IOException e) {
            loader.close();
            throw new Exception("Failed to read required server version from server file. File may be corrupted or invalid.", e);
        }

        LOGGER.info("Waiting Minecraft server " + version + " to be provided...");

        Logger patchLogger = LogManager.getLogger("ServerRemapper");
        MappedServerProvider provider = new MappedServerProvider(version, new File("patches"));

        File mcServerFile;
        try {
            mcServerFile = provider.provideServer(
                false,
                (RemapInfo info) -> patchLogger.info("PATCHING " + info.getFromName() + " -> " + info.getToName())
            );
        } catch (Exception e) {
            loader.close();
            throw new Exception("Server preparing process failed with error. Server cannot be started.", e);
        }

        loader.addURL(mcServerFile.toURI().toURL());

        MixinBootstrap.init();

        return loader;
    }

    public static PancakeLauncher launch(File serverFile, String[] args) throws Exception {
        if (launcher != null) throw new RuntimeException("Launcher already created");

        DynamicURLClassLoader innerClassLoader = prepareServerClassLoader(serverFile);
        ServerClassLoader serverClassLoader = new ServerClassLoader(new ModdedClassLoader(innerClassLoader, new MixinClassModder()));
        
        Thread.currentThread().setContextClassLoader(serverClassLoader);
        ServiceLoader<IPancakeServer> serverLoader = ServiceLoader.load(IPancakeServer.class);

        Optional<IPancakeServer> serverOptional = serverLoader.findFirst();
        if (!serverOptional.isPresent()) throw new Exception("Cannot find IPancakeServer instance. Server cannot be started.");

        IPancakeServer server = serverOptional.get();
        PancakeLauncher launcher = PancakeLauncher.launcher = new PancakeLauncher(server, serverClassLoader);

        LOGGER.info("Launching " + server.getClass().getSimpleName() + "...");
        server.start(args, innerClassLoader::addURL, launcher::finishMixin);

        return launcher;
    }

}
