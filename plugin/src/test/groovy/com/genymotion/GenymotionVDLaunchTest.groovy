package test.groovy.com.genymotion

import main.groovy.com.genymotion.GMTool
import main.groovy.com.genymotion.GenymotionVDLaunch
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
        GenymotionVDLaunch vdLaunch = new GenymotionVDLaunch([stopWhenFinish:false, template: TestTools.DEVICES."Nexus10-junit"])
        assertNull(vdLaunch.stopWhenFinish())
    }

    @Test
    public void setStopWhenFinish() {
        GenymotionVDLaunch vdLaunch = new GenymotionVDLaunch([deleteWhenFinish:true, template: TestTools.DEVICES."Nexus10-junit"])
        assertNotNull(vdLaunch.deleteWhenFinish())
    }

}
