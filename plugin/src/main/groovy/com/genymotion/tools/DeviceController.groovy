package com.genymotion.tools

import com.genymotion.model.DeviceLocation
import com.genymotion.model.GenymotionConfig
import com.genymotion.model.VDLaunchDsl

/**
 * Responsible for launching and finishing devices. Implemented differently for local and cloud devices.
 *
 * Users should get an instance via the createInstance static method.
 */
abstract class DeviceController {
    def GenymotionConfig config

    public static DeviceController createInstance(DeviceLocation location) {
        if (location == DeviceLocation.CLOUD) {
            return new CloudDeviceController()
        } else {
            return new LocalDeviceController()
        }
    }

    /**
     * Performs the job of the genymotionLaunch task
     * @param devices List of devices to launch
     */
    public void launch(Collection<? extends VDLaunchDsl> devices) {
        GMTool gmtool = createGMTool()
        String location = gmtool.deviceLocation.toString().toLowerCase()
        if (config.verbose) {
            Log.debug("Starting " + location + " devices")
        }

        prepareLaunch(gmtool)

        devices.each() {
            launchDevice(gmtool, it)
        }

        if (config.verbose) {
            Log.debug("Currently running " + location + " devices")
            gmtool.getRunningDevices(true)
        }
    }

    /**
     * Performs the job of the genymotionFinish task
     * @param devices List of devices to finish
     */
    public void finish(Collection<? extends VDLaunchDsl> devices) {
        GMTool gmtool = createGMTool()
        devices.each() {
            finishDevice(gmtool, it)
        }
    }

    /**
     * Called before launching devices. Gives the controller the opportunity to initialize things.
     * @param gmtool the GMTool instance to use
     */
    protected void prepareLaunch(GMTool gmtool) {
    }

    /**
     * Create a GMTool instance suitable for the controller
     * @return the created GMTool instance
     */
    protected abstract GMTool createGMTool()

    protected abstract void startDevice(GMTool gmtool, VDLaunchDsl launchDsl)

    protected abstract void stopDevice(GMTool gmtool, VDLaunchDsl launchDsl)

    protected void launchDevice(GMTool gmtool, VDLaunchDsl launchDsl) {
        // FIXME: GenymotionVirtualDevice should not have a gmtool instance
        launchDsl.gmtool = gmtool
        try {
            startDevice(gmtool, launchDsl)
            launchDsl.logcatClearIfNeeded()
            launchDsl.flash()
            launchDsl.install()
            launchDsl.pushBefore()
            launchDsl.pullBefore()
        } catch (Exception e) {
            e.printStackTrace()
            Log.error("An error occured. Deleting $launchDsl.name if needed.")
            stopDevice(gmtool, launchDsl)

            //then, we throw a new exception to end task, if needed
            if (config.abortOnError) {
                throw new GMToolException("GMTool command failed. " + e.getMessage())
            }
        }
    }

    protected void finishDevice(GMTool gmtool, VDLaunchDsl launchDsl) {
        // FIXME: GenymotionVirtualDevice should not have a gmtool instance
        launchDsl.gmtool = gmtool
        try {
            if (launchDsl.isRunning()) {
                launchDsl.logcatDump()
                launchDsl.pushAfter()
                launchDsl.pullAfter()
            }
            stopDevice(gmtool, launchDsl)

        } catch (Exception e) {
            e.printStackTrace()
            Log.error("An error occured. Deleting $launchDsl.name if needed.")
            stopDevice(gmtool, launchDsl)

            //then, we throw a new exception to end task, if needed
            if (config.abortOnError) {
                throw new GMToolException("GMTool command failed. " + e.getMessage())
            }
        }
    }
}
