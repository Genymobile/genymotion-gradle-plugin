/*
 * Copyright (C) 2016 Genymobile
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

package com.genymotion.model

import com.genymotion.tools.GMTool
import com.genymotion.tools.InvalidPropertyException
import com.genymotion.tools.Log

/**
 * Represents an entry inside the `cloudDevices` container
 */
class CloudVDLaunchDsl extends VDLaunchDsl {
    static final String[] LOCAL_ONLY_PROPERTIES = [
            "density", "width", "height", "virtualKeyboard", "navbarVisible", "nbCpu", "ram",
            ]

    CloudVDLaunchDsl(String name) {
        super(name)
        deviceLocation = DeviceLocation.CLOUD
    }

    // Since VDLaunchDsl currently inherits from GenymotionVirtualDevice, it exposes all device properties. Some of them
    // are not supported on the cloud, so we need to prevent the user from using them.
    // When VDLaunchDsl no longer inherits from GenymotionVirtualDevice these checks won't be necessary anymore.
    public void checkParams(GMTool gmtool, boolean abortOnError = true) {
        super.checkParams(gmtool, abortOnError)
        for (String property : LOCAL_ONLY_PROPERTIES) {
            def value = this."$property"
            if (value) {
                if (abortOnError) {
                    throw new InvalidPropertyException(property)
                } else {
                    Log.warn("On device \"$name\", \"$property\" is not a valid property")
                }
            }
        }
    }
}
