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

import com.genymotion.model.GenymotionConfig
import com.genymotion.tools.GMTool
import org.gradle.api.Project
import org.junit.After
import org.junit.Test

import static org.junit.Assert.fail

class GenymotionTasksTest extends CleanMetaTest {

    Project project
    GMTool gmtool

    @Test
    public void throwsWhenCommandErrorAndStopDevices() {

        GenymotionConfig config = new GenymotionConfig(abortOnError: true, genymotionPath: "wrong/path")
        (project, gmtool) = TestTools.init(GMTool.newInstance(config))

        GMTool.metaClass.static.newInstance = {
            gmtool
        }

        String deviceToStop = "deviceToStop"
        String deviceToDelete = "deviceToDelete"

        project.genymotion.devices {
            "$deviceToStop" {
                template "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"
                deleteWhenFinish false
            }
        }
        project.genymotion.devices {
            "$deviceToDelete" {
                template "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"
            }
        }

        try {
            project.tasks.genymotionLaunch.exec()
            fail("Expected GMToolException to be thrown")

        } catch (IOException e) {

        }
    }

    @After
    public void finishTest() {
        cleanMetaClass()
    }
}
