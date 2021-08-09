/*
 * Created on Mon Oct 05 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.launcher.mod;

/**
 * Class modding interface.
 */
public interface IClassModder {

    /**
     * Transform class data
     *
     * @param name Name of class
     * @param data Data of class
     * @return Transformed class data
     */
    byte[] transformClassData(String name, byte[] data);

}