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

import com.genymotion.model.CloudVDLaunchDsl
import com.genymotion.tools.GMTool
import com.genymotion.tools.InvalidPropertyException
import org.gradle.api.Project
import org.junit.Test

import static org.junit.Assert.fail

class CloudVDLaunchDslTest extends CleanMetaTest {
    Project project
    GMTool gmtool

    @Test
    public void cannotUseLocalDeviceOnlyProperties() {
        String templateName = "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"
        String deviceName = "testDevice"

        for (String property : CloudVDLaunchDsl.LOCAL_ONLY_PROPERTIES) {
            (project, gmtool) = TestTools.init()
            project.genymotion.cloudDevices {
                "$deviceName" {
                    template templateName
                    "$property" "value"
                }
            }

            try {
                project.genymotion.checkParams()
                fail("Expected an InvalidPropertyException to be thrown for property \"$property\"")
            } catch (InvalidPropertyException exc) {
                assert exc.name == property
            }
        }
    }
}
