/*
 * Copyright (C) 2015 Genymobile
 *
 * This file is part of GenymotionGradlePlugin.
 *
 * GenymotionGradlePlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version
 *
 * GenymotionGradlePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenymotionGradlePlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.genymotion.tasks

import com.genymotion.model.GenymotionVirtualDevice
import com.genymotion.tools.GMTool
import com.genymotion.tools.GMToolException
import com.genymotion.tools.Log
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenymotionLaunchTask extends DefaultTask {
    String flavor

    @TaskAction
    def exec() {
        if (project.genymotion.config.verbose) {
            Log.info("Starting devices")
        }

        def devices = project.genymotion.getDevices(flavor)

        if (devices?.size() == 0) {
            return
        }

        def runningDevices = []

        GMTool gmtool = GMTool.newInstance()
        def virtualDevices = gmtool.getAllDevices(project.genymotion.config.verbose, false, false)
        virtualDevices.each {
            if (it.state == GenymotionVirtualDevice.STATE_ON) {
                runningDevices.add(it.name)
            }
        }

        def virtualDevicesNames = virtualDevices*.name

        devices.each() {
            processDevice(it, runningDevices, virtualDevicesNames)
        }

        if (project.genymotion.config.verbose) {
            Log.debug("-- Running devices --")
            gmtool.getRunningDevices(true)
        }
    }

    def processDevice(device, runningDevices, virtualDevicesNames) {
        if (!device.start) {
            return
        }

        if (project.genymotion.config.verbose) {
            Log.debug("Starting ${device.name}")
        }

        try {
            if (device.name && runningDevices != null && !runningDevices?.contains(device.name)) {
                if (!virtualDevicesNames?.contains(device.name)) {
                    device.create()
                }
                device.checkAndEdit()
                device.start()
            }

            device.logcatClearIfNeeded()
            device.flash()
            device.install()
            device.pushBefore()
            device.pullBefore()

        } catch (Exception e) {
            e.printStackTrace()
            abortLaunch(device)

            //then, we thow a new exception to end task, if needed
            if (project.genymotion.config.abortOnError) {
                throw new GMToolException("GMTool command failed. " + e.getMessage())
            }
        }
    }

    public void abortLaunch(device) {
        Log.error("An error occured. Stopping and deleting all launched devices, if needed.")
        project.genymotion.getDevices(flavor).each() {
            device.stopWhenFinish()
            device.deleteWhenFinish()
        }
    }
}
