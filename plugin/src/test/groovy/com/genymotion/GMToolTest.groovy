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
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenymotionGradlePlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package test.groovy.com.genymotion

import main.groovy.com.genymotion.tools.GMToolException
import main.groovy.com.genymotion.tools.GMTool
import main.groovy.com.genymotion.model.GenymotionVirtualDevice
import org.junit.After
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.junit.Before
import org.gradle.api.Project

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class GMToolTest {

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
    public void isConfigOK() {
        def exitCode = GMTool.usage()
        assertTrue("Genymotion not accessible, check the GENYMOTION_PATH variable", exitCode == GMTool.RETURN_NO_ERROR)
    }


    @Test
    public void isTemplatesAvailable() {

        def templates = GMTool.getTemplates(true)
        assertTrue("No template found", templates.size() > 0)
        assertTrue("Empty template", (templates[0].name?.trim()) as boolean)
    }

    @Test
    public void canGetRunningDevices() {
        String name = TestTools.createADevice()

        GMTool.startDevice(name)
        def devices = GMTool.getRunningDevices(true, false, true)

        println  "devices " + devices
        assertTrue("Error, device not running", devices.contains(name))

        GMTool.stopDevice(name)

        GMTool.deleteDevice(name)
    }

    @Test
    public void canGetStoppedDevices() {
        String name = TestTools.createADevice()

        def runningDevices = GMTool.getRunningDevices(true, false, true)
        if(runningDevices.contains(name))
            GMTool.stopDevice(name)
        def devices = GMTool.getStoppedDevices(true, false, true)

        assertTrue("Error, device not stopped", devices.contains(name))

        GMTool.deleteDevice(name)
    }


    @Test
    public void canCreateDevice() {
        TestTools.createAllDevices()

        def devices = GMTool.getAllDevices(true)

        TestTools.DEVICES.each() { key, value ->
            boolean exists = false
            devices.each() {
                if(it.name == key){
                    exists = true
                    return
                }
            }
            assertTrue("${key} not found. Test failed", exists)

        }
        TestTools.deleteAllDevices()
    }

    @Test
    public void canDetailDevice() {

        String name = TestTools.createADevice()

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails(true)


        assertNotNull(device.androidVersion)
        assertNotNull(device.state)

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

        def newName = name+"-clone"
        GMTool.cloneDevice(device, newName)

        GenymotionVirtualDevice newDevice = new GenymotionVirtualDevice(newName)
        newDevice.fillFromDetails()

        assertEquals(device.androidVersion, newDevice.androidVersion)
        assertEquals(device.dpi, newDevice.dpi)
        assertEquals(device.height, newDevice.height)
        assertEquals(device.width, newDevice.width)
        assertEquals(device.navbarVisible, newDevice.navbarVisible)
        assertEquals(device.virtualKeyboard, newDevice.virtualKeyboard)

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

        assertEquals(device.androidVersion, newDevice.androidVersion)
        assertEquals(device.density, newDevice.density)
        assertEquals(device.dpi, newDevice.dpi)
        assertEquals(device.height, newDevice.height)
        assertEquals(device.width, newDevice.width)
        assertEquals(device.navbarVisible, newDevice.navbarVisible)
        assertEquals(device.virtualKeyboard, newDevice.virtualKeyboard)

        GMTool.deleteDevice(name)
    }

    @Test
    public void canStartDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name)

        assertTrue("Start failed", exitCode == 0)
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

        if(exitCode == 0)
            GMTool.stopDevice(name)

        assertTrue("Start failed", exitCode == 0)
        assertFalse("Stop failed", GMTool.isDeviceRunning(name))
    }



/*
    @Test
    public void canStopAllDevices() {

        //TODO uncomment when stopall is implemented
        GenymotionTestTools.createAllDevices()

        DEVICES.each(){
            GenymotionTool.startDevice(it.name)
        }

        GenymotionTool.stopAllDevices()

        GenymotionTestTools.deleteAllDevices()
    }
*/

/*
    @Test
    public void canResetDevice() {
        //TODO implement when stopall is implemented

    }
*/

