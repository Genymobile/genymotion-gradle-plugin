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
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.*

class GenymotionTasksTest {

    Project project

    @BeforeClass
    public static void setUpClass() {
        TestTools.init()
        TestTools.setDefaultUser(true)
    }

    @Before
    public void setUp() {
        project = TestTools.init()
    }


    @Test
    public void canLaunch() {

        def (String vdName, String density, int width, int height, int nbCpu, int ram, boolean deleteWhenFinish) = TestTools.declareADetailedDevice(project)

        project.tasks.genymotionLaunch.exec()

        GenymotionVirtualDevice device = GMTool.getDevice(vdName, true)

        //we test the VDLaunch
        assertEquals(true, project.genymotion.devices[0].start)
        assertEquals(deleteWhenFinish, project.genymotion.devices[0].deleteWhenFinish)

        //we test the created VD
        assertEquals(density, device.density)
        assertEquals(width, device.width)
        assertEquals(height, device.height)
        assertEquals(false, device.virtualKeyboard)
        assertEquals(false, device.navbarVisible)
        assertEquals(nbCpu, device.nbCpu)
        assertEquals(ram, device.ram)

        //we test if the device is running
        assertEquals(GenymotionVirtualDevice.STATE_ON, device.state)

        GMTool.stopDevice(vdName)
        GMTool.deleteDevice(vdName)
    }

    @Test
    public void canFinish() {

        def (String vdName, String density, int width, int height, int nbCpu, int ram, boolean deleteWhenFinish) = TestTools.declareADetailedDevice(project)

        project.tasks.genymotionLaunch.exec()

        project.tasks.genymotionFinish.exec()

        assertFalse(GMTool.isDeviceCreated(vdName))
    }

    @Test
    public void throwsWhenCommandError() {

        String deviceToStop = TestTools.getRandomName()
        String deviceToDelete = TestTools.getRandomName()
        String deviceToThrowError = TestTools.getRandomName()

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

            assertFalse(GMTool.isDeviceCreated(deviceToDelete))
            assertTrue(devicesAreStopped(project.genymotion.devices))
        }
    }

    boolean devicesAreStopped(def devices) {
        def stoppedDevices = GMTool.getRunningDevices(false, false, true)
        devices.each() {
            if (!it.deleteWhenFinish && !stoppedDevices.contains(it.name)) {
                return false
            }
        }
    }


    @Test
    public void canLoginAndRegister() {

        //ENTER HERE the path to a properties file containing good credential (username, password & license)
        String path = "res/test/default.properties"

        File f = new File(path)
        assert f.exists(), "Config file does not exist to test login feature. Set the path to be able to run the test"

        project.genymotion.config.fromFile = path

        project.genymotion.processConfiguration()

        GenymotionConfig config = GMTool.getConfig(true)

        assert project.genymotion.config.username == config.username

        //TODO test license registration
    }


    @After
    public void finishTest() {
        TestTools.cleanAfterTests()
    }
}
