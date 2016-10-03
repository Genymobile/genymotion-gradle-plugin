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
import com.genymotion.tools.GMToolException
import com.genymotion.tools.Log
import com.genymotion.tools.Tools
import groovy.transform.CompileStatic

/**
 * Base class for device definitions
 *
 * Contains properties available for both local and cloud devices
 */
@CompileStatic
class VDLaunchDsl extends GenymotionVirtualDevice {

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

    public void checkParams(GMTool gmtool, boolean abortOnError = true) {
        checkNameAndTemplate(gmtool, abortOnError)
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

    public void checkNameAndTemplate(GMTool gmtool, boolean abortOnError = true) {
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

    def doFlash(GMTool gmtool) {
        if (flash) {
            gmtool.flashDevice(this, flash)
        }
    }

    def doInstall(GMTool gmtool) {
        if (install) {
            gmtool.installToDevice(this, install)
        }
    }

    def doPushBefore(GMTool gmtool) {
        if (pushBefore) {
            gmtool.pushToDevice(this, pushBefore)
        }
    }

    def doPullBefore(GMTool gmtool) {
        if (pullBefore) {
            gmtool.pullFromDevice(this, pullBefore)
        }
    }

    def doPushAfter(GMTool gmtool) {
        if (pushAfter) {
            gmtool.pushToDevice(this, pushAfter)
        }
    }

    def doPullAfter(GMTool gmtool) {
        if (pullAfter) {
            gmtool.pullFromDevice(this, pullAfter)
        }
    }

    def logcatClearIfNeeded(GMTool gmtool) {
        if (logcat?.trim() && clearLogAfterBoot) {
            gmtool.logcatClear(this)
        }
    }

    def logcatDump(GMTool gmtool) {
        if (logcat?.trim()) {
            gmtool.logcatDump(this, logcat)
        }
    }
}
