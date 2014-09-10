package com.genymotion

import main.groovy.com.genymotion.GenymotionVirtualDevice
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenymotionTask extends DefaultTask {

    @TaskAction
    def exec() {

        println("Starting devices")
        //get the declared devices
        project.genymotion.getDevices().each(){
            println("Starting ${it.name}")
            def cmd = [project.genymotion.config.genymotionPath+"player", '--vm-name', it.name]

            def process = cmd.execute()
            println process.text
        }


//        println "${project.genymotionArgs.message} from ${project.genymotionArgs.sender}"
//        println "Receiver ${project.genymotionArgs.nested.name} from ${project.genymotionArgs.nested.email}"
//        println "Receiver2 ${project.genymotionArgs.nested2.name} from ${project.genymotionArgs.nested2.email}"
//        println "proxy: ${project.genymotionArgs.nestedProxy.ip}"
    }
}
