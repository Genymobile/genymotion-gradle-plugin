package main.groovy.com.genymotion

import org.gradle.api.Project
import org.gradle.api.Plugin


class GenymotionGradlePlugin implements Plugin<Project> {

    static final String PLUGIN_GROUP = "Genymotion"
    static final String TASK_LAUNCH = "genymotionLaunch"
    static final String TASK_FINISH = "genymotionFinish"

    void apply(Project project) {

        project.extensions.create('genymotion', GenymotionPluginExtension, project);
        project.genymotion.extensions.create('config', GenymotionConfig); //the extension name have to be different from the original nested element's name (receiver)
        project.genymotion.extensions.create('admin', GMToolAdmin); //the extension name have to be different from the original nested element's name (receiver)
        project.genymotion.extensions.create('devices', GenymotionDevices); //the extension name have to be different from the original nested element's name (receiver)

        project.task(TASK_LAUNCH, type: GenymotionLaunchTask){
            description 'Starting task for Genymotion plugin'
            group PLUGIN_GROUP
        }
        project.task(TASK_FINISH, type: GenymotionEndTask){
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