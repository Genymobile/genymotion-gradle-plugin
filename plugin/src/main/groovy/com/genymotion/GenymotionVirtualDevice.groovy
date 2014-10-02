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


    GenymotionVirtualDevice(String name) {
        this.name = name;
        //TODO check if we have the VD on the list declared and finish to load the config

    }

    GenymotionVirtualDevice(Map params) {
        if(params.name)
            this.name = params.name
        if(params.apiLevel )
            this.apiLevel = params.apiLevel
        if(params.dpi)
            this.dpi = params.dpi
        if(params.width)
            this.width = params.width
        if(params.height)
            this.height = params.height
        if(params.physicalButton)
            this.physicalButton = params.physicalButton
        if(params.navbar)
            this.navbar = params.navbar
        if(params.nbCpu)
            this.nbCpu= params.nbCpu
        if(params.ram)
            this.ram = params.ram
    }

    GenymotionVirtualDevice(def name, def apiLevel, def dpi, def width, def height, def physicalButton, def navbar, def nbCpu, def ram) {
        if(name?.trim())
            this.name = name
        if(apiLevel?.trim())
            this.apiLevel = apiLevel.toInteger()
        if(dpi?.trim())
            this.dpi = dpi.toInteger()
        if(width?.trim())
            this.width = width.toInteger()
        if(height?.trim())
            this.height = height.toInteger()
        if(physicalButton?.trim())
            this.physicalButton = physicalButton.toBoolean()
        if(navbar?.trim())
            this.navbar = navbar.toBoolean()
        if(nbCpu?.trim())
            this.nbCpu = nbCpu.toInteger()
        if(ram?.trim())
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

    void start(){
        //TODO
    }

    void restart(){
        //TODO
    }

    void stop(){
        //TODO
    }

    void reset(){
        //TODO
    }

    void adbdisconnect(){
        //TODO
    }

    void adbconnect(){
        //TODO
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

    def fillFromDetails(){
        GenymotionTool.getDevice(this)
        //TODO fill the current object with good content
    }
}
