package test.groovy.com.genymotion

import main.groovy.com.genymotion.GenymotionEndTask
import main.groovy.com.genymotion.GenymotionGradlePlugin
import main.groovy.com.genymotion.GenymotionLaunchTask
import org.junit.After
import org.junit.Ignore

import java.util.List
import main.groovy.com.genymotion.GMToolAdmin
import main.groovy.com.genymotion.GenymotionConfig
import main.groovy.com.genymotion.GMTool
import main.groovy.com.genymotion.GenymotionVirtualDevice
import main.groovy.com.genymotion.GenymotionPluginExtension
import org.junit.Before
import org.junit.Test
import org.gradle.api.Project


import static org.junit.Assert.*
import static org.junit.Assert.assertNotNull

class GenymotionGradlePluginTest {

    Project project

    @Before
    public void setUp() {
        project = TestTools.init()
    }


    @Test
    public void canAddsTaskToProject() {
        assertTrue(project.tasks.genymotionLaunch instanceof GenymotionLaunchTask)
        assertTrue(project.tasks.genymotionFinish instanceof GenymotionEndTask)
    }

    @Test
    public void canAddExtensionToProject() {
        assertTrue(project.genymotion instanceof GenymotionPluginExtension)
        assertTrue(project.genymotion.config instanceof GenymotionConfig)
        assertTrue(project.genymotion.admin instanceof GMToolAdmin)
        assertTrue(project.genymotion.devices instanceof List)
    }

