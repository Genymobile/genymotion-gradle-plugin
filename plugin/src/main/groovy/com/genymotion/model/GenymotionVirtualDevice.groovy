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
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenymotionGradlePlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package main.groovy.com.genymotion.model

import main.groovy.com.genymotion.tools.GMTool


class GenymotionVirtualDevice {

    static String STATE_ON = "On"
    static String STATE_OFF = "Off"

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
    String ip

    GenymotionVirtualDevice(String name, boolean fill=false) {
        this.name = name;

        if(fill)
            fillFromDetails()
    }


    GenymotionVirtualDevice(Map params) {
        if(params)
            init(params.name, params.density, params.width, params.height, params.virtualKeyboard, params.navbarVisible, params.nbCpu, params.ram)
    }

    GenymotionVirtualDevice(def name, def density, def width, def height, def virtualKeyboard, def navbarVisible, def nbCpu, def ram) {
        init(name, density, width, height, virtualKeyboard, navbarVisible, nbCpu, ram)
    }

    void init(def name, def density, def width, def height, def virtualKeyboard, def navbar, def nbCpu, def ram) {
        if(name?.trim())
            this.name = name
        if(density)
            this.density = density
        if(width)
            this.width = width.toInteger()
        if(height)
            this.height = height.toInteger()
        if(virtualKeyboard != null)
            this.virtualKeyboard = virtualKeyboard.toBoolean()
        if(navbar != null)
            this.navbarVisible = navbar.toBoolean()
        if(nbCpu)
            this.nbCpu = nbCpu.toInteger()
        if(ram)
            this.ram = ram.toInteger()
    }


    GenymotionVirtualDevice(String name, int dpi, int width, int height, boolean virtualKeyboard, boolean navbarVisible, int nbCpu, int ram) {
        this.name = name
        this.dpi = dpi
        this.width = width
        this.height = height
        this.virtualKeyboard = virtualKeyboard
        this.navbarVisible = navbarVisible
        this.nbCpu = nbCpu
        this.ram = ram
    }

    protected def start(){
        GMTool.startDevice(this)
    }

    protected def restart(){
        GMTool.restartDevice(this)
    }

    protected def stop(){
        GMTool.stopDevice(this)
    }

    protected def reset(){
        GMTool.resetDevice(this)
    }

    protected def adbdisconnect(){
        GMTool.adbDisconnectDevice(this)
    }

    protected def adbconnect(){
        GMTool.adbConnectDevice(this)
    }

    String toString() {
        String result = "Device: $name\n"
        result
    }



    boolean equals(GenymotionVirtualDevice other){
        (this.name == other.name)
    }

    def fillFromDetails(boolean verbose=false){
        GMTool.getDevice(this, verbose)
    }

    boolean isRunning(){
        state == STATE_ON
    }
}
