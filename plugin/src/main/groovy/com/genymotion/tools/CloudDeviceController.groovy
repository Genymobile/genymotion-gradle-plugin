package com.genymotion.tools

import com.genymotion.model.DeviceLocation
import com.genymotion.model.VDLaunchDsl

/**
 * A DeviceController for cloud devices
 */
class CloudDeviceController extends DeviceController {
    @Override
    protected GMTool createGMTool() {
        GMTool gmtool = GMTool.newInstance()
        gmtool.deviceLocation = DeviceLocation.CLOUD
        return gmtool
    }

    @Override
    protected void startDevice(GMTool gmtool, VDLaunchDsl launchDsl) {
        stopDevice(gmtool, launchDsl)
        if (config.verbose) {
            Log.debug("Creating ${launchDsl.name}")
        }
        gmtool.createDevice(launchDsl.template, launchDsl.name, launchDsl.density, launchDsl.width, launchDsl.height,
                launchDsl.virtualKeyboard, launchDsl.navbarVisible, launchDsl.nbCpu, launchDsl.ram)

        if (config.verbose) {
            Log.debug("Starting ${launchDsl.name}")
        }
        gmtool.startDevice(launchDsl.name)
    }

    @Override
    protected void stopDevice(GMTool gmtool, VDLaunchDsl launchDsl) {
        try {
            gmtool.stopDevice(launchDsl.name)
        } catch (GMToolException e) {
            Log.debug("Ignoring failure to stop device $launchDsl.name")
        }
        try {
            gmtool.deleteDevice(launchDsl.name)
        } catch (GMToolException e) {
            Log.debug("Ignoring failure to delete device $launchDsl.name")
        }
    }
}
