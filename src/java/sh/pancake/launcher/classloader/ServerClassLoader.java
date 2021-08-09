/*
 * Created on Mon Aug 09 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.launcher.classloader;

/**
 * Main server ClassLoader
 */
public class ServerClassLoader extends ClassLoader {

    public ServerClassLoader(ClassLoader loader) {
        super(loader);
    }
    
    public boolean isClassLoaded(String className) {
		synchronized (getClassLoadingLock(className)) {
			Class<?> c = findLoadedClass(className);

			return c != null;
		}
	}

}
