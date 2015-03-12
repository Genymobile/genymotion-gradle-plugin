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

import com.genymotion.GenymotionPluginExtension
import com.genymotion.model.GenymotionConfig
import com.genymotion.model.GenymotionVirtualDevice
import com.genymotion.tasks.GenymotionFinishTask
import com.genymotion.tasks.GenymotionLaunchTask
import com.genymotion.tools.GMTool
import com.genymotion.tools.GMToolException
import org.gradle.api.Project
import org.junit.*

import static org.junit.Assert.*

class GenymotionGradlePluginTest {

    Project project

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
    public void canAddsTaskToProject() {
        assertTrue(project.tasks.genymotionLaunch instanceof GenymotionLaunchTask)
        assertTrue(project.tasks.genymotionFinish instanceof GenymotionFinishTask)
    }

    @Test
    public void canAddExtensionToProject() {
        assertTrue(project.genymotion instanceof GenymotionPluginExtension)
        assertTrue(project.genymotion.config instanceof GenymotionConfig)
        assertTrue(project.genymotion.devices instanceof List)
    }

    @Test
    public void canConfigGenymotion() {
        String path = "TEST"+File.separator
        String previousPath = project.genymotion.config.genymotionPath
        project.genymotion.config.genymotionPath = path

        assert path == project.genymotion.config.genymotionPath

        project.genymotion.config.genymotionPath = previousPath
    }

    @Test
    public void canFixGenymotionPath() {

        project = TestTools.init()
        String defaultPath = project.genymotion.config.genymotionPath

        String path = "/path/to/test"
        project.genymotion.config.genymotionPath = path

        assert path+File.separator == project.genymotion.config.genymotionPath

        TestTools.setDefaultGenymotionPath(project, defaultPath)
    }

    @Test
    public void canAddNoDevice() {

        project.genymotion.devices{}
        assertEquals(0, project.genymotion.devices.size())
    }

    @Test(expected = GMToolException.class)
    public void throwsWhenAddDeviceWithoutNameAndTemplate() {

        project.genymotion.devices {
            "test" {pullAfter "buenos dias"}
        }
        project.genymotion.checkParams()
    }

    @Test(expected = GMToolException.class)
    public void throwsWhenAddDeviceWithNameNotCreated() {

        project.genymotion.devices {
            "DSFGTFSHgfgdfTFGQFQHG"{}
        }
        project.genymotion.checkParams()
    }

    @Test(expected = GMToolException.class)
    public void throwsWhenAddDeviceWithTemplateNotCreated() {

        project.genymotion.devices {
            "test" {
                template "DSFGTFSHgfgdfTFGQFQHG"
            }
        }
        project.genymotion.checkParams()
    }

    @Test(expected = GMToolException.class)
    public void throwsWhenAddDeviceWithNameAndTemplateNotCreated() {

        project.genymotion.devices {
            "DSFGTFSHTFGQFQHG" {
                template "ferrfgfgdshghGFGDFGfgfd"
            }
        }
        project.genymotion.checkParams()
    }

    @Test
    public void canAddDeviceToLaunchByName() {

        String vdName = TestTools.createADevice()

        project.genymotion.devices {
            "$vdName" {}
        }
        assertNull(project.genymotion.devices[0].template)
        assertEquals(vdName, project.genymotion.devices[0].name)

        GMTool.deleteDevice(vdName)
    }

    @Test
    public void canAddDeviceToLaunchByNameWithTemplate() {

        String vdName = TestTools.createADevice()
        String templateName = "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"

        project.genymotion.devices {
            "$vdName" {
                template templateName
            }
        }
        assertEquals(templateName, project.genymotion.devices[0].template)
        assertEquals(vdName, project.genymotion.devices[0].name)

        GMTool.deleteDevice(vdName)
    }


    @Test
    public void canAddDeviceToLaunchByNameWithTemplateNotCreated() {

        String vdName = TestTools.createADevice()

        project.genymotion.devices {
            "$vdName" {
                template "frtfgfdgtgsgrGFGFDGFD"
            }
        }
        project.genymotion.checkParams()

        assertFalse(project.genymotion.devices[0].templateExists)
        assertEquals(vdName, project.genymotion.devices[0].name)

        GMTool.deleteDevice(vdName)
    }

