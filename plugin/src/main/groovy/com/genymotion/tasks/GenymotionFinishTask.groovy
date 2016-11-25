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

import com.genymotion.model.DeviceLocation
import com.genymotion.tools.DeviceController
import com.genymotion.tools.Log
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenymotionFinishTask extends DefaultTask {

    String flavor = null

    @TaskAction
    def exec() {

        Log.info("Finishing devices")

        execByLocation(DeviceLocation.LOCAL)
        execByLocation(DeviceLocation.CLOUD)
    }

    def execByLocation(DeviceLocation deviceLocation) {
        def devices = project.genymotion.getDevicesByLocationAndFlavor(deviceLocation, flavor)

        if (!devices.empty) {
            DeviceController controller = DeviceController.createInstance(deviceLocation)
            controller.config = project.genymotion.config
            controller.finish(devices)
        }
    }
}
