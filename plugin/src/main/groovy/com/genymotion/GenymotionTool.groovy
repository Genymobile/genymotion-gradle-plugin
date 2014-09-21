package main.groovy.com.genymotion
/**
 * Created by eyal on 10/09/14.
 */
class GenymotionTool {

    public static GenymotionConfig CONFIG = null

    private static final String GENYTOOL = "genymotion-tool"

    private static final String ADMIN = "admin"
    private static final def LIST = "list" //"VBoxManage list "
    private static final def TEMPLATES = "templates"
    private static final def CREATE = "create"
    private static final def UPDTAE = ""//TODO
    private static final def DELETE = "delete"
    private static final def CLONE = "clone"
    private static final def DETAILS = "details"
    private static final def START = "start"
    private static final def RESTART = "" //TODO
    private static final def STOP = "stop"
    private static final def STOPALL = ""//TODO
    private static final def RESET = ""//TODO
    private static final def STARTAUTO = ""//TODO

    private static final String DEVICE = "" //TODO
    private static final def PUSH = "" //TODO
    private static final def PULL = "" //TODO
    private static final def INSTALL = "" //TODO
    private static final def FLASH = "" //TODO
    private static final def LOGCAT = "" //TODO
    private static final def ADBDISCONNECT = "" //TODO
    private static final def ADBCONNECT = "" //TODO


/*
    Usage: genymotion-tool
    ( --register|-r --username|-u username --password|-p password --license|-l licensekey ) |
    ( --count|-c ) |
    ( --verify|-v )
    ( --help|-h )
    ( signin userlogin passwd )
    ( signout )
    ( setlicense licensekey )
    ( licenseinfo )
    ( version )
    admin ( list )
    admin ( start VMname )
    admin ( stop VMname )
    admin ( delete VMname )
    admin ( details VMname )
    admin ( clone VMnameToCLone  NewVmName  )
    admin ( templates [ -l=|--login= -p=|--password= ] )
    admin ( create [ -l=|--login= -p=|--password= ] TemplateName VMName )
*/


    /*
    ADMIN
     */

    static def getAllDevices(boolean verbose=false){

        def devices = []

        cmd([GENYTOOL, ADMIN, LIST], verbose){line, count ->

            //we skip the first lines
            if(count<4)
                return

            String[] infos = line.split('\\|')
//            infos.eachWithIndex(){word, i ->
//                print i+" "
//                println word
//            }
            String name = infos[3].trim()
            def device = new GenymotionVirtualDevice(name)
            device.ip = infos[2].trim()
            device.state = infos[1].trim()
            devices.add(device)
        }
        devices.each(){
            it.fillFromDetails()
        }

        devices
    }

    static def getRunningDevices(boolean verbose=false){

        def devices = []

        def allDevices = getAllDevices(false)
        allDevices.each(){
            if(it.state.equals(GenymotionVirtualDevice.STATE_ON))
                if(verbose)
                    println it.name
                devices.add(it)
        }

/*      TODO uncomment when genymotiontool is ready
        cmd([GENYTOOL, ADMIN, LIST, "running"], verbose){line, count ->
            String name = it.split('"')[1]
            def device = new GenymotionVirtualDevice(name)
            device.fillFromDetails()
            devices.add(device)
        }
*/
        devices
    }

    static def getStoppedDevices(boolean verbose=false){

        def devices = []

        def allDevices = getAllDevices(false)
        allDevices.each(){
            if(it.state.equals(GenymotionVirtualDevice.STATE_OFF))
                if(verbose)
                    println it.name
            devices.add(it)
        }


        /*      TODO uncomment when genymotiontool is ready
        cmd([GENYTOOL, ADMIN, LIST, "off"], verbose){line, count ->
            String name = it.split('"')[1]
            def device = new GenymotionVirtualDevice(name)
            device.fillFromDetails()
            devices.add(device)
        }
*/
        devices
    }

    def getTemplates(){
        cmd([GENYTOOL, ADMIN, TEMPLATES], verbose){line, count ->
            //TODO check when Genytool is ready
            println it
            def template = new GenymotionTemplate(name)
            devices.add(device)
        }
    }

