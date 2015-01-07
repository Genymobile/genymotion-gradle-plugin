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
            init(params.name, params.density, params.width, params.height, params.virtualKeyboard, params.navbarVisible, params.nbCpu, params.ram)
    }

    GenymotionVirtualDevice(def name, def density, def width, def height, def virtualKeyboard, def navbarVisible, def nbCpu, def ram) {
        init(name, density, width, height, virtualKeyboard, navbarVisible, nbCpu, ram)
    }

    void init(def name, def density, def width, def height, def virtualKeyboard, def navbar, def nbCpu, def ram) {
        if(name?.trim())
            this.name = name
        if(density)
            this.density= density
        if(width)
            this.width = width.toInteger()
        if(height)
            this.height = height.toInteger()
        if(virtualKeyboard != null)
            this.virtualKeyboard = virtualKeyboard.toBoolean()
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
        GMTool.startDevice(this)
    }

    def restart(){
        GMTool.restartDevice(this)
    }

    def stop(){
        GMTool.stopDevice(this)
    }

    def reset(){
        GMTool.resetDevice(this)
    }

    def adbdisconnect(){
        GMTool.adbDisconnectDevice(this)
    }

    def adbconnect(){
        GMTool.adbConnectDevice(this)
    }

    String toString() {
        String keyboard = virtualKeyboard?"virtual":"physical"
        String result = "Device: $name\n"
        result += "${width}x${height}"
        result += " - $keyboard keyboard"

        result
    }



    boolean equals(GenymotionVirtualDevice other){
        (this.name == other.name)
    }

    def fillFromDetails(boolean verbose=false){
        GMTool.getDevice(this, verbose)
    }
}
