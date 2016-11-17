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

package com.genymotion

import com.genymotion.model.DeviceLocation
import com.genymotion.model.GenymotionConfig
import com.genymotion.model.GenymotionTemplate
import com.genymotion.model.GenymotionVirtualDevice
import com.genymotion.model.NetworkInfo
import com.genymotion.tools.GMTool
import com.genymotion.tools.GMToolException
import com.genymotion.tools.GMToolFeature
import com.genymotion.tools.Tools
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import java.util.concurrent.TimeoutException

import static com.genymotion.tools.GMToolDsl.*
import static org.mockito.Mockito.*

class GMToolTest extends CleanMetaTest {

    public static final String configPrintOutput = """\
statistics=off
username=testName
password=******
license_server=off
license_server_address=https://test.com
proxy=off
proxy_address=testAddress
proxy_port=12345
proxy_auth=on
proxy_username=testUsername
proxy_password=******
virtual_device_path=/simple/path/
sdk_path=/adb/path/
use_custom_sdk=off
screen_capture_path=/capture/path/"""

    public static final String templatesOutput = """\
Name                  : Sony Xperia Z - 4.2.2 - API 17 - 1080x1920
UUID                  : a4a62d7f-4293-4bdd-8e8d-8e25b80046f1
Description           : Sony Xperia Z (5\", 1080x1920, XXHDPI) AOSP4.2.2 API 17
Android Version       : 4.2.2
API Level             : 17
Genymotion Version    : 2.6.0
Screen Width          : 1080
Screen Height         : 1920
Screen Density        : xxhdpi
Screen DPI            : 480
Nb CPU                : 1
RAM                   : 2048
Internal Storage      : 16384
Telephony             : true
Nav Bar Visible       : true
Virtual Keyboard      : true

Name                  : Sony Xperia Z - 4.3 - API 18 - 1080x1920
UUID                  : 8a807602-5d00-407d-b7d8-87e1726aca8f
Description           : Sony Xperia Z (5\", 1080x1920, XXHDPI) AOSP4.3 API 18
Android Version       : 4.3
API Level             : 18
Genymotion Version    : 2.6.0
Screen Width          : 1080
Screen Height         : 1920
Screen Density        : xxhdpi
Screen DPI            : 480
Nb CPU                : 1
RAM                   : 2048
Internal Storage      : 16384
Telephony             : true
Nav Bar Visible       : true
Virtual Keyboard      : true"""

    public static final String listOneRunningDeviceOutput = """\
 State  |   ADB Serial    |                UUID                |      Name
--------+-----------------+------------------------------------+---------------
     On |  192.168.56.101 |01283849-3c02-4a38-831e-23e2b2d7adb2| randomDevice"""

    public static final String listTwoStoppedDevicesOutput = """\
 State  |   ADB Serial    |                UUID                |      Name
--------+-----------------+------------------------------------+---------------
    Off |         0.0.0.0 |d600d7e5-fce0-489e-8e18-3b14895eb25b| stoppedDevice1
    Off |         0.0.0.0 |290ed5b4-8f6b-4a1e-9742-92d0420cdfc7| stoppedDevice2"""

    public static final String listThreeDevicesOutput = """\
 State  |   ADB Serial    |                UUID                |      Name
--------+-----------------+------------------------------------+---------------
    Off |         0.0.0.0 |d600d7e5-fce0-489e-8e18-3b14895eb25b| stoppedDevice1
     On |  192.168.56.101 |01283849-3c02-4a38-831e-23e2b2d7adb2| randomDevice
    Off |         0.0.0.0 |290ed5b4-8f6b-4a1e-9742-92d0420cdfc7| stoppedDevice2"""

    public static final String listOneRunningCloudDeviceOutput = """\
 State  |   ADB Serial    |                UUID                |      Name
--------+-----------------+------------------------------------+---------------
     On | localhost:56789 |01283849-3c02-4a38-831e-23e2b2d7adb2| randomDevice"""

    public static final String deviceDetailOutput = """\
Name                  : randomDevice
UUID                  : 01283849-3c02-4a38-831e-23e2b2d7adb2
Android Version       : 4.4.4
API Level             : 19
Genymotion Version    : 2.6.0
Screen Width          : 1080
Screen Height         : 1920
Screen Density        : xxhdpi
Screen DPI            : 480
Nb CPU                : 4
RAM                   : 2048
Telephony             : true
Nav Bar Visible       : true
Virtual Keyboard      : true
Path                  : /Users/anonymous/.Genymobile/Genymotion/deployed/randomDevice
State                 : On
IP                    : 192.168.56.101
ADB Serial            : 192.168.56.101"""

    public static final String cloudDeviceDetailOutput = """\
Name                  : randomDevice
UUID                  : 01283849-3c02-4a38-831e-23e2b2d7adb2
Android Version       : 4.4.4
API Level             : 19
Genymotion Version    : 2.6.0
Screen Width          : 1080
Screen Height         : 1920
Screen Density        : xxhdpi
Screen DPI            : 480
Nb CPU                : 4
RAM                   : 2048
Telephony             : true
Nav Bar Visible       : true
Virtual Keyboard      : true
Path                  : /Users/anonymous/.Genymobile/Genymotion/deployed/randomDevice
State                 : On
IP                    : 0.0.0.0
ADB Serial            : localhost:56789"""

    public static final String createDeviceOutput = """\
Creating nexus4 from template Google Nexus 4 - 4.1.1 - API 16 - 768x1280...
Downloading template...
(147MB / 147MB)
Download finished
Template installed
Creating virtual device...
Virtual device created successfully"""

    public static final String startDisposableDeviceOutput = """\
Disposable virtual device started successfully"""

    public static final String installOutput = """\
Installing /Users/anonymous/Downloads/devices-release-unaligned.apk on nexus7...
File installed on nexus7"""

    public static final String pushOutput = """\
Pushing /Users/anonymous/Downloads/devices-release-unaligned.apk to nexus7...
File pushed to nexus7"""

