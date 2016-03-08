package com.genymotion

import com.genymotion.tools.GMTool
import org.gradle.api.Project
import org.junit.BeforeClass
import org.junit.Test

class VDLaunchDslIntegTest {

    static GMTool gmtool

    @BeforeClass
    public static void setUpClass() {
        Project project
        (project, gmtool) = IntegTestTools.init()
        IntegTestTools.setDefaultUser(true, gmtool)
    }

    @Test
    public void canUpdateWhenIsRunning() {
        String name = IntegTestTools.createADevice(gmtool)
        def device = gmtool.getDevice(name)
        assert !device.isRunning()
        gmtool.startDevice(device)
        assert device.isRunning()
    }
}
