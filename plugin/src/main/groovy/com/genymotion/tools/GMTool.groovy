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

import static com.genymotion.tools.GMToolDsl.*

class GMTool {

    static GenymotionConfig DEFAULT_CONFIG = null
    GenymotionConfig genymotionConfig = null

    static final String GENYMOTION_PATH_ERROR_MESSAGE =
            "gmtool command not found. You have to specify the Genymotion path with the genymotion.config.genymotionPath parameter."
    static final String GENYMOTION_VERSION_ERROR_MESSAGE =
            "Current gmtool version is not compatible with %s. Please update Genymotion following this link: $GENYMOTION_DOWNLOAD_URL"
    static final String GENYMOTION_DOWNLOAD_URL = "https://www.genymotion.com/#!/download"


    static GMTool newInstance(GenymotionConfig config = DEFAULT_CONFIG) {
        GMTool gmtool = new GMTool()

        if (config == null) {
            config = new GenymotionConfig()
        }
        gmtool.genymotionConfig = config

        return gmtool
    }

    def usage() {
        return cmd([GMTOOL, HELP])
    }

    String getVersion() {

        String version = null

        cmd([GMTOOL, VERSION]) { line, count ->
            String[] info = line.split(":")
            if (info.length > 1 && info[1].trim()) {

                switch (info[0].trim()) {
                    case "Version":
                        version = info[1].trim()
                }
            }
        }

        return version
    }

    /*
    CONFIG
     */

    def resetConfig() {
        return cmd([GMTOOL, CONFIG, RESET])
    }

    def clearCache() {
        return cmd([GMTOOL, CONFIG, CLEARCACHE])
    }

    def logzip(String path = "", String vdName = "") {

        def command = [GMTOOL, LOGZIP]

        if (vdName?.trim()) {
            command.push(OPT_NAME + vdName)
        }

        if (path?.trim()) {
            command.push(path)
        }

        return cmd([GMTOOL, LOGZIP])
    }

    def getConfig(boolean verbose = false) {
        getConfig(null, verbose)
    }

    def getConfig(GenymotionConfig config, boolean verbose = false) {

        if (config == null) {
            config = new GenymotionConfig()
        }

        def exitCode = cmd([GMTOOL, CONFIG, PRINT], verbose) { line, count ->

            String[] info = line.split("=")

            if (info.length > 1 && info[1].trim()) {
                String value = info[1].trim()

                switch (info[0].trim()) {
                    case "statistics":
                        config.statistics = isOn(value)
                        break
                    case "username":
                        config.username = value
                        break
                    case "license_server":
                        config.licenseServer = isOn(value)
                        break
                    case "license_server_address":
                        config.licenseServerAddress = value
                        break
                    case "proxy":
                        config.proxy = isOn(value)
                        break
                    case "proxy_address":
                        config.proxyAddress = value
                        break
                    case "proxy_port":
                        config.proxyPort = info[1].toInteger()
                        break
                    case "proxy_auth":
                        config.proxyAuth = isOn(value)
                        break
                    case "proxy_username":
                        config.proxyUsername = value
                        break
                    case "virtual_device_path":
                        config.virtualDevicePath = value
                        break
                    case "sdk_path":
                        config.androidSdkPath = value
                        break
                    case "use_custom_sdk":
                        config.useCustomSdk = isOn(value)
                        break
                    case "screen_capture_path":
                        config.screenCapturePath = value
                        break
                }
            }
        }

        if (exitCode != RETURN_NO_ERROR) {
            return exitCode
        }

        config.version = getVersion()

        if (exitCode == RETURN_NO_ERROR) {
            return config
        }

        return exitCode
    }

    def throwIfNotCompatible(String feature, String featureLabel, Closure c) {
        if (isCompatibleWith(feature)) {
            c()
        } else if (genymotionConfig.abortOnError) {
            throw new GMToolException(String.format(GENYMOTION_VERSION_ERROR_MESSAGE, featureLabel))
        } else {
            Log.warn("Genymotion warn: " + String.format(GENYMOTION_VERSION_ERROR_MESSAGE, featureLabel))
        }
    }

    def setConfig(GenymotionConfig config, boolean verbose = false) {
        if (!config) {
            return false
        }
        return setConfig(config.statistics, config.username, config.password, config.licenseServer,
                config.licenseServerAddress, config.proxy, config.proxyAddress, config.proxyPort, config.proxyAuth,
                config.proxyUsername, config.proxyPassword, config.virtualDevicePath, config.androidSdkPath,
                config.useCustomSdk, config.screenCapturePath, verbose)
    }


