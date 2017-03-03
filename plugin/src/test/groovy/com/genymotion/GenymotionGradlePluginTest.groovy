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
import com.genymotion.model.LocalVDLaunchDsl
import com.genymotion.model.VDLaunchDsl
import com.genymotion.tasks.GenymotionFinishTask
import com.genymotion.tasks.GenymotionLaunchTask
import com.genymotion.tools.GMTool
import com.genymotion.tools.GMToolException
import com.genymotion.tools.LocalDeviceController
import com.genymotion.tools.Log
import org.answerit.mock.slf4j.LoggingLevel
import org.answerit.mock.slf4j.MockSlf4j
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.rules.ExpectedException
import org.slf4j.Logger

import static com.genymotion.model.GenymotionVirtualDevice.*
import static org.answerit.mock.slf4j.MockSlf4jMatchers.*
import static org.hamcrest.CoreMatchers.allOf
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat
import static org.mockito.Mockito.*

class GenymotionGradlePluginTest extends CleanMetaTest {

    Project project
    GMTool gmtool

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        (project, gmtool) = TestTools.init()
    }

    @Test
    public void canAddsTaskToProject() {
        assert project.tasks.genymotionLaunch instanceof GenymotionLaunchTask
        assert project.tasks.genymotionFinish instanceof GenymotionFinishTask
    }

    @Test
    public void canAddExtensionToProject() {
        assert project.genymotion instanceof GenymotionPluginExtension
        assert project.genymotion.config instanceof GenymotionConfig
        assert project.genymotion.devices instanceof List
    }

    @Test
    public void canConfigGenymotion() {
        String path = "TEST" + File.separator
        project.genymotion.config.genymotionPath = path

        assert path == project.genymotion.config.genymotionPath
    }

    @Test
    public void canFixGenymotionPath() {
        (project, gmtool) = TestTools.init()

        String path = "/path/to/test"
        project.genymotion.config.genymotionPath = path

        assert path + File.separator == project.genymotion.config.genymotionPath
    }

    @Test
    public void canAddNoDevice() {
        project.genymotion.devices {}
        assert project.genymotion.devices.size() == 0
    }

    @Test
    public void throwsWhenAddDeviceWithoutNameAndTemplate() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        project.genymotion.devices {
            "test" { pullAfter "buenos dias": "dest/path" }
        }

        expectedException.expect(GMToolException)
        expectedException.expectMessage("On device \"test\", template: \"null\". " + VDLaunchDsl.INVALID_PARAMETER)
        project.genymotion.checkParams()

    }

    @Test
    public void throwsWhenAddDeviceWithNameNotCreated() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "sampleDevice"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(false)

        project.genymotion.devices {
            "$vdName" {}
        }

        expectedException.expect(GMToolException)
        expectedException.expectMessage("On device \"$vdName\", template: \"null\". " + VDLaunchDsl.INVALID_PARAMETER)
        project.genymotion.checkParams()

    }

    @Test
    public void throwsWhenAddDeviceWithTemplateNotCreated() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "imnothere"
        String templateName = "templatetahtdoesnotexists"

        when(gmtool.templateExists(templateName)).thenReturn(false)

        project.genymotion.devices {
            "$vdName" {
                template templateName
            }
        }

        expectedException.expect(GMToolException)
        expectedException.expectMessage("On device \"$vdName\", template: \"$templateName\". " + VDLaunchDsl.INVALID_PARAMETER)
        project.genymotion.checkParams()
    }

    @Test
    public void throwsWhenAddDeviceWithNameAndTemplateNotCreated() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "DSFGTFSHTFGQFQHG"
        String templateName = "ferrfgfgdshghGFGDFGfgfd"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(false)
        when(gmtool.templateExists(templateName)).thenReturn(false)

        project.genymotion.devices {
            "$vdName" {
                template templateName
            }
        }

        expectedException.expect(GMToolException)
        expectedException.expectMessage("On device \"$vdName\", template: \"$templateName\". " + VDLaunchDsl.INVALID_PARAMETER)
        project.genymotion.checkParams()
    }

    @Test
    public void canAddDeviceToLaunchByName() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {}
        }
        assert project.genymotion.devices[0].template == null
        assert project.genymotion.devices[0].name == vdName
    }

    @Test
    public void canAddDeviceToLaunchByNameWithTemplate() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String templateName = "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"
        String deviceName = "testDevice"

        when(gmtool.isDeviceCreated(deviceName)).thenReturn(true)
        when(gmtool.templateExists(templateName)).thenReturn(true)

        project.genymotion.devices {
            "$deviceName" {
                template templateName
            }
        }
        assert project.genymotion.devices[0].template == templateName
        assert project.genymotion.devices[0].name == deviceName
    }

    @Test
    public void canAddDeviceToLaunchByNameWithTemplateNotCreated() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        String templateName = "templatedoesnotexists"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)
        when(gmtool.templateExists(templateName)).thenReturn(false)


        project.genymotion.devices {
            "$vdName" {
                template templateName
            }
        }
        project.genymotion.checkParams()

        assert !project.genymotion.devices[0].templateExists
        assert project.genymotion.devices[0].name == vdName
    }

    @Test
    public void canAddDeviceToLaunchByTemplateWithNameNotCreated() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        String templateName = "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(false)
        when(gmtool.templateExists(templateName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                template templateName
            }
        }

        project.genymotion.checkParams()

        assert project.genymotion.devices[0] != null
        assert project.genymotion.devices[0].name != null
        assert project.genymotion.devices[0].create
        assert project.genymotion.devices[0].deleteWhenFinish == true
    }

    @Test
    public void canAvoidDeviceToBeLaunched() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        String templateName = "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(false)
        when(gmtool.templateExists(templateName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                template templateName
                start false
            }
        }

        assert !project.genymotion.devices[0].start
    }

    @Test
    public void canEditDeviceBeforeLaunch() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        String templateName = "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)
        when(gmtool.templateExists(templateName)).thenReturn(true)

        int intValue = 999
        String densityValue = "mdpi"

        project.genymotion.devices {
            "$vdName" {
                template templateName
                density densityValue
                width intValue
                height intValue
                virtualKeyboard false
                navbarVisible false
                nbCpu 1
                ram 2048
            }
        }

        assert project.genymotion.devices[0] != null
        assert project.genymotion.devices[0].name == vdName

        VDLaunchDsl device = project.genymotion.devices[0]
        assert densityValue == device.density
        assert intValue == device.width
        assert intValue == device.height
        assert !device.virtualKeyboard
        assert !device.navbarVisible
        assert 1 == device.nbCpu
        assert 2048 == device.ram

        gmtool.createDevice(templateName, vdName)

        LocalDeviceController.checkAndEdit(gmtool, device)
        verify(gmtool).editDevice(device)
    }

    @Test
    public void canLogcat() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        String path = "/tmp/${vdName}.logcat"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                logcat path
            }
        }

        project.evaluate()
        project.genymotion.devices[0].state = GenymotionVirtualDevice.STATE_ON

        project.tasks.genymotionFinish.exec()

        verify(gmtool).logcatDump(project.genymotion.devices[0], path)
    }

    @Test
    public void canLogcatAndAvoidClearLogcatAfterBoot() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        String path = "/tmp/${vdName}.logcat"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                logcat path
                clearLogAfterBoot false
            }
        }

        project.evaluate()
        project.genymotion.devices[0].state = GenymotionVirtualDevice.STATE_ON

        project.tasks.genymotionLaunch.exec()
        project.tasks.genymotionFinish.exec()

        verify(gmtool).logcatDump(project.genymotion.devices[0], path)
        verify(gmtool, never()).logcatClear(project.genymotion.devices[0])
    }

    @Test
    public void canClearLogcatAfterBoot() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        String path = "/tmp/${vdName}.logcat"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                logcat path
                clearLogAfterBoot true
            }
        }

        project.evaluate()
        project.genymotion.devices[0].state = GenymotionVirtualDevice.STATE_ON

        project.tasks.genymotionLaunch.exec()
        verify(gmtool).logcatClear(project.genymotion.devices[0])

        project.tasks.genymotionFinish.exec()
        verify(gmtool).logcatDump(project.genymotion.devices[0], path)
    }

    @Test
    public void canSetDeleteWhenFinish() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                deleteWhenFinish true
            }
        }
        LocalVDLaunchDsl device = project.genymotion.devices[0]

        project.tasks.genymotionLaunch.exec()
        verify(gmtool).startDevice(device.name)
        device.state = GenymotionVirtualDevice.STATE_ON

        project.tasks.genymotionFinish.exec()
        verify(gmtool).stopDevice(device.name)
        verify(gmtool).deleteDevice(device.name)
    }

    @Test
    public void canAvoidDeleteWhenFinish() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                deleteWhenFinish false
            }
        }
        LocalVDLaunchDsl device = project.genymotion.devices[0]

        project.tasks.genymotionLaunch.exec()
        verify(gmtool).startDevice(device.name)
        device.state = GenymotionVirtualDevice.STATE_ON

        project.tasks.genymotionFinish.exec()
        verify(gmtool).stopDevice(device.name)
        verify(gmtool, never()).deleteDevice(device.name)
    }

    @Test
    public void canInstallToDevice() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        String apkPath = "src/integTest/res/test/test.apk"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                install apkPath
            }
        }

        project.tasks.genymotionLaunch.exec()
        verify(gmtool).installToDevice(project.genymotion.devices[0], apkPath)
    }

    @Test
    public void canInstallListOfAppToDevice() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def listOfApps = ["src/integTest/res/test/test.apk", "src/integTest/res/test/test2.apk"]

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                install listOfApps
            }
        }

        project.tasks.genymotionLaunch.exec()
        verify(gmtool).installToDevice(project.genymotion.devices[0], listOfApps)
    }

    @Test
    public void canPushBeforeToDevice() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def path = "src/integTest/res/test/test.txt"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                pushBefore path
            }
        }
        project.tasks.genymotionLaunch.exec()
        verify(gmtool).pushToDevice(project.genymotion.devices[0], path)
    }

    @Test
    public void canPushAfterToDevice() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def path = "src/integTest/res/test/test.txt"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                pushAfter path
            }
        }
        VDLaunchDsl device = project.genymotion.devices[0]

        project.tasks.genymotionLaunch.exec()
        device.state = GenymotionVirtualDevice.STATE_ON
        verify(gmtool, never()).pushToDevice(device, path)

        project.tasks.genymotionFinish.exec()
        verify(gmtool).pushToDevice(device, path)
    }

    @Test
    public void canPushBeforeListToDevice() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def listOfFiles = ["src/integTest/res/test/test.txt", "src/integTest/res/test/test2.txt"]

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                pushBefore listOfFiles
            }
        }

        project.tasks.genymotionLaunch.exec()
        verify(gmtool).pushToDevice(project.genymotion.devices[0], listOfFiles)
    }

    @Test
    public void canPushAfterListToDevice() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def listOfFiles = ["src/integTest/res/test/test.txt", "src/integTest/res/test/test2.txt"]

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                pushAfter listOfFiles
            }
        }
        VDLaunchDsl device = project.genymotion.devices[0]

        project.tasks.genymotionLaunch.exec()
        device.state = GenymotionVirtualDevice.STATE_ON
        verify(gmtool, never()).pushToDevice(device, listOfFiles)

        project.tasks.genymotionFinish.exec()
        verify(gmtool).pushToDevice(device, listOfFiles)
    }

    @Test
    public void canPushBeforeToDeviceWithDest() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def destination = "/sdcard/"
        def listOfFiles = ["src/integTest/res/test/test.txt": destination]

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                pushBefore listOfFiles
            }
        }
        project.tasks.genymotionLaunch.exec()
        verify(gmtool).pushToDevice(project.genymotion.devices[0], listOfFiles)
    }

    @Test
    public void canPushAfterToDeviceWithDest() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def destination = "/sdcard/"
        def listOfFiles = ["src/integTest/res/test/test.txt": destination]

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                pushAfter listOfFiles
                stopWhenFinish false
            }
        }
        VDLaunchDsl device = project.genymotion.devices[0]

        project.tasks.genymotionLaunch.exec()
        device.state = GenymotionVirtualDevice.STATE_ON
        verify(gmtool, never()).pushToDevice(device, listOfFiles)

        project.tasks.genymotionFinish.exec()
        verify(gmtool).pushToDevice(device, listOfFiles)
    }

    @Test
    public void canPushBeforeListToDeviceWithDest() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def destination = "/sdcard/"
        def listOfFiles = ["src/integTest/res/test/test.txt": destination, "src/integTest/res/test/test2.txt": destination]

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                pushBefore listOfFiles
            }
        }
        project.tasks.genymotionLaunch.exec()
        verify(gmtool).pushToDevice(project.genymotion.devices[0], listOfFiles)
    }

    @Test
    public void canPushAfterListToDeviceWithDest() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def destination = "/sdcard/"
        def listOfFiles = ["src/integTest/res/test/test.txt": destination, "src/integTest/res/test/test2.txt": destination]

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                pushAfter listOfFiles
                stopWhenFinish false
            }
        }
        VDLaunchDsl device = project.genymotion.devices[0]

        project.tasks.genymotionLaunch.exec()
        device.state = GenymotionVirtualDevice.STATE_ON
        verify(gmtool, never()).pushToDevice(device, listOfFiles)

        project.tasks.genymotionFinish.exec()
        verify(gmtool).pushToDevice(device, listOfFiles)
    }

    @Test
    public void canPullBeforeFromDevice() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def destination = "/sdcard/"
        def listOfFiles = ["/system/build.prop": destination, "src/integTest/res/test/test2.txt": destination]

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                pullBefore listOfFiles
            }
        }
        project.tasks.genymotionLaunch.exec()
        verify(gmtool).pullFromDevice(project.genymotion.devices[0], listOfFiles)
    }

    @Test
    public void canPullAfterFromDevice() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def listOfFiles = ["/system/build.prop": "/tmp/"]

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                pullAfter listOfFiles
                stopWhenFinish false
            }
        }
        VDLaunchDsl device = project.genymotion.devices[0]

        project.tasks.genymotionLaunch.exec()
        device.state = GenymotionVirtualDevice.STATE_ON
        verify(gmtool, never()).pullFromDevice(device, listOfFiles)

        project.tasks.genymotionFinish.exec()
        verify(gmtool).pullFromDevice(device, listOfFiles)
    }

    @Test
    public void canPullBeforeListToDevice() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def listOfFiles = ["/system/build.prop": "/tmp/" + "build.prop", "/system/bin/adb": "/tmp/adb"]

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                pullBefore listOfFiles
            }
        }
        project.tasks.genymotionLaunch.exec()
        verify(gmtool).pullFromDevice(project.genymotion.devices[0], listOfFiles)
    }

    @Test
    public void canPullAfterListToDevice() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def listOfFiles = ["/system/build.prop": "/tmp/build.prop", "/system/bin/adb": "/tmp/adb"]

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                pullAfter listOfFiles
                stopWhenFinish false
            }
        }
        VDLaunchDsl device = project.genymotion.devices[0]

        project.tasks.genymotionLaunch.exec()
        device.state = GenymotionVirtualDevice.STATE_ON
        verify(gmtool, never()).pullFromDevice(device, listOfFiles)

        project.tasks.genymotionFinish.exec()
        verify(gmtool).pullFromDevice(device, listOfFiles)
    }

    @Test
    public void canFlashDevice() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def path = "src/integTest/res/test/test.zip"

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                flash path
            }
        }
        project.tasks.genymotionLaunch.exec()
        verify(gmtool).flashDevice(project.genymotion.devices[0], path)
    }

    @Test
    public void canFlashListToDevice() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdName = "testDevice"
        def listOfFiles = ["src/integTest/res/test/test.zip", "src/integTest/res/test/test2.zip"]

        when(gmtool.isDeviceCreated(vdName)).thenReturn(true)

        project.genymotion.devices {
            "$vdName" {
                flash listOfFiles
            }
        }

        project.tasks.genymotionLaunch.exec()
        verify(gmtool).flashDevice(project.genymotion.devices[0], listOfFiles)
    }

    @Test
    public void canAvoidAbortForGenymotionPath() {
        project.genymotion {
            config {
                abortOnError = false
                genymotionPath = "wrong/place"
            }
            devices {
                "random" {
                    template "wrong template"
                }
            }
        }

        //if exception throws => test fail
        project.evaluate()
    }

    @Test
    public void canAvoidAbortForDeviceConfig() {
        project.genymotion.config.abortOnError = false

        project.genymotion.devices {
            notExisting
            "random" {
                template "wrong template"
                pushBefore "wrong/path"
                pullBefore "wrong/path": "dest/path"
                pushAfter "wrong/path"
                pullAfter "wrong/path": "dest/path"
                flash "wrong/path"
                install "wrong/path"
            }
        }

        //if exception throws => test fail
        project.evaluate()
    }

    @Test
    @Category(Android)
    public void canAvoidAbortForFlavorConfig() {
        (project, gmtool) = TestTools.getAndroidProject()
        GMTool.metaClass.static.newInstance = { gmtool }

        TestTools.declareFlavors(project)

        project.genymotion.config.abortOnError = false

        project.genymotion.devices {
            notExisting
            "random" {
                productFlavors null, "notKnown", "flavor1"
            }
        }

        //if exception throws => test fail
        project.evaluate()
    }

    @Test
    public void warnWhenUsingConfigStoreCredentials() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        Logger logger = MockSlf4j.mockStatic(Log.class, "logger")

        project.genymotion.config.storeCredentials = true
        project.genymotion.config.verbose = false
        project.evaluate()

        assertThat(logger, hasEntriesCount(1, that(allOf(
                haveMessage(equalTo(GenymotionConfig.STORE_CREDENTIALS_ERROR)),
                haveLevel(LoggingLevel.WARN)
        ))))

    }

    @Test
    public void warnWhenUsingConfigStoreCredentialsWithVerbose() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        Logger logger = MockSlf4j.mockStatic(Log.class, "logger")

        project.genymotion.config.storeCredentials = true
        project.genymotion.config.verbose = true
        project.evaluate()

        assertThat(logger, hasEntriesCount(1, that(allOf(
                haveMessage(equalTo(GenymotionConfig.STORE_CREDENTIALS_ERROR)),
                haveLevel(LoggingLevel.WARN)
        ))))

    }

    @Test
    public void canLaunchAndFinishCloudDevice() {
        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }
        String vdName = "sampleDevice"
        String templateName = "templateName"

        project.genymotion.cloudDevices {
            "$vdName" {
                template templateName
            }
        }
        project.tasks.genymotionLaunch.exec()
        verify(gmtool).startDisposableDevice(templateName, vdName, null, null, null, null, null, null, null)
        verify(gmtool).stopDisposableDevice(vdName)  // stop is called in CloudDeviceController::startDevice
        project.tasks.genymotionFinish.exec()
        verify(gmtool, times(2)).stopDisposableDevice(vdName)
    }

    @After
    public void finishTest() {
        Log.clearLogger()
        cleanMetaClass()
    }

}
