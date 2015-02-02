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
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenymotionGradlePlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package main.groovy.com.genymotion

import main.groovy.com.genymotion.tools.AndroidPluginTools
import main.groovy.com.genymotion.tools.GMTool
import main.groovy.com.genymotion.model.GenymotionConfig
import main.groovy.com.genymotion.model.VDLaunchCall
import main.groovy.com.genymotion.tasks.GenymotionFinishTask
import main.groovy.com.genymotion.tasks.GenymotionLaunchTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException

class GenymotionPluginExtension {

    final Project project
    def genymotionConfig = new GenymotionConfig()

    private final NamedDomainObjectContainer<VDLaunchCall> deviceLaunches

    public GenymotionConfig currentConfiguration = null


    GenymotionPluginExtension(Project project, deviceLaunches) {
        this.project = project
        this.deviceLaunches = deviceLaunches
    }

    //TODO handle declaration when there is no closure after name (ex: genymotion.devices{"name"})
    def devices(Closure closure) {
        deviceLaunches.configure(closure)
    }

    def getDevices(String flavor = null){

        if(flavor == null)
            return deviceLaunches.toList()

        def devices = []
        deviceLaunches.each {
            if(it.hasFlavor(flavor))
                devices.add(it)
        }
        return devices
    }

    def checkParams(){
        //TODO Check all the Genymotion configuration and fire Exceptions if needed

        //check similar names
        //Check if the flavors entered are good?
        //check params types
        deviceLaunches.each {
            it.checkParams()
        }

        //check gmtool path is found
        GMTool.usage()
    }


    /**
     * Task management
     */

    void injectTasks() {
        def taskLaunch = project.genymotion.config.taskLaunch

        //if the automatic launch is disabled or the configuration is not correct we skip
        if (!project.genymotion.config.automaticLaunch || !taskLaunch)
            return

        try {
            //if taskLaunch is an array of tasks
            if(taskLaunch instanceof ArrayList){
                taskLaunch.each {
                    injectTasksInto(it)
                    //TODO fix to customize the launch/finish tasks name
                }
            }

            //if the task is the default android test task
            else if(taskLaunch == AndroidPluginTools.DEFAULT_ANDROID_TASK){

                //and we detect the android plugin or the default android test task
                if(AndroidPluginTools.hasAndroidPlugin(project) || project.tasks.findByName(AndroidPluginTools.DEFAULT_ANDROID_TASK) != null){

                    //if there are flavors
                    if(project.android.productFlavors.size() > 0) {
                        project.android.productFlavors.all { flavor ->
                            injectTasksInto(AndroidPluginTools.getFlavorTaskName(flavor.name), flavor.name)
                        }
                    } else {
                        injectTasksInto(AndroidPluginTools.DEFAULT_ANDROID_TASK)
                    }

                } else {
                    println "$AndroidPluginTools.DEFAULT_ANDROID_TASK not found, genymotionLaunch/Finish tasks are not injected and has to be launched manually."
                    return
                }
            }

            //else, we inject the genymotion tasks around the given taskLaunch
            else if(taskLaunch instanceof String) {
                injectTasksInto(taskLaunch)
            }

            else {
                println "not found, genymotionLaunch/Finish tasks are not injected and has to be launched manually."
                return
            }


        } catch (UnknownTaskException e) {
            println "Task $taskLaunch not found. genymotionLaunch/Finish tasks are not injected and has to be launched manually."
        }
    }

    void injectTasksInto(String taskName, String flavor = null) throws UnknownTaskException{
        def theTask = project.tasks.getByName(taskName)
        println "Adding genymotion dependency to " + taskName

        if(flavor?.trim()){
            Task launchTask = project.tasks.create(AndroidPluginTools.getFlavorLaunchTask(flavor), GenymotionLaunchTask)
            launchTask.flavor = flavor
            theTask.dependsOn(launchTask)

            Task finishTask = project.tasks.create(AndroidPluginTools.getFlavorFinishTask(flavor), GenymotionFinishTask)
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

        if(!config.isEmpty()){
            //if we persists the data
            if(config.persist){
                GMTool.setConfig(config, config.verbose)
            }
            //if we do not persist the data
            else {
                //we store the current configuration
                this.currentConfiguration = GMTool.getConfig(config.verbose)

                //we forget the login info during configuration
                String username = config.username
                String password = config.password
                config.username = null
                config.password = null
                GMTool.setConfig(config, config.verbose)
                config.username = username
                config.password = password
            }

            if(config.license)
                GMTool.setLicense(config.license)
        }
    }

    def endConfiguration() {
        //if we do not persist the data
        if(!config.persist && this.currentConfiguration){
            GMTool.setConfig(this.currentConfiguration, this.genymotionConfig.verbose)
        }
    }
}