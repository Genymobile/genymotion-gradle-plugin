package test.groovy.com.genymotion

import antlr.collections.List
import main.groovy.com.genymotion.GenymotionTool
import main.groovy.com.genymotion.GenymotionVirtualDevice
import org.gradle.api.Project
import org.junit.Before
import org.junit.Test

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
//        assertEquals(false, device.physicalButton) //TODO uncomment when bug on gmtool is fixed
//        assertEquals(false, device.navbar) //TODO uncomment when bug on gmtool is fixed
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
}
