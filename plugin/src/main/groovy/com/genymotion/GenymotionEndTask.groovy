package main.groovy.com.genymotion

import main.groovy.com.genymotion.GMTool
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenymotionEndTask extends DefaultTask {

    @TaskAction
    def exec() {

        println("Stopping devices")
        //get the declared devices
        project.genymotion.getDevices().each(){

            println("Stopping ${it.name}")
            //TODO check if the device is already started
            if(it.start) {

                try{
                    it.pushAfter()
                    it.pullAfter()
                    GMTool.stopDevice(it)
                    if(it.deleteWhenFinish)
                        GMTool.deleteDevice(it)
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
                //anyway...
                finally {
                    //we end the configuration
                    project.genymotion.endConfiguration()
                }

            }
        }
    }
}
