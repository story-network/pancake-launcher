/*
 * Created on Mon Aug 09 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.launcher.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * URLClassLoader but addURL method is exposed. Supports dynamically adding URL.
 */
public class DynamicURLClassLoader extends URLClassLoader {

    public DynamicURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
    
}
