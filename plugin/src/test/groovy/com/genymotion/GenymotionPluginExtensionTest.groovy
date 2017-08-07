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

import com.genymotion.tools.AndroidPluginTools
import com.genymotion.tools.GMTool
import com.genymotion.tools.GMToolException
import org.gradle.api.Project
import org.gradle.api.Task
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category

import static org.junit.Assert.fail

class GenymotionPluginExtensionTest extends CleanMetaTest {

    Project project
    GMTool gmtool

    @Test
    @Category(Android)
    public void canConfigFromLocalProperties() {

        (project, gmtool) = TestTools.getAndroidProject()
        GMTool.metaClass.static.newInstance = { gmtool }

        project.evaluate()

        //@formatter:off
        assert project.genymotion.config.username       == "user"
        assert project.genymotion.config.password       == "password"
        assert project.genymotion.config.statistics     == true
        assert project.genymotion.config.proxyPort      == 100
        assert project.genymotion.config.licenseServer  == true
        assert project.genymotion.config.genymotionPath == "/path/to/genymotion/"
        //@formatter:on

    }

    @Test
    @Category(Android)
    public void checkLocalPropertiesIsAppliedBeforeCallingGMTool() {

        (project, gmtool) = TestTools.getAndroidProject()
        GMTool.metaClass.getVersion = {
            cmd([""], {})
            return "2.6.0"
        }

        GMTool.metaClass.cmd = { def command, Closure c ->
            // We check the genymotion path has been set before calling the gmtool binary
            assert project.genymotion.config.genymotionPath == "/path/to/genymotion/"
        }

        project.evaluate()
    }

    @Test
    public void canInjectToCustomTask() {

        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String taskName = "dummy"
        project.task(taskName) << {}

        project.genymotion.config.taskLaunch = taskName
        project.evaluate()

        def task = project.tasks.getByName(taskName)
        def finishTask = project.tasks.getByName(GenymotionPluginExtension.getFinishTaskName(taskName))

        assert task.getTaskDependencies().getDependencies()*.name.contains(GenymotionPluginExtension.getLaunchTaskName(taskName))
        assert task.finalizedBy.getDependencies().contains(finishTask)
    }

    @Test
    public void canInjectToCustomTasks() {

        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        def tasks = []
        3.times {
            String taskName = "dummy$it"
            project.task(taskName) << {}
            tasks.add(taskName)
        }

        project.genymotion.config.taskLaunch = tasks
        project.evaluate()

        tasks.each {
            def task = project.tasks.getByName(it)
            def finishTask = project.tasks.getByName(GenymotionPluginExtension.getFinishTaskName(it))

            assert task.getTaskDependencies().getDependencies()*.name.contains(GenymotionPluginExtension.getLaunchTaskName(it))
            assert task.finalizedBy.getDependencies().contains(finishTask)
        }

    }

    @Test
    @Category(Android)
    public void canInjectToDefaultAndroidTask() {

        (project, gmtool) = TestTools.getAndroidProject()
        GMTool.metaClass.static.newInstance = { gmtool }

        project.evaluate()

        String version = AndroidPluginTestTools.getPluginVersion()
        String taskName = AndroidPluginTestTools.getDefaultTestTask(version)

        def task = project.tasks.getByName(taskName)
        def finishTask = project.tasks.getByName(GenymotionPluginExtension.getFinishTaskName(taskName))

        assert task.getTaskDependencies().getDependencies()*.name.contains(GenymotionPluginExtension.getLaunchTaskName(taskName))
        assert task.finalizedBy.getDependencies().contains(finishTask)
    }

    @Test
    @Category(Android)
    public void canInjectToVariants() {

        (project, gmtool) = TestTools.getAndroidProject()
        GMTool.metaClass.static.newInstance = { gmtool }

        project.genymotion.config.verbose = true

        TestTools.declareFlavors(project)
        project.evaluate()

        project.android.testVariants.all { variant ->

            Task connectedTask = variant.variantData.connectedTestTask
            Task launchTask = project.tasks.findByName(AndroidPluginTools.getFlavorLaunchTask(connectedTask.name))
            Task finishTask = project.tasks.findByName(AndroidPluginTools.getFlavorFinishTask(connectedTask.name))

            assert connectedTask.getTaskDependencies().getDependencies().contains(launchTask)
            assert connectedTask.finalizedBy.getDependencies().contains(finishTask)
        }
    }

    @Test
    @Category(Android)
    public void canCheckProductFlavorsAndAbort() {

        (project, gmtool) = TestTools.getAndroidProject()
        GMTool.metaClass.static.newInstance = { gmtool }

        TestTools.declareFlavors(project)

        def wrongFlavor = "NONONO"
        String device2 = "device2"

        project.genymotion.config.abortOnError = true
        project.genymotion.devices {
            "device1" {}
            "$device2" {
                productFlavors wrongFlavor
            }
        }

        try {
            project.evaluate()
            fail("Expected GMToolException")
        } catch (Exception e) {
            assert e.cause.causes[0] instanceof GMToolException
            assert e.cause.causes[0].message == "Product flavor $wrongFlavor on device $device2 does not exist."
        }
    }

    @Test
    @Category(Android)
    public void canCheckNullProductFlavorsAndAbort() {

        (project, gmtool) = TestTools.getAndroidProject()
        GMTool.metaClass.static.newInstance = { gmtool }

        TestTools.declareFlavors(project)

        String device2 = "device2"

        project.genymotion.config.abortOnError = true
        project.genymotion.devices {
            "device1" {}
            "$device2" {
                productFlavors "flavor1", null
            }
        }

        try {
            project.evaluate()
            fail("Expected GMToolException")
        } catch (Exception e) {
            assert e.cause.causes[0] instanceof GMToolException
            assert e.cause.causes[0].message == "You entered a null product flavor on device $device2. Please remove it to be able to continue the job"
        }
    }

    @Test
    public void canGetGenymotionDevices() {

        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        project.genymotion.devices {
            "default" {}
            "both" {
                productFlavors "flavor1", "flavor2"
            }
            "product1" {
                productFlavors "flavor1"
            }
            "product2" {
                productFlavors "flavor2"
            }
        }

        //IMPORTANT: Tab needs to be in alphabetical order
        assert project.genymotion.getDevices()*.name == ["both", "default", "product1", "product2"]
        assert project.genymotion.getDevices("flavor1")*.name == ["both", "default", "product1"]
        assert project.genymotion.getDevices("flavor2")*.name == ["both", "default", "product2"]
        assert project.genymotion.getDevices("toto")*.name == ["default"]
    }

    @Test
    public void canGetGenymotionCloudDevices() {

        (project, gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        project.genymotion.cloudDevices {
            "default" {}
            "both" {
                productFlavors "flavor1", "flavor2"
            }
            "product1" {
                productFlavors "flavor1"
            }
            "product2" {
                productFlavors "flavor2"
            }
        }

        //IMPORTANT: Tab needs to be in alphabetical order
        assert project.genymotion.getCloudDevices()*.name == ["both", "default", "product1", "product2"]
        assert project.genymotion.getCloudDevices("flavor1")*.name == ["both", "default", "product1"]
        assert project.genymotion.getCloudDevices("flavor2")*.name == ["both", "default", "product2"]
        assert project.genymotion.getCloudDevices("toto")*.name == ["default"]
    }

    @After
    public void finishTest() {
        cleanMetaClass()
    }
}
