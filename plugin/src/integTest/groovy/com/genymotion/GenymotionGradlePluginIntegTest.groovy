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
import com.genymotion.tools.Log
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.Test

class GenymotionGradlePluginIntegTest {

    Project project
    GMTool gmtool

    @Before
    public void setUp() {
        (project, gmtool) = IntegTestTools.init()
        IntegTestTools.setDefaultUser(true, gmtool)
    }

    @Test
    public void isGenymotionConfigBootstraped() {
        assert project.genymotion.config.version != "1.0"
    }

    @Test
    public void canAddDeviceToLaunchByNameWithTemplateNotCreated() {

        String vdName = IntegTestTools.createADevice(gmtool)

        project.genymotion.devices {
            "$vdName" {
                template "frtfgfdgtgsgrGFGFDGFD"
            }
        }
        project.genymotion.checkParams()

        assert !project.genymotion.devices[0].templateExists
        assert project.genymotion.devices[0].name == vdName
    }

    @Test
    public void canEditDeviceBeforeLaunch() {

        String vdName = "OKOK-junit"
        def devices = gmtool.getAllDevices(true, false, true)
        if (devices.contains(vdName)) {
            gmtool.deleteDevice(vdName)
        }

        int intValue = 999
        String densityValue = "mdpi"

        project.genymotion.devices {
            "$vdName" {
                template "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"
                density densityValue
                width intValue
                height intValue
                virtualKeyboard false
                navbarVisible false
                nbCpu 1
                ram 2048
            }
        }

        assert project.genymotion.devices[0] != null
        assert project.genymotion.devices[0].name == vdName

        project.genymotion.devices[0].create()
        project.genymotion.devices[0].checkAndEdit()

        GenymotionVirtualDevice device = gmtool.getDevice(vdName, true)
        assert densityValue == device.density
        assert intValue == device.width
        assert intValue == device.height
        assert !device.virtualKeyboard
        assert !device.navbarVisible
        assert 1 == device.nbCpu
        assert 2048 == device.ram
    }


    @Test
    public void canLogcat() {
        String vdName = IntegTestTools.createADevice(gmtool)

        String path = IntegTestTools.TEMP_PATH + vdName + ".logcat"

        project.genymotion.devices {
            "$vdName" {
                logcat path
            }
        }

        def (boolean clearedAfterBoot, boolean logcatDumped) = runAndCheckLogcat(path)

        assert clearedAfterBoot
        assert logcatDumped

    }

    @Test
    public void canLogcatAndAvoidLogcatClearAfterBoot() {
        String vdName = IntegTestTools.createADevice(gmtool)

        String path = IntegTestTools.TEMP_PATH + vdName + ".logcat"

        project.genymotion.devices {
            "$vdName" {
                logcat path
                clearLogAfterBoot false
            }
        }

        def (boolean clearedAfterBoot, boolean logcatDumped) = runAndCheckLogcat(path)

        assert !clearedAfterBoot
        assert logcatDumped

    }

    public List runAndCheckLogcat(String path) {
        project.evaluate()
        project.tasks.genymotionLaunch.exec()

        //we add a line into logcat
        String uniqueString = "GENYMOTION ROCKS DU PONEY " + System.currentTimeMillis()
        gmtool.cmd(["tools/adb", "shell", "log $uniqueString"], true)

        project.tasks.genymotionFinish.exec()

        //we reach the file created
        File file = new File(path)

        boolean clearedAfterBoot = true
        boolean logcatDumped = false

        file.eachLine {
            if (it.contains(">>>>>> AndroidRuntime START com.android.internal.os.ZygoteInit <<<<<<")) {
                clearedAfterBoot = false
            }
            if (it.contains(uniqueString)) {
                logcatDumped = true
            }
        }
        [clearedAfterBoot, logcatDumped]
    }

    @Test
    public void canSetDeleteWhenFinish() {
        String vdName = IntegTestTools.createADevice(gmtool)

        project.genymotion.devices {
            "$vdName" {
                deleteWhenFinish true
            }
        }
        project.tasks.genymotionLaunch.exec()
        project.tasks.genymotionFinish.exec()

        assert !gmtool.isDeviceCreated(vdName, true)
    }

    @Test
    public void canAvoidDeleteWhenFinish() {
        String vdName = IntegTestTools.createADevice(gmtool)

        project.genymotion.devices {
            "$vdName" {
                deleteWhenFinish false
            }
        }
        project.tasks.genymotionLaunch.exec()
        project.tasks.genymotionFinish.exec()

        assert gmtool.isDeviceCreated(vdName, true)
    }


