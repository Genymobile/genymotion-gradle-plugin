package main.groovy.com.genymotion
/**
 * Created by eyal on 10/09/14.
 */
class GenymotionTool {

    public static GenymotionConfig CONFIG = null

    private static String GENYTOOL_ADMIN_LIST = "VBoxManage list " //TODO
    private static String GENYTOOL_ADMIN_TEMPLATES = "" //TODO
    private static String GENYTOOL_ADMIN_CREATE = "" //TODO
    private static String GENYTOOL_ADMIN_UPDTAE = "" //TODO
    private static String GENYTOOL_ADMIN_DELETE = "" //TODO
    private static String GENYTOOL_ADMIN_CLONE = "" //TODO
    private static String GENYTOOL_ADMIN_DETAILS = "" //TODO
    private static String GENYTOOL_ADMIN_START = "" //TODO [project.genymotion.config.genymotionPath+"player", '--vm-name', it.name]
    private static String GENYTOOL_ADMIN_RESTART = "" //TODO
    private static String GENYTOOL_ADMIN_STOP = "" //TODO
    private static String GENYTOOL_ADMIN_STOPALL = "" //TODO
    private static String GENYTOOL_ADMIN_RESET = "" //TODO
    private static String GENYTOOL_ADMIN_STARTAUTO = "" //TODO

    private static String GENYTOOL_DEVICE = "" //TODO
    private static String PUSH = "" //TODO
    private static String PULL = "" //TODO
    private static String INSTALL = "" //TODO
    private static String FLASH = "" //TODO
    private static String LOGCAT = "" //TODO
    private static String ADBDISCONNECT = "" //TODO
    private static String ADBCONNECT = "" //TODO


    /*
    ADMIN
     */

    static def getAllDevices(boolean verbose=false){

        def devices = []

        "VBoxManage list vms".execute().text.eachLine{
            if(verbose)
                println it
            String name = it.split('"')[1] //temporary, using VBoxManage
            def device = new GenymotionVirtualDevice(name)
            devices.add(device)
        }

/*      TODO uncomment when genymotiontool is ready
        cmd(GENYTOOL_ADMIN_LIST, verbose){
            String name = it.split('"')[1]
            def device = new GenymotionVirtualDevice(name)
            device.fillFromDetails()
            devices.add(device)
        }

*/
        devices
    }

    static def getRunningDevices(boolean verbose=false){

        def devices = []

        "VBoxManage list runningvms".execute().text.eachLine{
            if(verbose)
                println it
            String name = it.split('"')[1] //temporary, using VBoxManage
            def device = new GenymotionVirtualDevice(name)
            devices.add(device)
        }

/*      TODO uncomment when genymotiontool is ready
        cmd([GENYTOOL_ADMIN_LIST, "running"], verbose){
            String name = it.split('"')[1]
            def device = new GenymotionVirtualDevice(name)
            device.fillFromDetails()
            devices.add(device)
        }
*/
        devices
    }

    static def getStoppedDevices(){

        def devices = []
        def runningDevices = GenymotionTool.runningDevices
        def allDevices = GenymotionTool.allDevices
        //for each devices
        allDevices.each {
            boolean isRunning = false
            //we browse the running devices
            runningDevices.each{ running ->
                //if the current device is not running
                if(running.name == it.name){
                    isRunning = true
                }
            }
            if(!isRunning){
                println it.name
                //we add it to the stopped devices
                devices.add(it)
            }
        }

        /*      TODO uncomment when genymotiontool is ready
        cmd([GENYTOOL_ADMIN_LIST, "off"], verbose){
            String name = it.split('"')[1]
            def device = new GenymotionVirtualDevice(name)
            device.fillFromDetails()
            devices.add(device)
        }
*/
        devices
    }

