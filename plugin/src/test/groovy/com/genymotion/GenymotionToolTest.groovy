package test.groovy.com.genymotion

import main.groovy.com.genymotion.GenymotionTool
import main.groovy.com.genymotion.GenymotionVirtualDevice
import org.junit.Test
import org.junit.Before
import org.gradle.api.Project

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue


class GenymotionToolTest {


    Project project

    @Before
    public void setUp() {
        project = GenymotionTestTools.init()
    }

    @Test
    public void isConfigOK() {
        def exitCode = GenymotionTool.usage()
        assertTrue("Genymotion not accessible, check the GENYMOTION_PATH variable", exitCode == 2)
    }


    @Test
    public void isTemplatesAvailable() {

        def templates = GenymotionTool.getTemplates(true)
        assertTrue("No template found", templates.size() > 0)
        assertTrue("Empty template", (templates[0].name?.trim()) as boolean)
    }

    @Test
    public void canGetRunningDevices() {
        String name = GenymotionTestTools.createADevice()

        GenymotionTool.startDevice(name)
        def devices = GenymotionTool.getRunningDevices(true, true)

        assertTrue("Error, device not running", devices.contains(name))

        GenymotionTool.stopDevice(name)

        GenymotionTestTools.deleteDevice(name)
    }

    @Test
    public void canGetStoppedDevices() {
        String name = GenymotionTestTools.createADevice()

        GenymotionTool.stopDevice(name)
        def devices = GenymotionTool.getStoppedDevices(true, true)

        assertTrue("Error, device not stopped", devices.contains(name))

        GenymotionTestTools.deleteDevice(name)
    }


    @Test
    public void canCreateDevice() {
        GenymotionTestTools.createAllDevices()

        def devices = GenymotionTool.getAllDevices(true)

        GenymotionTestTools.DEVICES.each() { key, value ->
            boolean exists = false
            devices.each() {
                if(it.name == key){
                    exists = true
                    return
                }
            }
            assertTrue("${key} not found. Test failed", exists)

        }
        GenymotionTestTools.deleteAllDevices()
    }

    @Test
    public void canDetailDevice() {

        String name = GenymotionTestTools.createADevice()

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails()


        assertNotNull(device.androidVersion)
        assertNotNull(device.state)

        GenymotionTool.deleteDevice(name)
    }

    @Test
    public void canListDevices() {

        GenymotionTestTools.createAllDevices()

        def devices = GenymotionTool.getAllDevices()
        assert devices.size() > 0


        GenymotionTestTools.deleteAllDevices()
    }

    @Test
    public void canCloneDevice() {

        String name = GenymotionTestTools.createADevice()

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails()

        def newName = name+"-clone"
        GenymotionTool.cloneDevice(device, newName)

        GenymotionVirtualDevice newDevice = new GenymotionVirtualDevice(newName)
        newDevice.fillFromDetails()

        assertEquals(device.apiLevel, newDevice.apiLevel)
        assertEquals(device.androidVersion, newDevice.androidVersion)
        assertEquals(device.dpi, newDevice.dpi)
        assertEquals(device.height, newDevice.height)
        assertEquals(device.width, newDevice.width)
        assertEquals(device.navbar, newDevice.navbar)
        assertEquals(device.physicalButton, newDevice.physicalButton)

        GenymotionTool.deleteDevice(name)
        GenymotionTool.deleteDevice(newName)
    }

    @Test
    public void canUpdateDevice() {

        String name = GenymotionTestTools.createADevice()

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails()

        device.navbar = false
        device.height = 600
        device.width = 800
        device.dpi = 260
        device.physicalButton = false
        device.nbCpu = 2
        device.ram = 2048

        GenymotionTool.updateDevice(device)

        GenymotionVirtualDevice newDevice = new GenymotionVirtualDevice(name)
        newDevice.fillFromDetails()

        assertEquals(device.apiLevel, newDevice.apiLevel)
        assertEquals(device.androidVersion, newDevice.androidVersion)
        assertEquals(device.dpi, newDevice.dpi)
        assertEquals(device.height, newDevice.height)
        assertEquals(device.width, newDevice.width)
        //TODO enable these tests when gmtool will be fixed
//        assertEquals(device.navbar, newDevice.navbar)
//        assertEquals(device.physicalButton, newDevice.physicalButton)

        GenymotionTool.deleteDevice(name)
    }

    @Test
    public void canStartDevice() {

        String name = GenymotionTestTools.createADevice()

        def exitCode = GenymotionTool.startDevice(name)

        assertTrue("Start failed", exitCode == 0)
    }

/*
    @Test
    public void canStopDevice() {

        //TODO implement it when stop is implemented

        String name = GenymotionTestTools.createADevice()

        def exitCode = GenymotionTool.startDevice(name)

        if(exitCode == 0)
            GenymotionTool.stopDevice(name)


        assertTrue("Start failed", exitCode == 0)
    }
*/


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
}
