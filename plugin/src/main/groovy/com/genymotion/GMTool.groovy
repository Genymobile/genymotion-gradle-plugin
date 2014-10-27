package main.groovy.com.genymotion

import org.codehaus.groovy.runtime.NullObject

/**
 * Created by eyal on 10/09/14.
 */
class GMTool {

    public static GenymotionConfig GENYMOTION_CONFIG = null

    private static final String GENYTOOL = "gmtool"
    private static final String VERBOSE  = "--verbose"

    //root actions
    private static final String LOGZIP        = "logzip"
    //admin actions
    private static final String ADMIN         = "admin"
    private static final String LIST          = "list" //"VBoxManage list "
    private static final String TEMPLATES     = "templates"
    private static final String CREATE        = "create"
    private static final String EDIT          = "edit"
    private static final String DELETE        = "delete"
    private static final String CLONE         = "clone"
    private static final String DETAILS       = "details"
    private static final String START         = "start"
    private static final String RESTART       = "restart"
    private static final String STOP          = "stop"
    private static final String STOPALL       = "stopall"
    private static final String FACTORY_RESET = "factoryreset"
    private static final String STARTAUTO = ""//TODO
    //device actions
    private static final String DEVICE        = "device"
    private static final String PUSH          = "push"
    private static final String PULL          = "pull"
    private static final String INSTALL       = "install"
    private static final String FLASH         = "flash"
    private static final String LOGCAT        = "logcat"
    private static final String ADBDISCONNECT = "adbdisconnect"
    private static final String ADBCONNECT    = "adbconnect"
    //config actions
    private static final String CONFIG               = "config"
    private static final String PRINT                = "print"
    private static final String RESET                = "reset"
    private static final String CLEARCACHE           = "clearcache"
    //license
    private static final String LICENSE  = "license"
    private static final String INFO     = "info"
    private static final String REGISTER = "register"
    private static final String COUNT    = "count"
    private static final String VERIFY   = "verify"
    private static final String VALIDITY = "validity"
    //options
    private static final String OPT_USERNAME             = "--username="
    private static final String OPT_STATISTICS           = "--statistics="
    private static final String OPT_PASSWORD             = "--password="
    private static final String OPT_STORE_CREDENTIALS    = "--store_credentials="
    private static final String OPT_PROXY                = "--proxy="
    private static final String OPT_PROXY_ADDRESS        = "--proxy_address="
    private static final String OPT_PROXY_PORT           = "--proxy_port="
    private static final String OPT_PROXY_AUTH           = "--proxy_auth="
    private static final String OPT_PROXY_USERNAME       = "--proxy_username="
    private static final String OPT_PROXY_PASSWORD       = "--proxy_password="
    private static final String OPT_DEVICES_PATH         = "--devices_path="
    private static final String OPT_SDK_PATH             = "--sdk_path="
    private static final String OPT_USE_CUSTOM_SDK       = "--use_custom_sdk="
    private static final String OPT_SCREEN_CAPTURES_PATH = "--screen_captures_path="
    private static final String OPT_RUNNING              = "--running"
    private static final String OPT_OFF                  = "--off"
    private static final String OPT_FULL                 = "--full"
    private static final String OPT_DENSITY              = '--density='
    private static final String OPT_WIDTH                = '--width='
    private static final String OPT_HEIGHT               = '--height='
    private static final String OPT_VIRTUAL_KEYBOARD     = '--virtualkeyboard='
    private static final String OPT_NAVBAR               = '--navbar='
    private static final String OPT_NBCPU                = '--cpu='
    private static final String OPT_RAM                  = "--ram="

