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

import com.genymotion.model.GenymotionVirtualDevice
import com.genymotion.tools.GMTool
import com.genymotion.tools.GMToolException
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.util.concurrent.TimeoutException

import static org.junit.Assert.fail

class GMToolTest {

    Project project
    def genymotionPath = null

    @BeforeClass
    public static void setUpClass() {
        TestTools.init()
        TestTools.setDefaultUser(true)
    }

    @Before
    public void setUp() {
        project = TestTools.init()
    }


    @Test
    public void isConfigOK() {
        def exitCode = GMTool.usage()
        assert exitCode == GMTool.RETURN_NO_ERROR
    }


    @Test
    public void checkGMToolNotFoundError() {
        this.genymotionPath = project.genymotion.config.genymotionPath
        project.genymotion.config.genymotionPath = "nowhere"
        project.genymotion.config.abortOnError = true

        try {
            GMTool.usage()
            fail("FileNotFoundException expected")
        } catch (Exception e) {
            assert "$GMTool.GENYMOTION_PATH_ERROR_MESSAGE Current value: $GMTool.GENYMOTION_CONFIG.genymotionPath" == e.message
        }
    }


    @Test
    public void isTemplatesAvailable() {

        def templates = GMTool.getTemplates(true)
        assert templates.size() > 0
        assert templates[0].name?.trim()
    }

    @Test
    public void canGetRunningDevices() {
        String name = TestTools.createADevice()

        GMTool.startDevice(name)
        def devices = GMTool.getRunningDevices(true, false, true)

        println "devices " + devices
        assert devices.contains(name)

        GMTool.stopDevice(name)

        GMTool.deleteDevice(name)
    }

    @Test
    public void canGetStoppedDevices() {
        String name = TestTools.createADevice()

        def runningDevices = GMTool.getRunningDevices(true, false, true)
        if (runningDevices.contains(name)) {
            GMTool.stopDevice(name)
        }
        def devices = GMTool.getStoppedDevices(true, false, true)

        assert devices.contains(name)

        GMTool.deleteDevice(name)
    }


    @Test
    public void canCreateDevice() {
        TestTools.createAllDevices()

        def devices = GMTool.getAllDevices(true)

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
        TestTools.deleteAllDevices()
    }

    @Test
    public void canDetailDevice() {

        String name = TestTools.createADevice()

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails(true)


        assert device.androidVersion != null
        assert device.state != null

        GMTool.deleteDevice(name)
    }

    @Test
    public void canListDevices() {

        TestTools.createAllDevices()

        def devices = GMTool.getAllDevices()
        assert devices.size() > 0


        TestTools.deleteAllDevices()
    }

    @Test
    public void canCloneDevice() {

        String name = TestTools.createADevice()

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails()

        def newName = name + "-clone"
        GMTool.cloneDevice(device, newName)

        GenymotionVirtualDevice newDevice = new GenymotionVirtualDevice(newName)
        newDevice.fillFromDetails()

        assert device.androidVersion == newDevice.androidVersion
        assert device.dpi == newDevice.dpi
        assert device.height == newDevice.height
        assert device.width == newDevice.width
        assert device.navbarVisible == newDevice.navbarVisible
        assert device.virtualKeyboard == newDevice.virtualKeyboard

        GMTool.deleteDevice(name)
        GMTool.deleteDevice(newName)
    }

    @Test
    public void canEditDevice() {

        String name = TestTools.createADevice()

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

        GMTool.editDevice(device)

        GenymotionVirtualDevice newDevice = new GenymotionVirtualDevice(name)
        newDevice.fillFromDetails()

        assert device.androidVersion == newDevice.androidVersion
        assert device.density == newDevice.density
        assert device.dpi == newDevice.dpi
        assert device.height == newDevice.height
        assert device.width == newDevice.width
        assert device.navbarVisible == newDevice.navbarVisible
        assert device.virtualKeyboard == newDevice.virtualKeyboard

        GMTool.deleteDevice(name)
    }

