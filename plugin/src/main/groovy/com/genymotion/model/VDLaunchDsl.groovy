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

import com.genymotion.tools.GMToolException
import com.genymotion.tools.Log
import com.genymotion.tools.Tools
import groovy.transform.CompileStatic

@CompileStatic
abstract class VDLaunchDsl extends GenymotionVirtualDevice {

    public static final String INVALID_PARAMETER = "You need to specify an already created device name " +
            "or a valid template to declare a device"

    List<String> productFlavors

    DeviceLocation deviceLocation

    protected def templateExists = null
    protected def deviceExists = null
    protected boolean create = false

    String template
    def pushBefore
    Map<String, String> pullBefore
    def pushAfter
    Map<String, String> pullAfter
    def install
    def flash
    String logcat
    boolean clearLogAfterBoot = true

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

    public void checkParams(boolean abortOnError = true) {
        checkNameAndTemplate(abortOnError)
        checkPaths(abortOnError)
    }

    def checkPaths(boolean abortOnError = true) {
        def file

        if ((file = Tools.checkFilesExist(pushBefore)) != true) {
            handlePathError("The file $file on pushBefore instruction for the device $name was not found.",
                    abortOnError)
        }

        if ((file = Tools.checkFilesExist(pushAfter)) != true) {
            handlePathError("The file $file on pushAfter instruction for the device $name was not found.", abortOnError)
        }

        if ((file = Tools.checkFilesExist(flash)) != true) {
            handlePathError("The file $file on flash instruction for the device $name was not found.", abortOnError)
        }

        if ((file = Tools.checkFilesExist(install)) != true) {
            handlePathError("The file $file on install instruction for the device $name was not found.", abortOnError)
        }
    }

    private def handlePathError(String message, boolean abortOnError = true) {
        if (abortOnError) {
            throw new FileNotFoundException(message)
        } else {
            Log.warn(message)
        }
    }

    public void checkNameAndTemplate(boolean abortOnError = true) {
        deviceExists = gmtool.isDeviceCreated(name)
        templateExists = gmtool.templateExists(template)

        if (!deviceExists && !templateExists) {
            if (abortOnError) {
                throw new GMToolException("On device \"$name\", template: \"$template\". " + INVALID_PARAMETER)
            } else {
                Log.warn("On device \"$name\", template: \"$template\". " + INVALID_PARAMETER)
            }

        } else if (deviceExists && template != null) {
            Log.info(name + " already exists. A new device won't be created before launch and template is ignored")

        } else if (templateExists) {
            create = true
        }
    }

    boolean checkAndEdit() {
        if (!gmtool.isDeviceCreated(this.name)) {
            return false
        }

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(this.name, true)

        if (isDifferentFrom(device)) {
            return gmtool.editDevice(this)
        }

        return false
    }

    //TODO: this could go into GenymotionVirtualDevice (override equals())
    public boolean isDifferentFrom(GenymotionVirtualDevice device) {
        return this.density != device.density ||
                this.width != device.width ||
                this.height != device.height ||
                this.virtualKeyboard != device.virtualKeyboard ||
                this.navbarVisible != device.navbarVisible ||
                this.nbCpu != device.nbCpu ||
                this.ram != device.ram ||
                this.networkInfo.mode != device.networkInfo.mode ||
                this.networkInfo.bridgeInterface != device.networkInfo.bridgeInterface
    }

    protected def flash() {
        if (flash) {
            gmtool.flashDevice(this, flash)
        }
    }

    protected def install() {
        if (install) {
            gmtool.installToDevice(this, install)
        }
    }

    protected def pushBefore() {
        if (pushBefore) {
            gmtool.pushToDevice(this, pushBefore)
        }
    }

    protected def pullBefore() {
        if (pullBefore) {
            gmtool.pullFromDevice(this, pullBefore)
        }
    }

    protected def pushAfter() {
        if (pushAfter) {
            gmtool.pushToDevice(this, pushAfter)
        }
    }

    protected def pullAfter() {
        if (pullAfter) {
            gmtool.pullFromDevice(this, pullAfter)
        }
    }

    protected def logcatClear() {
        gmtool.logcatClear(this)
    }

    protected def logcatClearIfNeeded() {
        if (logcat?.trim() && clearLogAfterBoot) {
            gmtool.logcatClear(this)
        }
    }

    protected def logcatDump() {
        if (logcat?.trim()) {
            gmtool.logcatDump(this, logcat)
        }
    }
}
