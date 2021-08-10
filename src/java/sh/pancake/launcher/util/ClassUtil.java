/*
 * Created on Mon Aug 09 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.launcher.util;

import javax.annotation.Nullable;

public class ClassUtil {
    
    public static String getClassFileName(String fullName) {
		return fullName.replace('.', '/') + ".class";
    }

    @Nullable
    public static String getPackageName(String fullName) {
        int lastIndex = fullName.lastIndexOf(".");
        if (lastIndex == -1) return null;

        return fullName.substring(0, lastIndex);
    }
    
}
