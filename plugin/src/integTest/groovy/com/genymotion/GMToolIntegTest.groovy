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
import com.genymotion.model.NetworkInfo
import com.genymotion.tools.GMTool
import com.genymotion.tools.GMToolDsl
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
        IntegrationTestTools.init()
    }

    @Before
    public void setUp() {
        (project, gmtool) = IntegrationTestTools.init()
        IntegrationTestTools.setDefaultUser(true, gmtool)
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
        String name = IntegrationTestTools.createADevice(gmtool)

        gmtool.startDevice(name)
        def devices = gmtool.getRunningDevices(true, false, true)

        assert devices.contains(name)

        gmtool.stopDevice(name)

        gmtool.deleteDevice(name)
    }

    @Test
    public void canGetStoppedDevices() {
        String name = IntegrationTestTools.createADevice(gmtool)

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
        IntegrationTestTools.createAllDevices(gmtool)

        def devices = gmtool.getAllDevices(true)

        IntegrationTestTools.DEVICES.each() { key, value ->
            boolean exists = false
            devices.each() {
                if (it.name == key) {
                    exists = true
                    return
                }
            }
            assert exists

        }
        IntegrationTestTools.deleteAllDevices(gmtool)
    }

    @Test
    public void canDetailDevice() {

        String name = IntegrationTestTools.createADevice(gmtool)

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails(true)


        assert device.androidVersion != null
        assert device.state != null

        gmtool.deleteDevice(name)
    }

    @Test
    public void canListDevices() {

        IntegrationTestTools.createAllDevices(gmtool)

        def devices = gmtool.getAllDevices()
        assert devices.size() > 0


        IntegrationTestTools.deleteAllDevices(gmtool)
    }

    @Test
    public void canCloneDevice() {
        String name = IntegrationTestTools.createADevice(gmtool)

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
        assert device.networkInfo.mode.equals(newDevice.networkInfo.mode)
        assert device.networkInfo.bridgeInterface.equals(newDevice.networkInfo.bridgeInterface)

        gmtool.deleteDevice(name)
        gmtool.deleteDevice(newName)
    }

    @Test
    public void canEditDevice() {
        String name = IntegrationTestTools.createADevice(gmtool)

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
        device.networkInfo = NetworkInfo.createBridgeNetworkInfo("eth0")

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
        assert device.networkInfo.mode.equals(GMToolDsl.BRIDGE_MODE)
        assert device.networkInfo.bridgeInterface.equals("eth0")

        gmtool.deleteDevice(name)
    }

    @Test
    public void canStartDevice() {
        String name = IntegrationTestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name)

        assert exitCode == 0
    }

    @Test
    public void canStopDevice() {

        String name = IntegrationTestTools.createADevice(gmtool)

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
        String name = IntegrationTestTools.createADevice(gmtool)
        def exitCode = gmtool.startDevice(name)
        assert exitCode == 0

        GenymotionVirtualDevice device = gmtool.getDevice(name)

        boolean gotIt = false
        String uniqueString = "GENYMOTION ROCKS DU PONEY " + System.currentTimeMillis()
        gmtool.cmd(["tools/adb", "-s", "$device.ip:5555", "shell", "log $uniqueString"], true)
        String path = IntegrationTestTools.TEMP_PATH + "logcat.dump"
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
        String name = IntegrationTestTools.createADevice(gmtool)
        def exitCode = gmtool.startDevice(name)
        assert exitCode == 0

        GenymotionVirtualDevice device = gmtool.getDevice(name)

        String uniqueString = "GENYMOTION ROCKS DU PONEY " + System.currentTimeMillis()
        gmtool.cmd(["tools/adb", "-s", "$device.ip:5555", "shell", "log $uniqueString"], true)

        String path = IntegrationTestTools.TEMP_PATH + "logcat.dump"

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

        String name = IntegrationTestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name)
        assert exitCode == 0

        gmtool.installToDevice(name, "src/integTest/res/test/test.apk", true)

        GenymotionVirtualDevice device = gmtool.getDevice(name)

        boolean installed = false
        gmtool.cmd(["tools/adb", "-s", "$device.ip:5555", "shell", "pm list packages"], true) { line, count ->
            if (line.contains("com.genymotion.test")) {
                installed = true
            }
        }
        assert installed
    }

    @Test
    public void canPushToDevice() {

        String name = IntegrationTestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        gmtool.pushToDevice(name, "src/integTest/res/test/test.txt", true)

        GenymotionVirtualDevice device = gmtool.getDevice(name)

        boolean pushed = false
        gmtool.cmd(["tools/adb", "-s", "$device.ip:5555", "shell", "ls /sdcard/Download/"], true) { line, count ->
            if (line.contains("test.txt")) {
                pushed = true
            }
        }
        assert pushed
    }

    @Test
    public void canPushToDeviceWithDest() {

        String name = IntegrationTestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        def destination = "/sdcard/"
        gmtool.pushToDevice(name, ["src/integTest/res/test/test.txt": destination], true)

        GenymotionVirtualDevice device = gmtool.getDevice(name)

        boolean pushed = false
        gmtool.cmd(["tools/adb", "-s", "$device.ip:5555", "shell", "ls", destination], true) { line, count ->
            if (line.contains("test.txt")) {
                pushed = true
            }
        }
        assert pushed
    }

    @Test
    public void canPullFromDevice() {

        String name = IntegrationTestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        //removing the pulled files
        IntegrationTestTools.recreatePulledDirectory()

        gmtool.pullFromDevice(name, "/system/build.prop", IntegrationTestTools.PULLED_PATH, true)
        File file = new File(IntegrationTestTools.PULLED_PATH + "build.prop")
        assert file.exists()
    }

    @Test
    public void canFlashDevice() {

        String name = IntegrationTestTools.createADevice(gmtool)

        def exitCode = gmtool.startDevice(name, true)
        assert exitCode == 0

        gmtool.flashDevice(name, "src/integTest/res/test/test.zip", true)

        GenymotionVirtualDevice device = gmtool.getDevice(name)

        boolean flashed = false
        gmtool.cmd(["tools/adb", "-s", "$device.ip:5555", "shell", "ls /system"], true) { line, count ->
            if (line.contains("touchdown")) {
                flashed = true
            }
        }
        assert flashed
    }

    @After
    public void finishTest() {
        IntegrationTestTools.cleanAfterTests(gmtool)
    }
}
