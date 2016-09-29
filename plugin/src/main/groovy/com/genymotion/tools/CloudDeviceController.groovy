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
            Log.debug("start-disposable ${launchDsl.name}")
        }
        gmtool.startDisposableDevice(launchDsl.template, launchDsl.name, launchDsl.density, launchDsl.width, launchDsl.height,
                launchDsl.virtualKeyboard, launchDsl.navbarVisible, launchDsl.nbCpu, launchDsl.ram)
    }

    @Override
    protected void stopDevice(GMTool gmtool, VDLaunchDsl launchDsl) {
        try {
            gmtool.stopDisposableDevice(launchDsl.name)
        } catch (GMToolException e) {
            Log.debug("Ignoring failure to stop-disposable device $launchDsl.name")
        }
    }
}
