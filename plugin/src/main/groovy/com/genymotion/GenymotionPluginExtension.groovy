package main.groovy.com.genymotion

import org.gradle.api.Project


class GenymotionPluginExtension {

    final Project project
    def genymotionConfig = new GenymotionConfig()
    def genymotionAdmin = new GMToolAdmin()
    def genymotionDevices = new GenymotionDevices()

    public GenymotionConfig currentConfiguration = null


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

    def processConfiguration() {
        GenymotionConfig config = this.genymotionConfig
        if(config.loginFromFile){
            //TODO get the content from file and put it on config
        }

        //if we persists the data
        if(config.persist){
            GMTool.setConfig(config, config.verbose)
        }
        //if we do not persist the data
        else {

            //we store the current configuration
            this.currentConfiguration = GMTool.getConfig(config.verbose)

            //we forget the login info during configuration
            String username = config.username
            String password = config.password
            config.username = null
            config.password = null
            GMTool.setConfig(config, config.verbose)
            config.username = username
            config.password = password
        }
    }

    def endConfiguration() {
        //if we persists the data
        if(!config.persist){
            GMTool.setConfig(this.currentConfiguration, this.genymotionConfig.verbose)
        }
    }
}