    @Test
    public void canStartDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name)

        assert exitCode == 0
    }

    @Test(expected = GMToolException.class)
    public void throwsWhenCommandError() {
        GMTool.GENYMOTION_CONFIG.abortOnError = true
        GMTool.getDevice("sqfqqfd", true)
    }

    @Test
    public void canStopDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name)

        if (exitCode == 0) {
            GMTool.stopDevice(name)
        }

        assert exitCode == 0
        assert !GMTool.isDeviceRunning(name)
    }


    @Test
    public void canStopAllDevices() {

        TestTools.createAllDevices()

        TestTools.DEVICES.each() {
            GMTool.startDevice(it.key)
        }

        GMTool.stopAllDevices()

        def runningDevices = GMTool.getRunningDevices(true, false, true)
        assert runningDevices == []

    }

    @Test
    public void canResetDevice() {
        //TODO
    }

    @Test
    public void canLogcatClear() {
        String name = TestTools.createADevice()
        def exitCode = GMTool.startDevice(name)
        assert exitCode == 0

        boolean gotIt = false
        String uniqueString = "GENYMOTION ROCKS DU PONEY " + System.currentTimeMillis()
        GMTool.cmd(["tools/adb", "shell", "log $uniqueString"], true)
        String path = TestTools.TEMP_PATH + "logcat.dump"
        File file = new File(path)
        file.delete()

        GMTool.logcatDump(name, path, true)

        file = new File(path)
        assert file.exists()
        file.eachLine {
            if (it.contains(uniqueString)) {
                gotIt = true
            }
        }
        assert gotIt
        file.delete()

        exitCode = GMTool.logcatClear(name, true)
        assert exitCode == 0

        gotIt = false
        GMTool.logcatDump(name, path)

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
        String name = TestTools.createADevice()
        def exitCode = GMTool.startDevice(name)
        assert exitCode == 0

        String uniqueString = "GENYMOTION ROCKS DU PONEY " + System.currentTimeMillis()
        GMTool.cmd(["tools/adb", "shell", "log $uniqueString"], true)

        String path = TestTools.TEMP_PATH + "logcat.dump"

        exitCode = GMTool.logcatDump(name, path, true)
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

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name)
        assert exitCode == 0

        GMTool.installToDevice(name, "res/test/test.apk", true)
        boolean installed = false
        GMTool.cmd(["tools/adb", "shell", "pm list packages"], true) { line, count ->
            if (line.contains("com.genymotion.test")) {
                installed = true
            }
        }
        assert installed
    }

    @Test
    public void canInstallListOfAppToDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assert exitCode == 0

        def listOfApps = ["res/test/test.apk", "res/test/test2.apk"]

        GMTool.installToDevice(name, listOfApps, true)

        int installed = 0
        GMTool.cmd(["tools/adb", "shell", "pm list packages"], true) { line, count ->
            if (line.contains("com.genymotion.test") || line.contains("com.genymotion.test2")) {
                installed++
            }
        }
        assert installed == listOfApps.size()
    }


    @Test
    public void canPushToDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assert exitCode == 0

        GMTool.pushToDevice(name, "res/test/test.txt", true)
        boolean pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) { line, count ->
            if (line.contains("test.txt")) {
                pushed = true
            }
        }
        assert pushed

    }

    @Test
    public void canPushListToDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assert exitCode == 0

        def listOfFiles = ["res/test/test.txt", "res/test/test2.txt"]
        GMTool.pushToDevice(name, listOfFiles, true)

        int pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) { line, count ->
            if (line.contains("test.txt") || line.contains("test2.txt")) {
                pushed++
            }
        }
        assert pushed == listOfFiles.size()

    }

    @Test
    public void canPushToDeviceWithDest() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assert exitCode == 0

        def destination = "/sdcard/"
        GMTool.pushToDevice(name, ["res/test/test.txt": destination], true)
        boolean pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true) { line, count ->
            if (line.contains("test.txt")) {
                pushed = true
            }
        }
        assert pushed
    }

    @Test
    public void canPushListToDeviceWithDest() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assert exitCode == 0

        def destination = "/sdcard/"
        def listOfFiles = ["res/test/test.txt": destination, "res/test/test2.txt": destination]
        GMTool.pushToDevice(name, listOfFiles, true)

        int pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true) { line, count ->
            if (line.contains("test.txt") || line.contains("test2.txt")) {
                pushed++
            }
        }
        assert pushed == listOfFiles.size()
    }


    @Test
    public void canPullFromDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assert exitCode == 0

        //removing the pulled files
        TestTools.recreatePulledDirectory()

        GMTool.pullFromDevice(name, "/system/build.prop", TestTools.PULLED_PATH, true)
        File file = new File(TestTools.PULLED_PATH + "build.prop")
        assert file.exists()
    }

    @Test
    public void canPullListFromDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assert exitCode == 0

        //removing the pulled files
        TestTools.recreatePulledDirectory()

        def listOfFiles = ["/system/build.prop": TestTools.PULLED_PATH, "/data/app/GestureBuilder.apk": TestTools.PULLED_PATH]
        GMTool.pullFromDevice(name, listOfFiles, true)

        File file = new File(TestTools.PULLED_PATH + "build.prop")
        assert file.exists()

        file = new File(TestTools.PULLED_PATH + "GestureBuilder.apk")
        assert file.exists()
    }


    @Test
    public void canFlashDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assert exitCode == 0

        GMTool.flashDevice(name, "res/test/test.zip", true)
        boolean flashed = false
        GMTool.cmd(["tools/adb", "shell", "ls /system"], true) { line, count ->
            if (line.contains("touchdown")) {
                flashed = true
            }
        }
        assert flashed

    }

    @Test
    public void canFlashListToDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assert exitCode == 0

        def listOfFiles = ["res/test/test.zip", "res/test/test2.zip"]
        GMTool.flashDevice(name, listOfFiles, true)

        int flashed = 0
        GMTool.cmd(["tools/adb", "shell", "ls /system"], true) { line, count ->
            if (line.contains("touchdown") || line.contains("touchdown2")) {
                flashed++
            }
        }
        assert flashed == listOfFiles.size()
    }

    @Test
    public void canHidePassword() {
        def command = ["ok", "nok", "--password=toto", "password=tutu", "--password=", "password="]
        def result = GMTool.cleanCommand(command)
        assert result == ["ok", "nok", "--password=toto", "password=*****", "--password=", "password="]
    }

    @Test(expected = TimeoutException)
    public void throwWhenProcessIsTooLong() {
        project.genymotion.config.processTimeout = 100
        project.genymotion.config.abortOnError = true
        GMTool.cmd("sleep 1", true, false)
    }

    @Test
    public void doNotThrowWhenProcessIsTooLong() {
        project.genymotion.config.processTimeout = 100
        project.genymotion.config.abortOnError = false
        GMTool.cmd("sleep 1", true, false)
    }

    @Test
    public void canHideSourceTag() {
        def command = ["ok", "nok", "--source=toto"]
        def result = GMTool.cleanCommand(command)
        assert result == ["ok", "nok"]
    }

    @Test
    public void canFormatCommand() {
        def command = [GMTool.GENYTOOL, "nok"]

        GMTool.GENYMOTION_CONFIG.verbose = false
        def result = GMTool.formatAndLogCommand(command)
        assert result == [GMTool.GENYMOTION_CONFIG.genymotionPath + GMTool.GENYTOOL, GMTool.SOURCE_GRADLE, "nok"]

        result = GMTool.formatAndLogCommand(command, true)
        assert result == [GMTool.GENYMOTION_CONFIG.genymotionPath + GMTool.GENYTOOL, GMTool.SOURCE_GRADLE, GMTool.VERBOSE, "nok"]

        GMTool.GENYMOTION_CONFIG.verbose = true
        result = GMTool.formatAndLogCommand(command, false)
        assert result == [GMTool.GENYMOTION_CONFIG.genymotionPath + GMTool.GENYTOOL, GMTool.SOURCE_GRADLE, GMTool.VERBOSE, "nok"]
    }


    @After
    public void finishTest() {
        if (genymotionPath != null) {
            project.genymotion.config.genymotionPath = genymotionPath
        }
        TestTools.cleanAfterTests()
    }

}
