/*
 * Created on Sun Aug 08 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.launcher;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import sh.pancake.launch.object.VersionInfo;
import sh.pancake.launch.object.VersionManifest;
import sh.pancake.launch.util.Hash;
import sh.pancake.launch.util.Hex;
import sh.pancake.launch.util.MCLauncherUtil;
import sh.pancake.sauce.PancakeSauce;
import sh.pancake.sauce.RemapInfo;
import sh.pancake.sauce.SaucePreprocessor;
import sh.pancake.sauce.parser.ConversionTable;
import sh.pancake.sauce.parser.IDupeResolver;
import sh.pancake.sauce.parser.ProguardParser;

public class MappedServerProvider {

    private String version;
    private File patchDirectory;

    public MappedServerProvider(String version, File patchDirectory) {
        this.version = version;
        this.patchDirectory = patchDirectory;
    }

    private boolean matchChecksum(File file, String sha1Hash) throws IOException {
        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
            String hash = Hex.byteArrayToHex(Hash.sha1From(new FileInputStream(file)));

            if (hash.equalsIgnoreCase(sha1Hash)) return true;
        }

        return false;
    }

    private void downloadFileTo(URL url, File to) throws IOException {
        try (
            BufferedInputStream input = new BufferedInputStream(url.openStream());
            FileOutputStream stream = new FileOutputStream(to);
        ) {
            input.transferTo(stream);
        }
    }

    private VersionInfo provideVersionInfo(boolean recache) throws IOException {
        String name = version + "-info.json";
        File versionFile = new File(patchDirectory, name);

        if (versionFile.exists() && !recache) {
            String read = Files.readString(versionFile.toPath());
            try {
                return MCLauncherUtil.getInfoFromJson(read);
            } catch (Exception e) {

            }
        }

        versionFile.getParentFile().mkdirs();

        VersionManifest manifest = MCLauncherUtil.fetchVersionManifest();

        String rawVersionInfo = MCLauncherUtil.fetchRawVersionInfo(manifest, version);
        if (rawVersionInfo == null) throw new IOException("Invalid version: " + version);

        Files.writeString(versionFile.toPath(), rawVersionInfo, StandardOpenOption.CREATE);

        return MCLauncherUtil.getInfoFromJson(rawVersionInfo);
    }

    private File provideRawMinecraftServer(boolean recache) throws IOException {
        VersionInfo info = provideVersionInfo(recache);

        File serverFile = new File(patchDirectory, version + "-server-raw.jar");

        if (serverFile.exists() && matchChecksum(serverFile, info.downloads.server.sha1) && !recache) {
            return serverFile;
        }

        serverFile.getParentFile().mkdirs();

        downloadFileTo(new URL(info.downloads.server.url), serverFile);

        return serverFile;
    }

    private File provideServerMapping(boolean recache) throws IOException {
        VersionInfo info = provideVersionInfo(recache);

        File mapFile = new File(patchDirectory, version + "-server-mapping.txt");

        if (mapFile.exists() && matchChecksum(mapFile, info.downloads.serverMappings.sha1) && !recache) {
            return mapFile;
        }

        mapFile.getParentFile().mkdirs();

        downloadFileTo(new URL(info.downloads.serverMappings.url), mapFile);

        return mapFile;
    }

    public File provideServer(boolean recache, Consumer<RemapInfo> progressHandler) throws Exception {
        File mappedFile = new File(patchDirectory, version + "-server-mapped.jar");

        if (mappedFile.exists() && !recache) {
            return mappedFile;
        }

        byte[] rawMCServer = Files.readAllBytes(provideRawMinecraftServer(recache).toPath());
        String mappingStr = Files.readString(provideServerMapping(recache).toPath());

        ProguardParser parser = new ProguardParser(IDupeResolver.SUFFIX_TAG_RESOLVER);
        
        ConversionTable table = parser.parse(mappingStr);
        try(ZipInputStream serverInput = new ZipInputStream(new ByteArrayInputStream(rawMCServer))) {
            SaucePreprocessor preprocessor = new SaucePreprocessor();

            preprocessor.process(serverInput, table);
        }

        try (
            ZipInputStream serverInput = new ZipInputStream(new ByteArrayInputStream(rawMCServer));
            ZipOutputStream serverOutput = new ZipOutputStream(new FileOutputStream(mappedFile))
        ) {
            PancakeSauce sauce = new PancakeSauce(
                serverInput,
                table,
                (entry) -> {
                    String name = entry.getName();

                    return !name.contains("/") || name.startsWith("com/mojang") || name.startsWith("net/minecraft");
                }
            );

            ExecutorService service = Executors.newCachedThreadPool();

            sauce.remapJarAsync(service, serverOutput, progressHandler);

            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
        }

        return mappedFile;
    }

}
