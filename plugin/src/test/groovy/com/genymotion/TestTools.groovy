package test.groovy.com.genymotion

import main.groovy.com.genymotion.GMTool
import main.groovy.com.genymotion.GenymotionConfig
import main.groovy.com.genymotion.GenymotionVDLaunch
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project

import java.util.logging.Logger


class TestTools {

    static def DEVICES = [
            "Nexus7-junit":"Google Nexus 7 - 4.1.1 - API 16 - 800x1280",
            "Nexus10-junit":"Google Nexus 10 - 4.4.4 - API 19 - 2560x1600",
            "Nexus4-junit":"Google Nexus 4 - 4.3 - API 18 - 768x1280"
    ]

    static def init(){

        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'genymotion'

        project.genymotion.config.genymotionPath = getDefaultConfig().genymotionPath
        project.genymotion.config.verbose = true
        project.genymotion.config.abortOnError = false
        //we set the config inside the GenymotionTool
        GMTool.GENYMOTION_CONFIG = project.genymotion.config

        GMTool.getConfig(true)

        project
    }

    static void deleteAllDevices() {
        DEVICES.each() { key, value ->
            GMTool.deleteDevice(key)
        }
    }

    static void createAllDevices() {
        DEVICES.each() { key, value ->
            GMTool.createDevice(value, key)
        }
    }

    static String createADevice() {

        Random rand = new Random()
        int index = rand.nextInt(DEVICES.size())

        String[] keys = DEVICES.keySet() as String[]
        String name = keys[index]
        GMTool.createDevice(DEVICES[name], name)

        name
    }

    static def declareADetailedDevice(Project project, boolean stopWhenFinish=true) {
        String vdName = GenymotionVDLaunch.getRandomName("-junit")
        String density = "mdpi"
        int height = 480
        int width = 320
        int ram = 2048
        int nbCpu = 1
        boolean deleteWhenFinish = true

        project.genymotion.device(
                name: vdName,
                template: "Google Nexus 7 - 4.1.1 - API 16 - 800x1280",
                density: density,
                width: width,
                height: height,
                virtualKeyboard: false,
                navbar: false,
                nbCpu: nbCpu,
                ram: ram,
                deleteWhenFinish: deleteWhenFinish,
                stopWhenFinish: stopWhenFinish
        )
        [vdName, density, width, height, nbCpu, ram, deleteWhenFinish]
    }


    static void cleanAfterTests(){

        println "Cleaning after tests"

        GMTool.getConfig(true)

        try{
            def devices = GMTool.getAllDevices(false, false, true)
            def pattern = ~/^.+?\-junit$/
            println devices

            devices.each(){
                if(pattern.matcher(it).matches()){
                    println "Removing $it"
                    GMTool.stopDevice(it, true)
                    GMTool.deleteDevice(it, true)
                }
            }
        } catch (Exception e){
            println e
        }
    }

    static void recreatePulledDirectory() {
        File tempDir = new File("temp/pulled")
        if (tempDir.exists()) {
            if (tempDir.isDirectory())
                tempDir.deleteDir()
            else
                tempDir.delete()
        }
        tempDir.mkdirs()
    }

    static GenymotionConfig getDefaultConfig(String path = "res/test/default.properties"){
        // We get the APK signing properties from a file
        GenymotionConfig config = new GenymotionConfig()
        config.fromFile = path

        if(config.applyConfigFromFile(null))
            return config

        return null
    }

    static setDefaultUser(registerLicense = false){
        GenymotionConfig config = getDefaultConfig()

        if(config.username && config.password){
            GMTool.setConfig(config, true)

            if(config.license && registerLicense)
                GMTool.setLicense(config.license, null, null, true)
        }
    }
}
