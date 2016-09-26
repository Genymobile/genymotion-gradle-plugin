package com.genymotion

import com.genymotion.model.VDLaunchDsl
import com.genymotion.tools.GMTool
import org.junit.After
import org.junit.Test

import static org.junit.Assert.fail

class VDLaunchDslTest extends CleanMetaTest {

    static GMTool gmtool


    @Test
    public void canCheckPaths() {
        def vd = new VDLaunchDsl("device_name")
        vd.pushBefore = ["src/integTest/res/test/test.txt", "src/integTest/res/test/test2.txt", "src/integTest/res/test/test.zip", "src/integTest/res/test/test2.zip"]
        vd.pushAfter = ["src/integTest/res/test/test.txt": "/sdcard/Downloads/", "src/integTest/res/test/test2.txt": "/sdcard/Downloads/",
                        "src/integTest/res/test/test.zip": "/sdcard/Downloads/", "src/integTest/res/test/test2.zip": "/sdcard/Downloads/"]
        vd.install = "src/integTest/res/test/test.txt"
        vd.flash = "src/integTest/res/test/test.txt"

        vd.checkPaths() //throws exception if problem


        vd = new VDLaunchDsl("device_name")
        vd.pushBefore = ["src/integTest/res/test/test.txt", "NOPE", "src/integTest/res/test/test.zip", "src/integTest/res/test/test2.zip"]
        try {
            vd.checkPaths()
            fail("Expected NotFoundException to be thrown")
        } catch (Exception e) {
            assert e instanceof FileNotFoundException
            assert e.message == "The file NOPE on pushBefore instruction for the device ${vd.name} was not found."
        }


        vd = new VDLaunchDsl("device_name")
        vd.pushAfter = ["src/integTest/res/test/test.txt": "/sdcard/Downloads/", "src/integTest/res/test/test2.txt": "/sdcard/Downloads/",
                        "src/integTest/res/test/test.zip": "/sdcard/Downloads/", "NOPE": "/sdcard/Downloads/"]
        try {
            vd.checkPaths()
            fail("Expected NotFoundException to be thrown")
        } catch (Exception e) {
            assert e instanceof FileNotFoundException
            assert e.message == "The file NOPE on pushAfter instruction for the device ${vd.name} was not found."
        }


        vd = new VDLaunchDsl("device_name")
        vd.install = "NOPE"
        try {
            vd.checkPaths()
            fail("Expected NotFoundException to be thrown")
        } catch (Exception e) {
            assert e instanceof FileNotFoundException
            assert e.message == "The file NOPE on install instruction for the device ${vd.name} was not found."
        }


        vd = new VDLaunchDsl("device_name")
        vd.flash = "NOPE"
        try {
            vd.checkPaths()
            fail("Expected NotFoundException to be thrown")
        } catch (Exception e) {
            assert e instanceof FileNotFoundException
            assert e.message == "The file NOPE on flash instruction for the device ${vd.name} was not found."
        }
    }

    @After
    public void finishTest() {
        cleanMetaClass()
    }
}
