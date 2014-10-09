package test.groovy.com.genymotion

import main.groovy.com.genymotion.GenymotionTool
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project

/**
 * Created by eyal on 08/10/14.
 */
class GenymotionTestTools {

    static def GENYMOTION_PATH = "/home/eyal/genymotion/genymotion-softs/build/"

    static def DEVICES = [
            "Nexus7-junit":"Google Nexus 7 - 4.1.1 - API 16 - 800x1280",
            "Nexus10-junit":"Google Nexus 10 - 4.4.4 - API 19 - 2560x1600",
            "Nexus4-junit":"Google Nexus 4 - 4.3 - API 18 - 768x1280"
    ]

    static def init(){
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'genymotion'

        project.genymotion.config.genymotionPath = GENYMOTION_PATH
        //we set the config inside the GenymotionTool
        GenymotionTool.GENYMOTION_CONFIG = project.genymotion.config

        project
    }

    static void deleteAllDevices() {
        DEVICES.each() { key, value ->
            GenymotionTool.deleteDevice(key)
        }
    }

    static void createAllDevices() {
        DEVICES.each() { key, value ->
            GenymotionTool.createDevice(value, key)
        }
    }

    static String createADevice() {

        Random rand = new Random()
        int index = rand.nextInt(DEVICES.size())

        String[] keys = DEVICES.keySet() as String[]
        String name = keys[index]
        GenymotionTool.createDevice(DEVICES[name], name)
        name
    }
}
