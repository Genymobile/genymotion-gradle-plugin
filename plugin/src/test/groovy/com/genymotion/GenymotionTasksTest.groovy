package test.groovy.com.genymotion

import main.groovy.com.genymotion.GMToolException
import main.groovy.com.genymotion.GenymotionTool
import main.groovy.com.genymotion.GenymotionVDLaunch
import main.groovy.com.genymotion.GenymotionVirtualDevice
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException


import static org.junit.Assert.*

class GenymotionTasksTest {

    Project project

    @Before
    public void setUp() {
        project = GenymotionTestTools.init()
    }

    @Test
    public void canLaunch() {

        def (String vdName, int dpi, int width, int height, int nbCpu, int ram, boolean deleteWhenFinish) = GenymotionTestTools.createADetailedDevice(project)

        project.tasks.genymotionLaunch.exec()

        GenymotionVirtualDevice device = GenymotionTool.getDevice(vdName, true)

        //we test the VDLaunch
        assertEquals(true, project.genymotion.devices[0].start)
        assertEquals(deleteWhenFinish, project.genymotion.devices[0].deleteWhenFinish)

        //we test the created VD
        assertEquals(dpi, device.dpi)
        assertEquals(width, device.width)
        assertEquals(height, device.height)
//        assertEquals(false, device.virtualKeyboard) //TODO uncomment when bug on gmtool is fixed
//        assertEquals(false, device.navbarVisible) //TODO uncomment when bug on gmtool is fixed
        assertEquals(nbCpu, device.nbCpu)
        assertEquals(ram, device.ram)

        //we test if the device is running
        assertEquals(GenymotionVirtualDevice.STATE_ON, device.state)

        //TODO test push, install, flash

        GenymotionTool.stopDevice(vdName)
        GenymotionTool.deleteDevice(vdName)
    }


    @Test
    public void canFinish() {

        def (String vdName, int dpi, int width, int height, int nbCpu, int ram, boolean deleteWhenFinish) = GenymotionTestTools.createADetailedDevice(project)

        project.tasks.genymotionLaunch.exec()

        project.tasks.genymotionFinish.exec()

        assertFalse(GenymotionTool.isDeviceCreated(vdName))
    }

    @Test
    public void throwsWhenCommandError() {

        String deviceToStop = GenymotionVDLaunch.getRandomName()
        String deviceToDelete = GenymotionVDLaunch.getRandomName()
        String deviceToThrowError = GenymotionVDLaunch.getRandomName()

        project.genymotion.device(name: deviceToStop, template:"Google Nexus 7 - 4.1.1 - API 16 - 800x1280", deleteWhenFinish: false)
        project.genymotion.device(name: deviceToDelete, template:"Google Nexus 7 - 4.1.1 - API 16 - 800x1280")

        try{
            project.genymotion.config.abordOnError = true
            project.genymotion.config.genymotionPath = "ssqfkjfksùfsdlkf"
            project.tasks.genymotionLaunch.exec()
            fail("Expected GMToolException to be thrown")

        } catch (IOException e){ //TODO check how we can produce GMToolException instead of IOException
            assertFalse(GenymotionTool.isDeviceCreated(deviceToDelete))
            assertTrue(devicesAreStopped(project.genymotion.devices))
        }
    }

    boolean devicesAreStopped(def devices) {
        def stoppedDevices = GenymotionTool.getRunningDevices(false, false, true)
        devices.each(){
            if(!it.deleteWhenFinish && !stoppedDevices.contains(it.name))
                return false
        }
    }

    @After
    public void finishTest(){
        GenymotionTestTools.cleanAfterTests()
    }
}