    @Test
    public void canConfigGenymotion(){
        String path = "TEST"
        String previousPath = project.genymotion.config.genymotionPath
        project.genymotion.config.genymotionPath = path

        assertEquals(path, project.genymotion.config.genymotionPath)

        project.genymotion.config.genymotionPath = previousPath
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenAddDeviceWithoutParams(){

        project.genymotion.device(null)

    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenAddDeviceWithoutNameAndTemplate(){

        project.genymotion.device(hola:"buenos dias")
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenAddDeviceWithNameNotCreated(){

        project.genymotion.device(name:"DSFGTFSHgfgdfTFGQFQHG")
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenAddDeviceWithTemplateNotCreated(){

        project.genymotion.device(template: "DSFGTFSHgfgdfTFGQFQHG")
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenAddDeviceWithNameAndTemplateNotCreated(){

        project.genymotion.device(name:"DSFGTFSHTFGQFQHG", template:"ferrfgfgdshghGFGDFGfgfd")
    }

    @Test
    public void throwsWhenAddDeviceWithNameAndTemplateNotCreated2(){

        project.genymotion.device(name:"DSFGTFSHTFGQFQHG", template:"Google Nexus 7 - 4.1.1 - API 16 - 800x1280")
    }

    @Test
    public void canAddDeviceToLaunchByName(){

        String vdName = TestTools.createADevice()

        project.genymotion.device(name:vdName)
        assertNull(project.genymotion.devices[0].template)
        assertEquals(vdName, project.genymotion.devices[0].name)

        GMTool.deleteDevice(vdName)
    }

    @Test
    public void canAddDeviceToLaunchByNameWithTemplate(){

        String vdName = TestTools.createADevice()

        project.genymotion.device(name:vdName, template: "Google Nexus 7 - 4.1.1 - API 16 - 800x1280")
        assertNull(project.genymotion.devices[0].template)
        assertEquals(vdName, project.genymotion.devices[0].name)

        GMTool.deleteDevice(vdName)
    }

    @Test
    public void canAddDeviceToLaunchByNameWithTemplateNotCreated(){

        String vdName = TestTools.createADevice()

        project.genymotion.device(name:vdName, template: "frtfgfdgtgsgrGFGFDGFD")
        assertNull(project.genymotion.devices[0].template)
        assertEquals(vdName, project.genymotion.devices[0].name)

        GMTool.deleteDevice(vdName)
    }

    @Test
    public void canAddDeviceToLaunchByTemplate(){

        project.genymotion.device(template:"Google Nexus 7 - 4.1.1 - API 16 - 800x1280")

        assertNotNull("No device found", project.genymotion.devices[0])
        assertNotNull("Device not filled", project.genymotion.devices[0].name)
        assertTrue("Device not created", project.genymotion.devices[0].create)
        assertTrue(project.genymotion.devices[0].deleteWhenFinish)
    }

    @Test
    public void canAddDeviceToLaunchByTemplateWithNameNotCreated(){

        project.genymotion.device(template:"Google Nexus 7 - 4.1.1 - API 16 - 800x1280", name: "dfsdgffgdgqsdg")

        assertNotNull("No device found", project.genymotion.devices[0])
        assertNotNull("Device not filled", project.genymotion.devices[0].name)
        assertTrue("Device not created", project.genymotion.devices[0].create)
        assertTrue(project.genymotion.devices[0].deleteWhenFinish)
    }


    @Test
    public void canAvoidDeviceToBeLaunched(){

        project.genymotion.device(template:"Google Nexus 7 - 4.1.1 - API 16 - 800x1280", start: false)

        assertFalse(project.genymotion.devices[0].start)
    }

    @Test
    public void canEditDeviceBeforeLaunch(){

        String vdName = "OKOK"
        GMTool.deleteDevice(vdName)

        int intValue = 999
        String density = "mdpi"

        project.genymotion.device(
                name: vdName,
                template:"Google Nexus 7 - 4.1.1 - API 16 - 800x1280",
                density: density,
                width: intValue,
                height: intValue,
                physicalButton: false,
                navbar: false,
                nbCpu: 1,
                ram: 2048
        )

        assertNotNull(project.genymotion.devices[0])
        assertEquals(project.genymotion.devices[0].name, vdName)

        project.genymotion.devices[0].create()
        project.genymotion.devices[0].checkAndEdit()

        GenymotionVirtualDevice device = GMTool.getDevice(vdName, true)
        assertEquals(density, device.density)
        assertEquals(intValue, device.width)
        assertEquals(intValue, device.height)
//        assertEquals(false, device.virtualKeyboard) //TODO uncomment when bug on gmtool is fixed
//        assertEquals(false, device.navbarVisible) //TODO uncomment when bug on gmtool is fixed
        assertEquals(1, device.nbCpu)
        assertEquals(2048, device.ram)

        GMTool.deleteDevice(vdName)
    }



    @Test
    public void canSetDeleteWhenFinish(){
        String vdName = TestTools.createADevice()

        project.genymotion.device(name:vdName, deleteWhenFinish: true)
        project.tasks.genymotionLaunch.exec()
        project.tasks.genymotionFinish.exec()

        assertFalse("The device is still existing", GMTool.isDeviceCreated(vdName, true))
    }

    @Test
    public void canAvoidDeleteWhenFinish(){
        String vdName = TestTools.createADevice()

        project.genymotion.device(name:vdName, deleteWhenFinish: false)
        project.tasks.genymotionLaunch.exec()
        project.tasks.genymotionFinish.exec()

        assertTrue("The device is still existing", GMTool.isDeviceCreated(vdName, true))
    }


    @Test
    public void canInstallToDevice() {

        String vdName = TestTools.createADevice()

        project.genymotion.device(name:vdName, install:"res/test/test.apk")
        project.tasks.genymotionLaunch.exec()

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

        def listOfApps = ["res/test/test.apk", "res/test/test2.apk"]
        project.genymotion.device(name:name, install:listOfApps)
        project.tasks.genymotionLaunch.exec()

        int installed = 0
        GMTool.cmd(["tools/adb", "shell", "pm list packages"], true){line, count ->
            if(line.contains("com.genymotion.test") || line.contains("com.genymotion.test2"))
                installed++
        }
        assertEquals("All apps are not found", listOfApps.size(), installed)
    }



    @Test
    public void canPushBeforeToDevice() {

        String name = TestTools.createADevice()

        project.genymotion.device(name:name, pushBefore:"res/test/test.txt")
        project.tasks.genymotionLaunch.exec()

        boolean pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true){line, count ->
            if(line.contains("test.txt"))
                pushed = true
        }
        assertTrue("Push failed", pushed)
    }

    @Test
    public void canPushAfterToDevice() {

        String name = TestTools.createADevice()

        project.genymotion.device(name:name, pushAfter:"res/test/test.txt")
        project.tasks.genymotionLaunch.exec()

        boolean pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true){line, count ->
            if(line.contains("test.txt"))
                pushed = true
        }
        assertFalse("Push happened but should not happen", pushed)

        project.tasks.genymotionFinish.exec()

        pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true){line, count ->
            if(line.contains("test.txt"))
                pushed = true
        }
        assertTrue("Push failed", pushed)
    }

    @Test
    public void canPushBeforeListToDevice() {

        String name = TestTools.createADevice()

        def listOfFiles = ["res/test/test.txt", "res/test/test2.txt"]
        project.genymotion.device(name:name, pushBefore:listOfFiles)
        project.tasks.genymotionLaunch.exec()

        int pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true){line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("All pushed files are not found", listOfFiles.size(), pushed)
    }

    @Test
    public void canPushAfterListToDevice() {

        def (String name, String density, int width, int height, int nbCpu, int ram, boolean deleteWhenFinish) = TestTools.createADetailedDevice(project, false)

        def listOfFiles = ["res/test/test.txt", "res/test/test2.txt"]
        project.genymotion.device(name:name, pushAfter:listOfFiles)
        project.tasks.genymotionLaunch.exec()

        int pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true){line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("Pushed files, it should not happen", 0, pushed)

        project.tasks.genymotionFinish.exec()

        pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls /sdcard/Download/"], true){line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("All pushed files are not found", listOfFiles.size(), pushed)
    }

    @Test
    public void canPushBeforeToDeviceWithDest() {

        String name = TestTools.createADevice()

        def destination = "/sdcard/"
        def listOfFiles = ["res/test/test.txt":destination]
        project.genymotion.device(name:name, pushBefore:listOfFiles)
        project.tasks.genymotionLaunch.exec()

        boolean pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true){line, count ->
            if(line.contains("test.txt"))
                pushed = true
        }
        assertTrue("Push failed", pushed)
    }

    @Test
    public void canPushAfterToDeviceWithDest() {

        def (String name, String density, int width, int height, int nbCpu, int ram, boolean deleteWhenFinish) = TestTools.createADetailedDevice(project, false)

        def destination = "/sdcard/"
        def listOfFiles = ["res/test/test.txt":destination]
        project.genymotion.device(name:name, pushAfter:listOfFiles)
        project.tasks.genymotionLaunch.exec()

        boolean pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true){line, count ->
            if(line.contains("test.txt"))
                pushed = true
        }
        assertFalse("Pushed done. Should not happen", pushed)

        project.tasks.genymotionFinish.exec()

        pushed = false
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true){line, count ->
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
        project.genymotion.device(name:name, pushBefore:listOfFiles)
        project.tasks.genymotionLaunch.exec()

        int pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true){line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("All pushed files are not found", listOfFiles.size(), pushed)
    }

