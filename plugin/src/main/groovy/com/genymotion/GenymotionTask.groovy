package main.groovy.com.genymotion

import main.groovy.com.genymotion.GenymotionTool
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenymotionTask extends DefaultTask {

    @TaskAction
    def exec() {

        //we set the config inside the GenymotionTool
        GenymotionTool.GENYMOTION_CONFIG = project.genymotion.config

        println("Starting devices")

        //process declared devices
        project.genymotion.getDevices().each(){

            if(it.start){
                println("Starting ${it.name}")
                it.create()
                it.start()
                it.checkAndUpdate()
                it.flash()
                it.install()
                it.push()
            }
        }

        println("-- Running devices --")
        GenymotionTool.getRunningDevices(true)
    }
}
