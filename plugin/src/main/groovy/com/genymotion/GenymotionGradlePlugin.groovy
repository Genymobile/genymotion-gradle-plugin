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

package main.groovy.com.genymotion

import main.groovy.com.genymotion.model.GenymotionConfig
import main.groovy.com.genymotion.model.VDLaunchDsl
import main.groovy.com.genymotion.model.VDLaunchDslFactory
import main.groovy.com.genymotion.tasks.GenymotionFinishTask
import main.groovy.com.genymotion.tasks.GenymotionLaunchTask
import main.groovy.com.genymotion.tools.GMTool
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

class GenymotionGradlePlugin implements Plugin<Project> {

    static final String PLUGIN_GROUP = "Genymotion"
    static final String TASK_LAUNCH = "genymotionLaunch"
    static final String TASK_FINISH = "genymotionFinish"

    protected Instantiator instantiator

    @Inject
    GenymotionGradlePlugin(Instantiator instantiator){
        this.instantiator = instantiator
        //TODO check if gradle.services.get(Instantiator) is a better way to reach it
    }

    void apply(Project project) {

        def devicesLaunch = project.container(VDLaunchDsl, new VDLaunchDslFactory(instantiator, project))

        project.extensions.create('genymotion', GenymotionPluginExtension, project, devicesLaunch)
        project.genymotion.extensions.create('config', GenymotionConfig) //the extension name have to be different from the original nested element's name (receiver)

        project.task(TASK_LAUNCH, type: GenymotionLaunchTask){
            description 'Starting task for Genymotion plugin'
            group PLUGIN_GROUP
        }
        project.task(TASK_FINISH, type: GenymotionFinishTask){
            description 'Finishing task for Genymotion plugin'
            group PLUGIN_GROUP
        }

        //we set the config inside the GMTool
        GMTool.GENYMOTION_CONFIG = project.genymotion.config

        project.afterEvaluate {

            project.genymotion.processConfiguration()
            project.genymotion.checkParams()
            project.genymotion.injectTasks()

        }
    }
}