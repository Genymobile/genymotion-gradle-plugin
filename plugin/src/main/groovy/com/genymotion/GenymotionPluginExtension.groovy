package main.groovy.com.genymotion

import org.gradle.api.Project


class GenymotionPluginExtension {

    final Project project
    def genymotionConfig = new GenymotionConfig()
    def genymotionAdmin = new GMToolAdmin()
    def genymotionDevices = new GenymotionDevices()


    GenymotionPluginExtension(Project project) {
        this.project = project

        this.genymotionDevices.project = project
    }

    void device(Map params){
        GenymotionVDLaunch device = new GenymotionVDLaunch(params)
        this.genymotionDevices.add(device)
    }

    def getDevices(){
        genymotionDevices.devices
    }

    static def checkParams(){
        //TODO Check all the Genymotion configuration and fire Exceptions if needed
    }
}