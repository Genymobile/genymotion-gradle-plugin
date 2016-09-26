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

import com.genymotion.model.DeviceLocation
import com.genymotion.model.GenymotionConfig
import com.genymotion.model.GenymotionTemplate
import com.genymotion.model.GenymotionVirtualDevice
import com.genymotion.model.NetworkInfo
import org.codehaus.groovy.runtime.NullObject

import java.util.concurrent.TimeoutException

import static com.genymotion.tools.GMToolDsl.*

class GMTool {
    static GenymotionConfig DEFAULT_CONFIG = null
    GenymotionConfig genymotionConfig = null

    DeviceLocation deviceLocation = DeviceLocation.LOCAL

    static final String GENYMOTION_PATH_ERROR_MESSAGE =
            "gmtool command not found. You have to specify the Genymotion path with the " +
                    "genymotion.config.genymotionPath parameter."
    static final String GENYMOTION_VERSION_ERROR_MESSAGE =
            "Current gmtool version is not compatible with %s. Please update Genymotion " +
                    "following this link: $GENYMOTION_DOWNLOAD_URL"
    static final String GENYMOTION_DOWNLOAD_URL = "https://www.genymotion.com/#!/download"

    private static final Set<String> CLOUD_ACTION_GROUPS = [ADMIN, DEVICE]

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

    def getConfig() {
        getConfig(null)
    }

