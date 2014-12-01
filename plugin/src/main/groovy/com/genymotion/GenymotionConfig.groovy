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
    def store_credentials       //keep the configured account logged in genymotion
    def license                 //set license
    def proxy                   //active le proxy ou non (maybe accept true|false)
    def proxy_address           //set l'adresse du proxy a ADRESS
    def proxy_port              //set the proxy port
    def proxy_auth              //definit le protocole du proxy, voir avec ced pour plus d infos
    def proxy_username          //definit le protocole du proxy, voir avec ced pour plus d infos
    def proxy_password          //definit le protocole du proxy, voir avec ced pour plus d infos
    def virtual_device_path     //set le dossier ou sont stockées les configs
    def sdk_path                //set le dossier du sdk alternatif
    def use_custom_sdk          //utilise le sdk alternatif ou non (maybe accept true|false)
    def screen_capture_path     //set le dossier de stockage des screenshots

    //task configs
    def taskLaunch = AndroidPluginTools.DEFAULT_ANDROID_TASK     //define the task that depends on the genymotion launch task
    boolean automaticLaunch = true               //enable or not the genymotion tasks injection
    int processTimeout = 300000                  //timeout for all the processes launched in command line
    boolean verbose = true                       //verbose mode
    boolean persist = true                       //persist the configurations on genymotion or reset them back after the task execution
    boolean abortOnError = false                 //abort the task execution if a GMTool error occurs


    boolean isEmpty(){
        if(statistics != null || username != null || password != null || store_credentials != null || license != null ||
           proxy != null || proxy_address != null || proxy_port != null || proxy_auth != null || proxy_username != null ||
           proxy_password != null || virtual_device_path != null || sdk_path != null || use_custom_sdk != null ||
           screen_capture_path != null)
            return false

        return true
    }
}
