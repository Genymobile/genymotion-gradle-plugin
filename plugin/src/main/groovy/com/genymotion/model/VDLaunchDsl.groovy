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

package com.genymotion.model

import groovy.transform.CompileStatic

@CompileStatic
class VDLaunchDsl extends GenymotionVDLaunch {

    List<String> productFlavors

    VDLaunchDsl(String name) {
        super(name)
    }

    boolean hasFlavor(String flavor) {
        //if there is no flavor defined, we consider it as true
        if (flavor == null || productFlavors == null) {
            return true
        }
        productFlavors.contains(flavor)
    }

    public void setProductFlavors(String... flavors) {
        if (flavors?.size() == 1) {
            productFlavors = [flavors[0]]
        } else {
            productFlavors = []
            productFlavors.addAll(flavors)
        }
    }

    public void setProductFlavors(String flavor) {
        if (flavor == null) {
            productFlavors = []
            return
        }

        productFlavors = [flavor]
    }

    public void setProductFlavors(Collection<String> flavors) {
        if (flavors == null) {
            productFlavors = []
            return
        }

        productFlavors = []
        productFlavors.addAll(flavors)
    }

    public void productFlavors(String flavor) {
        setProductFlavors(flavor)
    }

    public void productFlavors(String... flavors) {
        setProductFlavors(flavors)
    }

    public void productFlavors(Collection<String> flavors) {
        setProductFlavors(flavors)
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
