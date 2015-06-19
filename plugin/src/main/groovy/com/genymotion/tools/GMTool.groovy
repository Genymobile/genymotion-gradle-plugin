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

package com.genymotion.tools

import com.genymotion.model.GenymotionConfig
import com.genymotion.model.GenymotionTemplate
import com.genymotion.model.GenymotionVDLaunch
import com.genymotion.model.GenymotionVirtualDevice
import org.codehaus.groovy.runtime.NullObject

import java.util.concurrent.TimeoutException

class GMTool {

    public static GenymotionConfig GENYMOTION_CONFIG = null

    //@formatter:off
    private static final String GENYTOOL      = "gmtool"
    private static final String VERBOSE       = "--verbose"
    private static final String SOURCE        = "--source"
    private static final String SOURCE_GRADLE = SOURCE + "=gradle"

    //root actions
    private static final String LOGZIP        = "logzip"
    private static final String HELP          = "help"
    //admin actions
    private static final String ADMIN         = "admin"
    private static final String LIST          = "list"
    private static final String TEMPLATES     = "templates"
    private static final String CREATE        = "create"
    private static final String EDIT          = "edit"
    private static final String DELETE        = "delete"
    private static final String CLONE         = "clone"
    private static final String DETAILS       = "details"
    private static final String START         = "start"
    private static final String RESTART       = "restart"
    private static final String STOP          = "stop"
    private static final String STOPALL       = "stopall"
    private static final String FACTORY_RESET = "factoryreset"
    //device actions
    private static final String DEVICE        = "device"
    private static final String PUSH          = "push"
    private static final String PULL          = "pull"
    private static final String INSTALL       = "install"
    private static final String FLASH         = "flash"
    private static final String LOGCAT_CLEAR  = "logcatclear"
    private static final String LOGCAT_DUMP   = "logcatdump"
    private static final String ADBDISCONNECT = "adbdisconnect"
    private static final String ADBCONNECT    = "adbconnect"
    //config actions
    private static final String CONFIG        = "config"
    private static final String PRINT         = "print"
    private static final String RESET         = "reset"
    private static final String CLEARCACHE    = "clearcache"
    //license
    private static final String LICENSE       = "license"
    private static final String INFO          = "info"
    private static final String REGISTER      = "register"
    private static final String COUNT         = "count"
    private static final String VERIFY        = "verify"
    private static final String VALIDITY      = "validity"
    //options
    private static final String OPT_RUNNING              = "--running"
    private static final String OPT_OFF                  = "--off"
    private static final String OPT_FULL                 = "--full"
    private static final String OPT_DENSITY              = '--density='
    private static final String OPT_WIDTH                = '--width='
    private static final String OPT_HEIGHT               = '--height='
    private static final String OPT_VIRTUAL_KEYBOARD     = '--virtualkeyboard='
    private static final String OPT_NAVBAR               = '--navbar='
    private static final String OPT_NBCPU                = '--nbcpu='
    private static final String OPT_RAM                  = "--ram="
    private static final String OPT_NAME                 = "-n="
    private static final String OPT_STATISTICS           = "statistics="
    private static final String OPT_USERNAME_CONFIG      = "username="
    private static final String OPT_PASSWORD_CONFIG      = "password="
    private static final String OPT_STORE_CREDENTIALS    = "store_credentials="
    private static final String OPT_PROXY                = "proxy="
    private static final String OPT_PROXY_ADDRESS        = "proxy_address="
    private static final String OPT_PROXY_PORT           = "proxy_port="
    private static final String OPT_PROXY_AUTH           = "proxy_auth="
    private static final String OPT_PROXY_USERNAME       = "proxy_username="
    private static final String OPT_PROXY_PASSWORD       = "proxy_password="
    private static final String OPT_VIRTUAL_DEVICE_PATH  = "virtual_device_path="
    private static final String OPT_SDK_PATH             = "sdk_path="
    private static final String OPT_USE_CUSTOM_SDK       = "use_custom_sdk="
    private static final String OPT_SCREEN_CAPTURE_PATH  = "screen_capture_path="


