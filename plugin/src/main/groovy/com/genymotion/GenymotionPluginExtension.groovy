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
import com.genymotion.model.VDLaunchDsl
import com.genymotion.tasks.GenymotionFinishTask
import com.genymotion.tasks.GenymotionLaunchTask
import com.genymotion.tools.AndroidPluginTools
import com.genymotion.tools.GMTool
import com.genymotion.tools.GMToolException
import com.genymotion.tools.Log
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException

class GenymotionPluginExtension {

    final Project project

    def genymotionConfig = new GenymotionConfig()

    private final NamedDomainObjectContainer<VDLaunchDsl> deviceLaunches

    public GenymotionConfig currentConfiguration = null


    GenymotionPluginExtension(Project project, deviceLaunches) {
        this.project = project
        this.deviceLaunches = deviceLaunches
    }

    def devices(Closure closure) {
        deviceLaunches.configure(closure)
    }

    def getDevices(String flavor = null) {
        if (flavor == null) {
            return deviceLaunches.toList()
        }

        def devices = []
        deviceLaunches.each {
            if (it.hasFlavor(flavor)) {
                devices.add(it)
            }
        }
        return devices
    }

    def checkParams() {

        //Check if the flavors entered exist
        checkProductFlavors()

        deviceLaunches.each {
            it.checkParams(project.genymotion.config.abortOnError)
        }

        //check gmtool path is found
        GMTool.usage()
    }

    public void checkProductFlavors() {
        if (!AndroidPluginTools.hasAndroidPlugin(project)) {
            return
        }

        def androidFlavors = project.android.productFlavors*.name

        deviceLaunches.each {
            for (String flavor in it.productFlavors) {

                if (flavor == null) {
                    if (project.genymotion.config.abortOnError) {
                        throw new GMToolException("You entered a null product flavor on device $it.name. Please remove it to be able to continue the job")
                    } else {
                        Log.warn("You entered a null product flavor on device $it.name. It will be ignored.")
                    }

                } else if (!androidFlavors.contains(flavor)) {
                    if (project.genymotion.config.abortOnError) {
                        throw new GMToolException("Product flavor $flavor on device $it.name does not exist.")
                    } else {
                        Log.warn("Product flavor $flavor does not exist. It will be ignored.")
                    }
                }
            }

        }
    }
    /**
     * Task management
     */

    void injectTasks() {
        def taskLaunch = project.genymotion.config.taskLaunch

        if (!project.genymotion.config.automaticLaunch) {
            Log.info("Genymotion automatic launch disabled. Set automaticLaunch to true if you want to start genymotion devices automatically.")
            return
        }
        if (!taskLaunch) {
            Log.info("No task defined to launch Genymotion devices. Set a correct taskLaunch if you want to start genymotion devices automatically.")
            return
        }

        try {
            if (taskLaunch instanceof ArrayList) {
                taskLaunch.each {
                    injectTasksInto(it)
                }

            } else if (taskLaunch == AndroidPluginTools.DEFAULT_ANDROID_TASK) {

                //if we detect the android plugin or the default android test task
                if (AndroidPluginTools.hasAndroidPlugin(project) || project.tasks.findByName(AndroidPluginTools.DEFAULT_ANDROID_TASK) != null) {

                    if (project.android.productFlavors.size() > 0) {
                        project.android.productFlavors.all { flavor ->
                            injectTasksInto(AndroidPluginTools.getFlavorTestTaskName(flavor.name), flavor.name)
                        }
                    } else {
                        injectTasksInto(AndroidPluginTools.DEFAULT_ANDROID_TASK)
                    }

                } else {
                    Log.info("$AndroidPluginTools.DEFAULT_ANDROID_TASK not found, genymotionLaunch/Finish tasks are not injected and has to be launched manually.")
                    return
                }

            } else if (taskLaunch instanceof String) {
                injectTasksInto(taskLaunch)

            } else {
                Log.warn("No destination task found, genymotionLaunch/Finish tasks are not injected and has to be launched manually.")
                return
            }


        } catch (UnknownTaskException e) {
            Log.error("Task $taskLaunch not found. genymotionLaunch/Finish tasks are not injected and has to be launched manually.")
        }
    }

    public void injectTasksInto(String taskName, String flavor = null) throws UnknownTaskException {
        def theTask = project.tasks.getByName(taskName)
        if (project.genymotion.config.verbose) {
            Log.info("Adding genymotion dependency to " + taskName)
        }

        if (flavor?.trim()) {
            Task launchTask = project.tasks.create(AndroidPluginTools.getFlavorLaunchTask(taskName), GenymotionLaunchTask)
            launchTask.flavor = flavor
            theTask.dependsOn(launchTask)

            Task finishTask = project.tasks.create(AndroidPluginTools.getFlavorFinishTask(taskName), GenymotionFinishTask)
            finishTask.flavor = flavor
            theTask.finalizedBy(finishTask)

        } else {
            theTask.dependsOn(GenymotionGradlePlugin.TASK_LAUNCH)
            theTask.finalizedBy(GenymotionGradlePlugin.TASK_FINISH)
        }
    }

    /**
     * Configuration management
     */

    def processConfiguration() {
        GenymotionConfig config = project.genymotion.config
        config.applyConfigFromFile(project)

        if (!config.isEmpty()) {

            if (config.persist) {
                GMTool.setConfig(config, config.verbose)

            } else {
                //we store the current configuration
                this.currentConfiguration = GMTool.getConfig(config.verbose)

                //we don't change the login info during configuration
                String username = config.username
                String password = config.password
                config.username = null
                config.password = null
                GMTool.setConfig(config, config.verbose)
                config.username = username
                config.password = password
            }

            if (config.license) {
                GMTool.setLicense(config.license)
            }
        }
    }

    def endConfiguration() {
        if (!config.persist && this.currentConfiguration) {
            GMTool.setConfig(this.currentConfiguration, this.genymotionConfig.verbose)
        }
    }
}