    //code returned by gmtool or command line
    public static final int RETURN_DEVICE_NOT_FOUND       = -1
    public static final int RETURN_NO_ERROR               = 0
    public static final int RETURN_GENERIC_ERROR          = 1
    public static final int RETURN_NO_SUCH_ACTION         = 2
    public static final int RETURN_CANT_LOGIN             = 3
    public static final int RETURN_CANT_REGISTER_LICENSE  = 4
    public static final int RETURN_CANT_ACTIVATE_LICENSE  = 5
    public static final int RETURN_NO_ACTIVATED_LICENSE   = 6
    public static final int RETURN_INVALID_LICENSE        = 7
    public static final int RETURN_PENDING_ACTION         = 8
    public static final int RETURN_ARGS_ERROR             = 9
    public static final int RETURN_VM_NOT_STOPPED         = 10
    public static final int RETURN_COMMAND_NOT_FOUND_UNIX = 127


    static def usage(){
        return cmd([GENYTOOL, "-h"]){line, count ->
        }
    }

    /*
    CONFIG
     */

    static def setLicense(String license, String login="", String password=""){
        return cmd([GENYTOOL, SETLICENSE, license, "-l="+login, "-p="+password]){line, count ->
        }
    }

    static def resetConfig(){
        return cmd([GENYTOOL, CONFIG, RESET]){line, count ->
        }
    }

    static def clearCache(){
        return cmd([GENYTOOL, CONFIG, CLEARCACHE]){line, count ->
        }
    }

    static def logzip(String path="", String vdName=""){

        def command = [GENYTOOL, LOGZIP]

        if(vdName?.trim()){
            command.push("-n="+vdName)
        }

        if(path?.trim())
            command.push(path)

        return cmd([GENYTOOL, LOGZIP]){line, count ->
        }
    }

    static def getConfig(boolean verbose=false){

        GenymotionConfig config = new GenymotionConfig()

        def exitCode = cmd([GENYTOOL, CONFIG, PRINT], verbose) { line, count ->

            String[] info = line.split("\\=")
            if (info[1].trim()){

                switch (info[0].trim()) {
                    case "statistics":
                        config.statistics = info[1].trim()
                        break
                    case "username":
                        config.username = info[1].trim()
                        break
                    case "store_credentials":
                        config.store_credentials = info[1].trim()
                        break
                    case "proxy":
                        config.proxy = info[1].trim()
                        break
                    case "proxy_address":
                        config.proxy_address = info[1].trim()
                        break
                    case "proxy_port":
                        config.proxy_port = info[1].trim()
                        break
                    case "proxy_auth":
                        config.proxy_auth = info[1].trim()
                        break
                    case "proxy_username":
                        config.proxy_username = info[1].trim()
                        break
                    case "proxy_password":
                        config.proxy_password = info[1].trim()
                        break
                    case "devices_path":
                        config.devices_path = info[1].trim()
                        break
                    case "sdk_path":
                        config.sdk_path = info[1].trim()
                        break
                    case "use_custom_sdk":
                        config.use_custom_sdk = info[1].trim()
                        break
                    case "screen_captures_path":
                        config.screen_captures_path = info[1].trim()
                        break
                }
            }
        }
        if(exitCode == RETURN_NO_ERROR)
            return config

        return exitCode
    }

    static def setConfig(GenymotionConfig config, boolean verbose=false){
        return setConfig(config.statistics, config.username, config.password, config.store_credentials, config.proxy, config.proxy_address, config.proxy_port, config.proxy_auth, config.proxy_username, config.proxy_password, config.devices_path, config.sdk_path, config.use_custom_sdk, config.screen_captures_path=null, verbose)
    }


