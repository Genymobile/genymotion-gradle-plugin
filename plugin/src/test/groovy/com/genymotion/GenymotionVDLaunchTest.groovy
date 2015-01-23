package test.groovy.com.genymotion

import org.junit.*

import static org.junit.Assert.*

class GenymotionVDLaunchTest {

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

        assertNotNull(project.genymotion.devices)
        assertFalse(project.genymotion.devices[0].stopWhenFinish)
        assertNull(project.genymotion.devices[0].deleteWhenFinish)
        assertEquals(TestTools.DEVICES."Nexus10-junit", project.genymotion.devices[0].template)
        assertTrue(project.genymotion.devices[0].templateExists)
        assertFalse(project.genymotion.devices[0].deviceExists)
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

        assertNotNull(project.genymotion.devices)
        assertNull(project.genymotion.devices[0].stopWhenFinish)
        assertTrue(project.genymotion.devices[0].deleteWhenFinish)
        assertEquals(TestTools.DEVICES."Nexus10-junit", project.genymotion.devices[0].template)
        assertTrue(project.genymotion.devices[0].templateExists)
        assertFalse(project.genymotion.devices[0].deviceExists)
    }

}
