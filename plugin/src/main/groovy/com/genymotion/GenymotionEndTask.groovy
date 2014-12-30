package main.groovy.com.genymotion

import main.groovy.com.genymotion.GMTool
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenymotionEndTask extends DefaultTask {

    String flavor = null

    @TaskAction
    def exec() {

        println("Finishing devices")
        //get the declared devices
        project.genymotion.getDevices(flavor).each(){
            processDeviceEnd(it)
        }
    }

    def processDeviceEnd(device) {
        println("Finishing ${device.name}")
        //TODO check if the device is already started
        if (device.start) {

            try{
                device.pushAfter()
                device.pullAfter()
                device.stopWhenFinish()
                device.deleteWhenFinish()
            }
            //if a gmtool command fail
            catch(Exception e){
                e.printStackTrace()
                println e.getMessage()
                println "Stoping all launched devices and deleting when needed"
                project.genymotion.getDevices(flavor).each() {
                    //we close the opened devices
                    device.stopWhenFinish()
                    //and delete them if needed
                    device.deleteWhenFinish()
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