    static def createDevice(GenymotionVirtualDevice device){
        createDevice(device.template, device.apiLevel, device.name, device.dpi, device.width, device.height, device.physicalButton, device.navbar, device.nbcpu, device.ram)
    }

    static def createDevice(def template, def apiLevel, def deviceName, def dpi, def width, def height, def physicalButton, def navbar, def nbcpu, def ram){

        cmd([GENYTOOL, ADMIN, CREATE, template, apiLevel, deviceName,
             '--dpi='+dpi, '--width='+width, '--height='+height, '--physicalbutton='+physicalButton, '--navbar='+navbar, '--nbcpu='+nbcpu, "-ram="+ram]){line, count ->
            //TODO check the request's result
            //if ok: return the device created
            new GenymotionVirtualDevice(template, apiLevel, deviceName, dpi, width, height, physicalButton, navbar, nbcpu, ram)
        }
    }

    static def updateDevice(GenymotionVirtualDevice device){
        updateDevice(device.name, device.dpi, device.width, device.height, device.physicalButton, device.navbar, device.nbCpu, device.ram)
    }

    static def updateDevice(def deviceName, def dpi, def width, def height, def physicalButton, def navbar, def nbcpu, def ram){
        cmd([GENYTOOL, ADMIN, UPDTAE, deviceName,
             '--dpi='+dpi, '--width='+width, '--height='+height, '--physicalbutton='+physicalButton, '--navbar='+navbar, '--nbcpu='+nbcpu, "-ram="+ram]){line, count ->
            //TODO check the request's result
        }
    }

    static def deleteDevice(GenymotionVirtualDevice device){
        deleteDevice(device.name)
    }

    static def deleteDevice(def deviceName){
        cmd([GENYTOOL, ADMIN, DELETE, deviceName]){line, count ->
            //TODO check the request's result
        }
    }

    static def cloneDevice(GenymotionVirtualDevice device, def name){
        cloneDevice(device.name)
    }

    static def cloneDevice(def deviceName, def newName){
        cmd([GENYTOOL, ADMIN, CLONE, deviceName, newName]){line, count ->
            //TODO check the request's result
        }
    }

    static def getDevice(def device){
        //we get the device details
        cmd([GENYTOOL, ADMIN, DETAILS, device.name], false){line, count ->

            //we skip the first line
            if(count < 1)
                return

            String[] info = line.split("\\:")
            switch (info[0].trim()){
                case "Name":
                    device.name = info[1].trim()
                    break
                case "Android Version":
                    device.androidVersion = info[1].trim()
                    break
                case "Nb CPU":
                    device.nbCpu = info[1].trim()
                    break
                case "dpi":
                    device.dpi = info[1].trim()
                    break
                case "uuid":
                    device.uuid = info[1].trim()
                    break
                case "Genymotion Version":
                    device.genymotionVersion = info[1].trim()
                    break
                case "IP":
                    device.ip = info[1].trim()
                    break
                case "Nav Bar Visible":
                    device.navbar = info[1].trim()
                    break
                case "Path":
                    device.path = info[1].trim()
                    break
                case "Platform":
                    device.platform = info[1].trim()
                    break
                case "RAM":
                    device.ram = info[1].trim()
                    break
                case "Resolution":
                    String[] res = info[1].trim().split("x")
                    device.width = res[0]
                    device.height = res[1]
                    break
                case "State":
                    device.state = info[1].trim()
                    break
            }
/*
            Name                 : Google Nexus 5 - 4.4.2 - API 19 - 1080x1920
            Android Version      : 4.4.2
            Nb CPU               : 1
            dpi                  : 480
            uuid                 : 000000000000000
            Genymotion Version   : 2.2.2
            IP                   : 192.168.56.101
            Nav Bar Visible      : 1
            Path                 : /home/eyal/.Genymobile/Genymotion/deployed/Google Nexus 5 - 4.4.2 - API 19 - 1080x1920
            Platform             : p
            RAM                  : 2048
            Resolution           : 1920x1080
            State                : On
*/

        }
        device
    }

    static def startDevice(GenymotionVirtualDevice device){
        startDevice(device.name)
    }

