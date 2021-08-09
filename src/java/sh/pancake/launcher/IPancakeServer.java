/*
 * Created on Thu Oct 01 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.launcher;

/**
 * Interface for custom Minecraft server
 */
public interface IPancakeServer {

     // Impl must call finishMixin after finshing mixin configuration
     void start(String[] args, Runnable finishMixin);

}