    //code returned by gmtool or command line
    public static final int RETURN_NO_ERROR                = 0
    public static final int RETURN_NO_SUCH_ACTION          = 1
    public static final int RETURN_BAD_PARAM_VALUE         = 2
    public static final int RETURN_COMMAND_FAILED          = 3
    public static final int RETURN_VMENGINE_ERROR          = 4
    public static final int RETURN_DEVICE_NOT_FOUND        = 5
    public static final int RETURN_CANT_LOGIN              = 6
    public static final int RETURN_CANT_REGISTER_LICENSE   = 7
    public static final int RETURN_CANT_ACTIVATE_LICENSE   = 8
    public static final int RETURN_NO_ACTIVATED_LICENSE    = 9
    public static final int RETURN_INVALID_LICENSE         = 10
    public static final int RETURN_MISSING_ARGUMENTS       = 11
    public static final int RETURN_VM_NOT_STOPPED          = 12
    public static final int RETURN_LICENSE_REQUIRED        = 13
    public static final int RETURN_COMMAND_NOT_FOUND_UNIX  = 127
    public static final int RETURN_SIGTERM                 = 143

    //@formatter:on

    static String GENYMOTION_PATH_ERROR_MESSAGE = "gmtool command not found. You have to specify the Genymotion path " +
            "with the genymotion.config.genymotionPath parameter."


    static def usage() {
        return cmd([GENYTOOL, HELP])
    }

    /*
    CONFIG
     */

    static def resetConfig() {
        return cmd([GENYTOOL, CONFIG, RESET])
    }

    static def clearCache() {
        return cmd([GENYTOOL, CONFIG, CLEARCACHE])
    }

    static def logzip(String path = "", String vdName = "") {

        def command = [GENYTOOL, LOGZIP]

        if (vdName?.trim()) {
            command.push(OPT_NAME + vdName)
        }

        if (path?.trim()) {
            command.push(path)
        }

        return cmd([GENYTOOL, LOGZIP])
    }

    static def getConfig(boolean verbose = false) {

        GenymotionConfig config = new GenymotionConfig()

        def exitCode = cmd([GENYTOOL, CONFIG, PRINT], verbose) { line, count ->

            String[] info = line.split("=")
            if (info.length > 1 && info[1].trim()) {

                switch (info[0].trim()) {
                    case "statistics":
                        config.statistics = info[1].trim().toBoolean()
                        break
                    case "username":
                        config.username = info[1].trim()
                        break
                    case "store_credentials":
                        config.storeCredentials = info[1].trim().toBoolean()
                        break
                    case "proxy":
                        config.proxy = info[1].trim().toBoolean()
                        break
                    case "proxy_address":
                        config.proxyAddress = info[1].trim()
                        break
                    case "proxy_port":
                        config.proxyPort = info[1].toInteger()
                        break
                    case "proxy_auth":
                        config.proxyAuth = info[1].trim().toBoolean()
                        break
                    case "proxy_username":
                        config.proxyUsername = info[1].trim()
                        break
                    case "virtual_device_path":
                        config.virtualDevicePath = info[1].trim()
                        break
                    case "sdk_path":
                        config.androidSdkPath = info[1].trim()
                        break
                    case "use_custom_sdk":
                        config.useCustomSdk = info[1].trim().toBoolean()
                        break
                    case "screen_capture_path":
                        config.screenCapturePath = info[1].trim()
                        break
                }
            }
        }
        if (exitCode == RETURN_NO_ERROR) {
            return config
        }

        return exitCode
    }

    static def setConfig(GenymotionConfig config, boolean verbose = false) {
        if (!config) {
            return false
        }
        return setConfig(config.statistics, config.username, config.password, config.storeCredentials, config.proxy,
                config.proxyAddress, config.proxyPort, config.proxyAuth, config.proxyUsername, config.proxyPassword,
                config.virtualDevicePath, config.androidSdkPath, config.useCustomSdk, config.screenCapturePath, verbose)
    }