    static def setConfig(def statistics=null, def username=null, def password=null, def store_credentials=null, def proxy=null, def proxy_address=null, def proxy_port=null, def proxy_auth=null, def proxy_username=null, def proxy_password=null, def devices_path=null, def sdk_path=null, def use_custom_sdk=null, def screen_captures_path=null, boolean verbose=false){

        (username, password) = checkLogin(username, password)

        def command = [GENYTOOL, CONFIG]

        if(statistics)
            command.push(OPT_STATISTICS+statistics)
        if(username)
            command.push(OPT_USERNAME+username)
        if(password)
            command.push(OPT_PASSWORD+password)
        if(store_credentials)
            command.push(OPT_STORE_CREDENTIALS+store_credentials)
        if(proxy)
            command.push(OPT_PROXY+proxy)
        if(proxy_address)
            command.push(OPT_PROXY_ADDRESS+proxy_address)
        if(proxy_port)
            command.push(OPT_PROXY_PORT+proxy_port)
        if(proxy_auth)
            command.push(OPT_PROXY_AUTH+proxy_auth)
        if(proxy_username)
            command.push(OPT_PROXY_USERNAME+proxy_username)
        if(proxy_password)
            command.push(OPT_PROXY_PASSWORD+proxy_password)
        if(devices_path)
            command.push(OPT_DEVICES_PATH+devices_path)
        if(sdk_path)
            command.push(OPT_SDK_PATH+sdk_path)
        if(use_custom_sdk)
            command.push(OPT_USE_CUSTOM_SDK+use_custom_sdk)
        if(screen_captures_path)
            command.push(OPT_SCREEN_CAPTURES_PATH+screen_captures_path)

        return cmd(command, verbose) { line, count ->
        }
    }

    /*
    LICENSE
     */

    static def setLicense(String license, String username=null, String password=null, boolean verbose=false){
        def command = [GENYTOOL, LICENSE, REGISTER, license]

        (username, password) = checkLogin(username, password)

        if(username)
            command.push(OPT_USERNAME+username)
        if(password)
            command.push(OPT_PASSWORD+password)

        return cmd(command, verbose){line, count ->
        }
    }

    /*
    ADMIN
     */

    static def getAllDevices(boolean verbose=false, boolean fill=true, boolean nameOnly=false){

        def devices = []

        cmd([GENYTOOL, ADMIN, LIST], verbose){line, count ->
            def device = parseList(count, line, nameOnly)
            if(device)
                devices.add(device)
        }

        if(fill && !nameOnly){
            devices.each(){
                it.fillFromDetails()
            }
        }

        devices
    }

    static def getRunningDevices(boolean verbose=false, boolean fill=true, boolean nameOnly=false){

        def devices = []

        cmd([GENYTOOL, ADMIN, LIST, OPT_RUNNING], verbose){line, count ->
            def device = parseList(count, line, nameOnly)
            if(device)
                devices.add(device)
        }

        if(fill && !nameOnly){
            devices.each(){
                it.fillFromDetails()
            }
        }

        devices
    }

    static def getStoppedDevices(boolean verbose=false, boolean fill=true, boolean nameOnly=false){

        def devices = []

        cmd([GENYTOOL, ADMIN, LIST, OPT_OFF], verbose){line, count ->
            def device = parseList(count, line, nameOnly)
            if(device)
                devices.add(device)
        }

        if(fill && !nameOnly){
            devices.each(){
                it.fillFromDetails()
            }
        }

        devices
    }

    static boolean isDeviceRunning(def device, boolean verbose=false) {
        isDeviceRunning(device.name, verbose)
    }

    static boolean isDeviceRunning(String name, boolean verbose=false) {
        def devices = getRunningDevices(verbose, false, true)
        devices.contains(name)
    }

    private static def parseList(int count, String line, boolean nameOnly) {

        //we skip the first 2 lines
        if(count < 2)
            return

        def device

        String[] infos = line.split('\\|')

        String name = infos[3].trim()
        if (nameOnly) {
            device = name
        } else {
            device = new GenymotionVirtualDevice(name)
            device.ip = infos[1].trim()
            device.state = infos[0].trim()
        }
        device
    }


    static def isDeviceCreated(String name){

        if(!name?.trim())
            return false

        //we check if the VD name already exists
        boolean alreadyExists = false

        def devices = GMTool.getAllDevices(false, false)

        devices.each(){
            if(it.name.equals(name))
                alreadyExists = true
        }
        alreadyExists
    }