    @Test
    public void canAddDeviceToLaunchByTemplateWithNameNotCreated() {

        project.genymotion.devices {
            "dfsdgffgdgqsdg"{
                template "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"
            }
        }

        project.genymotion.checkParams()

        assertNotNull("No device found", project.genymotion.devices[0])
        assertNotNull("Device not filled", project.genymotion.devices[0].name)
        assertTrue("Device not created", project.genymotion.devices[0].create)
        assertNull(project.genymotion.devices[0].deleteWhenFinish)
    }

    @Test
    public void canAvoidDeviceToBeLaunched() {

        project.genymotion.devices {
            "test" {
                template "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"
                start false
            }
        }

        assertFalse(project.genymotion.devices[0].start)
    }

    @Test
    public void canEditDeviceBeforeLaunch() {

        String vdName = "OKOK-junit"
        def devices = GMTool.getAllDevices(true, false, true)
        if(devices.contains(vdName))
            GMTool.deleteDevice(vdName)

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

        assertNotNull(project.genymotion.devices[0])
        assertEquals(project.genymotion.devices[0].name, vdName)

        project.genymotion.devices[0].create()
        project.genymotion.devices[0].checkAndEdit()

        GenymotionVirtualDevice device = GMTool.getDevice(vdName, true)
        assertEquals(densityValue, device.density)
        assertEquals(intValue, device.width)
        assertEquals(intValue, device.height)
        assertFalse(device.virtualKeyboard)
        assertFalse(device.navbarVisible)
        assertEquals(1, device.nbCpu)
        assertEquals(2048, device.ram)

        GMTool.deleteDevice(vdName)
    }


    @Test
    public void canLogcat() {
        String vdName = TestTools.createADevice()

        String path = TestTools.TEMP_PATH+vdName+".logcat"

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
        String vdName = TestTools.createADevice()

        String path = TestTools.TEMP_PATH+vdName+".logcat"

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
        GMTool.cmd(["tools/adb", "shell", "log $uniqueString"], true)

        project.tasks.genymotionFinish.exec()

        //we reach the file created
        File file = new File(path)

        boolean clearedAfterBoot = true
        boolean logcatDumped = false

        file.eachLine {
            if (it.contains(">>>>>> AndroidRuntime START com.android.internal.os.ZygoteInit <<<<<<"))
                clearedAfterBoot = false
            if (it.contains(uniqueString))
                logcatDumped = true
        }
        [clearedAfterBoot, logcatDumped]
    }

    @Test
    public void canSetDeleteWhenFinish() {
        String vdName = TestTools.createADevice()

        project.genymotion.devices {
            "$vdName" {
                deleteWhenFinish true
            }
        }
        project.tasks.genymotionLaunch.exec()
        project.tasks.genymotionFinish.exec()

        assertFalse("The device still exists", GMTool.isDeviceCreated(vdName, true))
    }

    @Test
    public void canAvoidDeleteWhenFinish() {
        String vdName = TestTools.createADevice()

        project.genymotion.devices {
            "$vdName" {
                deleteWhenFinish false
            }
        }
        project.tasks.genymotionLaunch.exec()
        project.tasks.genymotionFinish.exec()

        assertTrue("The device has been deleted, should still be listed", GMTool.isDeviceCreated(vdName, true))
    }


    @Test
    public void canInstallToDevice() {

        String vdName = TestTools.createADevice()

        project.genymotion.devices {
            "$vdName" {
                install "res/test/test.apk"
            }
        }
        project.tasks.genymotionLaunch.exec()

        boolean installed = false
        GMTool.cmd(["tools/adb", "shell", "pm list packages"], true) {line, count ->
            if(line.contains("com.genymotion.test"))
                installed = true
        }
        assertTrue("Install failed", installed)
    }

    @Test
    public void canInstallListOfAppToDevice() {

        String name = TestTools.createADevice()

        def listOfApps = ["res/test/test.apk", "res/test/test2.apk"]
        project.genymotion.devices {
            "$name" {
                install listOfApps
            }
        }
        project.tasks.genymotionLaunch.exec()

        int installed = 0
        GMTool.cmd(["tools/adb", "shell", "pm list packages"], true) {line, count ->
            if(line.contains("com.genymotion.test") || line.contains("com.genymotion.test2"))
                installed++
        }
        assertEquals("All apps are not found", listOfApps.size(), installed)
    }