/*
    @Test
    public void canStartAutoDevice() {
        //TODO implement it when startauto is implemented

    }
*/


    @Test
    public void canInstallToDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name)
        assertTrue("Start failed", exitCode == 0)

        GMTool.installToDevice(name, "res/test/test.apk", true)
        boolean installed = false
        GMTool.cmd(["tools/adb", "shell", "pm list packages"], true){line, count ->
            if(line.contains("com.genymotion.test"))
                installed = true
        }
        assertTrue("Install failed", installed)
    }

    @Test
    public void canInstallListOfAppToDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assertTrue("Start failed", exitCode == 0)

        def listOfApps = ["res/test/test.apk", "res/test/test2.apk"]

        GMTool.installToDevice(name, listOfApps, true)

        int installed = 0
        GMTool.cmd(["tools/adb", "shell", "pm list packages"], true){line, count ->
            if(line.contains("com.genymotion.test") || line.contains("com.genymotion.test2"))
                installed++
        }
        assertEquals("All apps are not found", listOfApps.size(), installed)
    }



    @Test
    public void canPushToDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assertTrue("Start failed", exitCode == 0)

        GMTool.pushToDevice(name, "res/test/test.txt", true)
        boolean pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true){line, count ->
            if(line.contains("test.txt"))
                pushed = true
        }
        assertTrue("Push failed", pushed)

    }

    @Test
    public void canPushListToDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assertTrue("Start failed", exitCode == 0)

        def listOfFiles = ["res/test/test.txt", "res/test/test2.txt"]
        GMTool.pushToDevice(name, listOfFiles, true)

        int pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true){line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("All pushed files are not found", listOfFiles.size(), pushed)

    }

    @Test
    public void canPushToDeviceWithDest() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assertTrue("Start failed", exitCode == 0)

        def destination = "/sdcard/"
        GMTool.pushToDevice(name, ["res/test/test.txt":destination], true)
        boolean pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true){line, count ->
            if(line.contains("test.txt"))
                pushed = true
        }
        assertTrue("Push failed", pushed)
    }

    @Test
    public void canPushListToDeviceWithDest() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assertTrue("Start failed", exitCode == 0)

        def destination = "/sdcard/"
        def listOfFiles = ["res/test/test.txt":destination, "res/test/test2.txt":destination]
        GMTool.pushToDevice(name, listOfFiles, true)

        int pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true){line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("All pushed files are not found", listOfFiles.size(), pushed)
    }


    @Test
    public void canPullFromDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assertTrue("Start failed", exitCode == 0)

        //removing the pulled files
        TestTools.recreatePulledDirectory()

        GMTool.pullFromDevice(name, "/system/build.prop", "temp/pulled/", true)
        File file = new File("temp/pulled/build.prop")
        assertTrue("Pulled file not found", file.exists())
    }

    @Test
    public void canPullListFromDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assertTrue("Start failed", exitCode == 0)

        //removing the pulled files
        TestTools.recreatePulledDirectory()

        def listOfFiles = ["/system/build.prop":"temp/pulled/", "/data/app/GestureBuilder.apk":"temp/pulled/"]
        GMTool.pullFromDevice(name, listOfFiles, true)

        File file = new File("temp/pulled/build.prop")
        assertTrue("build.propfile not found", file.exists())

        file = new File("temp/pulled/GestureBuilder.apk")
        assertTrue("GestureBuilder.apk not found", file.exists())
    }


    @Test
    public void canFlashDevice() {

        String name = TestTools.createADevice()

        def exitCode = GMTool.startDevice(name, true)
        assertTrue("Start failed", exitCode == 0)

        GMTool.flashDevice(name, "res/test/test.zip", true)
        boolean flashed = false
        GMTool.cmd(["tools/adb", "shell", "ls /system"], true){line, count ->
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
        GMTool.flashDevice(name, listOfFiles, true)

        int flashed = 0
        GMTool.cmd(["tools/adb", "shell", "ls /system"], true){line, count ->
            if(line.contains("touchdown") || line.contains("touchdown2"))
                flashed++
        }
        assertEquals("All flashed files are not found", listOfFiles.size(), flashed)
    }


    @Test
    public void canLoginAuto() {
        String username = "testU"
        String password = "testP"

        GMTool.GENYMOTION_CONFIG.username = username
        GMTool.GENYMOTION_CONFIG.password = password
        GMTool.GENYMOTION_CONFIG.persist = true

        def (u, p) = GMTool.checkLogin(null, null)
        assertEquals([u, p], [null, null])

        GMTool.GENYMOTION_CONFIG.persist = false

        (u, p) = GMTool.checkLogin(null, null)
        assertEquals([u, p], [username, password])

        GMTool.GENYMOTION_CONFIG.username = ""
        GMTool.GENYMOTION_CONFIG.password = ""

        (u, p) = GMTool.checkLogin(username, password)
        assertEquals([u, p], [username, password])

        (u, p) = GMTool.checkLogin(null, null)
        assertEquals([u, p], [null, null])
    }


    @After
    public void finishTest(){
        TestTools.cleanAfterTests()
    }

}