    static def getTemplatesNames(boolean verbose=false, String username=null, String password=null) {

        def templates = []

        def template = null

        (username, password) = checkLogin(username, password)

        int exitCode = noNull {
            return cmd([GENYTOOL, ADMIN, TEMPLATES, OPT_USERNAME+username, OPT_PASSWORD+password], verbose) { line, count ->

                //if empty line and template filled
                if (!line && template){
                    templates.add(template)
                    template = null
                }

                String[] info = line.split("\\:")
                switch (info[0].trim()){
                    case "Name":
                        if(!template)
                            template = info[1].trim()
                        break
                }
            }
        }

        if(template)
            templates.add(template)

        if(exitCode == RETURN_NO_ERROR)
            return templates
        else
            return exitCode
    }

    static def getTemplates(boolean verbose=false, String username=null, String password=null){

        def templates = []

        def template = new GenymotionTemplate()

        (username, password) = checkLogin(username, password)

        int exitCode = noNull {
            return cmd([GENYTOOL, ADMIN, TEMPLATES, OPT_FULL, OPT_USERNAME + username, OPT_PASSWORD + password], verbose) { line, count ->

                //if empty line and template filled
                if (!line && template.name) {
                    templates.add(template)
                    template = new GenymotionTemplate()
                }

                String[] info = line.split("\\:")
                switch (info[0].trim()) {
                    case "Name":
                        if (!template.name)
                            template.name = info[1].trim()
                        break
                    case "UUID":
                        template.uuid = info[1].trim()
                        break
                    case "Description":
                        template.description = info[1].trim()
                        break
                    case "Android Version":
                        template.androidVersion = info[1].trim()
                        break
                    case "Genymotion Version":
                        template.genymotionVersion = info[1].trim()
                        break
                    case "Screen Width":
                        template.width = info[1].trim().toInteger()
                        break
                    case "Screen Height":
                        template.height = info[1].trim().toInteger()
                        break
                    case "Screen Density":
                        template.density = info[1].trim()
                        break
                    case "Screen DPI":
                        template.dpi = info[1].trim().toInteger()
                        break
                    case "Nb CPU":
                        template.nbCpu = info[1].trim().toInteger()
                        break
                    case "RAM":
                        template.ram = info[1].trim().toInteger()
                        break
                    case "Internal Storage":
                        template.internalStorage = info[1].trim().toInteger()
                        break
                    case "Telephony":
                        template.telephony = info[1].trim().toBoolean()
                        break
                    case "Nav Bar Visible":
                        template.navbarVisible = info[1].trim().toBoolean()
                        break
                    case "Virtual Keyboard":
                        template.virtualKeyboard = info[1].trim().toBoolean()
                        break
                }
            }

        }
        if(template.name)
            templates.add(template)

        if(exitCode == RETURN_NO_ERROR)
            return templates
        else
            return exitCode
    }

    static boolean isTemplateExists(String template, boolean verbose=false, String username=null, String password=null) {

        if(!template?.trim())
            return false

        def templates = getTemplatesNames(verbose, username, password)
        if(!template instanceof Integer)
            templates.contains(template)
        else
            return false
    }

    static def createDevice(GenymotionVDLaunch device, String username=null, String password=null){
        return createDevice(device.template, device.name, username, password)
    }

    static def createDevice(GenymotionTemplate template, String username=null, String password=null){
        return createDevice(template.name, template.name, username, password)
    }

    static def createDevice(def template, def deviceName, def density="", def width="", def height="", def virtualKeyboard="", def navbarVisible="", def nbcpu="", def ram="", String username=null, String password=null){

        (username, password) = checkLogin(username, password)

        def exitValue = noNull(){
            cmd([GENYTOOL, ADMIN, CREATE, template, deviceName,
                 OPT_DENSITY+density, OPT_WIDTH+width, OPT_HEIGHT+height, OPT_VIRTUAL_KEYBOARD +virtualKeyboard, OPT_NAVBAR +navbarVisible, OPT_NBCPU +nbcpu, OPT_RAM +ram, OPT_USERNAME+username, OPT_PASSWORD+password]){line, count ->
            }
        }

        if(exitValue == RETURN_NO_ERROR)
            return new GenymotionVirtualDevice(deviceName, density, width, height, virtualKeyboard, navbarVisible, nbcpu, ram)
        else
            return exitValue
    }

