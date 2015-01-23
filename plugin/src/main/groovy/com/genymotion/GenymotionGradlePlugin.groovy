package main.groovy.com.genymotion

import main.groovy.com.genymotion.tools.GMTool
import main.groovy.com.genymotion.model.GenymotionConfig
import main.groovy.com.genymotion.model.VDLaunchCall
import main.groovy.com.genymotion.model.VDLaunchCallFactory
import main.groovy.com.genymotion.tasks.GenymotionFinishTask
import main.groovy.com.genymotion.tasks.GenymotionLaunchTask
import org.gradle.api.Project
import org.gradle.api.Plugin
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

        def devicesLaunch = project.container(VDLaunchCall, new VDLaunchCallFactory(instantiator, project))

        project.extensions.create('genymotion', GenymotionPluginExtension, project, devicesLaunch)
        //TODO try to merge the next extensions adds to the previous "create genymotion extension" line. Liek it is done for productFlavors
        project.genymotion.extensions.create('config', GenymotionConfig) //the extension name have to be different from the original nested element's name (receiver)

        project.task(TASK_LAUNCH, type: GenymotionLaunchTask){
            description 'Starting task for Genymotion plugin'
            group PLUGIN_GROUP
        }
        project.task(TASK_FINISH, type: GenymotionFinishTask){
            description 'Finishing task for Genymotion plugin'
            group PLUGIN_GROUP
        }

        //we set the config inside the GenymotionTool
        GMTool.GENYMOTION_CONFIG = project.genymotion.config

        project.afterEvaluate {

            project.genymotion.checkParams()

            project.genymotion.injectTasks()

        }
    }
}