    static def setConfig(def statistics = null, String username = null, String password = null,
                         def storeCredentials = null, def proxy = null, String proxyAddress = null,
                         def proxyPort = null, def proxyAuth = null, String proxyUsername = null,
                         String proxyPassword = null, String virtualDevicePath = null, String androidSdkPath = null,
                         def useCustomSdk = null, String screenCapturePath = null, boolean verbose = false) {

        def command = [GENYTOOL, CONFIG]

        if (username != null && password != null) {
            command.push(OPT_USERNAME_CONFIG + username)
            command.push(OPT_PASSWORD_CONFIG + password)
        } else if ((username != null || password != null) && verbose) {
            Log.error("username and password need to be both transmitted. Ignoring both arguments")
        }

        if (statistics != null) {
            command.push(OPT_STATISTICS + statistics)
        }
        if (storeCredentials != null) {
            command.push(OPT_STORE_CREDENTIALS + storeCredentials)
        }
        if (proxy != null) {
            command.push(OPT_PROXY + proxy)
        }
        if (proxyAddress != null) {
            command.push(OPT_PROXY_ADDRESS + proxyAddress)
        }
        if (proxyPort != null) {
            command.push(OPT_PROXY_PORT + proxyPort)
        }
        if (proxyAuth != null) {
            command.push(OPT_PROXY_AUTH + proxyAuth)
        }
        if (proxyUsername != null) {
            command.push(OPT_PROXY_USERNAME + proxyUsername)
        }
        if (proxyPassword != null) {
            command.push(OPT_PROXY_PASSWORD + proxyPassword)
        }
        if (virtualDevicePath != null) {
            command.push(OPT_VIRTUAL_DEVICE_PATH + virtualDevicePath)
        }
        if (androidSdkPath != null) {
            command.push(OPT_SDK_PATH + androidSdkPath)
        }
        if (useCustomSdk != null) {
            command.push(OPT_USE_CUSTOM_SDK + useCustomSdk)
        }
        if (screenCapturePath != null) {
            command.push(OPT_SCREEN_CAPTURE_PATH + screenCapturePath)
        }

        return cmd(command, verbose) { line, count ->
        }
    }

    /*
    LICENSE
     */

    static def setLicense(String license, boolean verbose = false) {
        def command = [GENYTOOL, LICENSE, REGISTER, license]

        return cmd(command, verbose)
    }

    /*
    ADMIN
     */

    static def getAllDevices(boolean verbose = false, boolean fill = true, boolean nameOnly = false) {

        def devices = []

        cmd([GENYTOOL, ADMIN, LIST], verbose) { line, count ->
            def device = parseListLine(count, line, nameOnly)
            if (device) {
                devices.add(device)
            }
        }

        if (fill && !nameOnly) {
            devices.each() {
                it.fillFromDetails()
            }
        }

        devices
    }

    static def getRunningDevices(boolean verbose = false, boolean fill = true, boolean nameOnly = false) {

        def devices = []

        cmd([GENYTOOL, ADMIN, LIST, OPT_RUNNING], verbose) { line, count ->
            def device = parseListLine(count, line, nameOnly)
            if (device) {
                devices.add(device)
            }
        }

        if (fill && !nameOnly) {
            devices.each() {
                it.fillFromDetails()
            }
        }

        devices
    }

    static def getStoppedDevices(boolean verbose = false, boolean fill = true, boolean nameOnly = false) {

        def devices = []

        cmd([GENYTOOL, ADMIN, LIST, OPT_OFF], verbose) { line, count ->
            def device = parseListLine(count, line, nameOnly)
            if (device) {
                devices.add(device)
            }
        }

        if (fill && !nameOnly) {
            devices.each() {
                it.fillFromDetails()
            }
        }

        devices
    }

    static boolean isDeviceRunning(def device, boolean verbose = false) {
        isDeviceRunning(device.name, verbose)
    }

