/*
 * Copyright (C) 2016 Genymobile
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
import org.junit.Test

class GenymotionGradlePluginCloudIntegTest {

    Project project
    GMTool gmtool

    private static final String VD_NAME = "integtest-cloudvd-" + UUID.randomUUID().toString()

    @Before
    public void setUp() {
        (project, gmtool) = IntegrationTestTools.init()
        gmtool.deviceLocation = DeviceLocation.CLOUD
        IntegrationTestTools.setDefaultUser(true, gmtool)
    }

    @Test
    public void canStartCloudDevice() {
        project.genymotion.cloudDevices {
            "$VD_NAME" {
                template "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"
            }
        }
        project.tasks.genymotionLaunch.exec()

        assert gmtool.isDeviceCreated(VD_NAME)
        assert gmtool.isDeviceRunning(VD_NAME)

        project.tasks.genymotionFinish.exec()

        assert !gmtool.isDeviceRunning(VD_NAME)
        assert !gmtool.isDeviceCreated(VD_NAME)
    }

    @Test
    public void canLogcat() {
        String path = IntegrationTestTools.TEMP_PATH + VD_NAME + ".logcat"

        project.genymotion.cloudDevices {
            "$VD_NAME" {
                template "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"
                logcat path
            }
        }

        def (boolean clearedAfterBoot, boolean logcatDumped) = IntegrationTestTools.runAndCheckLogcat(project, gmtool,
                VD_NAME, path)

        assert clearedAfterBoot
        assert logcatDumped
    }
}
