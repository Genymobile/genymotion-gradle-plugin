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

import groovy.transform.CompileStatic
import main.groovy.com.genymotion.tools.GMTool
import main.groovy.com.genymotion.tools.Log
import main.groovy.com.genymotion.tools.Tools

@CompileStatic
class GenymotionVDLaunch extends GenymotionVirtualDevice{

    public static final String INVALID_PARAMETER = "You need to specify an already created device name or a valid template to declare a device"

    protected def templateExists = null
    protected def deviceExists = null
    protected boolean create = false

    boolean start = true
    String template
    def pushBefore
    def pullBefore
    def pushAfter
    def pullAfter
    def install
    def flash
    String logcat
    boolean clearLogAfterBoot = true
    def deleteWhenFinish = null
    def stopWhenFinish = null

    GenymotionVDLaunch(String name) {
        super(name)
    }

    public void checkParams() {
        checkNameAndTemplate()
        checkPaths()
    }

    def checkPaths() {
        def file

        if((file = Tools.checkFilesExist(pushBefore)) != true)
            throw new FileNotFoundException("The file $file on pushBefore instruction for the device $name is not found.")

        if((file = Tools.checkFilesExist(pushAfter)) != true)
            throw new FileNotFoundException("The file $file on pushAfter instruction for the device $name is not found.")

        if((file = Tools.checkFilesExist(flash)) != true)
            throw new FileNotFoundException("The file $file on flash instruction for the device $name is not found.")

        if((file = Tools.checkFilesExist(install)) != true)
            throw new FileNotFoundException("The file $file on install instruction for the device $name is not found.")

        return true
    }

    public void checkNameAndTemplate() {
        deviceExists = GMTool.isDeviceCreated(name)
        templateExists = GMTool.isTemplateExists(template)

        //if name & template are null or not existing
        if (!deviceExists && !templateExists) {
            throw new IllegalArgumentException("On device \"$name\", template: \"$template\". " + INVALID_PARAMETER)
        }

        //if declared device name exists
        else if (deviceExists) {

            //if a template is declared
            if (template != null) {
                Log.info(name + " already exists. A new device won't be created before launch and template is ignored")
            }
        }

        //if declared template exists
        else if (templateExists) {
            create = true
        }
    }


    boolean checkAndEdit() {
        if(!GMTool.isDeviceCreated(this.name))
            return false

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(this.name, true)

        //if the configuration is different from the created device
        if(!this.density?.equals(device.density) ||
           !this.width?.equals(device.width) ||
           !this.height?.equals(device.height) ||
           !this.virtualKeyboard == device.virtualKeyboard ||
           !this.navbarVisible == device.navbarVisible ||
           !this.nbCpu?.equals(device.nbCpu) ||
           !this.ram?.equals(device.ram)) {

            return GMTool.editDevice(this)
        }

        false
    }

    protected def start() {
        if(start)
            GMTool.startDevice(this)
    }
    protected def stop() {
        GMTool.stopDevice(this)
    }

    protected def stopWhenFinish() {
        //if stop is not explicitly disabled, we stop
        if(stopWhenFinish != false)
            stop()
    }

    protected def deleteWhenFinish() {
        //if stop and delete are not explicitly disabled, we delete
        if(stopWhenFinish != false && deleteWhenFinish != false)
            delete()
    }

    protected def create() {
        if(templateExists == null)
            checkParams()

        if(template != null && template.toString().trim() && templateExists)
            GMTool.createDevice(this)
    }

    protected def delete() {
        GMTool.deleteDevice(this)
    }

    protected def flash() {
        GMTool.flashDevice(this, flash)
    }

    protected def install() {
        GMTool.installToDevice(this, install)
    }

    protected def pushBefore() {
        GMTool.pushToDevice(this, pushBefore)
    }

    protected def pullBefore() {
        GMTool.pullFromDevice(this, pullBefore)
    }

    protected def pushAfter() {
        GMTool.pushToDevice(this, pushAfter)
    }

    protected def pullAfter() {
        GMTool.pullFromDevice(this, pullAfter)
    }

    protected def logcatClear() {
        GMTool.logcatClear(this)
    }

    protected def logcatClearIfNeeded() {
        if(logcat?.trim() && clearLogAfterBoot)
            GMTool.logcatClear(this)
    }

    protected def logcatDump() {
        if(logcat?.trim())
            GMTool.logcatDump(this, logcat)
    }
}
