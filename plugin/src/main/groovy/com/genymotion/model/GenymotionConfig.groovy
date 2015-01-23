package main.groovy.com.genymotion.model

import main.groovy.com.genymotion.tools.AndroidPluginTools
import org.gradle.api.Project

/**
 * Created by eyal on 05/09/14.
 */
class GenymotionConfig {

    //TODO prendre en charge avec et sans le '/' dans le build.gradle
    //plugin config
    def genymotionPath = ""           //set the Genymotion path to PATH

    //genymotion config
    def fromFile = null               //get the whole configuration from a file. The content get the priority to the .gradle file

    def statistics              //enable stats
    def username                //set the login
    def password                //set the password
    def storeCredentials       //keep the configured account logged in genymotion
    def license                 //set license
    def proxy                   //active le proxy ou non (maybe accept true|false)
    def proxyAddress           //set l'adresse du proxy a ADRESS
    def proxyPort              //set the proxy port
    def proxyAuth              //definit le protocole du proxy, voir avec ced pour plus d infos
    def proxyUsername          //definit le protocole du proxy, voir avec ced pour plus d infos
    def proxyPassword          //definit le protocole du proxy, voir avec ced pour plus d infos
    def virtualDevicePath     //set le dossier ou sont stockées les configs
    def sdkPath                //set le dossier du sdk alternatif
    def useCustomSdk          //utilise le sdk alternatif ou non (maybe accept true|false)
    def screenCapturePath     //set le dossier de stockage des screenshots

    //task configs
    def taskLaunch = AndroidPluginTools.DEFAULT_ANDROID_TASK     //define the task that depends on the genymotion launch task
    boolean automaticLaunch = true               //enable or not the genymotion tasks injection
    int processTimeout = 300000                  //timeout for all the processes launched in command line
    boolean verbose = false                       //verbose mode
    boolean persist = true                       //persist the configurations on genymotion or reset them back after the task execution
    boolean abortOnError = true                 //abort the task execution if a GMTool error occurs

    protected String CONFIG_PREFIX = "genymotion."
    private Map CONFIG_PROPERTIES = ["genymotionPath":String.class,
                                     "statistics":Boolean.class,
                                     "username":String.class,
                                     "password":String.class,
                                     "storeCredentials":Boolean.class,
                                     "license":String.class,
                                     "proxy":Boolean.class,
                                     "proxyAddress":String.class,
                                     "proxyPort":Integer.class,
                                     "proxyAuth":Boolean.class,
                                     "proxyUsername":String.class,
                                     "proxyPassword":String.class,
                                     "virtualDevicePath":String.class,
                                     "sdkPath":String.class,
                                     "useCustomSdk":Boolean.class,
                                     "screenCapturePath":String.class,
                                     "taskLaunch":String.class,
                                     "automaticLaunch":Boolean.class,
                                     "processTimeout":Integer.class,
                                     "verbose":Boolean.class,
                                     "persist":Boolean.class,
                                     "abortOnError":Boolean.class]

    boolean isEmpty(){
        if(statistics != null || username != null || password != null || storeCredentials != null || license != null ||
           proxy != null || proxyAddress != null || proxyPort != null || proxyAuth != null || proxyUsername != null ||
           proxyPassword != null || virtualDevicePath != null || sdkPath != null || useCustomSdk != null ||
           screenCapturePath != null)
            return false

        return true
    }

    /**
     * Fill the config object following a property file.
     * The file path is given by the fromFile field or the local.properties located at the root of the project.
     *
     * @param project the Project object
     *
     * @return returns true if the object has been field from a file or false otherwise
     */
    def applyConfigFromFile(Project project){
        // We get the APK signing properties from a file
        def Properties props = new Properties()
        def propFile

        if(fromFile == null && project != null){
            fromFile = AndroidPluginTools.DEFAULT_PROPERTIES
            propFile = new File(project.rootDir, fromFile)
        }
        else if(fromFile != null)
            propFile = new File(fromFile)
        else
            return false

        if (propFile.canRead()){
            props.load(new FileInputStream(propFile))

            if (props!=null){
                //Reflection could be another solution to fill the object but I prefer to avoid it.
                //This method allows a better control on the data changed and avoid side loading non-allowed values

                //we iterate on the properties list to fill the config object
                CONFIG_PROPERTIES.each {key, value ->
                    def val = props.getProperty(CONFIG_PREFIX + key)

                    if(val != null){
                        if(value == Boolean.class)
                            val = val.toBoolean()

                        this.setProperty(key, val.asType(value))
                    }
                }
            } else {
                println "$fromFile file is missing, impossible to load configuration"
                return false
            }
        } else {
            println "$fromFile file is missing, impossible to load configuration"
            return false
        }
        return true
    }
}
