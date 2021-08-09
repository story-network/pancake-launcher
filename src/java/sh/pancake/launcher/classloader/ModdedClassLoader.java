/*
 * Created on Mon Aug 09 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.launcher.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import sh.pancake.launcher.mod.IClassModder;
import sh.pancake.launcher.util.ClassUtil;

/**
 * A ClassLoader that wraps target ClassLoader and load Class data modded.
 * 
 * Any class need to be loaded from target ClassLoader can be modded.
 * ModdedClassLoader will not mod classes from target's parent ClassLoader.
 */
public class ModdedClassLoader extends ClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    private ClassLoader target;

    private IClassModder modder;

    public ModdedClassLoader(ClassLoader target, IClassModder modder) {
        super(target.getParent());
        this.target = target;
        this.modder = modder;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        synchronized (super.getClassLoadingLock(name)) {
            String pkgName = ClassUtil.getPackageName(name);

            if (pkgName != null && getDefinedPackage(pkgName) == null) {
                definePackage(pkgName, null, null, null, null, null, null, null);
            }
    
            String classFileName = ClassUtil.getClassFileName(name);
    
            try (InputStream stream = target.getResourceAsStream(classFileName)) {
                byte[] input = stream.readAllBytes();
    
                if (modder != null) {
                    input = modder.transformClassData(name, input);
                }
    
                return super.defineClass(name, input, 0, input.length);
            } catch (Exception e) {
                throw new ClassNotFoundException("Error while reading class: " + name, e);
            }
        }
    }

    @Override
    public URL getResource(String name) {
        return target.getResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return target.getResourceAsStream(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return target.getResources(name);
    }

}
