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

import com.genymotion.model.GenymotionConfig
import com.genymotion.model.GenymotionVirtualDevice
import com.genymotion.tools.GMTool
import com.genymotion.tools.GMToolException
import com.genymotion.tools.Tools
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.util.concurrent.TimeoutException

class GMToolTest extends CleanMetaTest {

    Project project
    GMTool gmtool

    def genymotionPath = null


    @BeforeClass
    public static void setUpClass() {
        TestTools.init()
    }
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
 State  |   IP Address    |                UUID                |      Name
--------+-----------------+------------------------------------+---------------
     On |  192.168.56.101 |01283849-3c02-4a38-831e-23e2b2d7adb2| randomDevice"""

    public static final String listTwoStoppedDevicesOutput = """\
 State  |   IP Address    |                UUID                |      Name
--------+-----------------+------------------------------------+---------------
    Off |         0.0.0.0 |d600d7e5-fce0-489e-8e18-3b14895eb25b| stoppedDevice1
    Off |         0.0.0.0 |290ed5b4-8f6b-4a1e-9742-92d0420cdfc7| stoppedDevice2"""

    public static final String listThreeDevicesOutput = """\
 State  |   IP Address    |                UUID                |      Name
--------+-----------------+------------------------------------+---------------
    Off |         0.0.0.0 |d600d7e5-fce0-489e-8e18-3b14895eb25b| stoppedDevice1
     On |  192.168.56.101 |01283849-3c02-4a38-831e-23e2b2d7adb2| randomDevice
    Off |         0.0.0.0 |290ed5b4-8f6b-4a1e-9742-92d0420cdfc7| stoppedDevice2"""

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
Path                  : /Users/eyal/.Genymobile/Genymotion/deployed/randomDevice
State                 : On
IP                    : 192.168.56.101"""

    public static final String createDeviceOutput = """\
Creating nexus4 from template Google Nexus 4 - 4.1.1 - API 16 - 768x1280...
Downloading template...
(147MB / 147MB)
Download finished
Template installed
Creating virtual device...
Virtual device created successfully"""

    public static final String installOutput = """\
Installing /Users/eyal/Downloads/devices-release-unaligned.apk on nexus7...
File installed on nexus7"""

