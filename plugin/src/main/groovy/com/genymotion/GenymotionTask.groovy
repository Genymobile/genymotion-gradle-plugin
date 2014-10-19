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

                try{
                    it.create()
                    it.start()
                    it.checkAndUpdate()
                    it.flash()
                    it.install()
                    it.pushBefore()
                    it.pullBefore()

                //if a gmtool command fail
                }catch(GMToolException e){

                    println e.getMessage()
                    println "Stoping all launched devices and deleting when needed"
                    project.genymotion.getDevices().each(){
                        //we close the opened devices
                        GenymotionTool.stopDevice(it)
                        //and delete them if needed
                        if(it.deleteWhenFinish)
                            GenymotionTool.deleteDevice(it)
                    }
                    //then, we thow a new exception to end task, if needed
                    if(project.genymotion.config.abortOnError)
                    throw new GMToolException("GMTool command failed. Check the output to solve the problem")
                }
            }
        }

        if (project.genymotion.config.verbose) {
            println("-- Running devices --")
            GenymotionTool.getRunningDevices(true)
        }
    }
}
