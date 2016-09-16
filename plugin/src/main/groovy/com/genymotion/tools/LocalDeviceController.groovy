package com.genymotion.tools

import com.genymotion.model.GenymotionVirtualDevice
import com.genymotion.model.VDLaunchDsl

/**
 * A DeviceController for local devices
 */
class LocalDeviceController extends DeviceController {
    private def runningDevices
    private def virtualDeviceNames

    @Override
    protected GMTool createGMTool() {
        return GMTool.newInstance()
    }

    @Override
    protected void prepareLaunch(GMTool gmtool) {
        def virtualDevices = gmtool.getAllDevices(false, false)

        runningDevices = []
        virtualDevices.each {
            if (it.state == GenymotionVirtualDevice.STATE_ON) {
                runningDevices.add(it.name)
            }
        }

        virtualDeviceNames = virtualDevices*.name
    }

    @Override
    protected void startDevice(GMTool gmtool, VDLaunchDsl launchDsl) {
        if (!launchDsl.start) {
            return
        }

        if (config.verbose) {
            Log.debug("Starting ${launchDsl.name}")
        }

        if (launchDsl.name && runningDevices != null && !runningDevices?.contains(launchDsl.name)) {
            if (!virtualDeviceNames?.contains(launchDsl.name)) {
                launchDsl.checkParams()
                gmtool.createDevice(launchDsl.template, launchDsl.name, launchDsl.density, launchDsl.width, launchDsl.height,
                        launchDsl.virtualKeyboard, launchDsl.navbarVisible, launchDsl.nbCpu, launchDsl.ram)
            }
            launchDsl.checkAndEdit()
            gmtool.startDevice(launchDsl.name)
        }
    }

    @Override
    protected void stopDevice(GMTool gmtool, VDLaunchDsl launchDsl) {
        try {
            if (!launchDsl.stopWhenFinish) {
                return
            }
            gmtool.stopDevice(launchDsl.name)
            if (!launchDsl.deleteWhenFinish) {
                return
            }
            gmtool.deleteDevice(launchDsl.name)
        } catch (GMToolException e) {
        }
    }
}