    public static final String pushOutput = """\
Pushing /Users/eyal/Downloads/devices-release-unaligned.apk to nexus7...
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


    @Before
    public void setUp() {
        (project, gmtool) = TestTools.init()
        TestTools.setDefaultUser(true, gmtool)
    }

    @Test
    public void isConfigOK() {
        def exitCode = gmtool.usage()
        assert exitCode == gmtool.RETURN_NO_ERROR
    }


    @Test
    public void checkGMToolNotFoundError() {
        this.genymotionPath = project.genymotion.config.genymotionPath
        project.genymotion.config.genymotionPath = "nowhere"
        project.genymotion.config.abortOnError = true

        try {
            gmtool.usage()
            fail("FileNotFoundException expected")
        } catch (Exception e) {
            assert "$gmtool.GENYMOTION_PATH_ERROR_MESSAGE Current value: $gmtool.genymotionConfig.genymotionPath" == e.message
        }
    }


    @Test
    public void isTemplatesAvailable() {

        def templates = gmtool.getTemplates(true)
        assert templates.size() > 0
        assert templates[0].name?.trim()
    }

    @Test
    public void canGetRunningDevices() {
        String name = TestTools.createADevice(gmtool)

        gmtool.startDevice(name)
        def devices = gmtool.getRunningDevices(true, false, true)

        println "devices " + devices
        assert devices.contains(name)

        gmtool.stopDevice(name)

        gmtool.deleteDevice(name)
    }

    @Test
    public void canGetStoppedDevices() {
        String name = TestTools.createADevice(gmtool)

        def runningDevices = gmtool.getRunningDevices(true, false, true)
        if (runningDevices.contains(name)) {
            gmtool.stopDevice(name)
        }
        def devices = gmtool.getStoppedDevices(true, false, true)

        assert devices.contains(name)

        gmtool.deleteDevice(name)
    }


    @Test
    public void canCreateDevice() {
        TestTools.createAllDevices(gmtool)

        def devices = gmtool.getAllDevices(true)

        TestTools.DEVICES.each() { key, value ->
            boolean exists = false
            devices.each() {
                if (it.name == key) {
                    exists = true
                    return
                }
            }
            assert exists

        }
        TestTools.deleteAllDevices(gmtool)
    }

    @Test
    public void canDetailDevice() {

        String name = TestTools.createADevice(gmtool)

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails(true)


        assert device.androidVersion != null
        assert device.state != null

        gmtool.deleteDevice(name)
    }

    @Test
    public void canListDevices() {

        TestTools.createAllDevices(gmtool)

        def devices = gmtool.getAllDevices()
        assert devices.size() > 0


        TestTools.deleteAllDevices(gmtool)
    }

    @Test
    public void canCloneDevice() {

        String name = TestTools.createADevice(gmtool)

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails()

        def newName = name + "-clone"
        gmtool.cloneDevice(device, newName)

        GenymotionVirtualDevice newDevice = new GenymotionVirtualDevice(newName)
        newDevice.fillFromDetails()

        assert device.androidVersion == newDevice.androidVersion
        assert device.dpi == newDevice.dpi
        assert device.height == newDevice.height
        assert device.width == newDevice.width
        assert device.navbarVisible == newDevice.navbarVisible
        assert device.virtualKeyboard == newDevice.virtualKeyboard

        gmtool.deleteDevice(name)
        gmtool.deleteDevice(newName)
    }

    @Test
    public void canEditDevice() {

        String name = TestTools.createADevice(gmtool)

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails()

        device.navbarVisible = false
        device.height = 600
        device.width = 800
        device.density = "hdpi"
        device.dpi = 260
        device.virtualKeyboard = false
        device.nbCpu = 2
        device.ram = 2048

        gmtool.editDevice(device)

        GenymotionVirtualDevice newDevice = new GenymotionVirtualDevice(name)
        newDevice.fillFromDetails()

        assert device.androidVersion == newDevice.androidVersion
        assert device.density == newDevice.density
        assert device.dpi == newDevice.dpi
        assert device.height == newDevice.height
        assert device.width == newDevice.width
        assert device.navbarVisible == newDevice.navbarVisible
        assert device.virtualKeyboard == newDevice.virtualKeyboard

        gmtool.deleteDevice(name)
    }

    @Test
    public void canStartDevice() {

        String name = TestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name)

        assert exitCode == 0
    }

    @Test(expected = GMToolException.class)
    public void throwsWhenCommandError() {
        gmtool.genymotionConfig.abortOnError = true
        gmtool.getDevice("sqfqqfd", true)
    }

    @Test
    public void canStopDevice() {

        String name = TestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name)

        if (exitCode == 0) {
            gmtool.stopDevice(name)
        }

        assert exitCode == 0
        assert !gmtool.isDeviceRunning(name)
    }


    @Test
    public void canStopAllDevices() {

        TestTools.createAllDevices(gmtool)

        TestTools.DEVICES.each() {
            gmtool.startDevice(it.key)
        }

        gmtool.stopAllDevices()

        def runningDevices = gmtool.getRunningDevices(true, false, true)
        assert runningDevices == []

    }

    @Test
    public void canResetDevice() {
        //TODO
    }

    @Test
    public void canLogcatClear() {
        String name = TestTools.createADevice(gmtool)
        def exitCode = gmtool.startDevice(name)
        assert exitCode == 0

        boolean gotIt = false
        String uniqueString = "GENYMOTION ROCKS DU PONEY " + System.currentTimeMillis()
        gmtool.cmd(["tools/adb", "shell", "log $uniqueString"], true)
        String path = TestTools.TEMP_PATH + "logcat.dump"
        File file = new File(path)
        file.delete()

        gmtool.logcatDump(name, path, true)

        file = new File(path)
        assert file.exists()
        file.eachLine {
            if (it.contains(uniqueString)) {
                gotIt = true
            }
        }
        assert gotIt
        file.delete()

        exitCode = gmtool.logcatClear(name, true)
        assert exitCode == 0

        gotIt = false
        gmtool.logcatDump(name, path)

        file = new File(path)
        assert file.exists()

        file.eachLine {
            if (it.contains(uniqueString)) {
                gotIt = true
            }
        }

        assert !gotIt
    }

    @Test
    public void canLogcatDump() {
        String name = TestTools.createADevice(gmtool)
        def exitCode = gmtool.startDevice(name)
        assert exitCode == 0

        String uniqueString = "GENYMOTION ROCKS DU PONEY " + System.currentTimeMillis()
        gmtool.cmd(["tools/adb", "shell", "log $uniqueString"], true)

        String path = TestTools.TEMP_PATH + "logcat.dump"

        exitCode = gmtool.logcatDump(name, path, true)
        assert exitCode == 0

        boolean gotIt = false

        File file = new File(path)
        assert file.exists()

        file.eachLine {
            if (it.contains(uniqueString)) {
                gotIt = true
            }
        }

        assert gotIt
    }


    @Test
    public void canInstallToDevice() {

        String name = TestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name)
        assert exitCode == 0

        gmtool.installToDevice(name, "res/test/test.apk", true)
        boolean installed = false
        gmtool.cmd(["tools/adb", "shell", "pm list packages"], true) { line, count ->
            if (line.contains("com.genymotion.test")) {
                installed = true
            }
        }
        assert installed
    }

    @Test
    public void canInstallListOfAppToDevice() {

        String name = TestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        def listOfApps = ["res/test/test.apk", "res/test/test2.apk"]

        gmtool.installToDevice(name, listOfApps, true)

        int installed = 0
        gmtool.cmd(["tools/adb", "shell", "pm list packages"], true) { line, count ->
            if (line.contains("com.genymotion.test") || line.contains("com.genymotion.test2")) {
                installed++
            }
        }
        assert installed == listOfApps.size()
    }


    @Test
    public void canPushToDevice() {

        String name = TestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        gmtool.pushToDevice(name, "res/test/test.txt", true)
        boolean pushed = false
        gmtool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) { line, count ->
            if (line.contains("test.txt")) {
                pushed = true
            }
        }
        assert pushed

    }

    @Test
    public void canPushListToDevice() {

        String name = TestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        def listOfFiles = ["res/test/test.txt", "res/test/test2.txt"]
        gmtool.pushToDevice(name, listOfFiles, true)

        int pushed = 0
        gmtool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) { line, count ->
            if (line.contains("test.txt") || line.contains("test2.txt")) {
                pushed++
            }
        }
        assert pushed == listOfFiles.size()

    }

    @Test
    public void canPushToDeviceWithDest() {

        String name = TestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        def destination = "/sdcard/"
        gmtool.pushToDevice(name, ["res/test/test.txt": destination], true)
        boolean pushed = false
        gmtool.cmd(["tools/adb", "shell", "ls", destination], true) { line, count ->
            if (line.contains("test.txt")) {
                pushed = true
            }
        }
        assert pushed
    }

    @Test
    public void canPushListToDeviceWithDest() {

        String name = TestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        def destination = "/sdcard/"
        def listOfFiles = ["res/test/test.txt": destination, "res/test/test2.txt": destination]
        gmtool.pushToDevice(name, listOfFiles, true)

        int pushed = 0
        gmtool.cmd(["tools/adb", "shell", "ls", destination], true) { line, count ->
            if (line.contains("test.txt") || line.contains("test2.txt")) {
                pushed++
            }
        }
        assert pushed == listOfFiles.size()
    }


    @Test
    public void canPullFromDevice() {

        String name = TestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        //removing the pulled files
        TestTools.recreatePulledDirectory()

        gmtool.pullFromDevice(name, "/system/build.prop", TestTools.PULLED_PATH, true)
        File file = new File(TestTools.PULLED_PATH + "build.prop")
        assert file.exists()
    }

    @Test
    public void canPullListFromDevice() {

        String name = TestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        //removing the pulled files
        TestTools.recreatePulledDirectory()

        def listOfFiles = ["/system/build.prop": TestTools.PULLED_PATH, "/data/app/GestureBuilder.apk": TestTools.PULLED_PATH]
        gmtool.pullFromDevice(name, listOfFiles, true)

        File file = new File(TestTools.PULLED_PATH + "build.prop")
        assert file.exists()

        file = new File(TestTools.PULLED_PATH + "GestureBuilder.apk")
        assert file.exists()
    }


    @Test
    public void canFlashDevice() {

        String name = TestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        gmtool.flashDevice(name, "res/test/test.zip", true)
        boolean flashed = false
        gmtool.cmd(["tools/adb", "shell", "ls /system"], true) { line, count ->
            if (line.contains("touchdown")) {
                flashed = true
            }
        }
        assert flashed

    }

    @Test
    public void canFlashListToDevice() {

        String name = TestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        def listOfFiles = ["res/test/test.zip", "res/test/test2.zip"]
        gmtool.flashDevice(name, listOfFiles, true)

        int flashed = 0
        gmtool.cmd(["tools/adb", "shell", "ls /system"], true) { line, count ->
            if (line.contains("touchdown") || line.contains("touchdown2")) {
                flashed++
            }
        }
        assert flashed == listOfFiles.size()
    }

    @Test
    public void canHidePassword() {
        def command = ["ok", "nok", "--password=toto", "password=tutu", "--password=", "password="]
        def result = gmtool.cleanCommand(command)
        assert result == ["ok", "nok", "--password=toto", "password=*****", "--password=", "password="]
    }

    @Test(expected = TimeoutException)
    public void throwWhenProcessIsTooLongOnUnix() {
        if (Tools.getOSName().toLowerCase().contains("windows")) {
            throw new TimeoutException() //we avoid the test on windows
        }

        project.genymotion.config.processTimeout = 100
        project.genymotion.config.abortOnError = true
        gmtool.cmd("sleep 1", true, false)
    }

    @Test(expected = GMToolException)
    public void throwWhenProcessIsTooLongOnWindows() {
        if (!Tools.getOSName().toLowerCase().contains("windows")) {
            throw new GMToolException() //we pass the test only on windows
        }

        project.genymotion.config.processTimeout = 100
        project.genymotion.config.abortOnError = true
        //XXX: gmtool admin list is supposed to take more than 1 millisecond
        gmtool.cmd([gmtool.GMTOOL, "admin", "list"], true)
    }

    @Test
    public void doNotThrowWhenProcessIsTooLongOnUnix() {
        if (Tools.getOSName().toLowerCase().contains("windows")) {
            return //we avoid the test on windows
        }

        project.genymotion.config.processTimeout = 100
        project.genymotion.config.abortOnError = false
        gmtool.cmd("sleep 1", true, false)
    }

    @Test
    public void doNotThrowWhenProcessIsTooLongOnWindows() {
        if (!Tools.getOSName().toLowerCase().contains("windows")) {
            return //we pass the test only on windows
        }

        project.genymotion.config.processTimeout = 100
        project.genymotion.config.abortOnError = false
        gmtool.cmd([GMTool.GMTOOL, "admin", "list"], true)
    }

    @Test
    public void canHideSourceTag() {
        def command = ["ok", "nok", "--source=toto"]
        def result = GMTool.cleanCommand(command)
        assert result == ["ok", "nok"]
    }

    @Test
    public void canFormatCommand() {
        def command = [GMTool.GMTOOL, "nok"]

        gmtool.genymotionConfig.verbose = false
        def result = gmtool.formatAndLogCommand(command)
        assert result == [gmtool.genymotionConfig.genymotionPath + GMTool.GMTOOL, GMTool.SOURCE_GRADLE, "nok"]

        result = gmtool.formatAndLogCommand(command, true)
        assert result == [gmtool.genymotionConfig.genymotionPath + GMTool.GMTOOL, GMTool.VERBOSE, GMTool.SOURCE_GRADLE, "nok"]

        gmtool.genymotionConfig.verbose = true
        result = gmtool.formatAndLogCommand(command, false)
        assert result == [gmtool.genymotionConfig.genymotionPath + GMTool.GMTOOL, GMTool.VERBOSE, GMTool.SOURCE_GRADLE, "nok"]
    }

    @Test
    public void canGetVersion() {

        gmtool.metaClass.cmd = { def command, boolean verbose = false, boolean addPath = true, Closure c ->
            """
            Version  : 2.4.5
            Revision : 20150629-a7e4623
            """.eachLine { line, count ->
                c(line, count)
            }
        }
        assert gmtool.getVersion() == "2.4.5"
    }

    @Test
    public void canCheckCompatibility() {

        project.genymotion.config.version = "2.4.5"
        assert !gmtool.isCompatibleWith(GMTool.FEATURE_SOURCE_PARAM)

        project.genymotion.config.version = GMTool.FEATURE_SOURCE_PARAM
        assert gmtool.isCompatibleWith(GMTool.FEATURE_SOURCE_PARAM)
    }

    @Test
    public void canCheckSourceCompatibility() {

        project.genymotion.config.verbose = false

        project.genymotion.config.version = GMTool.FEATURE_SOURCE_PARAM
        assert gmtool.formatAndLogCommand(["gmtool", "version"], false, false) == ["gmtool", "--source=gradle", "version"]


        project.genymotion.config.version = "2.4.5"
        assert gmtool.formatAndLogCommand(["gmtool", "version"], false, false) == ["gmtool", "version"]
    }

    @Test(expected = GMToolException)
    public void canCheckLicenseServerCompatibility() {

        GenymotionConfig config = new GenymotionConfig()
        config.licenseServer = true

        project.genymotion.config.version = GMTool.FEATURE_ONSITE_LICENSE_CONFIG
        gmtool.setConfig(config) //should pass

        project.genymotion.config.version = "2.4.5"
        gmtool.setConfig(config) //should throw exception
    }

    @Test(expected = GMToolException)
    public void canCheckLicenseServerAddressCompatibility() {

        GenymotionConfig config = new GenymotionConfig()
        config.licenseServerAddress = "test"

        project.genymotion.config.version = GMTool.FEATURE_ONSITE_LICENSE_CONFIG
        gmtool.setConfig(config) //should pass

        project.genymotion.config.version = "2.4.5"
        gmtool.setConfig(config) //should throw exception
    }


    @After
    public void finishTest() {
        project.genymotion.config.processTimeout = 300000
        cleanMetaClass()

        if (genymotionPath != null) {
            project.genymotion.config.genymotionPath = genymotionPath
        }
        TestTools.cleanAfterTests(gmtool)
    }

}