    @Test
    public void canPushAfterListToDeviceWithDest() {

        def (String name, String density, int width, int height, int nbCpu, int ram, boolean deleteWhenFinish) = TestTools.createADetailedDevice(project, false)

        def destination = "/sdcard/"
        def listOfFiles = ["res/test/test.txt":destination, "res/test/test2.txt":destination]
        project.genymotion.device(name:name, pushAfter:listOfFiles)
        project.tasks.genymotionLaunch.exec()


        int pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true){line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("Pushed done. Should not happen", 0, pushed)

        project.tasks.genymotionFinish.exec()

        pushed = 0
        GMTool.cmd(["tools/adb", "shell", "ls", destination], true){line, count ->
            if(line.contains("test.txt") || line.contains("test2.txt"))
                pushed++
        }
        assertEquals("All pushed files are not found", listOfFiles.size(), pushed)

    }

    @Test
    public void canPullBeforeFromDevice() {

        String name = TestTools.createADevice()

        //removing the pulled files
        TestTools.recreatePulledDirectory()

        project.genymotion.device(name:name, pullBefore:["/system/build.prop":"temp/pulled/"])
        project.tasks.genymotionLaunch.exec()

        File file = new File("temp/pulled/build.prop")
        assertTrue("Pulled file not found", file.exists())
    }