    public static final String versionOutput = """\
Version  : 2.4.5
Revision : 20150629-a7e4623
"""
    public static final String pullOutput = """\
Pulling /system/build.prop from nexus4...
File pulled from nexus4"""

    public static final String flashOutput = """\
Pushing gapps-kk-20140105-signed.zip to Google Nexus 5 - 4.4.4 - API 19 - 1080x1920...
Checking application gapps-kk-20140105-signed.zip on virtual device Google Nexus 5 - 4.4.4 - API 19 - 1080x1920...
Installing application gapps-kk-20140105-signed.zip on virtual device Google Nexus 5 - 4.4.4 - API 19 - 1080x1920...
File installed on Google Nexus 5 - 4.4.4 - API 19 - 1080x1920"""

    public static final String factoryResetOutput = "Virtual device restored to factory state"

    public static final String logcatClearOutput = "Cleaning logcat of nexus7"

    public static final String deviceNamePlaceHolder = /{deviceName}/

    public static final String logcatDumpOutput = "Writing logcat for nexus7 into /tmp/dump.log..."

    @Rule
    public ExpectedException expectedException = ExpectedException.none()

    @Before
    public void setUp() {
        GMTool.DEFAULT_CONFIG = new GenymotionConfig()
    }

    @Test
    public void canGetConfigFromGMTool() {
        GMTool gmtoolSpy = initSpyAndOutput(configPrintOutput)

        GenymotionConfig config = gmtoolSpy.getConfig()

        //@formatter:off
        assert false                == config.statistics
        assert "testName"           == config.username
        assert false                == config.licenseServer
        assert "https://test.com"   == config.licenseServerAddress
        assert false                == config.proxy
        assert "testAddress"        == config.proxyAddress
        assert 12345                == config.proxyPort
        assert true                 == config.proxyAuth
        assert "testUsername"       == config.proxyUsername
        assert "/simple/path/"      == config.virtualDevicePath
        assert "/adb/path/"         == config.androidSdkPath
        assert false                == config.useCustomSdk
        assert "/capture/path/"     == config.screenCapturePath
        //@formatter:on

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, CONFIG, PRINT])
    }

    @Test
    public void checkGMToolNotFoundError() {
        GMTool gmtool = GMTool.newInstance()

        gmtool.genymotionConfig.genymotionPath = "nowhere"
        gmtool.genymotionConfig.abortOnError = true

        expectedException.expect(FileNotFoundException)
        String message = "$GMTool.GENYMOTION_PATH_ERROR_MESSAGE Current value: $gmtool.genymotionConfig.genymotionPath"
        expectedException.expectMessage(message)

        gmtool.usage()
    }

    @Test
    public void isTemplatesAvailable() {
        GMTool gmtoolSpy = initSpyAndOutput(templatesOutput)

        def templates = gmtoolSpy.getTemplates()

        assert templates.size() == 2
        assert templates[0].name == "Sony Xperia Z - 4.2.2 - API 17 - 1080x1920"

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, TEMPLATES, OPT_FULL])
    }

    @Test
    public void canGetRunningDevicesByName() {
        GMTool gmtoolSpy = initSpyAndOutput(listOneRunningDeviceOutput)

        def devices = gmtoolSpy.getRunningDevices(false, true)

        assert devices.contains("randomDevice")
        assert !devices.contains("stoppedDevice2")
        assert !devices.contains("stoppedDevice1")

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, LIST, OPT_RUNNING])
    }

    @Test
    public void canGetRunningDevices() {
        GMTool gmtoolSpy = initSpyAndOutput(listOneRunningDeviceOutput)

        def devices = gmtoolSpy.getRunningDevices(false, false)

        assert devices[0].name == "randomDevice"
        assert devices[0].adbSerial == "192.168.56.101:5555"
        assert devices[0].state == "On"

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, LIST, OPT_RUNNING])
    }

    @Test
    public void canGetFilledRunningDevices() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn(
                [new StringBuffer().append(listOneRunningDeviceOutput), null, 0],
                [new StringBuffer().append(deviceDetailOutput), null, 0]
        ).when(gmtoolSpy).executeCommand(anyList())

        def devices = gmtoolSpy.getRunningDevices(true, false)

        checkDetailedDeviceContent(devices[0])

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, LIST, OPT_RUNNING])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, DETAILS, devices[0].name])
    }

    @Test
    public void canGetRunningCloudDevices() {
        GMTool gmtoolSpy = initSpyAndOutput(listOneRunningCloudDeviceOutput, DeviceLocation.CLOUD)

        def devices = gmtoolSpy.getRunningDevices(false, false)

        assert devices[0].name == "randomDevice"
        assert devices[0].adbSerial == "localhost:56789"
        assert devices[0].state == "On"

        // Note: no OPT_CLOUD here because cmd() is called without OPT_CLOUD, it adds the option itself
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, LIST, OPT_RUNNING])
    }

    @Test
    public void canGetFilledRunningCloudDevices() {
        GMTool gmtool = GMTool.newInstance()
        gmtool.deviceLocation = DeviceLocation.CLOUD
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn(
                [new StringBuffer().append(listOneRunningCloudDeviceOutput), null, 0],
                [new StringBuffer().append(cloudDeviceDetailOutput), null, 0]
        ).when(gmtoolSpy).executeCommand(anyList())

        def devices = gmtoolSpy.getRunningDevices(true, false)

        checkDetailedCloudDeviceContent(devices[0])

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, LIST, OPT_RUNNING])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, DETAILS, devices[0].name])
    }

    @Test
    public void canGetStoppedDevicesByName() {
        GMTool gmtoolSpy = initSpyAndOutput(listTwoStoppedDevicesOutput)

        def devices = gmtoolSpy.getStoppedDevices(false, true)

        assert !devices.contains("randomDevice")
        assert devices.contains("stoppedDevice2")
        assert devices.contains("stoppedDevice1")

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, LIST, OPT_OFF])
    }

    @Test
    public void canGetStoppedDevices() {
        GMTool gmtoolSpy = initSpyAndOutput(listTwoStoppedDevicesOutput)

        def devices = gmtoolSpy.getStoppedDevices(false, false)

        assert devices[0].name == "stoppedDevice1"
        assert devices[0].adbSerial == "0.0.0.0"
        assert devices[0].state == "Off"

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, LIST, OPT_OFF])
    }

    @Test
    public void canGetFilledStoppedDevices() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn(
                [new StringBuffer().append(listTwoStoppedDevicesOutput), null, 0],
                [new StringBuffer().append(deviceDetailOutput), null, 0]
        ).when(gmtoolSpy).executeCommand(anyList())

        def devices = gmtoolSpy.getStoppedDevices(true, false)

        assert devices.size() == 2
        checkDetailedDeviceContent(devices[0])
        checkDetailedDeviceContent(devices[1])

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, LIST, OPT_OFF])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, DETAILS, "stoppedDevice1"])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, DETAILS, "stoppedDevice2"])
    }

    @Test
    public void canGetAllDevicesByName() {
        GMTool gmtoolSpy = initSpyAndOutput(listThreeDevicesOutput)

        def devices = gmtoolSpy.getAllDevices(false, true)

        assert devices.size() == 3
        assert devices.containsAll(["randomDevice", "stoppedDevice1", "stoppedDevice2"])

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, LIST])
    }

    @Test
    public void canGetAllDevices() {
        GMTool gmtoolSpy = initSpyAndOutput(listThreeDevicesOutput)

        def devices = gmtoolSpy.getAllDevices(false, false)

        //@formatter:off
        assert devices.size()       == 3
        assert devices[0].name      == "stoppedDevice1"
        assert devices[0].adbSerial == "0.0.0.0"
        assert devices[0].state     == "Off"
        assert devices[1].name      == "randomDevice"
        assert devices[1].adbSerial == "192.168.56.101:5555"
        assert devices[1].state     == "On"
        assert devices[2].name      == "stoppedDevice2"
        assert devices[2].adbSerial == "0.0.0.0"
        assert devices[2].state     == "Off"
        //@formatter:on

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, LIST])
    }

    @Test
    public void canGetAllDevicesFilled() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn(
                [new StringBuffer().append(listThreeDevicesOutput), null, 0],
                [new StringBuffer().append(deviceDetailOutput), null, 0],
                [new StringBuffer().append(deviceDetailOutput), null, 0],
                [new StringBuffer().append(deviceDetailOutput), null, 0]
        ).when(gmtoolSpy).executeCommand(anyList())

        def devices = gmtoolSpy.getAllDevices(true, false)

        assert devices.size() == 3
        checkDetailedDeviceContent(devices[0])
        checkDetailedDeviceContent(devices[1])
        checkDetailedDeviceContent(devices[2])

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, LIST])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, DETAILS, "randomDevice"])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, DETAILS, "stoppedDevice1"])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, DETAILS, "stoppedDevice2"])
    }

    @Test
    public void canCreateDeviceFromTemplate() {
        GMTool gmtoolSpy = initSpyAndOutput(createDeviceOutput)

        GenymotionTemplate deviceToCreate = new GenymotionTemplate(name: "Genymotion Template")

        gmtoolSpy.createDevice(deviceToCreate)

        verifyGmtoolCmdWithClosure(gmtoolSpy,
                [GMTOOL, ADMIN, CREATE, deviceToCreate.name, deviceToCreate.name, OPT_DENSITY, OPT_WIDTH, OPT_HEIGHT,
                 OPT_VIRTUAL_KEYBOARD, OPT_NAVBAR, OPT_NBCPU, OPT_RAM, OPT_NETWORK_MODE, OPT_BRIDGE_INTERFACE])
    }

    @Test
    public void canCreateDeviceFromParams() {
        GMTool gmtoolSpy = initSpyAndOutput(createDeviceOutput)

        String template = "A template"
        def (String deviceName, String density, int width, int height, boolean virtualKeyboard, boolean navbarVisible,
        int nbcpu, int ram) = getDeviceParams()
        NetworkInfo networkInfo = NetworkInfo.createNatNetworkInfo()

        gmtoolSpy.createDevice(template, deviceName, density, width, height,
                virtualKeyboard, navbarVisible, nbcpu, ram, networkInfo.mode, networkInfo.bridgeInterface)

        verifyGmtoolCmdWithClosure(gmtoolSpy,
                [GMTOOL, ADMIN, CREATE, template, deviceName, OPT_DENSITY + density, OPT_WIDTH + width,
                 OPT_HEIGHT + height, OPT_VIRTUAL_KEYBOARD + virtualKeyboard, OPT_NAVBAR + navbarVisible,
                 OPT_NBCPU + nbcpu, OPT_RAM + ram, OPT_NETWORK_MODE + networkInfo.mode,
                 OPT_BRIDGE_INTERFACE + networkInfo.bridgeInterface])
    }

    @Test
    public void canCreateDeviceWithInBridgeModeWithBridgeInterface() {
        GMTool gmtoolSpy = initSpyAndOutput(createDeviceOutput)

        String template = "A template"
        def (String deviceName, String density, int width, int height, boolean virtualKeyboard, boolean navbarVisible,
        int nbcpu, int ram) = getDeviceParams()
        NetworkInfo networkInfo = NetworkInfo.createBridgeNetworkInfo("eth0")

        gmtoolSpy.createDevice(template, deviceName, density, width, height,
                virtualKeyboard, navbarVisible, nbcpu, ram, networkInfo.mode, networkInfo.bridgeInterface)

        verifyGmtoolCmdWithClosure(gmtoolSpy,
                [GMTOOL, ADMIN, CREATE, template, deviceName, OPT_DENSITY + density, OPT_WIDTH + width,
                 OPT_HEIGHT + height, OPT_VIRTUAL_KEYBOARD + virtualKeyboard, OPT_NAVBAR + navbarVisible,
                 OPT_NBCPU + nbcpu, OPT_RAM + ram, OPT_NETWORK_MODE + networkInfo.mode,
                 OPT_BRIDGE_INTERFACE + networkInfo.bridgeInterface])
    }

    @Test
    public void canStartDisposableDeviceFromParam() {
        GMTool gmtoolSpy = initSpyAndOutput(startDisposableDeviceOutput)

        String template = "A template"
        def (String deviceName, String density, int width, int height, boolean virtualKeyboard, boolean navbarVisible,
             int nbcpu, int ram) = getDeviceParams()
        NetworkInfo networkInfo = NetworkInfo.createNatNetworkInfo()

        gmtoolSpy.startDisposableDevice(template, deviceName, density, width, height,
                virtualKeyboard, navbarVisible, nbcpu, ram, networkInfo.mode, networkInfo.bridgeInterface)

        verifyGmtoolCmdWithClosure(gmtoolSpy,
                [GMTOOL, ADMIN, START_DISPOSABLE, template, deviceName])
    }

    @Test
    public void canGetDetailedDeviceByName() {
        def device = testGMToolByName method: "getDevice",
                output: deviceDetailOutput,
                expectedCommand: [GMTOOL, ADMIN, DETAILS, deviceNamePlaceHolder]

        checkDetailedDeviceContent(device)
    }

    @Test
    public void canGetDetailedDevice() {
        def device = testGMTool method: "updateDevice",
                output: deviceDetailOutput,
                expectedCommand: [GMTOOL, ADMIN, DETAILS, deviceNamePlaceHolder]

        checkDetailedDeviceContent(device)
    }

    @Test
    public void canCloneDevice() {
        GMTool gmtoolSpy = initSpyAndOutput("")

        String deviceName = "myDevice"
        GenymotionVirtualDevice deviceToClone = new GenymotionVirtualDevice(deviceName)
        String cloneName = "cloneDevice"

        int exitCode = gmtoolSpy.cloneDevice(deviceToClone, cloneName)

        assert exitCode == 0

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, CLONE, deviceName, cloneName])
    }

    @Test
    public void canCloneDeviceByName() {
        GMTool gmtoolSpy = initSpyAndOutput("")

        String deviceName = "myDevice"
        String cloneName = "cloneDevice"

        int exitCode = gmtoolSpy.cloneDevice(deviceName, cloneName)

        assert exitCode == 0

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, CLONE, deviceName, cloneName])
    }

    @Test
    public void canEditDevice() {
        GMTool gmtoolSpy = initSpyAndOutput("")

        def (String deviceName, String density, int width, int height, boolean virtualKeyboard, boolean navbarVisible,
        int nbcpu, int ram) = getDeviceParams()
        NetworkInfo networkInfo = NetworkInfo.createNatNetworkInfo()

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(deviceName, density, width, height,
                virtualKeyboard, navbarVisible, nbcpu, ram, networkInfo)

        int exitCode = gmtoolSpy.editDevice(device)

        assert exitCode == 0

        verifyGmtoolCmdWithClosure(gmtoolSpy,
                [GMTOOL, ADMIN, EDIT, deviceName, OPT_DENSITY + density, OPT_WIDTH + width, OPT_HEIGHT + height,
                 OPT_VIRTUAL_KEYBOARD + virtualKeyboard, OPT_NAVBAR + navbarVisible, OPT_NBCPU + nbcpu, OPT_RAM + ram,
                 OPT_NETWORK_MODE + networkInfo.mode, OPT_BRIDGE_INTERFACE + networkInfo.bridgeInterface])
    }

    @Test
    public void canEditDeviceByName() {
        GMTool gmtoolSpy = initSpyAndOutput("")

        def (String deviceName, String density, int width, int height, boolean virtualKeyboard, boolean navbarVisible,
        int nbcpu, int ram) = getDeviceParams()
        NetworkInfo networkInfo = NetworkInfo.createNatNetworkInfo()

        int exitCode = gmtoolSpy.editDevice(deviceName, density, width, height, virtualKeyboard, navbarVisible,
                nbcpu, ram, networkInfo.mode, networkInfo.bridgeInterface)

        assert exitCode == 0

        verifyGmtoolCmdWithClosure(gmtoolSpy,
                [GMTOOL, ADMIN, EDIT, deviceName, OPT_DENSITY + density, OPT_WIDTH + width, OPT_HEIGHT + height,
                 OPT_VIRTUAL_KEYBOARD + virtualKeyboard, OPT_NAVBAR + navbarVisible, OPT_NBCPU + nbcpu, OPT_RAM + ram,
                 OPT_NETWORK_MODE + networkInfo.mode, OPT_BRIDGE_INTERFACE + networkInfo.bridgeInterface])
    }

    @Test
    public void canStartDevice() {
        testGMTool method: "startDevice",
                output: "",
                expectedCommand: [GMTOOL, ADMIN, START, deviceNamePlaceHolder]
    }

    @Test
    public void throwsWhenCommandError() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)

        int exitCode = 9999 // Random unknown exit code
        String errorString = "This is a random error message"
        doReturn([new StringBuffer(),
                  new StringBuffer().append(errorString),
                  exitCode])
                .when(gmtoolSpy).executeCommand(anyList())

        expectedException.expect(GMToolException)
        expectedException.expectMessage("GMTool command failed. Error code: $exitCode." + errorString)

        gmtoolSpy.genymotionConfig.abortOnError = true
        gmtoolSpy.getDevice("sqfqqfd")
    }

    @Test
    public void canStopDevice() {
        testGMTool method: "stopDevice",
                output: "",
                expectedCommand: [GMTOOL, ADMIN, STOP, deviceNamePlaceHolder]
    }

    @Test
    public void canStopDeviceByName() {
        testGMToolByName method: "stopDevice",
                output: "",
                expectedCommand: [GMTOOL, ADMIN, STOP, deviceNamePlaceHolder]
    }

    @Test
    public void canStopDisposableDevice() {
        testGMTool method: "stopDisposableDevice",
                output: "",
                expectedCommand: [GMTOOL, ADMIN, STOP_DISPOSABLE, deviceNamePlaceHolder]
    }

    @Test
    public void canStopDisposableDeviceByName() {
        testGMToolByName method: "stopDisposableDevice",
                output: "",
                expectedCommand: [GMTOOL, ADMIN, STOP_DISPOSABLE, deviceNamePlaceHolder]
    }

    @Test
    public void canStopAllDevices() {
        GMTool gmtoolSpy = initSpyAndOutput("")

        int exitCode = gmtoolSpy.stopAllDevices()

        assert exitCode == 0
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, ADMIN, STOPALL])
    }

    @Test
    public void canResetDevice() {
        testGMTool method: "resetDevice",
                output: factoryResetOutput,
                expectedCommand: [GMTOOL, ADMIN, FACTORY_RESET, deviceNamePlaceHolder]
    }

    @Test
    public void canResetDeviceByName() {
        testGMToolByName method: "resetDevice",
                output: factoryResetOutput,
                expectedCommand: [GMTOOL, ADMIN, FACTORY_RESET, deviceNamePlaceHolder]
    }

    @Test
    public void canLogcatClear() {
        testGMTool method: "logcatClear",
                output: logcatClearOutput,
                expectedCommand: [GMTOOL, DEVICE, OPT_NAME + deviceNamePlaceHolder, LOGCAT_CLEAR]
    }

    @Test
    public void canLogcatClearByName() {
        testGMToolByName method: "logcatClear",
                output: logcatClearOutput,
                expectedCommand: [GMTOOL, DEVICE, OPT_NAME + deviceNamePlaceHolder, LOGCAT_CLEAR]
    }

    @Test
    public void canLogcatDump() {
        GMTool gmtoolSpy = initSpyAndOutput(logcatDumpOutput)

        String deviceName = "myDevice"
        GenymotionVirtualDevice device = new GenymotionVirtualDevice(deviceName)
        String path = "random/path"

        int exitCode = gmtoolSpy.logcatDump(device, path)

        assert exitCode == 0
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, LOGCAT_DUMP, path])
    }

    @Test
    public void canLogcatDumpByName() {
        GMTool gmtoolSpy = initSpyAndOutput(logcatDumpOutput)

        String deviceName = "myDevice"
        String path = "random/path"

        int exitCode = gmtoolSpy.logcatDump(deviceName, path)

        assert exitCode == 0
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, LOGCAT_DUMP, path])
    }

    @Test
    public void canInstallAnApkToDevice() {
        GMTool gmtoolSpy = initSpyAndOutput(installOutput)

        String deviceName = "myDevice"
        GenymotionVirtualDevice device = new GenymotionVirtualDevice(deviceName)
        String apkPath = "random/path"

        int exitCode = gmtoolSpy.installToDevice(device, apkPath)

        assert exitCode == 0
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, apkPath])
    }

    @Test
    public void canInstallAnApkToDeviceByName() {
        GMTool gmtoolSpy = initSpyAndOutput(installOutput)

        String deviceName = "myDevice"
        String apkPath = "random/path"

        int exitCode = gmtoolSpy.installToDevice(deviceName, apkPath)

        assert exitCode == 0
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, apkPath])
    }

    @Test
    public void canInstallSeveralApksToDevice() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn(
                [new StringBuffer().append(installOutput), null, 0],
                [new StringBuffer().append(installOutput), null, 0],
                [new StringBuffer().append(installOutput), null, 0]
        ).when(gmtoolSpy).executeCommand(anyList())

        String deviceName = "myDevice"
        GenymotionVirtualDevice device = new GenymotionVirtualDevice(deviceName)
        String apkPath1 = "random/path1"
        String apkPath2 = "random/path2"
        String apkPath3 = "random/path3"

        def exitCodes = gmtoolSpy.installToDevice(device, [apkPath1, apkPath2, apkPath3])

        assert exitCodes == [0, 0, 0]
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, apkPath1])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, apkPath2])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, apkPath3])
    }

    @Test
    public void canInstallSeveralApksToDeviceByName() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn(
                [new StringBuffer().append(installOutput), null, 0],
                [new StringBuffer().append(installOutput), null, 0],
                [new StringBuffer().append(installOutput), null, 0]
        ).when(gmtoolSpy).executeCommand(anyList())

        String deviceName = "myDevice"
        String apkPath1 = "random/path1"
        String apkPath2 = "random/path2"
        String apkPath3 = "random/path3"

        def exitCodes = gmtoolSpy.installToDevice(deviceName, [apkPath1, apkPath2, apkPath3])

        assert exitCodes == [0, 0, 0]
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, apkPath1])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, apkPath2])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, INSTALL, apkPath3])
    }

    @Test
    public void canPushToDevice() {
        GMTool gmtoolSpy = initSpyAndOutput(pushOutput)

        String deviceName = "myDevice"
        GenymotionVirtualDevice device = new GenymotionVirtualDevice(deviceName)
        String filePath = "random/path"

        def exitCode = gmtoolSpy.pushToDevice(device, filePath)

        assert exitCode == [0]
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PUSH, filePath])
    }

    @Test
    public void canPushToDeviceByName() {
        GMTool gmtoolSpy = initSpyAndOutput(pushOutput)

        String deviceName = "myDevice"
        String filePath = "random/path"

        def exitCode = gmtoolSpy.pushToDevice(deviceName, filePath)

        assert exitCode == [0]
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PUSH, filePath])
    }

    @Test
    public void canPushListToDevice() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn(
                [new StringBuffer().append(pushOutput), null, 0],
                [new StringBuffer().append(pushOutput), null, 0],
                [new StringBuffer().append(pushOutput), null, 0]
        ).when(gmtoolSpy).executeCommand(anyList())

        String deviceName = "myDevice"
        GenymotionVirtualDevice device = new GenymotionVirtualDevice(deviceName)
        String  filePath1 = "random/path1"
        String filePath2 = "random/path2"
        String filePath3 = "random/path3"

        def exitCodes = gmtoolSpy.pushToDevice(device, [filePath1, filePath2, filePath3])

        assert exitCodes == [0, 0, 0]
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PUSH, filePath1])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PUSH, filePath2])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PUSH, filePath3])
    }

    @Test
    public void canPushListToDeviceByName() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn(
                [new StringBuffer().append(pushOutput), null, 0],
                [new StringBuffer().append(pushOutput), null, 0],
                [new StringBuffer().append(pushOutput), null, 0]
        ).when(gmtoolSpy).executeCommand(anyList())

        String deviceName = "myDevice"
        String  filePath1 = "random/path1"
        String filePath2 = "random/path2"
        String filePath3 = "random/path3"

        def exitCodes = gmtoolSpy.pushToDevice(deviceName, [filePath1, filePath2, filePath3])

        assert exitCodes == [0, 0, 0]
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PUSH, filePath1])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PUSH, filePath2])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PUSH, filePath3])
    }

    @Test
    public void canPushToDeviceWithDest() {
        GMTool gmtoolSpy = initSpyAndOutput(pushOutput)

        String deviceName = "myDevice"
        String filePath = "random/path"
        String destination = "destination/path"

        def exitCode = gmtoolSpy.pushToDevice(deviceName, [(filePath): destination])

        assert exitCode == [0]

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PUSH, filePath, destination])
    }

    @Test
    public void canPushListToDeviceWithDest() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn(
                [new StringBuffer().append(pushOutput), null, 0],
                [new StringBuffer().append(pushOutput), null, 0],
                [new StringBuffer().append(pushOutput), null, 0]
        ).when(gmtoolSpy).executeCommand(anyList())

        String deviceName = "myDevice"
        String filePath1 = "random/path1"
        String filePath2 = "random/path2"
        String filePath3 = "random/path3"
        String destination1 = "destination/path1"
        String destination2 = "destination/path2"
        String destination3 = "destination/path3"

        def exitCodes = gmtoolSpy.pushToDevice(deviceName, [(filePath1): destination1, (filePath2): destination2,
                                                            (filePath3): destination3])

        assert exitCodes == [0, 0, 0]
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PUSH, filePath1, destination1])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PUSH, filePath2, destination2])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PUSH, filePath3, destination3])
    }

    @Test
    public void canPullFromDevice() {
        GMTool gmtoolSpy = initSpyAndOutput(pullOutput)

        String deviceName = "myDevice"
        GenymotionVirtualDevice device = new GenymotionVirtualDevice(deviceName)
        String filePath = "random/path"
        String destPath = "dest/path"

        def exitCode = gmtoolSpy.pullFromDevice(device, [(filePath): destPath])

        assert exitCode == [0]
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PULL, filePath, destPath])
    }

    @Test
    public void canPullFromDeviceByName() {
        GMTool gmtoolSpy = initSpyAndOutput(pullOutput)

        String deviceName = "myDevice"
        String filePath = "random/path"
        String destPath = "dest/path"

        def exitCode = gmtoolSpy.pullFromDevice(deviceName, filePath, destPath)

        assert exitCode == [0]
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PULL, filePath, destPath])
    }

    @Test
    public void canPullListFromDevice() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn(
                [new StringBuffer().append(pullOutput), null, 0],
                [new StringBuffer().append(pullOutput), null, 0],
                [new StringBuffer().append(pullOutput), null, 0]
        ).when(gmtoolSpy).executeCommand(anyList())

        String deviceName = "myDevice"
        GenymotionVirtualDevice device = new GenymotionVirtualDevice(deviceName)
        String  filePath1 = "random/path1"
        String filePath2 = "random/path2"
        String filePath3 = "random/path3"
        String destPath1 = "dest/path1"
        String destPath2 = "dest/path2"
        String destPath3 = "dest/path3"


        def exitCodes = gmtoolSpy.pullFromDevice(device, [(filePath1):destPath1, (filePath2):destPath2,
                                                          (filePath3):destPath3])

        assert exitCodes == [0, 0, 0]
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PULL, filePath1, destPath1])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PULL, filePath2, destPath2])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PULL, filePath3, destPath3])
    }

    @Test
    public void canPullListFromDeviceByName() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn(
                [new StringBuffer().append(pullOutput), null, 0],
                [new StringBuffer().append(pullOutput), null, 0],
                [new StringBuffer().append(pullOutput), null, 0]
        ).when(gmtoolSpy).executeCommand(anyList())

        String deviceName = "myDevice"
        String  filePath1 = "random/path1"
        String filePath2 = "random/path2"
        String filePath3 = "random/path3"
        String destPath1 = "dest/path1"
        String destPath2 = "dest/path2"
        String destPath3 = "dest/path3"

        def exitCodes = gmtoolSpy.pullFromDevice(deviceName, [(filePath1):destPath1, (filePath2):destPath2,
                                                              (filePath3):destPath3])

        assert exitCodes == [0, 0, 0]
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PULL, filePath1, destPath1])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PULL, filePath2, destPath2])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, PULL, filePath3, destPath3])
    }

    @Test
    public void canFlashDevice() {
        GMTool gmtoolSpy = initSpyAndOutput(flashOutput)

        String deviceName = "myDevice"
        GenymotionVirtualDevice device = new GenymotionVirtualDevice(deviceName)
        String zipPath = "path/flash.zip"

        def exitCode = gmtoolSpy.flashDevice(device, zipPath)

        assert exitCode == 0
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, FLASH, zipPath])
    }

    @Test
    public void canFlashDeviceByName() {
        GMTool gmtoolSpy = initSpyAndOutput(flashOutput)

        String deviceName = "myDevice"
        String zipPath = "path/flash.zip"

        def exitCode = gmtoolSpy.flashDevice(deviceName, zipPath)

        assert exitCode == 0
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, FLASH, zipPath])
    }

    @Test
    public void canFlashListToDevice() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn(
                [new StringBuffer().append(flashOutput), null, 0],
                [new StringBuffer().append(flashOutput), null, 0],
                [new StringBuffer().append(flashOutput), null, 0]
        ).when(gmtoolSpy).executeCommand(anyList())

        String deviceName = "myDevice"
        GenymotionVirtualDevice device = new GenymotionVirtualDevice(deviceName)
        String zipPath1 = "path/flash1.zip"
        String zipPath2 = "path/flash2.zip"
        String zipPath3 = "path/flash3.zip"

        def exitCodes = gmtoolSpy.flashDevice(device, [zipPath1, zipPath2, zipPath3])

        assert exitCodes == [0, 0, 0]
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, FLASH, zipPath1])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, FLASH, zipPath2])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, FLASH, zipPath3])
    }

    @Test
    public void canFlashListToDeviceByName() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn(
                [new StringBuffer().append(flashOutput), null, 0],
                [new StringBuffer().append(flashOutput), null, 0],
                [new StringBuffer().append(flashOutput), null, 0]
        ).when(gmtoolSpy).executeCommand(anyList())

        String deviceName = "myDevice"
        String zipPath1 = "path/flash1.zip"
        String zipPath2 = "path/flash2.zip"
        String zipPath3 = "path/flash3.zip"

        def exitCodes = gmtoolSpy.flashDevice(deviceName, [zipPath1, zipPath2, zipPath3])

        assert exitCodes == [0, 0, 0]
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, FLASH, zipPath1])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, FLASH, zipPath2])
        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, DEVICE, OPT_NAME + deviceName, FLASH, zipPath3])
    }

    @Test
    public void canHidePassword() {
        GMTool gmtool = GMTool.newInstance()

        def command = ["ok", "nok", "--password=toto", "password=tutu", "--password=", "password="]
        def result = gmtool.cleanCommand(command)
        assert result == ["ok", "nok", "--password=toto", "password=*****", "--password=", "password="]
    }

    @Test(expected = TimeoutException)
    public void throwWhenProcessIsTooLongOnUnix() {
        GMTool gmtool = GMTool.newInstance()

        if (Tools.getOSName().toLowerCase().contains("windows")) {
            throw new TimeoutException() //we avoid the test on windows
        }

        gmtool.genymotionConfig.processTimeout = 100
        gmtool.genymotionConfig.abortOnError = true
        gmtool.cmd(["sleep", "1"])
    }

    @Test(expected = GMToolException)
    public void throwWhenProcessIsTooLongOnWindows() {
        GMTool gmtool = GMTool.newInstance()

        if (!Tools.getOSName().toLowerCase().contains("windows")) {
            throw new GMToolException() //we pass the test only on windows
        }

        gmtool.genymotionConfig.processTimeout = 100
        gmtool.genymotionConfig.abortOnError = true
        //XXX: gmtool admin list is supposed to take more than 1 millisecond
        gmtool.cmd([GMTOOL, "admin", "list"])
    }

    @Test
    public void doNotThrowWhenProcessIsTooLongOnUnix() {
        GMTool gmtool = GMTool.newInstance()

        if (Tools.getOSName().toLowerCase().contains("windows")) {
            return //we avoid the test on windows
        }

        gmtool.genymotionConfig.processTimeout = 100
        gmtool.genymotionConfig.abortOnError = false
        gmtool.cmd(["sleep", "1"])
    }

    @Test
    public void doNotThrowWhenProcessIsTooLongOnWindows() {
        GMTool gmtool = GMTool.newInstance()

        if (!Tools.getOSName().toLowerCase().contains("windows")) {
            return //we pass the test only on windows
        }

        gmtool.genymotionConfig.processTimeout = 100
        gmtool.genymotionConfig.abortOnError = false
        gmtool.cmd([GMTOOL, "admin", "list"])
    }

    @Test
    public void canHideSourceTag() {

        def command = ["ok", "nok", "--source=toto"]
        def result = GMTool.cleanCommand(command)
        assert result == ["ok", "nok"]
    }

    @Test
    public void canFormatCommand() {
        GMTool gmtool = GMTool.newInstance()

        def command = [GMTOOL, "nok"]
        gmtool.genymotionConfig.verbose = false
        gmtool.genymotionConfig.version = FEATURE_SOURCE_PARAM

        def result = gmtool.formatAndLogCommand(command)
        assert result == [gmtool.genymotionConfig.genymotionPath + GMTOOL, SOURCE_GRADLE, "nok"]

        gmtool.genymotionConfig.verbose = true
        result = gmtool.formatAndLogCommand(command)
        assert result == [gmtool.genymotionConfig.genymotionPath + GMTOOL, VERBOSE, SOURCE_GRADLE, "nok"]
    }

    @Test
    public void canFormatCloudCommands() {
        GMTool gmtool = GMTool.newInstance()
        def gmtoolFilePath = gmtool.genymotionConfig.genymotionPath + GMTOOL
        gmtool.deviceLocation = DeviceLocation.CLOUD

        def result = gmtool.formatAndLogCommand([GMTOOL, ADMIN, CREATE, "tmpl", "name"])
        assert result == [gmtoolFilePath, OPT_CLOUD, ADMIN, CREATE, "tmpl", "name"]

        result = gmtool.formatAndLogCommand([GMTOOL, DEVICE, "foo"])
        assert result == [gmtoolFilePath, OPT_CLOUD, DEVICE, "foo"]

        // actions from other groups, like config, should not be affected
        result = gmtool.formatAndLogCommand([GMTOOL, CONFIG, VERSION])
        assert result == [gmtoolFilePath, CONFIG, VERSION]
    }

    @Test
    public void canGetVersion() {
        GMTool gmtoolSpy = initSpyAndOutput(versionOutput)

        assert gmtoolSpy.getVersion() == "2.4.5"

        verifyGmtoolCmdWithClosure(gmtoolSpy, [GMTOOL, VERSION])
    }

    @Test
    public void canCheckCompatibility() {
        GMTool gmtool = GMTool.newInstance()

        gmtool.genymotionConfig.version = "2.4.5"
        assert !gmtool.isCompatibleWith(FEATURE_SOURCE_PARAM)

        gmtool.genymotionConfig.version = FEATURE_SOURCE_PARAM
        assert gmtool.isCompatibleWith(FEATURE_SOURCE_PARAM)
    }

    @Test
    public void canCheckSourceCompatibility() {
        GMTool gmtool = GMTool.newInstance()

        gmtool.genymotionConfig.verbose = false
        gmtool.genymotionConfig.version = FEATURE_SOURCE_PARAM
        def command = gmtool.formatAndLogCommand(["gmtool", "version"])

        def gmtoolFilePath = gmtool.genymotionConfig.genymotionPath + GMTOOL
        assert command == [gmtoolFilePath, "--source=gradle", "version"]

        gmtool.genymotionConfig.version = "2.4.5"
        command = gmtool.formatAndLogCommand(["gmtool", "version"])

        assert command == [gmtoolFilePath, "version"]
    }

    @Test(expected = GMToolException)
    public void canCheckLicenseServerCompatibility() {
        GMTool gmtoolSpy = initSpyAndOutput()

        GenymotionConfig config = new GenymotionConfig()
        config.licenseServer = true

        gmtoolSpy.genymotionConfig.version = FEATURE_ONSITE_LICENSE_CONFIG
        gmtoolSpy.setConfig(config) //should pass

        gmtoolSpy.genymotionConfig.version = "2.4.5"
        gmtoolSpy.setConfig(config) //should throw exception
    }

    @Test(expected = GMToolException)
    public void canCheckLicenseServerAddressCompatibility() {
        GMTool gmtoolSpy = initSpyAndOutput()

        GenymotionConfig config = new GenymotionConfig()
        config.licenseServerAddress = "test"

        gmtoolSpy.genymotionConfig.version = FEATURE_ONSITE_LICENSE_CONFIG
        gmtoolSpy.setConfig(config) //should pass

        gmtoolSpy.genymotionConfig.version = "2.4.5"
        gmtoolSpy.setConfig(config) //should throw exception
    }

    @Test
    public void canCheckFeatureAvailability() {
        GMTool gmtool = GMTool.newInstance()
        GMTool gmtoolSpy = spy(gmtool)
        GMTool.metaClass.static.newInstance = { gmtoolSpy }

        doReturn("2.9.0").when(gmtoolSpy).getVersion()

        gmtoolSpy.checkAvailability(GMToolFeature.Feature.DISPOSABLE)
        gmtoolSpy.checkAvailability(GMToolFeature.Feature.EDIT_NETWORK)

        verify(gmtoolSpy, times(1)).getVersion()
    }

    @After
    public void finishTest() {
        cleanMetaClass()
    }


    private static verifyGmtoolCmdWithClosure(GMTool gmtoolSpy, ArrayList<String> command) {
        verify(gmtoolSpy).cmd(
                eq(command),
                any(Closure)
        )
    }

    private static checkDetailedDeviceContent(GenymotionVirtualDevice device) {
        assert device.name == "randomDevice"
        assert device.adbSerial == "192.168.56.101:5555"
        assert device.state == "On"
        assert device.uuid == "01283849-3c02-4a38-831e-23e2b2d7adb2"
        assert device.virtualKeyboard == true
        assert device.dpi == 480
        assert device.androidVersion == "4.4.4"
    }

    private static checkDetailedCloudDeviceContent(GenymotionVirtualDevice device) {
        assert device.name == "randomDevice"
        assert device.adbSerial == "localhost:56789"
        assert device.state == "On"
        assert device.uuid == "01283849-3c02-4a38-831e-23e2b2d7adb2"
        assert device.virtualKeyboard == true
        assert device.dpi == 480
        assert device.androidVersion == "4.4.4"
    }

    private def testGMTool(Map inputs /*String output, String method, ArrayList<String> expectedCommand*/) {
        GMTool gmtoolSpy = initSpyAndOutput(inputs.output)

        String deviceName = "myDevice"
        GenymotionVirtualDevice device = new GenymotionVirtualDevice(deviceName)
        def result = gmtoolSpy."${inputs.method}"(device)

        verifyTest(result, inputs, deviceName, gmtoolSpy)

        return result
    }

    private def testGMToolByName(Map inputs /*String output, String method, ArrayList<String> expectedCommand*/) {
        GMTool gmtoolSpy = initSpyAndOutput(inputs.output)

        String deviceName = "myDevice"
        def result = gmtoolSpy."${inputs.method}"(deviceName)

        verifyTest(result, inputs, deviceName, gmtoolSpy)

        return result
    }

    private initSpyAndOutput(String output="", DeviceLocation deviceLocation=DeviceLocation.LOCAL) {
        GMTool gmtool = GMTool.newInstance()
        gmtool.deviceLocation = deviceLocation
        GMTool gmtoolSpy = spy(gmtool)

        doReturn([new StringBuffer().append(output), null, 0]).when(gmtoolSpy).executeCommand(anyList())
        doReturn(Void).when(gmtoolSpy).checkAvailability(any())

        return gmtoolSpy
    }

    private verifyTest(def result, Map inputs, String deviceName, GMTool gmtoolSpy) {
        //if the result is an int, we check it as an exit code
        if(result instanceof Integer) {
            assert result == 0
        }

        ArrayList<String> command = inputs.expectedCommand*.replace(deviceNamePlaceHolder, deviceName)
        verifyGmtoolCmdWithClosure(gmtoolSpy, command)
    }

    private getDeviceParams() {
        String deviceName = "myDevice"
        String density = "XXHDPI"
        int width = 800
        int height = 1280
        boolean virtualKeyboard = true
        boolean isNavBarVisible = false
        int nbCpu = 2
        int ram = 512

        return [deviceName, density, width, height, virtualKeyboard, isNavBarVisible, nbCpu, ram]
    }
}
