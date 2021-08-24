/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.launcher;

import java.net.URL;
import java.util.function.Consumer;

/**
 * Interface for custom Minecraft server
 */
public interface IPancakeServer {

     /**
      * Server entrypoint
      *
      * @param args Server arguments
      * @param addURL Use this to load additional files to classpath.
      * @param finishMixin Server must call this before finishing mixin setup
      */
     void start(String[] args, Consumer<URL> addURL, Runnable finishMixin);

}
