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
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenymotionGradlePlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package main.groovy.com.genymotion.tools

import main.groovy.com.genymotion.GenymotionGradlePlugin
import org.gradle.api.Project

class AndroidPluginTools {

    public static final DEFAULT_ANDROID_TASK = "connectedAndroidTest"
    public static final DEFAULT_PROPERTIES = "local.properties"

    public static String getFlavorTaskName(String flavor) {
        DEFAULT_ANDROID_TASK + flavor.capitalize() + "Debug"
    }

    public static String getFlavorFinishTask(String flavor) {
        GenymotionGradlePlugin.TASK_FINISH + flavor.capitalize()
    }

    public static String getFlavorLaunchTask(String flavor) {
        GenymotionGradlePlugin.TASK_LAUNCH + flavor.capitalize()
    }

    static boolean hasAndroidPlugin(Project project) {
        project.plugins.hasPlugin('android') || project.plugins.hasPlugin('com.android.application')
    }
}
