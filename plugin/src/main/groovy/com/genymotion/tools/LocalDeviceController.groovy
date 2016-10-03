package com.genymotion.tools

import com.genymotion.model.GenymotionVirtualDevice
import com.genymotion.model.LocalVDLaunchDsl
import com.genymotion.model.VDLaunchDsl

/**
 * A {@link DeviceController} for local devices
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
        LocalVDLaunchDsl localLaunchDsl = (LocalVDLaunchDsl)launchDsl
        if (!localLaunchDsl.start) {
            return
        }

        if (config.verbose) {
            Log.debug("Starting ${localLaunchDsl.name}")
        }

        if (localLaunchDsl.name && runningDevices != null && !runningDevices?.contains(localLaunchDsl.name)) {
            if (!virtualDeviceNames?.contains(localLaunchDsl.name)) {
                localLaunchDsl.checkParams(gmtool)
                gmtool.createDevice(localLaunchDsl.template, localLaunchDsl.name, localLaunchDsl.density,
                        localLaunchDsl.width, localLaunchDsl.height, localLaunchDsl.virtualKeyboard,
                        localLaunchDsl.navbarVisible, localLaunchDsl.nbCpu, localLaunchDsl.ram)
            }
            checkAndEdit(gmtool, localLaunchDsl)
            gmtool.startDevice(localLaunchDsl.name)
        }
    }

    @Override
    protected void stopDevice(GMTool gmtool, VDLaunchDsl launchDsl) {
        LocalVDLaunchDsl localLaunchDsl = (LocalVDLaunchDsl)launchDsl
        try {
            if (!localLaunchDsl.stopWhenFinish) {
                return
            }
            gmtool.stopDevice(localLaunchDsl.name)
            if (!localLaunchDsl.deleteWhenFinish) {
                return
            }
            gmtool.deleteDevice(localLaunchDsl.name)
        } catch (GMToolException e) {
        }
    }

    static void checkAndEdit(GMTool gmtool, VDLaunchDsl launchDsl) {
        if (!gmtool.isDeviceCreated(launchDsl.name)) {
            return
        }

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(launchDsl.name)
        gmtool.updateDevice(device)

        if (device != launchDsl) {
            gmtool.editDevice(launchDsl)
        }
    }
}
