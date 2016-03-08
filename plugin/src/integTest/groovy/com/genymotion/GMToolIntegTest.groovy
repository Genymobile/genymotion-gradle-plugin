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
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class GMToolIntegTest {

    Project project
    GMTool gmtool

    @BeforeClass
    public static void setUpClass() {
        IntegTestTools.init()
    }

    @Before
    public void setUp() {
        (project, gmtool) = IntegTestTools.init()
        IntegTestTools.setDefaultUser(true, gmtool)
    }

    @Test
    public void isConfigOK() {
        def exitCode = gmtool.usage()
        assert exitCode == com.genymotion.tools.GMToolDsl.RETURN_NO_ERROR
    }

    @Test
    public void isTemplatesAvailable() {

        def templates = gmtool.getTemplates(true)
        assert templates.size() > 0
        assert templates[0].name?.trim()
    }

    @Test
    public void canGetRunningDevices() {
        String name = IntegTestTools.createADevice(gmtool)

        gmtool.startDevice(name)
        def devices = gmtool.getRunningDevices(true, false, true)

        assert devices.contains(name)

        gmtool.stopDevice(name)

        gmtool.deleteDevice(name)
    }

    @Test
    public void canGetStoppedDevices() {
        String name = IntegTestTools.createADevice(gmtool)

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
        IntegTestTools.createAllDevices(gmtool)

        def devices = gmtool.getAllDevices(true)

        IntegTestTools.DEVICES.each() { key, value ->
            boolean exists = false
            devices.each() {
                if (it.name == key) {
                    exists = true
                    return
                }
            }
            assert exists

        }
        IntegTestTools.deleteAllDevices(gmtool)
    }

    @Test
    public void canDetailDevice() {

        String name = IntegTestTools.createADevice(gmtool)

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails(true)


        assert device.androidVersion != null
        assert device.state != null

        gmtool.deleteDevice(name)
    }

    @Test
    public void canListDevices() {

        IntegTestTools.createAllDevices(gmtool)

        def devices = gmtool.getAllDevices()
        assert devices.size() > 0


        IntegTestTools.deleteAllDevices(gmtool)
    }

    @Test
    public void canCloneDevice() {

        String name = IntegTestTools.createADevice(gmtool)

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

        String name = IntegTestTools.createADevice(gmtool)

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails()

        device.navbarVisible = false
        device.height = 600
        device.width = 800
        device.density = "hdpi"
        device.dpi = 240
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

        String name = IntegTestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name)

        assert exitCode == 0
    }

    @Test
    public void canStopDevice() {

        String name = IntegTestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name)

        if (exitCode == 0) {
            gmtool.stopDevice(name)
        }

        assert exitCode == 0
        assert !gmtool.isDeviceRunning(name)
    }

    @Test
    public void canResetDevice() {
        //TODO
    }

    @Test
    public void canLogcatClear() {
        String name = IntegTestTools.createADevice(gmtool)
        def exitCode = gmtool.startDevice(name)
        assert exitCode == 0

        boolean gotIt = false
        String uniqueString = "GENYMOTION ROCKS DU PONEY " + System.currentTimeMillis()
        gmtool.cmd(["tools/adb", "shell", "log $uniqueString"], true)
        String path = IntegTestTools.TEMP_PATH + "logcat.dump"
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
        String name = IntegTestTools.createADevice(gmtool)
        def exitCode = gmtool.startDevice(name)
        assert exitCode == 0

        String uniqueString = "GENYMOTION ROCKS DU PONEY " + System.currentTimeMillis()
        gmtool.cmd(["tools/adb", "shell", "log $uniqueString"], true)

        String path = IntegTestTools.TEMP_PATH + "logcat.dump"

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

        String name = IntegTestTools.createADevice(gmtool)

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
    public void canPushToDevice() {

        String name = IntegTestTools.createADevice(gmtool)

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
    public void canPushToDeviceWithDest() {

        String name = IntegTestTools.createADevice(gmtool)

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
    public void canPullFromDevice() {

        String name = IntegTestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        //removing the pulled files
        IntegTestTools.recreatePulledDirectory()

        gmtool.pullFromDevice(name, "/system/build.prop", IntegTestTools.PULLED_PATH, true)
        File file = new File(IntegTestTools.PULLED_PATH + "build.prop")
        assert file.exists()
    }

    @Test
    public void canFlashDevice() {

        String name = IntegTestTools.createADevice(gmtool)

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

    @After
    public void finishTest() {
        IntegTestTools.cleanAfterTests(gmtool)
    }

}
