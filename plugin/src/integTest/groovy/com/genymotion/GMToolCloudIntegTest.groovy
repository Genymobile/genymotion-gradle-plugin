/*
 * Copyright (C) 2015 Genymobile
 *
 * This file is part of GenymotionGradlePlugin.
 *
 * GenymotionGradlePlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version
 *
 * GenymotionGradlePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenymotionGradlePlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.genymotion

import com.genymotion.model.DeviceLocation
import com.genymotion.tools.GMTool
import org.gradle.api.Project
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class GMToolCloudIntegTest {

    @Rule
    public RetryRule retryRule = new RetryRule()

    Project project
    GMTool gmtool

    @BeforeClass
    public static void setUpClass() {
        IntegrationTestTools.init(DeviceLocation.CLOUD)
    }

    @Before
    public void setUp() {
        (project, gmtool) = IntegrationTestTools.init(DeviceLocation.CLOUD)
        IntegrationTestTools.setDefaultUser(true, gmtool)
    }

    @Test
    public void canStartAndStopDisposableDevice() {
        String name = IntegrationTestTools.startADisposableDevice(gmtool)
        def runningDevices = gmtool.getRunningDevices(false, true)
        def allDevices = gmtool.getAllDevices(false, true)

        assert allDevices.contains(name)
        assert runningDevices.contains(name)

        gmtool.stopDisposableDevice(name)

        runningDevices = gmtool.getRunningDevices(false, true)
        allDevices = gmtool.getAllDevices(false, true)

        assert !allDevices.contains(name)
        assert !runningDevices.contains(name)
    }
}
