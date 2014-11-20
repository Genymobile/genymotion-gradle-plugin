package test.groovy.com.genymotion

import main.groovy.com.genymotion.GMTool
import main.groovy.com.genymotion.GenymotionConfig
import main.groovy.com.genymotion.GenymotionVDLaunch
import main.groovy.com.genymotion.GenymotionVirtualDevice
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.IOException


import static org.junit.Assert.*

class GenymotionTasksTest {

    Project project

    @Before
    public void setUp() {
        project = TestTools.init()
    }


    @Test
    public void canLaunch() {

        def (String vdName, String density, int width, int height, int nbCpu, int ram, boolean deleteWhenFinish) = TestTools.createADetailedDevice(project)

        project.tasks.genymotionLaunch.exec()

        GenymotionVirtualDevice device = GMTool.getDevice(vdName, true)

        //we test the VDLaunch
        assertEquals(true, project.genymotion.devices[0].start)
        assertEquals(deleteWhenFinish, project.genymotion.devices[0].deleteWhenFinish)

        //we test the created VD
        assertEquals(density, device.density)
        assertEquals(width, device.width)
        assertEquals(height, device.height)
//        assertEquals(false, device.telephony) //TODO uncomment when implemented on gmtool and plugin
//        assertEquals(false, device.virtualKeyboard) //TODO uncomment when bug on gmtool is fixed
//        assertEquals(false, device.navbarVisible) //TODO uncomment when bug on gmtool is fixed
//        assertEquals(nbCpu, device.nbCpu) //TODO uncomment when bug on gmtool is fixed
//        assertEquals(ram, device.ram) //TODO uncomment when bug on gmtool is fixed

        //we test if the device is running
        assertEquals(GenymotionVirtualDevice.STATE_ON, device.state)

        //TODO test push, install, flash

        GMTool.stopDevice(vdName)
        GMTool.deleteDevice(vdName)
    }

    @Test
    public void canFinish() {

        def (String vdName, String density, int width, int height, int nbCpu, int ram, boolean deleteWhenFinish) = TestTools.createADetailedDevice(project)

        project.tasks.genymotionLaunch.exec()

        project.tasks.genymotionFinish.exec()

        assertFalse(GMTool.isDeviceCreated(vdName))
    }

    @Test
    public void throwsWhenCommandError() {

        String deviceToStop = GenymotionVDLaunch.getRandomName()
        String deviceToDelete = GenymotionVDLaunch.getRandomName()
        String deviceToThrowError = GenymotionVDLaunch.getRandomName()

        project.genymotion.device(name: deviceToStop, template:"Google Nexus 7 - 4.1.1 - API 16 - 800x1280", deleteWhenFinish: false)
        project.genymotion.device(name: deviceToDelete, template:"Google Nexus 7 - 4.1.1 - API 16 - 800x1280")

        String goodPath = project.genymotion.config.genymotionPath

        try{
            project.genymotion.config.abordOnError = true
            project.genymotion.config.genymotionPath = "ssqfkjfksùfsdlkf"
            project.tasks.genymotionLaunch.exec()
            fail("Expected GMToolException to be thrown")

        } catch (IOException e){ //TODO check how we can produce GMToolException instead of IOException with another command
            //we fix the path
            project.genymotion.config.genymotionPath = goodPath

            assertFalse(GMTool.isDeviceCreated(deviceToDelete))
            assertTrue(devicesAreStopped(project.genymotion.devices))
        }
    }

    boolean devicesAreStopped(def devices) {
        def stoppedDevices = GMTool.getRunningDevices(false, false, true)
        devices.each(){
            if(!it.deleteWhenFinish && !stoppedDevices.contains(it.name))
                return false
        }
    }


    @Test
    public void canLoginAndRegister() {

        //ENTER HERE the path to a properties file containing good credential (username, password & license)
        String path = "/home/eyal/genymotion/gradle-plugin/junit/config.properties"

        File f = new File(path)
        assertTrue("Config file does not exists to test login feature. Set the path to be able to run the test", f.exists())

        project.genymotion.config.fromFile = path
        project.genymotion.config.persist = true

        //we set the config file
        project.tasks.genymotionLaunch.exec()

        GenymotionConfig config = GMTool.getConfig(true)

        assertEquals(project.genymotion.config.username, config.username)

        //TODO test license registration
    }


    @After
    public void finishTest(){
        TestTools.cleanAfterTests()
    }
}
