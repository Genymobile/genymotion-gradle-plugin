package main.groovy.com.genymotion.tasks

import main.groovy.com.genymotion.tools.GMTool
import main.groovy.com.genymotion.tools.GMToolException
import main.groovy.com.genymotion.model.GenymotionVirtualDevice
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

        if(devices.size() == 0)
            return

        def runningDevices = []

        def virtualDevices = GMTool.getAllDevices(project.genymotion.config.verbose, false, false)
        virtualDevices.each {
            if(it.state == GenymotionVirtualDevice.STATE_ON)
                runningDevices.add(it.name)
        }

        def virtualDevicesNames = virtualDevices*.name

        //process declared devices
        devices.each(){
            processDevice(it, runningDevices, virtualDevicesNames)
        }

        if (project.genymotion.config.verbose) {
            println("-- Running devices --")
            GMTool.getRunningDevices(true)
        }
    }

    def processDevice(device, runningDevices, virtualDevicesNames) {
        if (!device.start)
            return

        if (project.genymotion.config.verbose)
            println("Starting ${device.name}")

        try {
            if (device.name && runningDevices != null && !runningDevices?.contains(device.name)) {
                if(!virtualDevicesNames?.contains(device.name))
                    device.create()
                device.checkAndEdit()
                device.start()
            }
            device.logcat()
            device.flash()
            device.install()
            device.pushBefore()
            device.pullBefore()

        } catch (Exception e) { //if a gmtool command fail
            e.printStackTrace()
            abortLaunch(device)
            //then, we thow a new exception to end task, if needed
            if (project.genymotion.config.abortOnError)
                throw new GMToolException("GMTool command failed. "+e.getMessage())
        }
    }

    public void abortLaunch(device) {
        println "An error occured. Stoping and deleting all launched devices, if needed."
        project.genymotion.getDevices(flavor).each() {
            //we close the opened devices
            device.stopWhenFinish()
            //and delete them if needed
            device.deleteWhenFinish()
        }
    }
}