    static def startDevice(def deviceName){
        Thread.start {
            cmd([GENYTOOL, ADMIN, START, deviceName]) {line, count ->
            }
        }
    }

    static def restartDevice(GenymotionVirtualDevice device){
        restartDevice(device.name)
    }

    static def restartDevice(def deviceName){
        cmd([GENYTOOL, ADMIN, RESTART, deviceName]){line, count ->
            //TODO check the request's result
        }
    }

    static def stopDevice(GenymotionVirtualDevice device){
        stopDevice(device.name)
    }

    static def stopDevice(def deviceName){
        cmd([GENYTOOL, ADMIN, STOP, deviceName]){line, count ->
            //TODO check the request's result
        }
    }

    static def stopAllDevices(){
        cmd([GENYTOOL, ADMIN, STOPALL]){line, count ->
            //TODO check the request's result
        }
    }

    static def resetDevice(GenymotionVirtualDevice device){
        resetDevice(device.name)
    }

    static def resetDevice(def deviceName){
        cmd([GENYTOOL, ADMIN, START, RESET, deviceName]){line, count ->
            //TODO check the request's result
        }
    }

    static def startAutoDevice(def template, def apiLevel){
        def device = createDevice(template, apiLevel, "")
        startDevice(device)
        //TODO check if we need to provide a name
    }


    /*
    Device
     */

    static def pushToDevice(GenymotionVirtualDevice device, def files){
        pushToDevice(device.name, files)
    }

    static def pushToDevice(def deviceName, def files){
        files.each(){
            //TODO Check what behavior when just a file is provided
            cmd([GENYTOOL, DEVICE, deviceName, PUSH, it.key, it.value]){line, count ->
            }

        }
    }

    static def pullFromDevice(GenymotionVirtualDevice device, def files){
        pullFromDevice(device.name, files)
    }

    static def pullFromDevice(def deviceName, def files){
        files.each(){
            //TODO Check what behavior when just a file is provided
            cmd([GENYTOOL, DEVICE, deviceName, PULL, it.key, it.value]){line, count ->
            }

        }
    }

    static def installToDevice(GenymotionVirtualDevice device, def apks){
        installToDevice(device.name, apks)
    }

    static def installToDevice(def deviceName, def apks){
        apks.each(){
            cmd([GENYTOOL, DEVICE, deviceName, INSTALL, it]){line, count ->
            }
        }
        //TODO Check the request's feedback
    }

    static def flashDevice(GenymotionVirtualDevice device, def zips){
        flashDevice(device.name, zips)
    }

    static def flashDevice(def deviceName, def zips){
        zips.each(){
            cmd([GENYTOOL, DEVICE, deviceName, FLASH, it]){line, count ->
            }
        }
        //TODO Check the request's feedback
    }

    static def adbDisconnectDevice(GenymotionVirtualDevice device){
        adbDisconnectDevice(device.name)
    }

    static def adbDisconnectDevice(def deviceName){
        cmd([GENYTOOL, DEVICE, deviceName, ADBDISCONNECT]){line, count ->
        }
        //TODO Check the request's feedback
    }

    static def adbConnectDevice(GenymotionVirtualDevice device){
        adbConnectDevice(device.name)
    }

    static def adbConnectDevice(def deviceName){
        cmd([GENYTOOL, DEVICE, deviceName, ADBCONNECT]){line, count ->
        }
        //TODO Check the request's feedback
    }

    static def routeLogcatDevice(GenymotionVirtualDevice device, path){
        routeLogcatDevice(device.name, path)
    }

    static def routeLogcatDevice(def deviceName, def path){
        cmd([GENYTOOL, DEVICE, deviceName, LOGCAT, path]){line, count ->
        }
        //TODO Check the request's feedback
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

        def toExec = command.clone()
        //we eventually insert the genymotion binary path
        if(CONFIG != null && CONFIG.genymotionPath != null){
            if(toExec instanceof String){
                toExec = CONFIG.genymotionPath + toExec
            } else {
                toExec[0] = CONFIG.genymotionPath + toExec[0]
            }
        }
        toExec.execute().text.eachLine {line, count ->

            if(verbose){
//                print count
                println line
            }
            c(line, count)
        }
    }

}