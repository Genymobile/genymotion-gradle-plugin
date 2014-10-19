package main.groovy.com.genymotion

/**
 * Created by eyal on 05/09/14.
 */
class GenymotionVirtualDevice {

    static String STATE_ON = "On"
    static String STATE_OFF = "Off"

    String name
    String androidVersion
    String genymotionVersion
    def width
    def height
    String density
    def dpi
    def nbCpu
    def ram
    def telephony
    def navbarVisible
    def virtualKeyboard
    String uuid
    String path
    String state
    String ip

    GenymotionVirtualDevice(String name, boolean fill=false) {
        this.name = name;

        if(fill)
            fillFromDetails()
    }


    GenymotionVirtualDevice(Map params) {
        if(params)
            init(params.name, params.dpi, params.width, params.height, params.virtualKeyboard, params.navbarVisible, params.nbCpu, params.ram)
    }

    GenymotionVirtualDevice(def name, def dpi, def width, def height, def virtualKeyboard, def navbarVisible, def nbCpu, def ram) {
        init(name, dpi, width, height, virtualKeyboard, navbarVisible, nbCpu, ram)
    }

    void init(def name, def dpi, def width, def height, def physicalButton, def navbar, def nbCpu, def ram) {
        if(name?.trim())
            this.name = name
        if(dpi)
            this.dpi = dpi.toInteger()
        if(width)
            this.width = width.toInteger()
        if(height)
            this.height = height.toInteger()
        if(physicalButton != null)
            this.virtualKeyboard = physicalButton.toBoolean()
        if(navbar != null)
            this.navbarVisible = navbar.toBoolean()
        if(nbCpu)
            this.nbCpu = nbCpu.toInteger()
        if(ram)
            this.ram = ram.toInteger()
    }


    GenymotionVirtualDevice(String name, int dpi, int width, int height, boolean virtualKeyboard, boolean navbarVisible, int nbCpu, int ram) {
        this.name = name
        this.dpi = dpi
        this.width = width
        this.height = height
        this.virtualKeyboard = virtualKeyboard
        this.navbarVisible = navbarVisible
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

    String toString() {
        String result = "-- Virtual Device --\n"
        result += "name: ${name}\n"
        result += "w.h: ${width}x${height}\n"
        result += "virtual keyboard: ${virtualKeyboard}\n"
        result += "navbar : ${navbarVisible}\n"
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