    def setConfig(def statistics = null, String username = null, String password = null, def licenseServer = null,
                  String licenseServerAddress = null, def proxy = null, String proxyAddress = null,
                  def proxyPort = null, def proxyAuth = null, String proxyUsername = null, String proxyPassword = null,
                  String virtualDevicePath = null, String androidSdkPath = null, def useCustomSdk = null,
                  String screenCapturePath = null, boolean verbose = false) {

        def command = [GMTOOL, CONFIG]

        if (username != null && password != null) {
            command.push(OPT_USERNAME_CONFIG + username)
            command.push(OPT_PASSWORD_CONFIG + password)
        } else if ((username != null || password != null) && verbose) {
            Log.error("username and password need to be both transmitted. Ignoring both arguments")
        }

        if (statistics != null) {
            command.push(OPT_STATISTICS + statistics)
        }
        if (licenseServer != null) {
            throwIfNotCompatible(FEATURE_ONSITE_LICENSE_CONFIG, "config $OPT_LICENSE_SERVER") {
                command.push(OPT_LICENSE_SERVER + licenseServer)
            }
        }
        if (licenseServerAddress != null) {
            throwIfNotCompatible(FEATURE_ONSITE_LICENSE_CONFIG, "config $OPT_LICENSE_SERVER_ADDRESS") {
                command.push(OPT_LICENSE_SERVER_ADDRESS + licenseServerAddress)
            }
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

    def setLicense(String license, boolean verbose = false) {
        def command = [GMTOOL, LICENSE, REGISTER, license]

        return cmd(command, verbose)
    }

    /*
    ADMIN
     */

    def getAllDevices(boolean verbose = false, boolean fill = true, boolean nameOnly = false) {

        def devices = []

        cmd([GMTOOL, ADMIN, LIST], verbose) { line, count ->
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

    def getRunningDevices(boolean verbose = false, boolean fill = true, boolean nameOnly = false) {

        def devices = []

        cmd([GMTOOL, ADMIN, LIST, OPT_RUNNING], verbose) { line, count ->
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

    def getStoppedDevices(boolean verbose = false, boolean fill = true, boolean nameOnly = false) {

        def devices = []

        cmd([GMTOOL, ADMIN, LIST, OPT_OFF], verbose) { line, count ->
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

    boolean isDeviceRunning(def device, boolean verbose = false) {
        isDeviceRunning(device.name, verbose)
    }

    boolean isDeviceRunning(String name, boolean verbose = false) {
        def devices = getRunningDevices(verbose, false, true)
        return devices?.contains(name)
    }

    private def parseListLine(int count, String line, boolean nameOnly) {

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

    boolean isDeviceCreated(String name, boolean verbose = false) {

        if (!name?.trim()) {
            return false
        }

        boolean alreadyExists = false

        def devices = getAllDevices(verbose, false)

        devices.each() {
            if (it.name.equals(name)) {
                alreadyExists = true
            }
        }
        return alreadyExists
    }

    def getTemplatesNames(boolean verbose = false) {

        def templates = []

        def template = null

        def exitCode = noNull {
            return cmd([GMTOOL, ADMIN, TEMPLATES], verbose) { line, count ->

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

    def getTemplates(boolean verbose = false) {

        def templates = []

        def template = new GenymotionTemplate()

        int exitCode = noNull {
            return cmd([GMTOOL, ADMIN, TEMPLATES, OPT_FULL], verbose) { line, count ->

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

    boolean templateExists(String template, boolean verbose = false) {

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

    def createDevice(GenymotionVDLaunch device) {
        return createDevice(device.template, device.name)
    }

    def createDevice(GenymotionTemplate template) {
        return createDevice(template.name, template.name)
    }

    def createDevice(def template, def deviceName, def density = "", def width = "", def height = "",
                     def virtualKeyboard = "", def navbarVisible = "", def nbcpu = "", def ram = "") {

        def exitValue = noNull() {
            cmd([GMTOOL, ADMIN, CREATE, template, deviceName, OPT_DENSITY + density, OPT_WIDTH + width,
                 OPT_HEIGHT + height, OPT_VIRTUAL_KEYBOARD + virtualKeyboard, OPT_NAVBAR + navbarVisible,
                 OPT_NBCPU + nbcpu, OPT_RAM + ram])
        }

        if (exitValue == RETURN_NO_ERROR) {
            return new GenymotionVirtualDevice(deviceName, density, width, height, virtualKeyboard, navbarVisible, nbcpu, ram)
        } else {
            return exitValue
        }
    }

    def editDevice(GenymotionVirtualDevice device) {
        return editDevice(device.name, device.density, device.width, device.height, device.virtualKeyboard, device.navbarVisible, device.nbCpu, device.ram)
    }

    def editDevice(def deviceName, def density = "", def width = "", def height = "", def virtualKeyboard = "",
                   def navbarVisible = "", def nbcpu = "", def ram = "") {

        return noNull() {
            return cmd([GMTOOL, ADMIN, EDIT, deviceName, OPT_DENSITY + density, OPT_WIDTH + width,
                        OPT_HEIGHT + height, OPT_VIRTUAL_KEYBOARD + virtualKeyboard, OPT_NAVBAR + navbarVisible,
                        OPT_NBCPU + nbcpu, OPT_RAM + ram])
        }
    }

    def deleteDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return deleteDevice(device.name, verbose)
    }

    def deleteDevice(def deviceName, boolean verbose = false) {
        return cmd([GMTOOL, ADMIN, DELETE, deviceName], verbose)
    }

    def cloneDevice(GenymotionVirtualDevice device, def name, boolean verbose = false) {
        return cloneDevice(device.name, name, verbose)
    }

    def cloneDevice(def deviceName, def newName, boolean verbose = false) {
        return cmd([GMTOOL, ADMIN, CLONE, deviceName, newName], verbose)
    }

    def getDevice(String name, boolean verbose = false) {

        if (name == null) {
            return null
        }

        def device = new GenymotionVirtualDevice(name)
        return getDevice(device, verbose)
    }

    def getDevice(def device, boolean verbose = false) {

        if (device == null) {
            return null
        }

        cmd([GMTOOL, ADMIN, DETAILS, device.name], verbose) { line, count ->

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

    def startDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return startDevice(device.name, verbose)
    }

    def startDevice(def deviceName, boolean verbose = false) {
        return cmd([GMTOOL, ADMIN, START, deviceName], verbose)
    }

    def restartDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return restartDevice(device.name, verbose)
    }

    def restartDevice(def deviceName, boolean verbose = false) {
        return cmd([GMTOOL, ADMIN, RESTART, deviceName], verbose)
    }

    def stopDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return stopDevice(device.name, verbose)
    }

    def stopDevice(def deviceName, boolean verbose = false) {
        return cmd([GMTOOL, ADMIN, STOP, deviceName], verbose)
    }

    def stopAllDevices(boolean verbose = false) {
        return cmd([GMTOOL, ADMIN, STOPALL], verbose)
    }

    def resetDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return resetDevice(device.name, verbose)
    }

    def resetDevice(def deviceName, boolean verbose = false) {
        return cmd([GMTOOL, ADMIN, FACTORY_RESET, deviceName], verbose)
    }

    def startAutoDevice(def template, def deviceName, boolean verbose = false) {
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

    def pushToDevice(GenymotionVirtualDevice device, def files, boolean verbose = false) {
        pushToDevice(device.name, files, verbose)
    }

    def pushToDevice(def deviceName, def files, boolean verbose = false) {

        if (!files) {
            return false
        }

        def exitValues = []

        if (files instanceof String) {
            files = [files]
        }

        files.each() {

            def command = [GMTOOL, DEVICE, OPT_NAME + deviceName, PUSH]
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

    def pullFromDevice(String deviceName, String source, String destination, boolean verbose = false) {
        pullFromDevice(deviceName, [(source): destination], verbose)
    }

    def pullFromDevice(GenymotionVirtualDevice device, Map<String, String> files, boolean verbose = false) {
        pullFromDevice(device.name, files, verbose)
    }

    def pullFromDevice(def deviceName, Map<String, String> files, boolean verbose = false) {

        if (!files) {
            return false
        }

        def exitValues = []

        files.each() {

            def command = [GMTOOL, DEVICE, OPT_NAME + deviceName, PULL, it.key, it.value]

            int exitValue = cmd(command, verbose)
            exitValues.add(exitValue)
        }

        return exitValues
    }

    def installToDevice(GenymotionVirtualDevice device, def apks, boolean verbose = false) {
        installToDevice(device.name, apks, verbose)
    }

    def installToDevice(def deviceName, def apks, boolean verbose = false) {

        if (!apks) {
            return false
        }

        if (apks instanceof String) {
            cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, apks], verbose)

        } else if (apks instanceof ArrayList) {

            def exitValues = []
            apks.each() {
                int exitValue = cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, it], verbose)
                exitValues.add(exitValue)
            }
            return exitValues
        }

        return false
    }

    def flashDevice(GenymotionVirtualDevice device, def zips, boolean verbose = false) {
        return flashDevice(device.name, zips, verbose)
    }

    def flashDevice(def deviceName, def zips, boolean verbose = false) {

        if (!zips) {
            return false
        }

        if (zips instanceof String) {
            return cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, FLASH, zips], verbose)

        } else if (zips instanceof ArrayList) {
            def exitValues = []
            zips.each() {
                int exitValue = cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, FLASH, it], verbose)
                exitValues.add(exitValue)
            }
            return exitValues
        }
        return false
    }

    def adbDisconnectDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return adbDisconnectDevice(device.name, verbose)
    }

    def adbDisconnectDevice(def deviceName, boolean verbose = false) {
        return cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, ADBDISCONNECT], verbose)
    }

    def adbConnectDevice(GenymotionVirtualDevice device, boolean verbose = false) {
        return adbConnectDevice(device.name, verbose)
    }

    def adbConnectDevice(def deviceName, boolean verbose = false) {
        return cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, ADBCONNECT], verbose)
    }

    def logcatClear(GenymotionVirtualDevice device, boolean verbose = false) {
        return logcatClear(device.name, verbose)
    }

    def logcatClear(def deviceName, boolean verbose = false) {
        return cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, LOGCAT_CLEAR], verbose)
    }

    def logcatDump(GenymotionVirtualDevice device, path, boolean verbose = false) {
        return logcatDump(device.name, path, verbose)
    }

    def logcatDump(def deviceName, def path, boolean verbose = false) {
        return cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, LOGCAT_DUMP, path], verbose)
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
    def cmd(def command, boolean verbose = false, boolean addPath = true) {
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
    def cmd(def command, boolean verbose = false, boolean addPath = true, Closure c) {

        if (genymotionConfig == null) {
            return
        }

        def toExec = formatAndLogCommand(command, verbose, addPath)

        try {
            Process p = toExec.execute()
            StringBuffer error = new StringBuffer()
            StringBuffer out = new StringBuffer()
            p.consumeProcessOutput(out, error)

            p.waitForOrKill(genymotionConfig.processTimeout)

            if (verbose || genymotionConfig.verbose) {
                Log.debug("out:" + out.toString())
            }

            if (c != null) {
                out.eachLine { line, count ->
                    c(line, count)
                }
            }

            return handleExitValue(p.exitValue(), error)

        } catch (IOException e) {
            if (genymotionConfig.abortOnError) {
                throw new FileNotFoundException(GENYMOTION_PATH_ERROR_MESSAGE +
                        " Current value: " + genymotionConfig.genymotionPath)
            } else {
                Log.warn(GENYMOTION_PATH_ERROR_MESSAGE +
                        " Current value: " + genymotionConfig.genymotionPath +
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
    def formatAndLogCommand(command, boolean verbose = false, boolean addPath = true) {
        def toExec = command

        //we eventually insert the genymotion binary path
        if (genymotionConfig.genymotionPath != null && addPath) {
            if (toExec instanceof String) {
                toExec = genymotionConfig.genymotionPath + toExec
            } else {
                toExec = command.clone()
                toExec[0] = genymotionConfig.genymotionPath + toExec[0]
            }
        }

        if (toExec[0]?.contains(GMTOOL)) {

            if (isCompatibleWith(FEATURE_SOURCE_PARAM)) {
                toExec.addAll(1, [SOURCE_GRADLE])
            }

            if (verbose || genymotionConfig.verbose) {
                toExec.addAll(1, [VERBOSE])
                Log.debug(cleanCommand(toExec))
            }
        }

        return toExec
    }

    /**
     * Get the compatibility between the current gmtool binary and a gradle plugin feature
     * @param feature
     *
     * @return true if the plugin is compatible with it and false otherwise
     */
    boolean isCompatibleWith(String feature) {
        return genymotionConfig.version >= feature
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
    def handleExitValue(int exitValue, StringBuffer error) {
        if (exitValue == RETURN_NO_ERROR) {
            //do nothing

        } else if (exitValue == RETURN_COMMAND_NOT_FOUND_UNIX) {
            if (genymotionConfig.abortOnError) {
                throw new FileNotFoundException(GENYMOTION_PATH_ERROR_MESSAGE +
                        " Current value: " + genymotionConfig.genymotionPath)
            } else {
                Log.warn(GENYMOTION_PATH_ERROR_MESSAGE +
                        " Current value: \"" + genymotionConfig.genymotionPath + "\"" +
                        " Genymotion Gradle plugin cannot work.")
            }
        } else if (exitValue == RETURN_SIGTERM) {
            String message = "Your command exceeds the current $genymotionConfig.processTimeout ms timeout. To solve " +
                    "this problem, try to increase this parameter by setting the genymotion.config.processTimeout " +
                    "value in your build.gradle"
            if (genymotionConfig.abortOnError) {
                throw new TimeoutException(message)
            } else {
                Log.warn("Timeout occured. $message")
            }
        } else {
            if (genymotionConfig.abortOnError) {
                throw new GMToolException("GMTool command failed. Error code: $exitValue." + error.toString())
            } else {
                Log.warn("Genymotion warn: " + error.toString())
            }
        }
        return exitValue
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