    static boolean isDeviceRunning(String name, boolean verbose = false) {
        def devices = getRunningDevices(verbose, false, true)
        return devices?.contains(name)
    }

    private static def parseListLine(int count, String line, boolean nameOnly) {

        //we skip the first 2 lines
        if (count < 2) {
            return
        }

        def device

        String[] infos = line.split('\\|')

        String name = infos[3].trim()
        if (nameOnly) {
            device = name
        } else {
            device = new GenymotionVirtualDevice(name)
            device.ip = infos[1].trim()
            device.state = infos[0].trim()
        }
        device
    }


    static def isDeviceCreated(String name, boolean verbose = false) {

        if (!name?.trim()) {
            return false
        }

        boolean alreadyExists = false

        def devices = GMTool.getAllDevices(verbose, false)

        devices.each() {
            if (it.name.equals(name)) {
                alreadyExists = true
            }
        }
        return alreadyExists
    }

    static def getTemplatesNames(boolean verbose = false) {

        def templates = []

        def template = null

        def exitCode = noNull {
            return cmd([GENYTOOL, ADMIN, TEMPLATES], verbose) { line, count ->

                //if empty line and template filled
                if (!line && template) {
                    templates.add(template)
                    template = null
                }

                String[] info = line.split("\\:")
                switch (info[0].trim()) {
                    case "Name":
                        if (!template) {
                            template = info[1].trim()
                        }
                        break
                }
            }
        }

        if (template) {
            templates.add(template)
        }

        if (exitCode == RETURN_NO_ERROR) {
            return templates
        } else {
            return exitCode
        }
    }

    static def getTemplates(boolean verbose = false) {

        def templates = []

        def template = new GenymotionTemplate()

        int exitCode = noNull {
            return cmd([GENYTOOL, ADMIN, TEMPLATES, OPT_FULL], verbose) { line, count ->

                //if empty line and the template is filled
                if (!line && template.name) {
                    templates.add(template)
                    template = new GenymotionTemplate()
                }

                String[] info = line.split("\\:")
                switch (info[0].trim()) {
                    case "Name":
                        if (!template.name) {
                            template.name = info[1].trim()
                        }
                        break
                    case "UUID":
                        template.uuid = info[1].trim()
                        break
                    case "Description":
                        template.description = info[1].trim()
                        break
                    case "Android Version":
                        template.androidVersion = info[1].trim()
                        break
                    case "Genymotion Version":
                        template.genymotionVersion = info[1].trim()
                        break
                    case "Screen Width":
                        template.width = info[1].trim().toInteger()
                        break
                    case "Screen Height":
                        template.height = info[1].trim().toInteger()
                        break
                    case "Screen Density":
                        template.density = info[1].trim()
                        break
                    case "Screen DPI":
                        template.dpi = info[1].trim().toInteger()
                        break
                    case "Nb CPU":
                        template.nbCpu = info[1].trim().toInteger()
                        break
                    case "RAM":
                        template.ram = info[1].trim().toInteger()
                        break
                    case "Internal Storage":
                        template.internalStorage = info[1].trim().toInteger()
                        break
                    case "Telephony":
                        template.telephony = info[1].trim().toBoolean()
                        break
                    case "Nav Bar Visible":
                        template.navbarVisible = info[1].trim().toBoolean()
                        break
                    case "Virtual Keyboard":
                        template.virtualKeyboard = info[1].trim().toBoolean()
                        break
                }
            }

        }
        if (template.name) {
            templates.add(template)
        }

        if (exitCode == RETURN_NO_ERROR) {
            return templates
        } else {
            return exitCode
        }
    }

    static boolean templateExists(String template, boolean verbose = false) {

        if (!template?.trim()) {
            return false
        }

        def templates = getTemplatesNames(verbose)
        if (templates instanceof ArrayList) {
            templates?.contains(template)
        } else {
            return false
        }
    }

