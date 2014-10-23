package test.groovy.com.genymotion

import main.groovy.com.genymotion.GMToolException
import main.groovy.com.genymotion.GMTool
import main.groovy.com.genymotion.GenymotionVirtualDevice
import org.junit.After
import org.junit.Test
import org.junit.Before
import org.gradle.api.Project

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue


class GMToolTest {


    Project project

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
        device.fillFromDetails()


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
    public void canUpdateDevice() {

        String name = TestTools.createADevice()

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails()

        device.navbarVisible = false
        device.height = 600
        device.width = 800
        device.dpi = 260
        device.virtualKeyboard = false
        device.nbCpu = 2
        device.ram = 2048

        GMTool.editDevice(device)

        GenymotionVirtualDevice newDevice = new GenymotionVirtualDevice(name)
        newDevice.fillFromDetails()

        assertEquals(device.androidVersion, newDevice.androidVersion)
        assertEquals(device.dpi, newDevice.dpi)
        assertEquals(device.height, newDevice.height)
        assertEquals(device.width, newDevice.width)
        //TODO enable these tests when gmtool will be fixed
//        assertEquals(device.navbarVisible, newDevice.navbarVisible)
//        assertEquals(device.virtualKeyboard, newDevice.virtualKeyboard)

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

        //TODO implement it when stop is implemented

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

/*
    @Test
    public void canInstallToDevice() {

        //TODO
        String name = GenymotionTestTools.createADevice()

        def exitCode = GenymotionTool.startDevice(name)
        GenymotionTool.installToDevice(name, apks)

        assertTrue("Start failed", exitCode == 0)
    }
*/

/*
    @Test
    public void canPushToDevice() {

        //TODO
        String name = GenymotionTestTools.createADevice()

        def exitCode = GenymotionTool.startDevice(name)
        GenymotionTool.installToDevice(name, )

        assertTrue("Start failed", exitCode == 0)
    }
*/

/*
    @Test
    public void canPullFromDevice() {

        //TODO
        String name = GenymotionTestTools.createADevice()

        def exitCode = GenymotionTool.startDevice(name)
        GenymotionTool.installToDevice(name, )

        assertTrue("Start failed", exitCode == 0)
    }
*/

/*
    @Test
    public void canFlashDevice() {

        //TODO
        String name = GenymotionTestTools.createADevice()

        def exitCode = GenymotionTool.startDevice(name)
        GenymotionTool.installToDevice(name, )

        assertTrue("Start failed", exitCode == 0)
    }
*/

    @After
    public void finishTest(){
        TestTools.cleanAfterTests()
    }

}
