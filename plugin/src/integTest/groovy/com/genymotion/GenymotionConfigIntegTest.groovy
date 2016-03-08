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

class GenymotionConfigIntegTest {

    Project project
    GMTool gmtool
    boolean changedUser = false


    @Test
    public void canGetConfigFromGMTool() {
        (project, gmtool) = IntegTestTools.init()
        GenymotionConfig config = new GenymotionConfig()
        config.fromFile = "res/test/config.properties"
        config.applyConfigFromFile(project)

        changedUser = true
        gmtool.setConfig(config, true)
        gmtool.getConfig(project.genymotion.config, true)

        //@formatter:off
        assert false                                        == project.genymotion.config.statistics
        assert "testName"                                   == project.genymotion.config.username
        assert false                                        == project.genymotion.config.proxy
        assert "testAddress"                                == project.genymotion.config.proxyAddress
        assert false                                        == project.genymotion.config.proxy
        assert 12345                                        == project.genymotion.config.proxyPort
        assert true                                         == project.genymotion.config.proxyAuth
        assert "testUsername"                               == project.genymotion.config.proxyUsername
        assert true                                         == project.genymotion.config.useCustomSdk
        //@formatter:on
    }

    @After
    public void finishTest() {
        if (changedUser) {
            IntegTestTools.setDefaultUser(true, gmtool)
            changedUser = false
        }

        if (gmtool != null) {
            gmtool.resetConfig()
        }
    }
}
