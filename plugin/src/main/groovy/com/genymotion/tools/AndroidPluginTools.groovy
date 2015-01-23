package main.groovy.com.genymotion.tools

import main.groovy.com.genymotion.GenymotionGradlePlugin
import org.gradle.api.Project

/**
 * Created by eyal on 28/11/14.
 */
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