    static def editDevice(GenymotionVirtualDevice device){
        return editDevice(device.name, device.density, device.width, device.height, device.virtualKeyboard, device.navbarVisible, device.nbCpu, device.ram)
    }

    static def editDevice(def deviceName, def density="", def width="", def height="", def virtualKeyboard="", def navbarVisible="", def nbcpu="", def ram=""){

        return noNull(){
            return cmd([GENYTOOL, ADMIN, EDIT, deviceName,
                 OPT_DENSITY +density, OPT_WIDTH +width, OPT_HEIGHT +height, OPT_VIRTUAL_KEYBOARD +virtualKeyboard, OPT_NAVBAR +navbarVisible, OPT_NBCPU +nbcpu, OPT_RAM +ram]){line, count ->
            }
        }
    }

    static def deleteDevice(GenymotionVirtualDevice device, boolean verbose=false){
        return deleteDevice(device.name, verbose)
    }

    static def deleteDevice(def deviceName, boolean verbose=false){
        return cmd([GENYTOOL, ADMIN, DELETE, deviceName], verbose){line, count ->
        }
    }

    static def cloneDevice(GenymotionVirtualDevice device, def name, boolean verbose=false){
        return cloneDevice(device.name, name, verbose)
    }

    static def cloneDevice(def deviceName, def newName, boolean verbose=false){
        return cmd([GENYTOOL, ADMIN, CLONE, deviceName, newName], verbose){line, count ->
        }
    }

    static def getDevice(String name, boolean verbose=false){

        if(name == null)
            return null

        def device = new GenymotionVirtualDevice(name)
        return getDevice(device, verbose)
    }

    static def getDevice(def device, boolean verbose=false){

        if(device == null)
            return null

        //we get the device details
        cmd([GENYTOOL, ADMIN, DETAILS, device.name], verbose){line, count ->

            String[] info = line.split("\\:")
            switch (info[0].trim()){
                case "Name":
                    device.name = info[1].trim()
                    break
                case "Android Version":
                    device.androidVersion = info[1].trim()
                    break
                case "Genymotion Version":
                    device.genymotionVersion = info[1].trim()
                    break
                case "Screen Width":
                    device.width = info[1].trim().toInteger()
                    break
                case "Screen Height":
                    device.height = info[1].trim().toInteger()
                    break
                case "Screen Density":
                    device.density = info[1].trim()
                    break
                case "Screen DPI":
                    device.dpi = info[1].trim().toInteger()
                    break
                case "Nb CPU":
                    device.nbCpu = info[1].trim().toInteger()
                    break
                case "RAM":
                    device.ram = info[1].trim().toInteger()
                    break
                case "Telephony":
                    device.telephony = info[1].trim().toBoolean()
                    break
                case "Nav Bar Visible":
                    device.navbarVisible = info[1].trim().toBoolean()
                    break
                case "Virtual Keyboard":
                    device.virtualKeyboard = info[1].trim().toBoolean()
                    break
                case "UUID":
                    device.uuid = info[1].trim()
                    break
                case "Path":
                    device.path = info[1].trim()
                    break
                case "State":
                    device.state = info[1].trim()
                    break
                case "IP":
                    device.ip = info[1].trim()
                    break
            }
        }
        device
    }

    static def startDevice(GenymotionVirtualDevice device, boolean verbose=false){
        return startDevice(device.name, verbose)
    }

