package main.groovy.com.genymotion

import org.codehaus.groovy.control.messages.ExceptionMessage
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenymotionLaunchTask extends DefaultTask {

    @TaskAction
    def exec() {
        //we configure the environment
        project.genymotion.processConfiguration()

        if (project.genymotion.config.verbose)
            println("Starting devices")

        //process declared devices
        project.genymotion.getDevices().each(){

            if(it.start){
                if (project.genymotion.config.verbose)
                    println("Starting ${it.name}")

                try{
                    it.create()
                    it.checkAndEdit()
                    it.start()
                    it.flash()
                    it.install()
                    it.pushBefore()
                    it.pullBefore()
                }
                //if a gmtool command fail
                catch(Exception e){

                    println e.getMessage()
                    println "Stoping all launched devices and deleting when needed"
                    project.genymotion.getDevices().each(){
                        //we close the opened devices
                        GMTool.stopDevice(it)
                        //and delete them if needed
                        if(it.deleteWhenFinish)
                            GMTool.deleteDevice(it)
                    }
                    //then, we thow a new exception to end task, if needed
                    if(project.genymotion.config.abortOnError)
                        throw new GMToolException("GMTool command failed. Check the output to solve the problem")
                }
            }
        }

        if (project.genymotion.config.verbose) {
            println("-- Running devices --")
            GMTool.getRunningDevices(true)
        }
    }
}