    static def createDevice(GenymotionVDLaunch device) {
        return createDevice(device.template, device.name)
    }

    static def createDevice(GenymotionTemplate template) {
        return createDevice(template.name, template.name)
    }

    static def createDevice(def template, def deviceName, def density = "", def width = "", def height = "",
                            def virtualKeyboard = "", def navbarVisible = "", def nbcpu = "", def ram = "") {

        def exitValue = noNull() {
            cmd([GENYTOOL, ADMIN, CREATE, template, deviceName, OPT_DENSITY + density, OPT_WIDTH + width,
                 OPT_HEIGHT + height, OPT_VIRTUAL_KEYBOARD + virtualKeyboard, OPT_NAVBAR + navbarVisible,
                 OPT_NBCPU + nbcpu, OPT_RAM + ram])
        }

        if (exitValue == RETURN_NO_ERROR) {
            return new GenymotionVirtualDevice(deviceName, density, width, height, virtualKeyboard, navbarVisible, nbcpu, ram)
        } else {
            return exitValue
        }
    }

    static def editDevice(GenymotionVirtualDevice device) {
        return editDevice(device.name, device.density, device.width, device.height, device.virtualKeyboard, device.navbarVisible, device.nbCpu, device.ram)
    }

    static def editDevice(def deviceName, def density = "", def width = "", def height = "", def virtualKeyboard = "",
                          def navbarVisible = "", def nbcpu = "", def ram = "") {

        return noNull() {
            return cmd([GENYTOOL, ADMIN, EDIT, deviceName, OPT_DENSITY + density, OPT_WIDTH + width,
                        OPT_HEIGHT + height, OPT_VIRTUAL_KEYBOARD + virtualKeyboard, OPT_NAVBAR + navbarVisible,
                        OPT_NBCPU + nbcpu, OPT_RAM + ram])
        }
    }