    static def startDevice(def deviceName, boolean verbose=false){
        return cmd([GENYTOOL, ADMIN, START, deviceName], verbose) {line, count ->
        }
    }

    static def restartDevice(GenymotionVirtualDevice device, boolean verbose=false){
        return restartDevice(device.name, verbose)
    }

    static def restartDevice(def deviceName, boolean verbose=false){
        return cmd([GENYTOOL, ADMIN, RESTART, deviceName], verbose){line, count ->
        }
    }

    static def stopDevice(GenymotionVirtualDevice device, boolean verbose=false){
        return stopDevice(device.name, verbose)
    }

    static def stopDevice(def deviceName, boolean verbose=false){
        return cmd([GENYTOOL, ADMIN, STOP, deviceName], verbose){line, count ->
        }
    }

    static def stopAllDevices(boolean verbose=false){
        return cmd([GENYTOOL, ADMIN, STOPALL], verbose){line, count ->
        }
    }

    static def resetDevice(GenymotionVirtualDevice device, boolean verbose=false){
        return resetDevice(device.name, verbose)
    }

    static def resetDevice(def deviceName, boolean verbose=false){
        return cmd([GENYTOOL, ADMIN, START, FACTORY_RESET, deviceName], verbose){line, count ->
        }
    }

    static def startAutoDevice(def template, def deviceName, boolean verbose=false){
        def device = createDevice(template, deviceName, verbose)

        if(!device instanceof GenymotionVirtualDevice)
            return device

        def startExit = startDevice(device)

        if(startExit == RETURN_NO_ERROR)
            return device
        else
            return startExit
    }


    /*
    Device
     */

    static def pushToDevice(GenymotionVirtualDevice device, def files, boolean verbose=false){
        pushToDevice(device.name, files, verbose)
    }

    static def pushToDevice(def deviceName, def files, boolean verbose=false){

        if(!files)
            return false

        def exitValues = []

        if(files instanceof String)
            files = [files]

        files.each(){

            def command = [GENYTOOL, DEVICE, '-n='+deviceName, PUSH]
            if(files instanceof Map){
                command.push(it.key)
                command.push(it.value)
            }
            else
                command.push(it)

            int exitValue = cmd(command, verbose){line, count ->
            }
            exitValues.add(exitValue)
        }

        return exitValues
    }

    static def pullFromDevice(GenymotionVirtualDevice device, def files, boolean verbose=false){
        pullFromDevice(device.name, files, verbose)
    }

    static def pullFromDevice(String deviceName, String source, String destination, boolean verbose=false){
        pullFromDevice(deviceName, [(source):destination], verbose)
    }

    static def pullFromDevice(def deviceName, def files, boolean verbose=false){

        if(!files)
            return false

        def exitValues = []

        if(files instanceof String)
            files = [files]

        files.each(){

            def command = [GENYTOOL, DEVICE, '-n='+deviceName, PULL]
            if(files instanceof Map){
                command.push(it.key)
                command.push(it.value)
            }
            else
                command.push(it)

            int exitValue = cmd(command, verbose){line, count ->
            }
            exitValues.add(exitValue)
        }

        return exitValues
    }

    static def installToDevice(GenymotionVirtualDevice device, def apks, boolean verbose=false){
        installToDevice(device.name, apks, verbose)
    }

    static def installToDevice(def deviceName, def apks, boolean verbose=false){

        if(!apks)
            return false

        if(apks instanceof String){
            cmd([GENYTOOL, DEVICE, '-n='+deviceName, INSTALL, apks], verbose){line, count ->
            }

        } else if(apks instanceof List){
            def exitValues = []
            apks.each(){
                int exitValue = cmd([GENYTOOL, DEVICE, '-n='+deviceName, INSTALL, it], verbose){line, count ->
                }
                exitValues.add(exitValue)
            }
            return exitValues
        }
    }

    static def flashDevice(GenymotionVirtualDevice device, def zips, boolean verbose=false){
        return flashDevice(device.name, zips, verbose)
    }

