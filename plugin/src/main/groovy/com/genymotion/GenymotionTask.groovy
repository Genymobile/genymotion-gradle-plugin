package main.groovy.com.genymotion

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenymotionTask extends DefaultTask {

    @TaskAction
    def exec() {

        if (project.genymotion.config.verbose)
            println("Starting devices")

        //process declared devices
        project.genymotion.getDevices().each(){

            if(it.start){
                if (project.genymotion.config.verbose)
                    println("Starting ${it.name}")
                it.create()
                it.start()
                it.checkAndUpdate()
                it.flash()
                it.install()
                it.push()
            }
        }

        if (project.genymotion.config.verbose) {
            println("-- Running devices --")
            GenymotionTool.getRunningDevices(true)
        }
    }
}
