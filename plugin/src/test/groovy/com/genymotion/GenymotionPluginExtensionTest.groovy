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
import com.genymotion.tools.GMToolException
import org.gradle.api.Project
import org.gradle.api.Task
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.*

class GenymotionPluginExtensionTest {

    Project project

    @Before
    public void setUp() {
    }

    @Test
    public void canConfigFromLocalProperties() {

        project = TestTools.getAndroidProject()
        project.evaluate()
        project.genymotion.processConfiguration()

        assertEquals("genymotion.username is not catched from local.properties",    "user",     project.genymotion.config.username)
        assertEquals("genymotion.password is not catched from local.properties",    "password", project.genymotion.config.password)
        assertEquals("genymotion.statistics is not catched from local.properties",  true,       project.genymotion.config.statistics)
        assertEquals("genymotion.proxyPort is not catched from local.properties",   100,        project.genymotion.config.proxyPort)
    }

    @Test
    public void canInjectToCustomTask() {

        project = TestTools.init()

        String taskName = "dummy"
        project.task(taskName) << {}

        project.genymotion.config.taskLaunch = taskName
        project.genymotion.injectTasks()

        def task = project.tasks.getByName(taskName)
        def finishTask = project.tasks.getByName(GenymotionGradlePlugin.TASK_FINISH)

        assertTrue("Launch task not injected", task.dependsOn.contains(GenymotionGradlePlugin.TASK_LAUNCH))
        assertTrue("Finish task not injected", task.finalizedBy.getDependencies().contains(finishTask))
    }

    @Test
    public void canInjectToCustomTasks() {

        project = TestTools.init()

        def tasks = []
        3.times {
            String taskName = "dummy$it"
            project.task(taskName) << {}
            tasks.add(taskName)
        }

        project.genymotion.config.taskLaunch = tasks

        project.genymotion.injectTasks()

        tasks.each {
            def task = project.tasks.getByName(it)
            def finishTask = project.tasks.getByName(GenymotionGradlePlugin.TASK_FINISH)

            assertTrue("Launch task not injected", task.dependsOn.contains(GenymotionGradlePlugin.TASK_LAUNCH))
            assertTrue("Finish task not injected", task.finalizedBy.getDependencies().contains(finishTask))
        }

    }

    @Test
    public void canInjectToDefaultAndroidTask() {

        project = TestTools.getAndroidProject()
        project.genymotion.config.verbose = true
        project.evaluate()

        String taskName = AndroidPluginTools.DEFAULT_ANDROID_TASK

        def task = project.tasks.getByName(taskName)
        def finishTask = project.tasks.getByName(GenymotionGradlePlugin.TASK_FINISH)

        assertTrue("Launch task not injected", task.dependsOn.contains(GenymotionGradlePlugin.TASK_LAUNCH))
        assertTrue("Finish task not injected", task.finalizedBy.getDependencies().contains(finishTask))

        def debugTaskName = AndroidPluginTools.getFlavorAssembleDebugTaskName()
        def debugTask = project.tasks.getByName(debugTaskName) //throw exception if task not found
        def taskLaunch = project.tasks.getByName(AndroidPluginTools.getFlavorLaunchTask(debugTaskName))
        assert debugTask.dependsOn.contains(taskLaunch)
    }

    @Test
    public void canInjectToVariants() {

        project = TestTools.getAndroidProject()
        project.android.productFlavors{
            flavor1
            flavor2
        }
        project.evaluate()

        String taskName = AndroidPluginTools.DEFAULT_ANDROID_TASK

        project.android.productFlavors.all { flavor ->
            String flavorTaskName = AndroidPluginTools.getFlavorTestTaskName(flavor.name)
            def task = project.tasks.getByName(flavorTaskName)

            Task launchTask = project.tasks.findByName(AndroidPluginTools.getFlavorLaunchTask(flavorTaskName))
            Task finishTask = project.tasks.findByName(AndroidPluginTools.getFlavorFinishTask(flavorTaskName))

            assert task.dependsOn.contains(launchTask)
            assert task.finalizedBy.getDependencies().contains(finishTask)

            def debugTaskName = AndroidPluginTools.getFlavorAssembleDebugTaskName("flavor1")
            def debugTask = project.tasks.getByName(debugTaskName)
            def taskLaunch = project.tasks.getByName(AndroidPluginTools.getFlavorLaunchTask(debugTaskName))
            assert debugTask.dependsOn.contains(taskLaunch)

            debugTaskName = AndroidPluginTools.getFlavorAssembleDebugTaskName("flavor2")
            debugTask = project.tasks.getByName(debugTaskName)
            taskLaunch = project.tasks.getByName(AndroidPluginTools.getFlavorLaunchTask(debugTaskName))
            assert debugTask.dependsOn.contains(taskLaunch )
        }
    }

    @Test
    public void canCheckProductFlavorsAndAbort() {
        project = TestTools.getAndroidProject()
        project.android.productFlavors{
            flavor1
            flavor2
        }

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
    public void canCheckNullProductFlavorsAndAbort() {
        project = TestTools.getAndroidProject()
        project.android.productFlavors{
            flavor1
            flavor2
        }

        def wrongFlavor = "NONONO"
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

        project = TestTools.init()

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
        assertEquals(["both", "default", "product1", "product2"], project.genymotion.getDevices()*.name)
        assertEquals(["both", "default", "product1"], project.genymotion.getDevices("flavor1")*.name)
        assertEquals(["both", "default", "product2"], project.genymotion.getDevices("flavor2")*.name)
        assertEquals(["default"], project.genymotion.getDevices("toto")*.name)
    }

    @After
    public void finishTest() {
        TestTools.cleanAfterTests()
    }
}
