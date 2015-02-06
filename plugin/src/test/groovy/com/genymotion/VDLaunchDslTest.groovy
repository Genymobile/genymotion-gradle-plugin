package com.genymotion

import com.genymotion.model.GenymotionVDLaunch
import com.genymotion.model.VDLaunchDsl
import com.genymotion.tools.GMTool
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.*

class VDLaunchDslTest {

    @BeforeClass
    public static void setUpClass() {
        TestTools.init()
        TestTools.setDefaultUser(true)
    }

    @Test
    public void deleteWhenFinish() {
        def project = TestTools.init()
        project.genymotion.devices {
            "test-fdfdsfd" {
                stopWhenFinish false
                template TestTools.DEVICES."Nexus10-junit"
            }
        }
        project.genymotion.checkParams()

        assert project.genymotion.devices != null
        assert project.genymotion.devices[0].stopWhenFinish == false
        assert project.genymotion.devices[0].deleteWhenFinish == null
        assert TestTools.DEVICES."Nexus10-junit" == project.genymotion.devices[0].template
        assert project.genymotion.devices[0].templateExists
        assert project.genymotion.devices[0].deviceExists == false
    }

    @Test
    public void setStopWhenFinish() {
        def project = TestTools.init()
        project.genymotion.devices {
            "test-fdfqd" {
                deleteWhenFinish true
                template TestTools.DEVICES."Nexus10-junit"
            }
        }
        project.genymotion.checkParams()

        assert project.genymotion.devices != null
        assert project.genymotion.devices[0].stopWhenFinish == null
        assert project.genymotion.devices[0].deleteWhenFinish == true
        assert TestTools.DEVICES."Nexus10-junit" == project.genymotion.devices[0].template
        assert project.genymotion.devices[0].templateExists
        assert project.genymotion.devices[0].deviceExists == false
    }

    @Test
    public void canUpdateWhenIsRunning() {
        String name = TestTools.createADevice()
        def device = GMTool.getDevice(name)
        assertFalse(device.isRunning())
        GMTool.startDevice(device)
        assertTrue(device.isRunning())
    }

    @Test
    public void canCheckPaths() {
        def vd = new GenymotionVDLaunch("device_name")
        vd.pushBefore = ["res/test/test.txt", "res/test/test2.txt", "res/test/test.zip", "res/test/test2.zip"]
        vd.pushAfter = ["res/test/test.txt":"/sdcard/Downloads/", "res/test/test2.txt":"/sdcard/Downloads/",
                        "res/test/test.zip":"/sdcard/Downloads/", "res/test/test2.zip":"/sdcard/Downloads/"]
        vd.install = "res/test/test.txt"
        vd.flash = "res/test/test.txt"

        vd.checkPaths() //throw exception if problem


        vd = new GenymotionVDLaunch("device_name")
        vd.pushBefore = ["res/test/test.txt", "NOPE", "res/test/test.zip", "res/test/test2.zip"]
        try {
            vd.checkPaths()
            fail("Expected NotFoundException to be thrown")
        } catch (Exception e) {
            assert e instanceof FileNotFoundException
            assert e.message == "The file NOPE on pushBefore instruction for the device ${vd.name} is not found."
        }


        vd = new GenymotionVDLaunch("device_name")
        vd.pushAfter = ["res/test/test.txt":"/sdcard/Downloads/", "res/test/test2.txt":"/sdcard/Downloads/",
                        "res/test/test.zip":"/sdcard/Downloads/", "NOPE":"/sdcard/Downloads/"]
        try {
            vd.checkPaths()
            fail("Expected NotFoundException to be thrown")
        } catch (Exception e) {
            assert e instanceof FileNotFoundException
            assert e.message == "The file NOPE on pushAfter instruction for the device ${vd.name} is not found."
        }


        vd = new GenymotionVDLaunch("device_name")
        vd.install = "NOPE"
        try {
            vd.checkPaths()
            fail("Expected NotFoundException to be thrown")
        } catch (Exception e) {
            assert e instanceof FileNotFoundException
            assert e.message == "The file NOPE on install instruction for the device ${vd.name} is not found."
        }


        vd = new GenymotionVDLaunch("device_name")
        vd.flash = "NOPE"
        try {
            vd.checkPaths()
            fail("Expected NotFoundException to be thrown")
        } catch (Exception e) {
            assert e instanceof FileNotFoundException
            assert e.message == "The file NOPE on flash instruction for the device ${vd.name} is not found."
        }
    }

    @Test
    public void canSetProductFlavor() {
        VDLaunchDsl vd = new VDLaunchDsl("device")

        vd.productFlavors "NONO"
        assert vd.productFlavors == ["NONO"]

        vd.productFlavors = ["NONO", "NINI"]
        assert vd.productFlavors == ["NONO", "NINI"]

        vd.productFlavors "NONO", "NINI"
        assert vd.productFlavors == ["NONO", "NINI"]

        vd.productFlavors = null
        assert vd.productFlavors == []
    }

}
