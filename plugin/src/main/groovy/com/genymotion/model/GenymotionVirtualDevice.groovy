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

import com.genymotion.tools.GMTool
import com.genymotion.tools.Log
import sun.nio.ch.Net


class GenymotionVirtualDevice {
    static final String STATE_ON = "On"
    static final String STATE_OFF = "Off"

    String name
    String androidVersion
    String genymotionVersion
    def width
    def height
    String density
    def dpi
    def nbCpu
    def ram
    def telephony
    def navbarVisible
    def virtualKeyboard
    String uuid
    String path
    String state
    String adbSerial
    NetworkInfo networkInfo

    GenymotionVirtualDevice(String name) {
        this.name = name
        this.networkInfo = NetworkInfo.createNatNetworkInfo()
    }

    GenymotionVirtualDevice(def name, def density, def width, def height, def virtualKeyboard, def navbarVisible,
                            def nbCpu, def ram, def networkInfo) {
        init(name, density, width, height, virtualKeyboard, navbarVisible, nbCpu, ram, networkInfo)
    }

    void init(def name, def density, def width, def height, def virtualKeyboard, def navbarVisible, def nbCpu, def ram,
                def networkingInfo) {
        if (name?.trim()) {
            this.name = name
        }
        if (density?.trim()) {
            this.density = density
        }
        if (width) {
            this.width = width.toInteger()
        }
        if (height) {
            this.height = height.toInteger()
        }
        if (virtualKeyboard != null) {
            this.virtualKeyboard = virtualKeyboard.toBoolean()
        }
        if (navbarVisible != null) {
            this.navbarVisible = navbarVisible.toBoolean()
        }
        if (nbCpu) {
            this.nbCpu = nbCpu.toInteger()
        }
        if (ram) {
            this.ram = ram.toInteger()
        }
        if (networkingInfo != null) {
            this.networkInfo = networkingInfo
        } else {
            this.networkInfo = NetworkInfo.createNatNetworkInfo()
        }
    }

    GenymotionVirtualDevice(String name, int dpi, int width, int height, boolean virtualKeyboard, boolean navbarVisible,
                            int nbCpu, int ram, NetworkInfo networkInfo) {
        this.name = name
        this.dpi = dpi
        this.width = width
        this.height = height
        this.virtualKeyboard = virtualKeyboard
        this.navbarVisible = navbarVisible
        this.nbCpu = nbCpu
        this.ram = ram
        this.networkInfo = networkInfo
    }

    String toString() {
        "Device: $name\n"
    }

    boolean equals(GenymotionVirtualDevice other) {
        name == other.name &&
                density == other.density &&
                width == other.width &&
                height == other.height &&
                virtualKeyboard == other.virtualKeyboard &&
                navbarVisible == other.navbarVisible &&
                nbCpu == other.nbCpu &&
                ram == other.ram &&
                networkInfo.mode == other.networkInfo.mode &&
                networkInfo.bridgeInterface == other.networkInfo.bridgeInterface
    }

    boolean isRunning() {
        state == STATE_ON
    }
}