    def getConfig(GenymotionConfig config) {
        if (config == null) {
            config = new GenymotionConfig()
        }

        def exitCode = cmd([GMTOOL, CONFIG, PRINT]) { line, count ->

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

    def setConfig(GenymotionConfig config) {
        if (!config) {
            return false
        }
        return setConfig(config.statistics, config.username, config.password, config.licenseServer,
                config.licenseServerAddress, config.proxy, config.proxyAddress, config.proxyPort, config.proxyAuth,
                config.proxyUsername, config.proxyPassword, config.virtualDevicePath, config.androidSdkPath,
                config.useCustomSdk, config.screenCapturePath, config.verbose)
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

        return cmd(command) { line, count ->
        }
    }

    /*
    LICENSE
     */

    def setLicense(String license) {
        def command = [GMTOOL, LICENSE, REGISTER, license]

        return cmd(command)
    }

    /*
    ADMIN
     */

    def getAllDevices(boolean fill = true, boolean nameOnly = false) {
        def devices = []

        cmd([GMTOOL, ADMIN, LIST]) { line, count ->
            def device = parseListLine(count, line, nameOnly)
            if (device) {
                devices.add(device)
            }
        }

        if (fill && !nameOnly) {
            devices.each() {
                it.update()
            }
        }

        devices
    }

    def getRunningDevices(boolean fill = true, boolean nameOnly = false) {
        def devices = []

        cmd([GMTOOL, ADMIN, LIST, OPT_RUNNING]) { line, count ->
            def device = parseListLine(count, line, nameOnly)
            if (device) {
                devices.add(device)
            }
        }

        if (fill && !nameOnly) {
            devices.each() {
                it.update()
            }
        }

        devices
    }

    def getStoppedDevices(boolean fill = true, boolean nameOnly = false) {
        def devices = []

        cmd([GMTOOL, ADMIN, LIST, OPT_OFF]) { line, count ->
            def device = parseListLine(count, line, nameOnly)
            if (device) {
                devices.add(device)
            }
        }

        if (fill && !nameOnly) {
            devices.each() {
                it.update()
            }
        }

        devices
    }

    boolean isDeviceRunning(def device) {
        isDeviceRunning(device.name)
    }

    boolean isDeviceRunning(String name) {
        def devices = getRunningDevices(false, true)
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

    boolean isDeviceCreated(String name) {
        if (!name?.trim()) {
            return false
        }

        boolean alreadyExists = false

        def devices = getAllDevices(false)

        devices.each() {
            if (it.name.equals(name)) {
                alreadyExists = true
            }
        }
        return alreadyExists
    }

    def getTemplatesNames() {
        def templates = []

        def template = null

        def exitCode = noNull {
            return cmd([GMTOOL, ADMIN, TEMPLATES]) { line, count ->

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

    def getTemplates() {
        def templates = []

        def template = new GenymotionTemplate()

        int exitCode = noNull {
            return cmd([GMTOOL, ADMIN, TEMPLATES, OPT_FULL]) { line, count ->

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

    boolean templateExists(String template) {
        if (!template?.trim()) {
            return false
        }

        def templates = getTemplatesNames()
        if (templates instanceof ArrayList) {
            templates?.contains(template)
        } else {
            return false
        }
    }

    def createDevice(GenymotionTemplate template) {
        return createDevice(template.name, template.name)
    }

    def createDevice(def template, def deviceName, def density = "", def width = "", def height = "",
                     def virtualKeyboard = "", def navbarVisible = "", def nbcpu = "", def ram = "",
                     def networkMode = "", def bridgeInterface = "") {
        def exitValue = noNull() {
            def args = [GMTOOL, ADMIN, CREATE, template, deviceName]
            // FIXME Instead of skipping all options for cloud devices, just do not add options which are set to default values
            if (deviceLocation == DeviceLocation.LOCAL) {
                args.addAll(
                        OPT_DENSITY + density, OPT_WIDTH + width,
                        OPT_HEIGHT + height, OPT_VIRTUAL_KEYBOARD + virtualKeyboard, OPT_NAVBAR + navbarVisible,
                        OPT_NBCPU + nbcpu, OPT_RAM + ram, OPT_NETWORK_MODE + networkMode,
                        OPT_BRIDGE_INTERFACE + bridgeInterface)
            }
            cmd(args)
        }

        if (exitValue == RETURN_NO_ERROR) {
            NetworkInfo networkInfo = new NetworkInfo(networkMode, bridgeInterface);

            return new GenymotionVirtualDevice(deviceName, density, width, height, virtualKeyboard, navbarVisible,
                    nbcpu, ram, networkInfo)
        } else {
            return exitValue
        }
    }

    def editDevice(GenymotionVirtualDevice device) {
        return editDevice(device.name, device.density, device.width, device.height, device.virtualKeyboard,
                device.navbarVisible, device.nbCpu, device.ram, device.networkInfo.mode,
                device.networkInfo.bridgeInterface)
    }

    def editDevice(def deviceName, def density = "", def width = "", def height = "", def virtualKeyboard = "",
                   def navbarVisible = "", def nbcpu = "", def ram = "", def networkMode = "",
                   def bridgeInterface = "") {

        return noNull() {
            return cmd([GMTOOL, ADMIN, EDIT, deviceName, OPT_DENSITY + density, OPT_WIDTH + width,
                        OPT_HEIGHT + height, OPT_VIRTUAL_KEYBOARD + virtualKeyboard, OPT_NAVBAR + navbarVisible,
                        OPT_NBCPU + nbcpu, OPT_RAM + ram, OPT_NETWORK_MODE + networkMode,
                        OPT_BRIDGE_INTERFACE + bridgeInterface])
        }
    }

    def deleteDevice(GenymotionVirtualDevice device) {
        return deleteDevice(device.name)
    }

    def deleteDevice(def deviceName) {
        return cmd([GMTOOL, ADMIN, DELETE, deviceName])
    }

    def cloneDevice(GenymotionVirtualDevice device, def name) {
        return cloneDevice(device.name, name)
    }

    def cloneDevice(def deviceName, def newName) {
        return cmd([GMTOOL, ADMIN, CLONE, deviceName, newName])
    }

    def getDevice(String name) {
        if (name == null) {
            return null
        }

        def device = new GenymotionVirtualDevice(name)
        return updateDevice(device)
    }

    def updateDevice(def device) {
        if (device == null) {
            return null
        }

        cmd([GMTOOL, ADMIN, DETAILS, device.name]) { line, count ->
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
                case "Network mode":
                    device.networkInfo = NetworkInfo.fromGMtoolDeviceDetails(info[1].trim())
            }
        }
        device
    }

    def startDevice(GenymotionVirtualDevice device) {
        return startDevice(device.name)
    }

    def startDevice(def deviceName) {
        return cmd([GMTOOL, ADMIN, START, deviceName])
    }

    def restartDevice(GenymotionVirtualDevice device) {
        return restartDevice(device.name)
    }

    def restartDevice(def deviceName) {
        return cmd([GMTOOL, ADMIN, RESTART, deviceName])
    }

    def stopDevice(GenymotionVirtualDevice device) {
        return stopDevice(device.name)
    }

    def stopDevice(def deviceName) {
        return cmd([GMTOOL, ADMIN, STOP, deviceName])
    }

    def stopAllDevices() {
        return cmd([GMTOOL, ADMIN, STOPALL])
    }

    def resetDevice(GenymotionVirtualDevice device) {
        return resetDevice(device.name)
    }

    def resetDevice(def deviceName) {
        return cmd([GMTOOL, ADMIN, FACTORY_RESET, deviceName])
    }

    def startAutoDevice(def template, def deviceName) {
        def device = createDevice(template, deviceName)

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

    def pushToDevice(GenymotionVirtualDevice device, def files) {
        pushToDevice(device.name, files)
    }

    def pushToDevice(def deviceName, def files) {
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

            int exitValue = cmd(command)
            exitValues.add(exitValue)
        }

        return exitValues
    }

    def pullFromDevice(String deviceName, String source, String destination) {
        pullFromDevice(deviceName, [(source): destination])
    }

    def pullFromDevice(GenymotionVirtualDevice device, Map<String, String> files) {
        pullFromDevice(device.name, files)
    }

    def pullFromDevice(def deviceName, Map<String, String> files) {
        if (!files) {
            return false
        }

        def exitValues = []

        files.each() {

            def command = [GMTOOL, DEVICE, OPT_NAME + deviceName, PULL, it.key, it.value]

            int exitValue = cmd(command)
            exitValues.add(exitValue)
        }

        return exitValues
    }

    def installToDevice(GenymotionVirtualDevice device, def apks) {
        installToDevice(device.name, apks)
    }

    def installToDevice(def deviceName, def apks) {
        if (!apks) {
            return false
        }

        if (apks instanceof String) {
            return cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, apks])

        } else if (apks instanceof ArrayList) {

            def exitValues = []
            apks.each() {
                int exitValue = cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, it])
                exitValues.add(exitValue)
            }
            return exitValues
        }

        return false
    }

    def flashDevice(GenymotionVirtualDevice device, def zips) {
        return flashDevice(device.name, zips)
    }

    def flashDevice(def deviceName, def zips) {
        if (!zips) {
            return false
        }

        if (zips instanceof String) {
            return cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, FLASH, zips])

        } else if (zips instanceof ArrayList) {
            def exitValues = []
            zips.each() {
                int exitValue = cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, FLASH, it])
                exitValues.add(exitValue)
            }
            return exitValues
        }
        return false
    }