    @Test
    public void canInstallToDevice() {

        String vdName = IntegTestTools.createADevice(gmtool)

        project.genymotion.devices {
            "$vdName" {
                install "res/test/test.apk"
            }
        }
        project.tasks.genymotionLaunch.exec()

        boolean installed = false
        gmtool.cmd(["tools/adb", "shell", "pm list packages"], true) { line, count ->
            if (line.contains("com.genymotion.test")) {
                installed = true
            }
        }
        assert installed
    }

    @Test
    public void canPushBeforeToDevice() {

        String name = IntegTestTools.createADevice(gmtool)

        project.genymotion.devices {
            "$name" {
                pushBefore "res/test/test.txt"
            }
        }
        project.tasks.genymotionLaunch.exec()

        boolean pushed = false
        gmtool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) { line, count ->
            if (line.contains("test.txt")) {
                pushed = true
            }
        }
        assert pushed
    }

    @Test
    public void canPushAfterToDevice() {

        String name = IntegTestTools.createADevice(gmtool)

        project.genymotion.devices {
            "$name" {
                pushAfter "res/test/test.txt"
                stopWhenFinish false
            }
        }
        project.tasks.genymotionLaunch.exec()

        boolean pushed = false
        gmtool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) { line, count ->
            if (line.contains("test.txt")) {
                pushed = true
            }
        }
        assert !pushed

        project.tasks.genymotionFinish.exec()

        pushed = false
        gmtool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) { line, count ->
            if (line.contains("test.txt")) {
                pushed = true
            }
        }
        assert pushed
    }

    @Test
    public void canPushBeforeToDeviceWithDest() {

        String name = IntegTestTools.createADevice(gmtool)

        def destination = "/sdcard/"
        def listOfFiles = ["res/test/test.txt": destination]
        project.genymotion.devices {
            "$name" {
                pushBefore listOfFiles
            }
        }
        project.tasks.genymotionLaunch.exec()

        boolean pushed = false
        gmtool.cmd(["tools/adb", "shell", "ls", destination], true) { line, count ->
            if (line.contains("test.txt")) {
                pushed = true
            }
        }
        assert pushed
    }

    @Test
    public void canPushAfterToDeviceWithDest() {

        String name = IntegTestTools.createADevice(gmtool)

        def destination = "/sdcard/"
        def listOfFiles = ["res/test/test.txt": destination]
        project.genymotion.devices {
            "$name" {
                pushAfter listOfFiles
                stopWhenFinish false
            }
        }
        project.tasks.genymotionLaunch.exec()

        boolean pushed = false
        gmtool.cmd(["tools/adb", "shell", "ls", destination], true) { line, count ->
            if (line.contains("test.txt")) {
                pushed = true
            }
        }
        assert !pushed

        project.tasks.genymotionFinish.exec()

        pushed = false
        gmtool.cmd(["tools/adb", "shell", "ls", destination], true) { line, count ->
            if (line.contains("test.txt")) {
                pushed = true
            }
        }
        assert pushed
    }

    @Test
    public void canPullBeforeFromDevice() {
        String name = IntegTestTools.createADevice(gmtool)

        //removing the pulled files
        IntegTestTools.recreatePulledDirectory()

        project.genymotion.devices {
            "$name" {
                pullBefore "/system/build.prop": IntegTestTools.PULLED_PATH
            }
        }
        project.tasks.genymotionLaunch.exec()

        File file = new File(IntegTestTools.PULLED_PATH + "build.prop")
        assert file.exists()
    }

    @Test
    public void canPullAfterFromDevice() {
        String name = IntegTestTools.createADevice(gmtool)

        //removing the pulled files
        IntegTestTools.recreatePulledDirectory()

        project.genymotion.devices {
            "$name" {
                pullAfter "/system/build.prop": IntegTestTools.PULLED_PATH
                stopWhenFinish false
            }
        }
        project.tasks.genymotionLaunch.exec()

        File file = new File(IntegTestTools.PULLED_PATH + "build.prop")
        assert !file.exists()

        project.tasks.genymotionFinish.exec()

        file = new File(IntegTestTools.PULLED_PATH + "build.prop")
        assert file.exists()
    }

    @Test
    public void canFlashDevice() {
        String name = IntegTestTools.createADevice(gmtool)

        project.genymotion.devices {
            "$name" {
                flash "res/test/test.zip"
            }
        }
        project.tasks.genymotionLaunch.exec()

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

        Log.clearLogger()
        IntegTestTools.cleanAfterTests(gmtool)
    }
}
