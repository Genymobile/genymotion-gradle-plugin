package main.groovy.com.genymotion

/**
 * Created by eyal on 05/09/14.
 */
class GenymotionConfig {

    static final DEFAULT_ANDROID_TASK = "connectedAndroidTest"

    //plugin config
    def genymotionPath = ""           //set the Genymotion path to PATH

    //genymotion config
    String fromFile = ""                 //TODO get the whole configuration from a file

    boolean statistics = true            //active les stats (maybe accept true|false)
    String username = ""                 //set le login a VALUE
    String password = ""                 //set le pass a VALUE
    String loginFromFile = ""            //TODO set login & password from a config file
    boolean store_credentials = true
    String licence = ""                  //set la clé d enregistrement a value
    boolean proxy = false                //active le proxy ou non (maybe accept true|false)
    String proxyFromFile = ""            //TODO set proxy info from a file
    String proxy_address = ""            //set l'adresse du proxy a ADRESS
    boolean proxy_port = 80              //set le port du proxy a PORT
    boolean proxy_auth = false           //definit le protocole du proxy, voir avec ced pour plus d infos
    String proxy_username = false        //definit le protocole du proxy, voir avec ced pour plus d infos
    String proxy_password = false        //definit le protocole du proxy, voir avec ced pour plus d infos
    String devices_path = ""             //set le dossier ou sont stockées les configs
    String sdk_path = ""                 //set le dossier du sdk alternatif
    boolean use_custom_sdk = false       //utilise le sdk alternatif ou non (maybe accept true|false)

    //task configs
    String screen_captures_path = ""             //set le dossier de stockage des screenshots
    String taskLaunch = DEFAULT_ANDROID_TASK     //define the task that depends on the genymotion launch task
    String taskFinish = DEFAULT_ANDROID_TASK     //define the task that the genymotion finish task depends on
    boolean automaticLaunch = true               //enable or not the genymotion tasks injection
    int processTimeout = 300000                  //timeout for all the process launched in command line
    boolean verbose = true                       //verbose mode
    boolean persist = true                       //persist the configurations on genymotion or reset them back after the task execution
    boolean abortOnError = false                 //abort the task execution if a GMTool error occurs

}
