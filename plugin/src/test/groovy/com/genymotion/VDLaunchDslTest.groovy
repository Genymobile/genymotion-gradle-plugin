package com.genymotion

import com.genymotion.model.GenymotionVDLaunch
import com.genymotion.model.VDLaunchDsl
import com.genymotion.tools.GMTool
import org.gradle.api.Project
import org.junit.Test

import static org.junit.Assert.fail
import static org.mockito.Mockito.when

class VDLaunchDslTest {

    static GMTool gmtool

    @Test
    public void deleteWhenFinish() {

        def (Project project, GMTool gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdTemplate = "templateName"
        when(gmtool.templateExists(vdTemplate)).thenReturn(true)

        project.genymotion.devices {
            "test-fdfdsfd" {
                stopWhenFinish false
                template vdTemplate
            }
        }
        project.genymotion.checkParams()

        assert project.genymotion.devices != null
        assert project.genymotion.devices[0].stopWhenFinish == false
        assert project.genymotion.devices[0].deleteWhenFinish == null
        assert vdTemplate == project.genymotion.devices[0].template
        assert project.genymotion.devices[0].templateExists
        assert project.genymotion.devices[0].deviceExists == false
    }

    @Test
    public void setStopWhenFinish() {

        def (Project project, GMTool gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdTemplate = "templateName"
        when(gmtool.templateExists(vdTemplate)).thenReturn(true)

        project.genymotion.devices {
            "test-fdfqd" {
                deleteWhenFinish true
                template vdTemplate
            }
        }
        project.genymotion.checkParams()

        assert project.genymotion.devices != null
        assert project.genymotion.devices[0].stopWhenFinish == null
        assert project.genymotion.devices[0].deleteWhenFinish == true
        assert vdTemplate == project.genymotion.devices[0].template
        assert project.genymotion.devices[0].templateExists
        assert project.genymotion.devices[0].deviceExists == false
    }

    @Test
    public void canCheckPaths() {
        def vd = new GenymotionVDLaunch("device_name")
        vd.pushBefore = ["res/test/test.txt", "res/test/test2.txt", "res/test/test.zip", "res/test/test2.zip"]
        vd.pushAfter = ["res/test/test.txt": "/sdcard/Downloads/", "res/test/test2.txt": "/sdcard/Downloads/",
                        "res/test/test.zip": "/sdcard/Downloads/", "res/test/test2.zip": "/sdcard/Downloads/"]
        vd.install = "res/test/test.txt"
        vd.flash = "res/test/test.txt"

        vd.checkPaths() //throws exception if problem


        vd = new GenymotionVDLaunch("device_name")
        vd.pushBefore = ["res/test/test.txt", "NOPE", "res/test/test.zip", "res/test/test2.zip"]
        try {
            vd.checkPaths()
            fail("Expected NotFoundException to be thrown")
        } catch (Exception e) {
            assert e instanceof FileNotFoundException
            assert e.message == "The file NOPE on pushBefore instruction for the device ${vd.name} was not found."
        }


        vd = new GenymotionVDLaunch("device_name")
        vd.pushAfter = ["res/test/test.txt": "/sdcard/Downloads/", "res/test/test2.txt": "/sdcard/Downloads/",
                        "res/test/test.zip": "/sdcard/Downloads/", "NOPE": "/sdcard/Downloads/"]
        try {
            vd.checkPaths()
            fail("Expected NotFoundException to be thrown")
        } catch (Exception e) {
            assert e instanceof FileNotFoundException
            assert e.message == "The file NOPE on pushAfter instruction for the device ${vd.name} was not found."
        }


        vd = new GenymotionVDLaunch("device_name")
        vd.install = "NOPE"
        try {
            vd.checkPaths()
            fail("Expected NotFoundException to be thrown")
        } catch (Exception e) {
            assert e instanceof FileNotFoundException
            assert e.message == "The file NOPE on install instruction for the device ${vd.name} was not found."
        }


        vd = new GenymotionVDLaunch("device_name")
        vd.flash = "NOPE"
        try {
            vd.checkPaths()
            fail("Expected NotFoundException to be thrown")
        } catch (Exception e) {
            assert e instanceof FileNotFoundException
            assert e.message == "The file NOPE on flash instruction for the device ${vd.name} was not found."
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
