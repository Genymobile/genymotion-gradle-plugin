package main.groovy.com.genymotion

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.internal.reflect.Instantiator
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry

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

        def productFlavors = project.container(ProductFlavor, new ProductFlavorFactory(instantiator, project))

        project.extensions.create('genymotion', GenymotionPluginExtension, project, productFlavors)
        //TODO try to merge the next extensions adds to the previous "create genymotion extension" line. Liek it is done for productFlavors
        project.genymotion.extensions.create('config', GenymotionConfig) //the extension name have to be different from the original nested element's name (receiver)
        project.genymotion.extensions.create('admin', GMToolAdmin) //the extension name have to be different from the original nested element's name (receiver)
        project.genymotion.extensions.create('devices', GenymotionDevices) //the extension name have to be different from the original nested element's name (receiver)

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