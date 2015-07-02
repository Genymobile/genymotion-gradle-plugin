package com.genymotion.tasks

import com.genymotion.model.GenymotionVirtualDevice
import com.genymotion.tools.GMTool
import com.genymotion.tools.Log
import org.gradle.api.tasks.TaskAction


class GenymotionSingleLaunchTask extends GenymotionLaunchTask {

    def device

    @TaskAction
    def exec() {

        if (project.genymotion.config.verbose) {
            Log.info("Starting devices")
        }

        def runningDevices = []

        def virtualDevices = GMTool.getAllDevices(project.genymotion.config.verbose, false, false)
        virtualDevices.each {
            if (it.state == GenymotionVirtualDevice.STATE_ON) {
                runningDevices.add(it.name)
            }
        }

        def virtualDevicesNames = virtualDevices*.name

        processDevice(device, runningDevices, virtualDevicesNames)

        if (project.genymotion.config.verbose) {
            Log.debug("-- Running devices --")
            GMTool.getRunningDevices(true)
        }
    }
}
