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
import com.genymotion.tools.Log
import com.genymotion.tools.Tools
import org.answerit.mock.slf4j.LoggingLevel
import org.answerit.mock.slf4j.MockSlf4j
import org.gradle.api.Project
import org.junit.After
import org.junit.Test
import org.slf4j.Logger

import static org.answerit.mock.slf4j.MockSlf4jMatchers.*
import static org.hamcrest.CoreMatchers.allOf
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class GenymotionConfigTest extends CleanMetaTest {

    Project project
    boolean changedUser = false

    @Test
    public void isEmptyWhenEmpty() {
        GenymotionConfig config = new GenymotionConfig()

        assert config.isEmpty()
    }

    @Test
    public void canFixGenymotionPath() {
        GenymotionConfig config = new GenymotionConfig()
        config.genymotionPath = "something/without/slash"

        assert File.separator == config.genymotionPath.getAt(config.genymotionPath.size() - 1)
    }

    @Test
    public void isNotEmptyWhenNotEmpty() {

        ["statistics",
         "username",
         "password",
         "license",
         "licenseServer",
         "licenseServerAddress",
         "proxy",
         "proxyAddress",
         "proxyPort",
         "proxyAuth",
         "proxyUsername",
         "proxyPassword",
         "virtualDevicePath",
         "androidSdkPath",
         "useCustomSdk",
         "screenCapturePath"].each {
            testEmptyFromValue(it, "notNull")
        }
    }

    private testEmptyFromValue(String valueName, def value) {
        GenymotionConfig config = new GenymotionConfig()
        config.setProperty(valueName, value)

        assert !config.isEmpty()
    }

    @Test
    public void canConfigFromFile() {

        project = TestTools.init()
        GenymotionConfig config = GMTool.getConfig(true)

        project.genymotion.config.fromFile = "res/test/config.properties"

        //we set the user as changed to set it again after the test
        changedUser = true

        //we set the config file
        project.genymotion.processConfiguration()

        //@formatter:off
        assert false            == project.genymotion.config.statistics
        assert "testName"       == project.genymotion.config.username
        assert "testPWD"        == project.genymotion.config.password
        assert true             == project.genymotion.config.licenseServer
        assert "licenseServ"    == project.genymotion.config.licenseServerAddress
        assert "testLicense"    == project.genymotion.config.license
        assert false            == project.genymotion.config.proxy
        assert "testAddress"    == project.genymotion.config.proxyAddress
        assert false            == project.genymotion.config.proxy
        assert 12345            == project.genymotion.config.proxyPort
        assert true             == project.genymotion.config.proxyAuth
        assert "testUsername"   == project.genymotion.config.proxyUsername
        assert "testPWD"        == project.genymotion.config.proxyPassword
        assert "testPath"       == project.genymotion.config.virtualDevicePath
        assert "testPath"       == project.genymotion.config.androidSdkPath
        assert true             == project.genymotion.config.useCustomSdk
        assert "testPath"       == project.genymotion.config.screenCapturePath
        assert "testTask"       == project.genymotion.config.taskLaunch
        assert true             == project.genymotion.config.automaticLaunch
        assert 500000           == project.genymotion.config.processTimeout
        assert true             == project.genymotion.config.verbose
        assert false            == project.genymotion.config.abortOnError
        //@formatter:on

        //we set the last config back
        GMTool.setConfig(config, true)

        //we set the default config credentials
        GMTool.setConfig(TestTools.getDefaultConfig(), true)
    }

    @Test
    public void canGetConfigFromGMTool() {
        project = TestTools.init()
        GenymotionConfig config = new GenymotionConfig()
        config.fromFile = "res/test/config.properties"
        config.applyConfigFromFile(project)

        changedUser = true
        GMTool.setConfig(config, true)
        GMTool.getConfig(project.genymotion.config, true)

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


    @Test
    public void warnWhenUsingStoreCredentials() {

        Logger logger = MockSlf4j.mockStatic(Log.class, "logger")

        GenymotionConfig config = new GenymotionConfig()
        config.storeCredentials = true

        assertThat(logger, hasEntriesCount(1, that(allOf(
                haveMessage(equalTo(GenymotionConfig.STORE_CREDENTIALS_ERROR)),
                haveLevel(LoggingLevel.WARN)
        ))))
    }

    @Test
    public void canAutoConfigGenymotionPath() {
        //we emulate a mac
        Tools.metaClass.static.getOSName = { return 'mac' }
        GenymotionConfig config = new GenymotionConfig()
        assert config.genymotionPath == GenymotionConfig.DEFAULT_GENYMOTION_PATH_MAC

        Tools.metaClass.static.getOSName = { return 'Mac' }
        config = new GenymotionConfig()
        assert config.genymotionPath == GenymotionConfig.DEFAULT_GENYMOTION_PATH_MAC

        //we emulate a windows
        Tools.metaClass.static.getOSName = { return 'windows' }
        config = new GenymotionConfig()
        assert config.genymotionPath == GenymotionConfig.DEFAULT_GENYMOTION_PATH_WINDOWS

        Tools.metaClass.static.getOSName = { return 'Windows' }
        config = new GenymotionConfig()
        assert config.genymotionPath == GenymotionConfig.DEFAULT_GENYMOTION_PATH_WINDOWS

        //we emulate a linux
        Tools.metaClass.static.getOSName = { return 'linux' }
        config = new GenymotionConfig()
        assert config.genymotionPath == GenymotionConfig.DEFAULT_GENYMOTION_PATH_LINUX

        Tools.metaClass.static.getOSName = { return 'Linux' }
        config = new GenymotionConfig()
        assert config.genymotionPath == GenymotionConfig.DEFAULT_GENYMOTION_PATH_LINUX

        //we emulate a strange thing
        Tools.metaClass.static.getOSName = { return 'RGFQS' }
        config = new GenymotionConfig()
        assert config.genymotionPath == GenymotionConfig.DEFAULT_GENYMOTION_PATH
    }

    @After
    public void finishTest() {
        cleanMetaClass()
        Log.clearLogger()

        if (changedUser) {
            TestTools.setDefaultUser(true)
            changedUser = false
        }

        GMTool.resetConfig()
    }
}