    @Test
    public void canPushBeforeToDevice() {

        String name = TestTools.createADevice()

        project.genymotion.devices{
            "$name" {
                pushBefore "res/test/test.txt"
            }
        }
        project.tasks.genymotionLaunch.exec()

        boolean pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) {line, count ->
            if(line.contains("test.txt"))
                pushed = true
        }
        assertTrue("Push failed", pushed)
    }

    @Test
    public void canPushAfterToDevice() {

        String name = TestTools.createADevice()

        project.genymotion.devices {
            "$name" {
                pushAfter "res/test/test.txt"
                stopWhenFinish false
            }
        }
        project.tasks.genymotionLaunch.exec()

        boolean pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) {line, count ->
            if(line.contains("test.txt"))
                pushed = true
        }
        assertFalse("Push happened but should not happen", pushed)

        project.tasks.genymotionFinish.exec()

        pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) {line, count ->
            if(line.contains("test.txt"))
                pushed = true
        }
        assertTrue("Push failed", pushed)
    }

    @Test
    public void canPushBeforeListToDevice() {

        String name = TestTools.createADevice()

        def listOfFiles = ["res/test/test.txt", "res/test/test2.txt"]
        project.genymotion.devices {
            "$name" {
                pushBefore listOfFiles
            }
        }
        project.tasks.genymotionLaunch.exec()

        int pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) {line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("One or all pushed files are missing", listOfFiles.size(), pushed)
    }

    @Test
    public void canPushAfterListToDevice() {

        String name = TestTools.createADevice()

        def listOfFiles = ["res/test/test.txt", "res/test/test2.txt"]
        project.genymotion.devices {
            "$name" {
                pushAfter listOfFiles
                stopWhenFinish false
            }
        }
        project.tasks.genymotionLaunch.exec()

        int pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) {line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("Pushed files, it should not happen", 0, pushed)

        project.tasks.genymotionFinish.exec()

        pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) {line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("One or all pushed files are missing", listOfFiles.size(), pushed)
    }

    @Test
    public void canPushBeforeToDeviceWithDest() {

        String name = TestTools.createADevice()

        def destination = "/sdcard/"
        def listOfFiles = ["res/test/test.txt":destination]
        project.genymotion.devices {
            "$name" {
                pushBefore listOfFiles
            }
        }
        project.tasks.genymotionLaunch.exec()

        boolean pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true) {line, count ->
            if(line.contains("test.txt"))
                pushed = true
        }
        assertTrue("Push failed", pushed)
    }

    @Test
    public void canPushAfterToDeviceWithDest() {

        String name = TestTools.createADevice()

        def destination = "/sdcard/"
        def listOfFiles = ["res/test/test.txt":destination]
        project.genymotion.devices {
            "$name" {
                pushAfter listOfFiles
                stopWhenFinish false
            }
        }
        project.tasks.genymotionLaunch.exec()

        boolean pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true) {line, count ->
            if(line.contains("test.txt"))
                pushed = true
        }
        assertFalse("Pushed done. Should not happen", pushed)

        project.tasks.genymotionFinish.exec()

        pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true) {line, count ->
            if(line.contains("test.txt"))
                pushed = true
        }
        assertTrue("Push failed", pushed)
    }

    @Test
    public void canPushBeforeListToDeviceWithDest() {
        String name = TestTools.createADevice()

        def destination = "/sdcard/"
        def listOfFiles = ["res/test/test.txt":destination, "res/test/test2.txt":destination]
        project.genymotion.devices {
            "$name" {
                pushBefore listOfFiles
            }
        }
        project.tasks.genymotionLaunch.exec()

        int pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true) {line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("One or all pushed files are missing", listOfFiles.size(), pushed)
    }

    @Test
    public void canPushAfterListToDeviceWithDest() {
        String name = TestTools.createADevice()

        def destination = "/sdcard/"
        def listOfFiles = ["res/test/test.txt":destination, "res/test/test2.txt":destination]
        project.genymotion.devices {
            "$name" {
                pushAfter listOfFiles
                stopWhenFinish false
            }
        }
        project.tasks.genymotionLaunch.exec()


        int pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true) {line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("Pushed done. Should not happen", 0, pushed)

        project.tasks.genymotionFinish.exec()

        pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true) {line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("One or all pushed files are missing", listOfFiles.size(), pushed)

    }

    @Test
    public void canPullBeforeFromDevice() {
        String name = TestTools.createADevice()

        //removing the pulled files
        TestTools.recreatePulledDirectory()

        project.genymotion.devices {
            "$name" {
                pullBefore "/system/build.prop":TestTools.PULLED_PATH
            }
        }
        project.tasks.genymotionLaunch.exec()

        File file = new File(TestTools.PULLED_PATH+"build.prop")
        assertTrue("Pulled file not found", file.exists())
    }

    @Test
    public void canPullAfterFromDevice() {
        String name = TestTools.createADevice()

        //removing the pulled files
        TestTools.recreatePulledDirectory()

        project.genymotion.devices {
            "$name" {
                pullAfter "/system/build.prop":TestTools.PULLED_PATH
                stopWhenFinish false
            }
        }
        project.tasks.genymotionLaunch.exec()

        File file = new File(TestTools.PULLED_PATH+"build.prop")
        assertFalse("Pulled file found. Should not happen", file.exists())

        project.tasks.genymotionFinish.exec()

        file = new File(TestTools.PULLED_PATH+"build.prop")
        assertTrue("Pulled file not found", file.exists())
    }

    @Test
    public void canPullBeforeListToDevice() {
        String name = TestTools.createADevice()

        //removing the pulled files
        TestTools.recreatePulledDirectory()

        def listOfFiles = ["/system/build.prop":TestTools.PULLED_PATH+"build.prop", "/system/bin/adb":TestTools.PULLED_PATH+"adb"]
        project.genymotion.devices {
            "$name" {
                pullBefore listOfFiles
            }
        }
        project.tasks.genymotionLaunch.exec()

        int pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true) {line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }

        listOfFiles.each {key, value ->
            File file = new File(value)
            assertTrue("Pulled file not found", file.exists())
        }
    }

    @Test
    public void canPullAfterListToDevice() {
        String name = TestTools.createADevice()

        //removing the pulled files
        TestTools.recreatePulledDirectory()

        def listOfFiles = ["/system/build.prop":TestTools.PULLED_PATH+"build.prop", "/system/bin/adb":TestTools.PULLED_PATH+"adb"]
        project.genymotion.devices {
            "$name" {
                pullAfter listOfFiles
                stopWhenFinish false
            }
        }
        project.tasks.genymotionLaunch.exec()

        listOfFiles.each {key, value ->
            File file = new File(value)
            assertFalse("Pulled file found. Should not happen", file.exists())
        }

        project.tasks.genymotionFinish.exec()

        listOfFiles.each {key, value ->
            File file = new File(value)
            assertTrue("Pulled file not found", file.exists())
        }
    }


    @Test
    public void canFlashDevice() {
        String name = TestTools.createADevice()

        project.genymotion.devices {
            "$name" {
                flash "res/test/test.zip"
            }
        }
        project.tasks.genymotionLaunch.exec()

        boolean flashed = false
        GMTool.cmd(["tools/adb", "shell", "ls /system"], true) {line, count ->
            if(line.contains("touchdown"))
                flashed = true
        }
        assertTrue("Flash failed", flashed)

    }

    @Test
    public void canFlashListToDevice() {
        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assertTrue("Start failed", exitCode == 0)

        def listOfFiles = ["res/test/test.zip", "res/test/test2.zip"]
        project.genymotion.devices {
            "$name" {
                flash listOfFiles
            }
        }
        project.tasks.genymotionLaunch.exec()

        int flashed = 0
        GMTool.cmd(["tools/adb", "shell", "ls /system"], true) {line, count ->
            if(line.contains("touchdown") || line.contains("touchdown2"))
                flashed++
        }
        assertEquals("All flashed files are not found", listOfFiles.size(), flashed)
    }

    @After
    public void finishTest() {
        TestTools.cleanAfterTests()
    }
}
