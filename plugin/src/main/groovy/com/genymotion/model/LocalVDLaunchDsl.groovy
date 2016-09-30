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

/**
 * Represents a device definition inside the `devices` container
 *
 * Adds properties which are specific to local devices
 */
class LocalVDLaunchDsl extends VDLaunchDsl {
    boolean start = true
    boolean deleteWhenFinish = true
    boolean stopWhenFinish = true

    LocalVDLaunchDsl(String name) {
        super(name)
        deviceLocation = DeviceLocation.LOCAL
    }

    public void setNetworkMode(String... networkingMode) {
        if (networkingMode == null) {
            networkInfo = NetworkInfo.createNatNetworkInfo()
        }

        if (NetworkInfo.isNetworkModeValid(networkingMode[0])) {
            networkInfo = new NetworkInfo(networkingMode[0], networkingMode[1])
        } else {
            networkInfo = NetworkInfo.createNatNetworkInfo()
        }
    }

    public void setNetworkMode(String networkingMode) {
        if (networkingMode == null) {
            networkInfo = NetworkInfo.createNatNetworkInfo()
            return
        }

        if (NetworkInfo.isNetworkModeValid(networkingMode)) {
            networkInfo = new NetworkInfo(networkingMode, "")
        } else {
            networkInfo = NetworkInfo.createNatNetworkInfo()
        }
    }

    public void networkMode(String networkingMode) {
        setNetworkMode(networkingMode)
    }

    public void networkMode(String... networkingMode) {
        setNetworkMode(networkingMode)
    }
}