    static def deleteDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return deleteDevice(device.name, verbose)
    }

    static def deleteDevice(def deviceName, boolean verbose = false) {
        return cmd([GENYTOOL, ADMIN, DELETE, deviceName], verbose)
    }

    static def cloneDevice(GenymotionVirtualDevice device, def name, boolean verbose = false) {
        return cloneDevice(device.name, name, verbose)
    }

    static def cloneDevice(def deviceName, def newName, boolean verbose = false) {
        return cmd([GENYTOOL, ADMIN, CLONE, deviceName, newName], verbose)
    }

    static def getDevice(String name, boolean verbose = false) {

        if (name == null) {
            return null
        }

        def device = new GenymotionVirtualDevice(name)
        return getDevice(device, verbose)
    }

    static def getDevice(def device, boolean verbose = false) {

        if (device == null) {
            return null
        }

        cmd([GENYTOOL, ADMIN, DETAILS, device.name], verbose) { line, count ->

            String[] info = line.split("\\:")
            switch (info[0].trim()) {
                case "Name":
                    device.name = info[1].trim()
                    break
                case "Android Version":
                    device.androidVersion = info[1].trim()
                    break
                case "Genymotion Version":
                    device.genymotionVersion = info[1].trim()
                    break
                case "Screen Width":
                    device.width = info[1].trim().toInteger()
                    break
                case "Screen Height":
                    device.height = info[1].trim().toInteger()
                    break
                case "Screen Density":
                    device.density = info[1].trim()
                    break
                case "Screen DPI":
                    device.dpi = info[1].trim().toInteger()
                    break
                case "Nb CPU":
                    device.nbCpu = info[1].trim().toInteger()
                    break
                case "RAM":
                    device.ram = info[1].trim().toInteger()
                    break
                case "Telephony":
                    device.telephony = info[1].trim().toBoolean()
                    break
                case "Nav Bar Visible":
                    device.navbarVisible = info[1].trim().toBoolean()
                    break
                case "Virtual Keyboard":
                    device.virtualKeyboard = info[1].trim().toBoolean()
                    break
                case "UUID":
                    device.uuid = info[1].trim()
                    break
                case "Path":
                    device.path = info[1].trim()
                    break
                case "State":
                    device.state = info[1].trim()
                    break
                case "IP":
                    device.ip = info[1].trim()
                    break
            }
        }
        device
    }

    static def startDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return startDevice(device.name, verbose)
    }

    static def startDevice(def deviceName, boolean verbose = false) {
        return cmd([GENYTOOL, ADMIN, START, deviceName], verbose)
    }

    static def restartDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return restartDevice(device.name, verbose)
    }

    static def restartDevice(def deviceName, boolean verbose = false) {
        return cmd([GENYTOOL, ADMIN, RESTART, deviceName], verbose)
    }

    static def stopDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return stopDevice(device.name, verbose)
    }

    static def stopDevice(def deviceName, boolean verbose = false) {
        return cmd([GENYTOOL, ADMIN, STOP, deviceName], verbose)
    }

    static def stopAllDevices(boolean verbose = false) {
        return cmd([GENYTOOL, ADMIN, STOPALL], verbose)
    }

    static def resetDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return resetDevice(device.name, verbose)
    }

    static def resetDevice(def deviceName, boolean verbose = false) {
        return cmd([GENYTOOL, ADMIN, START, FACTORY_RESET, deviceName], verbose)
    }

    static def startAutoDevice(def template, def deviceName, boolean verbose = false) {
        def device = createDevice(template, deviceName, verbose)

        if (!device instanceof GenymotionVirtualDevice) {
            return device
        }

        def startExit = startDevice(device)

        if (startExit == RETURN_NO_ERROR) {
            return device
        } else {
            return startExit
        }
    }

    /*
    Device
     */

    static def pushToDevice(GenymotionVirtualDevice device, def files, boolean verbose = false) {
        pushToDevice(device.name, files, verbose)
    }

    static def pushToDevice(def deviceName, def files, boolean verbose = false) {

        if (!files) {
            return false
        }

        def exitValues = []

        if (files instanceof String) {
            files = [files]
        }

        files.each() {

            def command = [GENYTOOL, DEVICE, OPT_NAME + deviceName, PUSH]
            if (files instanceof Map) {
                command.push(it.key)
                command.push(it.value)
            } else {
                command.push(it)
            }

            int exitValue = cmd(command, verbose)
            exitValues.add(exitValue)
        }

        return exitValues
    }

    static def pullFromDevice(GenymotionVirtualDevice device, def files, boolean verbose = false) {
        pullFromDevice(device.name, files, verbose)
    }

    static def pullFromDevice(String deviceName, String source, String destination, boolean verbose = false) {
        pullFromDevice(deviceName, [(source): destination], verbose)
    }

    static def pullFromDevice(def deviceName, def files, boolean verbose = false) {

        if (!files) {
            return false
        }

        def exitValues = []

        if (files instanceof String) {
            files = [files]
        }

        files.each() {

            def command = [GENYTOOL, DEVICE, OPT_NAME + deviceName, PULL]
            if (files instanceof Map) {
                command.push(it.key)
                command.push(it.value)
            } else {
                command.push(it)
            }

            int exitValue = cmd(command, verbose)
            exitValues.add(exitValue)
        }

        return exitValues
    }

    static def installToDevice(GenymotionVirtualDevice device, def apks, boolean verbose = false) {
        installToDevice(device.name, apks, verbose)
    }

    static def installToDevice(def deviceName, def apks, boolean verbose = false) {

        if (!apks) {
            return false
        }

        if (apks instanceof String) {
            cmd([GENYTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, apks], verbose)

        } else if (apks instanceof ArrayList) {

            def exitValues = []
            apks.each() {
                int exitValue = cmd([GENYTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, it], verbose)
                exitValues.add(exitValue)
            }
            return exitValues
        }

        return false
    }

    static def flashDevice(GenymotionVirtualDevice device, def zips, boolean verbose = false) {
        return flashDevice(device.name, zips, verbose)
    }

    static def flashDevice(def deviceName, def zips, boolean verbose = false) {

        if (!zips) {
            return false
        }

        if (zips instanceof String) {
            return cmd([GENYTOOL, DEVICE, OPT_NAME + deviceName, FLASH, zips], verbose)

        } else if (zips instanceof ArrayList) {
            def exitValues = []
            zips.each() {
                int exitValue = cmd([GENYTOOL, DEVICE, OPT_NAME + deviceName, FLASH, it], verbose)
                exitValues.add(exitValue)
            }
            return exitValues
        }
        return false
    }

    static def adbDisconnectDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return adbDisconnectDevice(device.name, verbose)
    }

    static def adbDisconnectDevice(def deviceName, boolean verbose = false) {
        return cmd([GENYTOOL, DEVICE, OPT_NAME + deviceName, ADBDISCONNECT], verbose)
    }

    static def adbConnectDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return adbConnectDevice(device.name, verbose)
    }

    static def adbConnectDevice(def deviceName, boolean verbose = false) {
        return cmd([GENYTOOL, DEVICE, OPT_NAME + deviceName, ADBCONNECT], verbose)
    }

    static def logcatClear(GenymotionVirtualDevice device, boolean verbose = false) {
        return logcatClear(device.name, verbose)
    }

    static def logcatClear(def deviceName, boolean verbose = false) {
        return cmd([GENYTOOL, DEVICE, OPT_NAME + deviceName, LOGCAT_CLEAR], verbose)
    }

    static def logcatDump(GenymotionVirtualDevice device, path, boolean verbose = false) {
        return logcatDump(device.name, path, verbose)
    }

    static def logcatDump(def deviceName, def path, boolean verbose = false) {
        return cmd([GENYTOOL, DEVICE, OPT_NAME + deviceName, LOGCAT_DUMP, path], verbose)
    }

    /*
    TOOLS
     */

    /**
     * Fire a command line and process the result.
     * This function runs a closure for each line returned by the prompt.
     * The closure contains the parameters:
     * - <b>line</b> (containing the line's text)
     * - <b>count</b> (index of the line)
     *
     * @param command the command line to execute. It can be a String or a table
     * @param verbose true if you want to print each line returned by the prompt
     * @param addPath true if you want to add the Genymotion path at the begining of the command
     */
    static def cmd(def command, boolean verbose = false, boolean addPath = true) {
        cmd(command, verbose, addPath, null)
    }

    /**
     * Fire a command line and process the result.
     * This function runs a closure for each line returned by the prompt.
     * The closure contains the parameters:
     * - <b>line</b> (containing the line's text)
     * - <b>count</b> (index of the line)
     *
     * @param command the command line to execute. It can be a String or a table
     * @param verbose true if you want to print each line returned by the prompt
     * @param addPath true if you want to add the Genymotion path at the begining of the command
     * @param c the closure to implement after the call
     */
    static def cmd(def command, boolean verbose = false, boolean addPath = true, Closure c) {

        if (GENYMOTION_CONFIG == null) {
            return
        }

        def toExec = formatAndLogCommand(command, verbose, addPath)

        try {
            Process p = toExec.execute()
            StringBuffer error = new StringBuffer()
            StringBuffer out = new StringBuffer()
            p.consumeProcessOutput(out, error)

            p.waitForOrKill(GENYMOTION_CONFIG.processTimeout)

            if (verbose || GENYMOTION_CONFIG.verbose) {
                Log.debug("out:" + out.toString())
            }

            if (c != null) {
                out.eachLine { line, count ->
                    c(line, count)
                }
            }

            return handleExitValue(p.exitValue(), error)

        } catch (IOException e) {
            if (GENYMOTION_CONFIG.abortOnError) {
                throw new FileNotFoundException(GENYMOTION_PATH_ERROR_MESSAGE +
                        " Current value: " + GENYMOTION_CONFIG.genymotionPath)
            } else {
                Log.warn(GENYMOTION_PATH_ERROR_MESSAGE +
                        " Current value: " + GENYMOTION_CONFIG.genymotionPath +
                        " Genymotion Gradle plugin won't work.")
            }
        }
    }

    /**
     * Format and log a command line before being executed:
     * - Add the gmtool path if needed
     * - And handle the verbose and log it if needed
     * - Add the source tag to a gmtool command
     *
     * @param command the command to execute
     * @param verbose the explicite verbosity
     * @return returns the command to execute
     */
    static def formatAndLogCommand(command, boolean verbose=false, boolean addPath = true) {
        def toExec = command

        //we eventually insert the genymotion binary path
        if (GENYMOTION_CONFIG.genymotionPath != null && addPath) {
            if (toExec instanceof String) {
                toExec = GENYMOTION_CONFIG.genymotionPath + toExec
            } else {
                toExec = command.clone()
                toExec[0] = GENYMOTION_CONFIG.genymotionPath + toExec[0]
            }
        }

        if (toExec[0]?.contains(GENYTOOL)) {

            if (verbose || GENYMOTION_CONFIG.verbose) {
                toExec.addAll(1, [VERBOSE])
                Log.debug(cleanCommand(toExec))
            }

            toExec.addAll(1, [SOURCE_GRADLE])
        }

        return toExec
    }

    /**
     * Remove password from the command line. Useful before displaying it.
     *
     * @param list the command line
     *
     * @return the same list, without the content of password options
     */
    static def cleanCommand(def list) {
        def output = []
        for (String entry in list) {
            if (entry.startsWith(OPT_PASSWORD_CONFIG) && entry.split("=").size() > 1) {
                output << OPT_PASSWORD_CONFIG + "*****"
            } else if (entry.startsWith(SOURCE)) {
                //no op avoid  printing the source tag
            } else {
                output << entry
            }
        }
        return output
    }

    /**
     * Handle the exit code after a command line execution.
     * This function analyse the return and throws an exception if needed.
     *
     * @param exitValue the command line exit value
     * @param error the error output from command line
     *
     * @return returns the exitCode if nothing is thrown
     */
    static def handleExitValue(int exitValue, StringBuffer error) {
        if (exitValue == RETURN_NO_ERROR) {
            //do nothing

        } else if (exitValue == RETURN_COMMAND_NOT_FOUND_UNIX) {
            if (GENYMOTION_CONFIG.abortOnError) {
                throw new FileNotFoundException(GENYMOTION_PATH_ERROR_MESSAGE +
                        " Current value: " + GENYMOTION_CONFIG.genymotionPath)
            } else {
                Log.warn(GENYMOTION_PATH_ERROR_MESSAGE +
                        " Current value: \"" + GENYMOTION_CONFIG.genymotionPath + "\"" +
                        " Genymotion Gradle plugin cannot work.")
            }
        } else if (exitValue == RETURN_SIGTERM) {
            String message = "Your command exceeds the current $GENYMOTION_CONFIG.processTimeout ms timeout. To solve " +
                    "this problem, try to increase this parameter by setting the genymotion.config.processTimeout " +
                    "value in your build.gradle"
            if (GENYMOTION_CONFIG.abortOnError) {
                throw new TimeoutException(message)
            } else {
                Log.warn("Timeout occured. $message")
            }
        } else {
            if (GENYMOTION_CONFIG.abortOnError) {
                throw new GMToolException("GMTool command failed. Error code: $exitValue." + error.toString())
            } else {
                Log.warn("Genymotion warn: " + error.toString())
            }
        }
        return exitValue
    }

    /**
     * Avoid null.toString returning "null"
     *
     * @param c the code to execute
     * @return the c's return
     */
    static def noNull(Closure c) {
        //set null.toString to return ""
        String nullLabel = null.toString()
        NullObject.metaClass.toString = { return '' }

        def exit = c()

        //set as default
        NullObject.metaClass.toString = { return nullLabel }

        return exit
    }
}
