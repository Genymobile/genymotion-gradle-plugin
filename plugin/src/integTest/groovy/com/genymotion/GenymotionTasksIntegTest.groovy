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
import com.genymotion.model.GenymotionVirtualDevice
import com.genymotion.tools.GMTool
import com.genymotion.tools.GMToolDsl
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

import static org.junit.Assert.fail

class GenymotionTasksIntegTest {

    @Rule public RetryRule retryRule = new RetryRule(3)

    Project project
    GMTool gmtool

    @BeforeClass
    public static void setUpClass() {
        def (project, gmtool) = IntegrationTestTools.init()
        IntegrationTestTools.setDefaultUser(true, gmtool)
    }

    @Before
    public void setUp() {
        (project, gmtool) = IntegrationTestTools.init()
    }

    @Test
    public void canLaunch() {
        def (String vdName, String density, int width, int height, int nbCpu, int ram,
            boolean deleteWhenFinish) = IntegrationTestTools.declareADetailedDevice(project)

        project.tasks.genymotionLaunch.exec()

        GenymotionVirtualDevice device = gmtool.getDevice(vdName, true)

        //we test the VDLaunch
        assert project.genymotion.devices[0].start
        assert project.genymotion.devices[0].deleteWhenFinish == deleteWhenFinish

        //we test the created VD
        assert device.density == density
        assert device.width == width
        assert device.height == height
        assert !device.virtualKeyboard
        assert !device.navbarVisible
        assert device.nbCpu == nbCpu
        assert device.ram == ram

        //we test if the device is running
        assert device.state == GenymotionVirtualDevice.STATE_ON

        gmtool.stopDevice(vdName)
        gmtool.deleteDevice(vdName)
    }

    @Test
    public void canLaunchInBridgeMode() {
        def (String vdName, String density, int width, int height, int nbCpu, int ram,
        boolean deleteWhenFinish) = IntegrationTestTools.declareADetailedDevice(project)

        project.genymotion.devices {
            "$vdName" {
                networkMode "bridge"
            }
        }

        project.tasks.genymotionLaunch.exec()

        GenymotionVirtualDevice device = gmtool.getDevice(vdName, true)

        //we test the VDLaunch
        assert project.genymotion.devices[0].start
        assert project.genymotion.devices[0].deleteWhenFinish == deleteWhenFinish

        //we test the created VD
        assert device.density == density
        assert device.width == width
        assert device.height == height
        assert !device.virtualKeyboard
        assert !device.navbarVisible
        assert device.nbCpu == nbCpu
        assert device.networkInfo.mode == GMToolDsl.BRIDGE_MODE

        //we test if the device is running
        assert device.state == GenymotionVirtualDevice.STATE_ON

        gmtool.stopDevice(vdName)
        gmtool.deleteDevice(vdName)
    }

    @Test
    public void canFinish() {
        def (String vdName, String density, int width, int height, int nbCpu, int ram,
            boolean deleteWhenFinish) = IntegrationTestTools.declareADetailedDevice(project)

        project.tasks.genymotionLaunch.exec()

        project.tasks.genymotionFinish.exec()

        assert !gmtool.isDeviceCreated(vdName)
    }

    @Test
    public void throwsWhenCommandError() {
        String deviceToStop = IntegrationTestTools.getRandomName()
        String deviceToDelete = IntegrationTestTools.getRandomName()
        String deviceToThrowError = IntegrationTestTools.getRandomName()

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

        String goodPath = project.genymotion.config.genymotionPath

        try {
            project.genymotion.config.abortOnError = true
            project.genymotion.config.genymotionPath = "ssqfkjfksùfsdlkf"
            project.tasks.genymotionLaunch.exec()
            fail("Expected GMToolException to be thrown")

        } catch (IOException e) {
            //TODO check how we can produce GMToolException instead of IOException with another command
            project.genymotion.config.genymotionPath = goodPath

            assert !gmtool.isDeviceCreated(deviceToDelete)
            assert devicesAreStopped(project.genymotion.devices)
        }
    }

    boolean devicesAreStopped(def devices) {
        def stoppedDevices = gmtool.getRunningDevices(false, false, true)
        devices.each() {
            if (!it.deleteWhenFinish && !stoppedDevices.contains(it.name)) {
                return false
            }
        }
    }

    @Test
    public void canLoginAndRegister() {
        //ENTER HERE the path to a properties file containing good credential (username, password & license)
        String path = "src/integTest/res/test/default.properties"

        File f = new File(path)
        assert f.exists(), "Config file does not exist to test login feature. Set the path to be able to run the test"

        project.genymotion.config.fromFile = path

        project.genymotion.processConfiguration()

        GenymotionConfig config = gmtool.getConfig(true)

        assert project.genymotion.config.username == config.username

        //TODO test license registration
    }

    @After
    public void finishTest() {
        IntegrationTestTools.cleanAfterTests(gmtool)
    }
}
