/*
 * Copyright (C) 2015 Genymobile
 *
 * This file is part of GenymotionGradlePlugin.
 *
 * GenymotionGradlePlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version
 *
 * GenymotionGradlePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenymotionGradlePlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.genymotion

import com.android.build.gradle.BasePlugin
import com.genymotion.tools.AndroidPluginTools

import java.util.jar.Attributes
import java.util.jar.Manifest

class AndroidPluginTestTools extends AndroidPluginTools {

    /**
     * Get the default connectedTestTask for the variant when no product flavor is defined.
     *
     * @param version the Android Gradle plugin version
     * @return the name of the default connected task
     */
    static String getDefaultTestTask(String version) {
        if (version >= "1.3.0") {
            DEFAULT_ANDROID_TASK_1_3
        } else if (version >= "1.2.0") {
            DEFAULT_ANDROID_TASK_1_2
        } else {
            DEFAULT_ANDROID_TASK_1_0
        }
    }

    /**
     * Get the Android Gradle plugin version.
     *
     * This method ues 2 ways to get the information:
     * - One is Version.ANDROID_GRADLE_PLUGIN_VERSION available for Android Gradle plugin version 1.3.+;
     * - The second is extracted from AOSP: BasePlugin.getLocalVersion(), released under Apache2 license.
     * It get the plugin version from the Android Gradle plugin JAR's manifest.
     *
     * TODO remove the backward compatibility when we will decide to support ONLY 1.3.+ and use standard call to Version class
     *
     * @return the plugin version name of the Android Gradle plugin loaded for this project
     */
    static String getPluginVersion() {

        //we first use the current standard way to get the information compatible with Android Gradle plugin 1.3.+
        try {
            return Eval.me("com.android.builder.Version.ANDROID_GRADLE_PLUGIN_VERSION")
        } catch (Throwable t) {
            //no-op
        }

        //XXX: backward compatibility hack
        try {
            Class clazz = BasePlugin.class
            String className = clazz.getSimpleName() + ".class"
            String classPath = clazz.getResource(className).toString()
            if (!classPath.startsWith("jar")) {
                // Class not from JAR, unlikely
                return null
            }
            String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +
                    "/META-INF/MANIFEST.MF";

            URLConnection jarConnection = new URL(manifestPath).openConnection();
            jarConnection.setUseCaches(false);
            InputStream jarInputStream = jarConnection.getInputStream();
            Attributes attr = new Manifest(jarInputStream).getMainAttributes();
            jarInputStream.close();
            return attr.getValue("Plugin-Version");
        } catch (Throwable t) {
            return "1.3.1";
        }
    }


}
