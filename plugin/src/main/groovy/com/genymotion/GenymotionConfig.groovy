package main.groovy.com.genymotion

/**
 * Created by eyal on 05/09/14.
 */
class GenymotionConfig {

    //plugin config
    def genymotionPath = ""           //set the Genymotion path to PATH

    //genymotion config
    def fromFile                //get the whole configuration from a file. The content get the priority to the .gradle file

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


    boolean isEmpty(){
        if(statistics != null || username != null || password != null || storeCredentials != null || license != null ||
           proxy != null || proxyAddress != null || proxyPort != null || proxyAuth != null || proxyUsername != null ||
           proxyPassword != null || virtualDevicePath != null || sdkPath != null || useCustomSdk != null ||
           screenCapturePath != null)
            return false

        return true
    }
}
