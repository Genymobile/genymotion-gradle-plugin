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

package com.genymotion.tools

import com.genymotion.GenymotionGradlePlugin
import org.gradle.api.Project

class AndroidPluginTools {

    public static final DEFAULT_ANDROID_TASK = "connectedAndroidTest"
    public static final ASSEMBLE_PREFIX = "assemble"
    public static final DEFAULT_PROPERTIES = "local.properties"


    public static String getFlavorTestTaskName(String flavor) {
        DEFAULT_ANDROID_TASK + flavor.capitalize() + "Debug"
    }

    public static String getFlavorAssembleDebugTaskName(String flavor = null) {
        if (flavor == null) {
            return ASSEMBLE_PREFIX + "Debug"
        } else {
            return ASSEMBLE_PREFIX + flavor.capitalize() + "Debug"
        }
    }

    public static String getFlavorFinishTask(String suffix) {
        GenymotionGradlePlugin.TASK_FINISH + suffix.capitalize()
    }

    public static String getFlavorLaunchTask(String suffix) {
        GenymotionGradlePlugin.TASK_LAUNCH + suffix.capitalize()
    }

    public static boolean hasAndroidPlugin(Project project) {
        project.plugins.hasPlugin('android') || project.plugins.hasPlugin('com.android.application')
    }
}
