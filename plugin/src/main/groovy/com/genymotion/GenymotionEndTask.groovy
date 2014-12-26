package main.groovy.com.genymotion

import main.groovy.com.genymotion.GMTool
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenymotionEndTask extends DefaultTask {

    String flavor = null

    @TaskAction
    def exec() {

        println("Stopping devices")
        //get the declared devices
        project.genymotion.getDevices(flavor).each(){
            processDeviceEnd(it)
        }
    }

    private void processDeviceEnd(device) {
        println("Stopping ${device.name}")
        //TODO check if the device is already started
        if (device.start) {

                try{
                    it.pushAfter()
                    it.pullAfter()

                    if(it.stopWhenFinish || it.deleteWhenFinish)
                        it.stop()
                    if(it.deleteWhenFinish)
                        GMTool.deleteDevice(it)
                }
                //if a gmtool command fail
                catch(Exception e){

                    println e.getMessage()
                println "Stoping all launched devices and deleting when needed"
                project.genymotion.getDevices(flavor).each() {
                    //we close the opened devices
                    GMTool.stopDevice(device)
                    //and delete them if needed
                    if (device.deleteWhenFinish)
                        GMTool.deleteDevice(device)
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
