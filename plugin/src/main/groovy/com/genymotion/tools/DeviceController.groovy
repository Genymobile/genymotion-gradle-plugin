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

    /**
     * Create (if necessary) and start the device defined in launchDsl
     *
     * @param gmtool The gmtool instance to use
     * @param launchDsl The definition of the device
     */
    protected abstract void startDevice(GMTool gmtool, VDLaunchDsl launchDsl)

    /**
     * Stop (and delete if necessary) the device defined in launchDsl
     *
     * @param gmtool The gmtool instance to use
     * @param launchDsl The definition of the device
     */
    protected abstract void stopDevice(GMTool gmtool, VDLaunchDsl launchDsl)

    /**
     * Call {@link #startDevice} then perform all actions defined in launchDsl up to the *Before actions
     *
     * @param gmtool The gmtool instance to use
     * @param launchDsl The definition of the device
     */
    protected void launchDevice(GMTool gmtool, VDLaunchDsl launchDsl) {
        try {
            startDevice(gmtool, launchDsl)
            launchDsl.logcatClearIfNeeded(gmtool)
            launchDsl.doFlash(gmtool)
            launchDsl.doInstall(gmtool)
            launchDsl.doPushBefore(gmtool)
            launchDsl.doPullBefore(gmtool)
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

    /**
     * Perform all actions defined after the *Before actions, then call {@link #stopDevice}
     * @param gmtool
     * @param launchDsl
     */
    protected void finishDevice(GMTool gmtool, VDLaunchDsl launchDsl) {
        try {
            gmtool.updateDevice(launchDsl)
            if (launchDsl.isRunning()) {
                launchDsl.logcatDump(gmtool)
                launchDsl.doPushAfter(gmtool)
                launchDsl.doPullAfter(gmtool)
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
