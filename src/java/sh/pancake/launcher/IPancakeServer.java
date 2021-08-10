/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.launcher;

import sh.pancake.launcher.classloader.DynamicURLClassLoader;

/**
 * Interface for custom Minecraft server
 */
public interface IPancakeServer {

     /**
      * Server entrypoint
      *
      * @param args Server arguments
      * @param urlLoader Use this to load additional files to classpath. Implmentation should not store or expose this.
      * @param finishMixin Server must call this before finishing mixin setup
      */
     void start(String[] args, DynamicURLClassLoader urlLoader, Runnable finishMixin);

}
