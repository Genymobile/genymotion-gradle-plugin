package test.groovy.com.genymotion

import main.groovy.com.genymotion.GMTool
import main.groovy.com.genymotion.GenymotionConfig
import main.groovy.com.genymotion.GenymotionVDLaunch
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project

import java.util.logging.Logger


class TestTools {

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
        project.genymotion.config.verbose = true
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
                physicalButton: false,
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
        def Properties props = new Properties()
        def propFile = new File(path)
        if (propFile.canRead()){
            props.load(new FileInputStream(propFile))

            if (props!=null){
                //Reflection could be another solution to fill the object but I prefer to avoid it.
                //This method allows a better control on the data changed and avoid side loading non-allowed values
                if(props.statistics)
                    config.statistics = props.statistics.toBoolean()
                if(props.username)
                    config.username = props.username
                if(props.password)
                    config.password = props.password
                if(props.store_credentials)
                    config.store_credentials = props.store_credentials.toBoolean()
                if(props.license)
                    config.license = props.license
                if(props.proxy)
                    config.proxy = props.proxy.toBoolean()
                if(props.proxy_address)
                    config.proxy_address = props.proxy_address
                if(props.proxy_port)
                    config.proxy_port = props.proxy_port.toInteger()
                if(props.proxy_auth)
                    config.proxy_auth = props.proxy_auth.toBoolean()
                if(props.proxy_username)
                    config.proxy_username = props.proxy_username
                if(props.proxy_password)
                    config.proxy_password = props.proxy_password
                if(props.virtual_device_path)
                    config.virtual_device_path = props.virtual_device_path
                if(props.sdk_path)
                    config.sdk_path = props.sdk_path
                if(props.use_custom_sdk)
                    config.use_custom_sdk = props.use_custom_sdk.toBoolean()
                if(props.screen_capture_path)
                    config.screen_capture_path = props.screen_capture_path
                if(props.taskLaunch)
                    config.taskLaunch = props.taskLaunch
                if(props.taskFinish)
                    config.taskFinish = props.taskFinish
                if(props.automaticLaunch)
                    config.automaticLaunch = props.automaticLaunch.toBoolean()
                if(props.processTimeout)
                    config.processTimeout = props.processTimeout.toInteger()
                if(props.verbose)
                    config.verbose = props.verbose.toBoolean()
                if(props.persist)
                    config.persist = props.persist.toBoolean()
                if(props.abortOnError)
                    config.abortOnError = props.abortOnError.toBoolean()
                if(props.genymotionPath)
                    config.genymotionPath = props.genymotionPath

            } else {
                Logger.anonymousLogger.error("$config.fromFile file is missing, impossible to load configuration")
                return null
            }
        } else {
            Logger.anonymousLogger.error("$config.fromFile file is missing, impossible to load configuration")
            return null
        }
        return config
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
