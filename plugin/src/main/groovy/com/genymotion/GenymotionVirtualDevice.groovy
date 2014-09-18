package main.groovy.com.genymotion

/**
 * Created by eyal on 05/09/14.
 */
class GenymotionVirtualDevice {

    static String STATE_ON = "On"
    static String STATE_OFF = "Off"

    def name
    def apiLevel
    def template
    def dpi = 0
    def width = 0
    def height = 0
    def physicalButton = true
    def navbar = true
    def nbCpu = 0
    def ram = 0
    def ip
    def state
    def platform //p|t|pt
    def path
    def genymotionVersion
    def uuid
    def androidVersion


    GenymotionVirtualDevice(String name) {
        this.name = name;
        //TODO check if we have the VD on the list declared and finish to load the config

    }

    GenymotionVirtualDevice(Map params) {
        if(params.name)
            this.name = params.name
        if(params.apiLevel )
            this.apiLevel = params.apiLevel
        if(params.template)
            this.template = params.template
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

    GenymotionVirtualDevice(name, apiLevel, template, dpi, width, height, physicalButton, navbar, nbCpu, ram) {
        this.name = name
        this.apiLevel = apiLevel
        this.template = template
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
        String result = "Virtual Device - name:" //-- Virtual Device - name: ${name} template: ${template} api: ${apiLevel} w.h: ${width}x${height} button: ${physicalButton} navbar: ${navbar} nb cpu: ${nbCpu} ram: ${ram}"

        result
    }
*/

    String toString() {
        String result = "-- Virtual Device --\n"
        result += "name: ${name}\n"
        result += "template: ${template}\n"
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
