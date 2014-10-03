package test.groovy.com.genymotion

import main.groovy.com.genymotion.GenymotionConfig
import main.groovy.com.genymotion.GenymotionTool
import main.groovy.com.genymotion.GenymotionVirtualDevice
import org.junit.Test
import org.junit.Before
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue


class GenymotionToolTest {

    static def GENYMOTION_PATH = "/home/eyal/genymotion/genymotion-softs/build/"

    static def DEVICES = [
            "Nexus7-junit":"Google Nexus 7 - 4.1.1 - API 16 - 800x1280",
            "Nexus10-junit":"Google Nexus 10 - 4.4.2 - API 19 - 2560x1600",
            "Nexus4-junit":"Google Nexus 4 - 4.3 - API 18 - 768x1280"
    ]

    Project project

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'genymotion'

        project.genymotion.config.genymotionPath = GENYMOTION_PATH
        //we set the config inside the GenymotionTool
        GenymotionTool.CONFIG = project.genymotion.config
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
    public void canCreateDevice() {
        createAllDevices()

        def devices = GenymotionTool.getAllDevices(true)

        DEVICES.each() { key, value ->
            boolean exists = false
            devices.each() {
                if(it.name == key){
                    exists = true
                    return
                }
            }
            assertTrue("${key} not found. Test failed", exists)

        }
            deleteAllDevices()
    }

    @Test
    public void canDetailDevice() {

        String name = createADevice()

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(name)
        device.fillFromDetails()


        assertNotNull(device.androidVersion)
        assertNotNull(device.state)

        GenymotionTool.deleteDevice(name)
    }

    @Test
    public void canListDevices() {

        createAllDevices()

        def devices = GenymotionTool.getAllDevices()
        assert devices.size() > 0


        deleteAllDevices()
    }

    @Test
    public void canCloneDevice() {

        String name = createADevice()

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

        String name = createADevice()

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

        String name = createADevice()

        def exitCode = GenymotionTool.startDevice(name)

        assertTrue("Start failed", exitCode == 0)
    }

/*
    @Test
    public void canStopDevice() {

        //TODO implement it when stop is implemented

        String name = createADevice()

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
        createAllDevices()

        DEVICES.each(){
            GenymotionTool.startDevice(it.name)
        }

        GenymotionTool.stopAllDevices()

        deleteAllDevices()
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

    /**
     * TOOLS
     */

    private void deleteAllDevices() {
        DEVICES.each() { key, value ->
            GenymotionTool.deleteDevice(key)
        }
    }

    private void createAllDevices() {
        DEVICES.each() { key, value ->
            GenymotionTool.createDevice(value, key)
        }
    }

    private String createADevice() {

        Random rand = new Random()
        int index = rand.nextInt(DEVICES.size())

        String[] keys = DEVICES.keySet() as String[]
        String name = keys[index]
        GenymotionTool.createDevice(DEVICES[name], name)
        name
    }
}
