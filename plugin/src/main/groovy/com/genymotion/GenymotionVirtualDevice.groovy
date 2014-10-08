package main.groovy.com.genymotion

/**
 * Created by eyal on 05/09/14.
 */
class GenymotionVirtualDevice {

    static String STATE_ON = "On"
    static String STATE_OFF = "Off"

    String name
    String apiLevel
    int dpi = 0
    int width = 0
    int height = 0
    boolean physicalButton = true
    boolean navbar = true
    int nbCpu = 0
    int ram = 0
    String ip
    String state
    String platform //p|t|pt
    String path
    String genymotionVersion
    String uuid
    String androidVersion


    GenymotionVirtualDevice(String name, boolean fill=false) {
        this.name = name;

        if(fill)
            fillFromDetails()
    }


    GenymotionVirtualDevice(Map params) {
        if(params)
            init(params.name, params.apiLevel, params.dpi, params.width, params.height, params.physicalButton, params.navbar, params.nbCpu, params.ram)
    }

    GenymotionVirtualDevice(def name, def apiLevel, def dpi, def width, def height, def physicalButton, def navbar, def nbCpu, def ram) {
        init(name, apiLevel, dpi, width, height, physicalButton, navbar, nbCpu, ram)
    }

    void init(def name, def apiLevel, def dpi, def width, def height, def physicalButton, def navbar, def nbCpu, def ram) {
        if(name?.trim())
            this.name = name
        if(apiLevel)
            this.apiLevel = apiLevel.toInteger()
        if(dpi)
            this.dpi = dpi.toInteger()
        if(width)
            this.width = width.toInteger()
        if(height)
            this.height = height.toInteger()
        if(physicalButton != null)
            this.physicalButton = physicalButton.toBoolean()
        if(navbar != null)
            this.navbar = navbar.toBoolean()
        if(nbCpu)
            this.nbCpu = nbCpu.toInteger()
        if(ram)
            this.ram = ram.toInteger()
    }


    GenymotionVirtualDevice(String name, int apiLevel, int dpi, int width, int height, boolean physicalButton, boolean navbar, int nbCpu, int ram) {
        this.name = name
        this.apiLevel = apiLevel
        this.dpi = dpi
        this.width = width
        this.height = height
        this.physicalButton = physicalButton
        this.navbar = navbar
        this.nbCpu = nbCpu
        this.ram = ram
    }

    def start(){
        GenymotionTool.startDevice(this)
    }

    def restart(){
        GenymotionTool.restartDevice(this)
    }

    def stop(){
        GenymotionTool.stopDevice(this)
    }

    def reset(){
        GenymotionTool.resetDevice(this)
    }

    def adbdisconnect(){
        GenymotionTool.adbDisconnectDevice(this)
    }

    def adbconnect(){
        GenymotionTool.adbConnectDevice(this)
    }

/*
    String toString() {
        String result = "Virtual Device - name:" //-- Virtual Device - name: ${name} api: ${apiLevel} w.h: ${width}x${height} button: ${physicalButton} navbar: ${navbar} nb cpu: ${nbCpu} ram: ${ram}"

        result
    }
*/

    String toString() {
        String result = "-- Virtual Device --\n"
        result += "name: ${name}\n"
        result += "api: ${apiLevel}\n"
        result += "w.h: ${width}x${height}\n"
        result += "button: ${physicalButton}\n"
        result += "navbar: ${navbar}\n"
        result += "nb cpu: ${nbCpu}\n"
        result += "ram: ${ram}\n"

        result
    }



    boolean equals(GenymotionVirtualDevice other){
        println "compare"+this.name+" "+other.name
        (this.name == other.name)
    }

    def fillFromDetails(boolean verbose=false){
        GenymotionTool.getDevice(this, verbose)
    }
}
