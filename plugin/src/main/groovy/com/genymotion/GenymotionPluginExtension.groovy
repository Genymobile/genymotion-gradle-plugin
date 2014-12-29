package main.groovy.com.genymotion

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException


class GenymotionPluginExtension {

    final Project project
    def genymotionConfig = new GenymotionConfig()
    def genymotionAdmin = new GMToolAdmin()
    def genymotionDevices = new GenymotionDevices()

    final NamedDomainObjectContainer<ProductFlavor> productFlavors
    def productFlavorsDevices = []

    public GenymotionConfig currentConfiguration = null


    GenymotionPluginExtension(Project project, productFlavors) {
        this.project = project
        this.productFlavors = productFlavors

        this.genymotionDevices.project = project
    }

    def productFlavors(Closure closure) {
        productFlavors.configure(closure)
    }


    void device(Map params){
        GenymotionVDLaunch device = new GenymotionVDLaunch(params)
        this.genymotionDevices.add(device)
    }

    void device(Map params, String flavorName){
        GenymotionVDLaunch device = new GenymotionVDLaunch(params)

        if(!productFlavorsDevices[flavorName])
            productFlavorsDevices[flavorName] = new GenymotionDevices()

        productFlavorsDevices[flavorName].add(device)
    }


    def getDevices(String flavor = null){
        def devices = []
        devices.addAll(genymotionDevices.devices)

        if(flavor && productFlavorsDevices[flavor]){
            devices.addAll(productFlavorsDevices[flavor])
        }

        return devices
    }

    def checkParams(){
        //TODO Check all the Genymotion configuration and fire Exceptions if needed

        //Check if the flavors entered are good?
    }


    /**
     * Task management
     */

    void injectTasks() {
        def taskLaunch = project.genymotion.config.taskLaunch

        //if the automatic launch is disabled or the configuration is not correct we skip
        if (!project.genymotion.config.automaticLaunch || !taskLaunch)
            return

        try {
            //if taskLaunch is an array of tasks
            if(taskLaunch instanceof ArrayList){
                taskLaunch.each {
                    injectTasksInto(it)
                    //TODO fix to customize the launch/end tasks name
                }
            }

            //if the task is the default android test task
            else if(taskLaunch == AndroidPluginTools.DEFAULT_ANDROID_TASK){

                //and we detect the android plugin or the default android test task
                if(AndroidPluginTools.hasAndroidPlugin(project) || project.tasks.findByName(AndroidPluginTools.DEFAULT_ANDROID_TASK) != null){

                    //if there are flavors
                    if(project.android.productFlavors.size() > 0) {
                        project.android.productFlavors.all { flavor ->
                            injectTasksInto(AndroidPluginTools.getFlavorTaskName(flavor.name), flavor.name)
                        }
                    } else {
                        injectTasksInto(AndroidPluginTools.DEFAULT_ANDROID_TASK)
                    }

                } else {
                    println "$AndroidPluginTools.DEFAULT_ANDROID_TASK not found, genymotionLaunch/Finish tasks are not injected and has to be launched manually."
                    return
                }
            }

            //else, we inject the genymotion tasks around the given taskLaunch
            else if(taskLaunch instanceof String) {
                injectTasksInto(taskLaunch)
            }

            else {
                println "not found, genymotionLaunch/Finish tasks are not injected and has to be launched manually."
                return
            }


        } catch (UnknownTaskException e) {
            println "Task $taskLaunch not found. genymotionLaunch/Finish tasks are not injected and has to be launched manually."
        }
    }

    void injectTasksInto(String taskName, String flavor = null) throws UnknownTaskException{
        def theTask = project.tasks.getByName(taskName)
        println "Adding genymotion dependency to " + taskName

        if(flavor?.trim()){
            Task launchTask = project.tasks.create(AndroidPluginTools.getFlavorLaunchTask(flavor), GenymotionLaunchTask)
            launchTask.flavor = flavor
            theTask.dependsOn(launchTask)

            Task endTask = project.tasks.create(AndroidPluginTools.getFlavorEndTask(flavor), GenymotionEndTask)
            endTask.flavor = flavor
            theTask.finalizedBy(endTask)

        } else {
            theTask.dependsOn(GenymotionGradlePlugin.TASK_LAUNCH)
            theTask.finalizedBy(GenymotionGradlePlugin.TASK_FINISH)
        }
    }


    /**
     * Configuration management
     */

    def processConfiguration() {
        GenymotionConfig config = project.genymotion.config
        if(config.fromFile){
            applyConfigFromFile()
        }

        if(!config.isEmpty()){
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

            if(config.license)
                GMTool.setLicense(config.license)
        }
    }

    def endConfiguration() {
        //if we do not persist the data
        if(!config.persist && this.currentConfiguration){
            GMTool.setConfig(this.currentConfiguration, this.genymotionConfig.verbose)
        }
    }

    def applyConfigFromFile(){
        // We get the APK signing properties from a file
        GenymotionConfig config = project.genymotion.config
        def Properties props = new Properties()
        def propFile = new File(config.fromFile)
        if (propFile.canRead()){
            props.load(new FileInputStream(propFile))

            if (props!=null){
                //Reflection could be another solution to fill the object but I prefer to avoid it.
                //This method allows a better control on the data changed and avoid side loading non-allowed values
                if(props.statistics)
                    config.statistics = props.statistics.toBoolean()
                if(props.username)
                    config.username = props.username
                if(props.password)
                    config.password = props.password
                if(props.store_credentials)
                    config.store_credentials = props.store_credentials.toBoolean()
                if(props.license)
                    config.license = props.license
                if(props.proxy)
                    config.proxy = props.proxy.toBoolean()
                if(props.proxy_address)
                    config.proxy_address = props.proxy_address
                if(props.proxy_port)
                    config.proxy_port = props.proxy_port.toInteger()
                if(props.proxy_auth)
                    config.proxy_auth = props.proxy_auth.toBoolean()
                if(props.proxy_username)
                    config.proxy_username = props.proxy_username
                if(props.proxy_password)
                    config.proxy_password = props.proxy_password
                if(props.virtual_device_path)
                    config.virtual_device_path = props.virtual_device_path
                if(props.sdk_path)
                    config.sdk_path = props.sdk_path
                if(props.use_custom_sdk)
                    config.use_custom_sdk = props.use_custom_sdk.toBoolean()
                if(props.screen_capture_path)
                    config.screen_capture_path = props.screen_capture_path
                if(props.taskLaunch)
                    config.taskLaunch = props.taskLaunch
                if(props.automaticLaunch)
                    config.automaticLaunch = props.automaticLaunch.toBoolean()
                if(props.processTimeout)
                    config.processTimeout = props.processTimeout.toInteger()
                if(props.verbose)
                    config.verbose = props.verbose.toBoolean()
                if(props.persist)
                    config.persist = props.persist.toBoolean()
                if(props.abortOnError)
                    config.abortOnError = props.abortOnError.toBoolean()

            } else {
                logger.error("$config.fromFile file is missing, impossible to load configuration")
            }
        } else {
            logger.error("$config.fromFile file is missing, impossible to load configuration")
        }
    }
}