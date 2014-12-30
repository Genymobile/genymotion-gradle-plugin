package main.groovy.com.genymotion

import org.codehaus.groovy.control.messages.ExceptionMessage
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenymotionLaunchTask extends DefaultTask {

    String flavor

    @TaskAction
    def exec() {
        //we configure the environment
        project.genymotion.processConfiguration()

        if (project.genymotion.config.verbose)
            println("Starting devices")

        def devices = project.genymotion.getDevices(flavor)
        def runningDevices = []

        if(devices.size() > 0)
            runningDevices = GMTool.getRunningDevices(project.genymotion.config.verbose, false, true)

        //process declared devices
        devices.each(){
            processDevice(it, runningDevices)
        }

        if (project.genymotion.config.verbose) {
            println("-- Running devices --")
            GMTool.getRunningDevices(true)
        }
    }

    def processDevice(device, runningDevices) {
        if (device.start) {
            if (project.genymotion.config.verbose)
                println("Starting ${device.name}")

            try {
                if (device.name && !runningDevices.contains(device.name)) {
                    device.create()
                    device.checkAndEdit()
                    device.start()
                }
                device.logcat()
                device.flash()
                device.install()
                device.pushBefore()
                device.pullBefore()
            }
            //if a gmtool command fail
            catch (Exception e) {
                e.printStackTrace()
                println e.getMessage()
                println "Stoping all launched devices and deleting when needed"
                project.genymotion.getDevices(flavor).each() {
                    //we close the opened devices
                    device.stopWhenFinish()
                    //and delete them if needed
                    device.deleteWhenFinish()
                }
                //then, we thow a new exception to end task, if needed
                if (project.genymotion.config.abortOnError)
                    throw new GMToolException("GMTool command failed. Check the output to solve the problem")
            }
        }
    }
}
