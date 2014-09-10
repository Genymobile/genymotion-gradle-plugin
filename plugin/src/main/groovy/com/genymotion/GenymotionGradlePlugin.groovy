package com.genymotion

import main.groovy.com.genymotion.GenymotionAdmin
import main.groovy.com.genymotion.GenymotionConfig
import main.groovy.com.genymotion.GenymotionDevices
import org.gradle.api.Project
import org.gradle.api.Plugin


class GenymotionGradlePlugin implements Plugin<Project> {

    void apply(Project project) {
        project.extensions.create('genymotion', GenymotionPluginExtension, project);
        project.genymotion.extensions.create('config', GenymotionConfig); //the extension name have to be different from the original nested element's name (receiver)
        project.genymotion.extensions.create('admin', GenymotionAdmin); //the extension name have to be different from the original nested element's name (receiver)
        project.genymotion.extensions.create('devices', GenymotionDevices); //the extension name have to be different from the original nested element's name (receiver)


        if(project.plugins.hasPlugin('android')){

            //inject an action before compiling to launch devices
            preBuild.doFirst {

                println "Do before preBuilt"
                //execute the genymotion.admin commands
                //TODO

                //start and configure genymotion.devices
                //TODO
                println "Done before preBuilt"

            }

            connectedAndroidTest.doLast{
                //inject an event after testing to close devices
                //TODO

                println "Done after connectedAndroidTest"
            }
        }


        project.task('genymotion', type: GenymotionTask)
        project.task('cmd', type: CmdTask)
    }
}