    def getTemplates(){
        def cmd = GENYTOOL_ADMIN_TEMPLATES
        cmd.execute().text.eachLine {
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

        cmd([GENYTOOL_ADMIN_CREATE, template, apiLevel, deviceName,
             '--dpi='+dpi, '--width='+width, '--height='+height, '--physicalbutton='+physicalButton, '--navbar='+navbar, '--nbcpu='+nbcpu, "-ram="+ram]){
            //TODO check the request's result
            //if ok: return the device created
            new GenymotionVirtualDevice(template, apiLevel, deviceName, dpi, width, height, physicalButton, navbar, nbcpu, ram)
        }
    }

    static def updateDevice(GenymotionVirtualDevice device){
        updateDevice(device.name, device.dpi, device.width, device.height, device.physicalButton, device.navbar, device.nbCpu, device.ram)
    }

    static def updateDevice(def deviceName, def dpi, def width, def height, def physicalButton, def navbar, def nbcpu, def ram){
        cmd([GENYTOOL_ADMIN_UPDTAE, deviceName,
             '--dpi='+dpi, '--width='+width, '--height='+height, '--physicalbutton='+physicalButton, '--navbar='+navbar, '--nbcpu='+nbcpu, "-ram="+ram]){
            //TODO check the request's result
        }
    }

    static def deleteDevice(GenymotionVirtualDevice device){
        deleteDevice(device.name)
    }

    static def deleteDevice(def deviceName){
        cmd([GENYTOOL_ADMIN_DELETE, deviceName]){
            //TODO check the request's result
        }
    }

    static def cloneDevice(GenymotionVirtualDevice device, def name){
        cloneDevice(device.name)
    }

    static def cloneDevice(def deviceName, def newName){
        cmd([GENYTOOL_ADMIN_CLONE, deviceName, newName]){
            //TODO check the request's result
        }
    }

    static def getDevice(def deviceName){
        GenymotionVirtualDevice device = new GenymotionVirtualDevice()
        //we get the device details
        cmd([GENYTOOL_ADMIN_DETAILS, deviceName]){
            //TODO check the request's result
        }
        device
    }

    static def startDevice(GenymotionVirtualDevice device){
        startDevice(device.name)
    }

    static def startDevice(def deviceName){
        Thread.start {
            cmd(["player", '--vm-name', deviceName]) {
            }
        }

/*      //TODO enable this when it will be ready
        cmd([GENYTOOL_ADMIN_START, deviceName]){
            //TODO check the request's result
        }
*/
    }

    static def restartDevice(GenymotionVirtualDevice device){
        restartDevice(device.name)
    }

    static def restartDevice(def deviceName){
        cmd([GENYTOOL_ADMIN_RESTART, deviceName]){
            //TODO check the request's result
        }
    }

    static def stopDevice(GenymotionVirtualDevice device){
        stopDevice(device.name)
    }

    static def stopDevice(def deviceName){
        cmd([GENYTOOL_ADMIN_STOP, deviceName]){
            //TODO check the request's result
        }
    }

    static def stopAllDevices(){
        cmd(GENYTOOL_ADMIN_STOPALL){
            //TODO check the request's result
        }
    }

    static def resetDevice(GenymotionVirtualDevice device){
        resetDevice(device.name)
    }

    static def resetDevice(def deviceName){
        cmd([GENYTOOL_ADMIN_RESET, deviceName]){
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
            cmd([GENYTOOL_DEVICE, deviceName, PUSH, it.key, it.value]){
            }

        }
    }

    static def pullFromDevice(GenymotionVirtualDevice device, def files){
        pullFromDevice(device.name, files)
    }

    static def pullFromDevice(def deviceName, def files){
        files.each(){
            //TODO Check what behavior when just a file is provided
            cmd([GENYTOOL_DEVICE, deviceName, PULL, it.key, it.value]){
            }

        }
    }

    static def installToDevice(GenymotionVirtualDevice device, def apks){
        installToDevice(device.name, apks)
    }

    static def installToDevice(def deviceName, def apks){
        apks.each(){
            cmd([GENYTOOL_DEVICE, deviceName, INSTALL, it]){
            }
        }
        //TODO Check the request's feedback
    }

    static def flashDevice(GenymotionVirtualDevice device, def zips){
        flashDevice(device.name, zips)
    }

    static def flashDevice(def deviceName, def zips){
        zips.each(){
            cmd([GENYTOOL_DEVICE, deviceName, FLASH, it]){
            }
        }
        //TODO Check the request's feedback
    }

    static def adbDisconnectDevice(GenymotionVirtualDevice device){
        adbDisconnectDevice(device.name)
    }

    static def adbDisconnectDevice(def deviceName){
        cmd([GENYTOOL_DEVICE, deviceName, ADBDISCONNECT]){
        }
        //TODO Check the request's feedback
    }

    static def adbConnectDevice(GenymotionVirtualDevice device){
        adbConnectDevice(device.name)
    }

    static def adbConnectDevice(def deviceName){
        cmd([GENYTOOL_DEVICE, deviceName, ADBCONNECT]){
        }
        //TODO Check the request's feedback
    }

    static def routeLogcatDevice(GenymotionVirtualDevice device, path){
        routeLogcatDevice(device.name, path)
    }

    static def routeLogcatDevice(def deviceName, def path){
        cmd([GENYTOOL_DEVICE, deviceName, LOGCAT, path]){
        }
        //TODO Check the request's feedback
    }



    /*
    TOOLS
     */
    static def cmd(def command, boolean verbose=true, Closure c){

        //we eventually insert the genymotion binary path
        if(CONFIG != null && CONFIG.genymotionPath != null){
            if(command instanceof String){
                command = CONFIG.genymotionPath + command
            } else {
                command[0] = CONFIG.genymotionPath + command[0]
            }
        }
        command.execute().text.eachLine {
            if(verbose)
                println it
            c.doCall(it)
        }
    }



}
