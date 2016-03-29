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
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class GenymotionConfigIntegTest {
    private static final String TEST_CONFIG_FILE = "src/integTest/res/test/config.properties"

    Project project
    GMTool gmtool
    GenymotionConfig config

    @Before
    public void init() {
        (project, gmtool) = IntegrationTestTools.init()
        config = new GenymotionConfig()
        config.fromFile = TEST_CONFIG_FILE
        config.applyConfigFromFile(project)
    }

    @Test
    public void canGetConfigFromGMTool() {
        gmtool.setConfig(config, true)

        gmtool.getConfig(project.genymotion.config, true)

        assertConfiguration(project.genymotion.config)
    }

    private void assertConfiguration(GenymotionConfig config) {
        assert false == config.statistics
        assert "testName" == config.username
        assert false == config.proxy
        assert "testAddress" == config.proxyAddress
        assert false == config.proxy
        assert 12345 == config.proxyPort
        assert true == config.proxyAuth
        assert "testUsername" == config.proxyUsername
        assert true == config.useCustomSdk
    }

    @After
    public void finishTest() {
        IntegrationTestTools.setDefaultUser(true, gmtool)
    }
}
