package main.groovy.com.genymotion

/**
 * Created by eyal on 05/09/14.
 */
class GenymotionConfig {

    static final DEFAULT_ANDROID_TASK = "connectedAndroidTest"

    def genymotionPath = ""           //set the Genymotion path to PATH
    def stats = true                  //active les stats (maybe accept true|false)
    def notifs = true                 //active les notifs (maybe accept true|false)
    def login = ""                    //set le login a VALUE
    def password = ""                 //set le pass a VALUE
    def loginFromFile = ""            //set login & password from a config file
    def licence = ""                  //set la clé d enregistrement a value
    def proxy_status = false          //active le proxy ou non (maybe accept true|false)
    def proxy_address = ""            //set l'adresse du proxy a ADRESS
    def proxy_port = 80               //set le port du proxy a PORT
    def proxy_protocol = "http"       //definit le protocole du proxy, voir avec ced pour plus d infos
    def configuration_path = ""       //set le dossier ou sont stockées les configs
    def sdk_path = ""                 //set le dossier du sdk alternatif
    def use_custom_sdk = false        //utilise le sdk alternatif ou non (maybe accept true|false)
    def storage_path = ""             //set le dossier de stockage des screenshots
    def max_cache_size = 0            //set la taille du cache a SIZE
    def log_path = ""                 //set le dossier de destination des logs a PATH
    def global_logs = true            //active les logs globaux (maybe accept true|false)
    def taskLaunch = DEFAULT_ANDROID_TASK     //define the task that depends on the genymotion launch task
    def taskFinish = DEFAULT_ANDROID_TASK     //define the task that the genymotion finish task depends on
    def automaticLaunch = true        //enable or not the genymotion tasks injection
    def processTimeout = 300000        //timeout for all the process launched in command line
}