    @Test
    public void canPullAfterFromDevice() {

        def (String name, String density, int width, int height, int nbCpu, int ram, boolean deleteWhenFinish) = TestTools.createADetailedDevice(project, false)

        //removing the pulled files
        TestTools.recreatePulledDirectory()

        project.genymotion.device(name:name, pullAfter:["/system/build.prop":"temp/pulled/"])
        project.tasks.genymotionLaunch.exec()

        File file = new File("temp/pulled/build.prop")
        assertFalse("Pulled file found. Should not happen", file.exists())

        project.tasks.genymotionFinish.exec()

        file = new File("temp/pulled/build.prop")
        assertTrue("Pulled file not found", file.exists())
    }
/*

//TODO
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

*/

    @Test
    public void canInjectTasks(){

        String taskName = "dummy"
        project.task(taskName) << {}

        project.genymotion.config.taskLaunch = taskName
        //we inject the genymotion task on the task hierarchy
        project.genymotion.injectTasks()

        def task = project.tasks.getByName(taskName) //throw exception if task not found
        def finishTask = project.tasks.getByName(GenymotionGradlePlugin.TASK_FINISH) //throw exception if task not found

        assertTrue("Launch task not injected", task.dependsOn.contains(GenymotionGradlePlugin.TASK_LAUNCH))
        assertTrue("Finish task not injected", task.finalizedBy.getDependencies().contains(finishTask))
    }

        @Test
    public void canConfigFromFile(){

        GenymotionConfig config = GMTool.getConfig(true)

        project.genymotion.config.fromFile = "res/test/config.properties"

        //we set the config file
        project.tasks.genymotionLaunch.exec()

        assertEquals("statistics not loaded from file",           false,          project.genymotion.config.statistics)
        assertEquals("username not loaded from file",             "testName",     project.genymotion.config.username)
        assertEquals("password not loaded from file",             "testPWD",      project.genymotion.config.password)
        assertEquals("store_credentials not loaded from file",    true,          project.genymotion.config.store_credentials)
        assertEquals("license not loaded from file",              "testLicense",  project.genymotion.config.license)
        assertEquals("proxy not loaded from file",                true,           project.genymotion.config.proxy)
        assertEquals("proxy_address not loaded from file",        "testAddress",  project.genymotion.config.proxy_address)
        assertEquals("proxy not loaded from file",                true,           project.genymotion.config.proxy)
        assertEquals("proxy_port not loaded from file",           12345,          project.genymotion.config.proxy_port)
        assertEquals("proxy_auth not loaded from file",           true,           project.genymotion.config.proxy_auth)
        assertEquals("proxy_username not loaded from file",       "testUsername", project.genymotion.config.proxy_username)
        assertEquals("proxy_password not loaded from file",       "testPWD",      project.genymotion.config.proxy_password)
        assertEquals("virtual_device_path not loaded from file",  "testPath",     project.genymotion.config.virtual_device_path)
        assertEquals("sdk_path not loaded from file",             "testPath",     project.genymotion.config.sdk_path)
        assertEquals("use_custom_sdk not loaded from file",       true,           project.genymotion.config.use_custom_sdk)
        assertEquals("screen_capture_path not loaded from file",  "testPath",     project.genymotion.config.screen_capture_path)
        assertEquals("taskLaunch not loaded from file",           "testTask",     project.genymotion.config.taskLaunch)
        assertEquals("taskFinish not loaded from file",           "testTask",     project.genymotion.config.taskFinish)
        assertEquals("automaticLaunch not loaded from file",      true,           project.genymotion.config.automaticLaunch)
        assertEquals("processTimeout not loaded from file",       500000,         project.genymotion.config.processTimeout)
        assertEquals("verbose not loaded from file",              true,           project.genymotion.config.verbose)
        assertEquals("persist not loaded from file",              true,           project.genymotion.config.persist)
        assertEquals("abortOnError not loaded from file",         false,          project.genymotion.config.abortOnError)

        //we set the last config back
        GMTool.setConfig(config, true)

        //ENTER HERE the path to a properties file containing good credential (username, password & license)
        String path = "/home/eyal/genymotion/gradle-plugin/junit/config.properties"

        File f = new File(path)
        assertTrue("Config file does not exists to restore good a configuration. Set the path on the source code to continue correctly the tests", f.exists())

        project.genymotion.config.fromFile = path
        project.genymotion.config.persist = true

        //we set the config file
        project.genymotion.applyConfigFromFile()
        GMTool.setConfig(config, true)
    }

    @After
    public void finishTest(){
        TestTools.cleanAfterTests()
    }
}