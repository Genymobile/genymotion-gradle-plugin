package com.genymotion

import com.genymotion.tools.GMTool
import org.gradle.api.Project
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class VDLaunchDslIntegTest {

    @Rule public RetryRule retryRule = new RetryRule()

    static GMTool gmtool

    @BeforeClass
    public static void setUpClass() {
        Project project
        (project, gmtool) = IntegrationTestTools.init()
        IntegrationTestTools.setDefaultUser(true, gmtool)
    }

    @Test
    public void canUpdateWhenIsRunning() {
        String name = IntegrationTestTools.createADevice(gmtool)
        def device = gmtool.getDevice(name)
        assert !device.isRunning()
        gmtool.startDevice(device)
        gmtool.updateDevice(device)
        assert device.isRunning()
    }
}