    static def flashDevice(def deviceName, def zips, boolean verbose=false){

        if(!zips)
            return false

        if(zips instanceof String){
            return cmd([GENYTOOL, DEVICE, '-n='+deviceName, FLASH, zips], verbose){line, count ->
            }

        } else if(zips instanceof List){
            def exitValues = []
            zips.each(){
                int exitValue = cmd([GENYTOOL, DEVICE, '-n='+deviceName, FLASH, it], verbose){line, count ->
                }
                exitValues.add(exitValue)
            }
            return exitValues
        }
    }

    static def adbDisconnectDevice(GenymotionVirtualDevice device, boolean verbose=false){
        return adbDisconnectDevice(device.name, verbose)
    }

    static def adbDisconnectDevice(def deviceName, boolean verbose=false){
        return cmd([GENYTOOL, DEVICE, deviceName, ADBDISCONNECT], verbose){line, count ->
        }
    }

    static def adbConnectDevice(GenymotionVirtualDevice device, boolean verbose=false){
        return adbConnectDevice(device.name, verbose)
    }

    static def adbConnectDevice(def deviceName, boolean verbose=false){
        return cmd([GENYTOOL, DEVICE, deviceName, ADBCONNECT], verbose){line, count ->
        }
    }

    static def routeLogcat(GenymotionVirtualDevice device, path, boolean verbose=false){
        return routeLogcat(device.name, path, verbose)
    }

    static def routeLogcat(def deviceName, def path, boolean verbose=false){
        return cmd([GENYTOOL, DEVICE, deviceName, LOGCAT, path], verbose){line, count ->
        }
    }



    /*
    TOOLS
     */

    /**
     * Fire a command line and process the result.
     * This function runs a closure for each line returned by the prompt.
     * The closure contains the parameters:
     * - <b>line</b> (containing the line's text)
     * - <b>count</b> (index of the line)
     *
     * @param command the command line to execute. It can be a String or a table
     * @param verbose true if you want to print each line returned by the prompt
     * @param c the closure to implement after the call
     */
    static def cmd(def command, boolean verbose=true, Closure c){

        def toExec = command

        //we eventually insert the genymotion binary path
        if(GENYMOTION_CONFIG != null && GENYMOTION_CONFIG.genymotionPath != null){
            if(toExec instanceof String){
                toExec = GENYMOTION_CONFIG.genymotionPath + toExec
            } else {
                toExec = command.clone()
                toExec[0] = GENYMOTION_CONFIG.genymotionPath + toExec[0]
            }
        }

        if(verbose) {
            if(toExec[0].contains(GENYTOOL))
                toExec.addAll(1, [VERBOSE])

            println toExec
        }
        Process p = toExec.execute()
        StringBuffer error = new StringBuffer()
        StringBuffer out = new StringBuffer()
        p.consumeProcessOutput(out, error)

        p.waitForOrKill(GENYMOTION_CONFIG.processTimeout)

        if(verbose){
            println "out:" + out.toString()
        }

        out.eachLine {line, count ->
            c(line, count)
        }

        return handleExitValue(p.exitValue(), error)
    }

    static def handleExitValue(int exitValue, StringBuffer error) {
        if(exitValue == RETURN_NO_ERROR){
            //do nothing
        } else {
            println "error: "+error.toString()

            if(GENYMOTION_CONFIG.abortOnError){
                throw new GMToolException("GMTool command failed. Error code: $exitValue. Check the output to solve the problem")
            }
        }
        exitValue
    }
/**
     * Avoid null.toString returning "null"
     *
     * @param c the code to execute
     * @return the c's return
     */
    static def noNull(Closure c){
        //set null.toString to return ""
        String nullLabel = null.toString()
        NullObject.metaClass.toString = {return ''}

        def exit = c()

        //set as defaut
        NullObject.metaClass.toString = {return nullLabel}

        return exit
    }


}
