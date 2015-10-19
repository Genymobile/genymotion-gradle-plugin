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

    public static final DEFAULT_ANDROID_TASK_1_0 = "connectedAndroidTest"
    public static final DEFAULT_ANDROID_TASK_1_2 = "connectedAndroidTestDebug"
    public static final DEFAULT_ANDROID_TASK_1_3 = "connectedDebugAndroidTest"
    public static final DEFAULT_PROPERTIES = "local.properties"

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