    def adbDisconnectDevice(GenymotionVirtualDevice device) {
        return adbDisconnectDevice(device.name)
    }

    def adbDisconnectDevice(def deviceName) {
        return cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, ADBDISCONNECT])
    }

    def adbConnectDevice(GenymotionVirtualDevice device) {
        return adbConnectDevice(device.name)
    }

    def adbConnectDevice(def deviceName) {
        return cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, ADBCONNECT])
    }

    def logcatClear(GenymotionVirtualDevice device) {
        return logcatClear(device.name)
    }

    def logcatClear(def deviceName) {
        return cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, LOGCAT_CLEAR])
    }

    def logcatDump(GenymotionVirtualDevice device, path) {
        return logcatDump(device.name, path)
    }

    def logcatDump(def deviceName, def path) {
        return cmd([GMTOOL, DEVICE, OPT_NAME + deviceName, LOGCAT_DUMP, path])
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
     */
    def cmd(def command) {
        cmd(command, null)
    }

    /**
     * Fire a command line and process the result.
     * This function runs a closure for each line returned by the prompt.
     * The closure contains the parameters:
     * - <b>line</b> (containing the line's text)
     * - <b>count</b> (index of the line)
     *
     * @param command the command line to execute. It can be a String or a table
     * @param c the closure to implement after the call
     */
    def cmd(def command, Closure c) {
        if (genymotionConfig == null) {
            return
        }

        def toExec = formatAndLogCommand(command)

        try {
            def (StringBuffer out, StringBuffer error, int exitValue) = executeCommand(toExec)

            if (genymotionConfig.verbose) {
                Log.debug("out:" + out.toString())
            }

            if (c != null) {
                out.eachLine { line, count ->
                    c(line, count)
                }
            }

            return handleExitValue(exitValue, error)

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

    def executeCommand(toExec) {
        Process p = toExec.execute()
        StringBuffer error = new StringBuffer()
        StringBuffer out = new StringBuffer()
        p.consumeProcessOutput(out, error)

        p.waitForOrKill(genymotionConfig.processTimeout)
        return [out, error, p.exitValue()]
    }

    /**
     * Format and log a command line before being executed:
     * - Add the gmtool path if needed
     * - And handle the verbose and log it if needed
     * - Add the source tag to a gmtool command
     *
     * @param command the command to execute
     * @return returns the command to execute
     */
    def formatAndLogCommand(command) {
        def toExec = command

        // insert the genymotion binary path
        if (genymotionConfig.genymotionPath != null) {
            if (toExec instanceof String) {
                toExec = prependGenymotionPath(toExec)
            } else {
                toExec = command.clone()
                toExec[0] = prependGenymotionPath(toExec[0])
            }
        }

        if (toExec[0]?.contains(GMTOOL)) {
            if (deviceLocation == DeviceLocation.CLOUD) {
                String actionGroup = toExec[1]
                if (CLOUD_ACTION_GROUPS.contains(actionGroup)) {
                    toExec.add(1, OPT_CLOUD)
                }
            }

            if (isCompatibleWith(FEATURE_SOURCE_PARAM)) {
                toExec.addAll(1, [SOURCE_GRADLE])
            }

            if (genymotionConfig.verbose) {
                toExec.addAll(1, [VERBOSE])
                Log.debug(cleanCommand(toExec))
            }
        }

        return toExec
    }

    /**
     * If binary is gmtool or one of the tools we ship in tools/, prepend genymotionPath
     *
     * FIXME: This should not be needed. GMTool.cmd should only be used to run gmtool, not other binaries
     * @param binary to run
     * @return binary to run, potentially with full path
     */
    private String prependGenymotionPath(String binary) {
        if (binary == GMTOOL || binary.startsWith("tools/")) {
            return genymotionConfig.genymotionPath + binary
        } else {
            return binary
        }
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
