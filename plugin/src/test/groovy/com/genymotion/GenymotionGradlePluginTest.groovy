package test.groovy.com.genymotion

import main.groovy.com.genymotion.GenymotionEndTask
import main.groovy.com.genymotion.GenymotionLaunchTask
import org.junit.After

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
        project.genymotion.config.genymotionPath = path

        assertEquals(path, project.genymotion.config.genymotionPath)
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
        int dpi = "mdpi"

        project.genymotion.device(
                name: vdName,
                template:"Google Nexus 7 - 4.1.1 - API 16 - 800x1280",
                dpi: dpi,
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
        assertEquals(dpi, device.dpi)
        assertEquals(intValue, device.width)
        assertEquals(intValue, device.height)
//        assertEquals(false, device.virtualKeyboard) //TODO uncomment when bug on gmtool is fixed
//        assertEquals(false, device.navbarVisible) //TODO uncomment when bug on gmtool is fixed
        assertEquals(1, device.nbCpu)
        assertEquals(2048, device.ram)

        GMTool.deleteDevice(vdName)
    }


/*
    @Test
    public void canSetDeleteWhenFinish(){

    }
*/

/*
    @Test
    public void canPushToDevice(){

    }
*/


/*
    @Test
    public void canPushToDevice(){

    }
*/

/*
    @Test
    public void canPullFromDevice(){

    }
*/

/*
    @Test
    public void canInstallAPK(){

    }
*/

/*
    @Test
    public void canFlashDevice(){

    }
*/

/*
    @Test
    public void canInjectLaunchTask(){

    }
*/

/*
    @Test
    public void canInjectFinishTask(){

    }
*/

    @After
    public void finishTest(){
        TestTools.cleanAfterTests()
    }
}