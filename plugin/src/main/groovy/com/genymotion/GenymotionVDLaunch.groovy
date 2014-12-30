package main.groovy.com.genymotion

import groovy.transform.CompileStatic

@CompileStatic
class GenymotionVDLaunch extends GenymotionVirtualDevice{

    private static String[] RANDOM_NAMES = ["Sam", "Julien", "Dan", "Pascal", "Guillaume", "Damien", "Thomas", "Sylvain", "Philippe", "Cedric", "Charly", "Morgan", "Bruno"]

    boolean start = true
    String template
    def pushBefore
    def pullBefore
    def pushAfter
    def pullAfter
    def install
    def flash
    String logcat
    def deleteWhenFinish = null
    def stopWhenFinish = null
    private boolean create = false

    private static String INVALID_PARAMETER = "You need to specify a valid name or template to declare a device"


    GenymotionVDLaunch(Map params) {
        super(params)

        //if no params
        if(!params){
            throw new IllegalArgumentException(INVALID_PARAMETER)
        }

        boolean deviceExists = GMTool.isDeviceCreated(params.name.toString())
        boolean templateExists = GMTool.isTemplateExists(params.template.toString())

        //if name & template are null or not existing
        if(!deviceExists && !templateExists){
            throw new IllegalArgumentException("Template: $params.template, Name: $params.name. "+INVALID_PARAMETER)
        }

        //if declared device name exists
        else if(deviceExists){

            //if a template is declared
            if(params.template != null){
                println params.name.toString() + " already exists. A new device won't be created before launch and template is ignored"
            }
        }

        //if declared template exists
        else if(templateExists){

            //if name is not declared
            if(params.name == null){
                //we create a random name
                this.name = getRandomName()

                //we enable the deletion on finish for th VD
                this.deleteWhenFinish = true
            }

            this.create = true

        }

        if(params.stopWhenFinish != null)
            this.stopWhenFinish = params.stopWhenFinish
        if(params.deleteWhenFinish != null)
            this.deleteWhenFinish = params.deleteWhenFinish
        if(params.start != null)
            this.start = params.start
        if(params.template != null && params.template?.toString().trim()) //quick fix, safe navigation (?) buggy with groovy 2.3.6 when CompileStatic
            this.template = params.template
        if(params.pushBefore)
            this.pushBefore = params.pushBefore
        if(params.pullBefore)
            this.pullBefore = params.pullBefore
        if(params.pushAfter)
            this.pushAfter = params.pushAfter
        if(params.pullAfter)
            this.pullAfter = params.pullAfter
        if(params.install)
            this.install = params.install
        if(params.flash)
            this.flash = params.flash
        if(params.logcat != null && params.logcat?.toString().trim()) //quick fix, safe navigation (?) buggy with groovy 2.3.6 when CompileStatic
            this.logcat = params.logcat
    }


    boolean checkAndEdit(){
        if(!GMTool.isDeviceCreated(this.name))
            return false

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(this.name, true)

        //if the configuration is different from the created device
        if(!this.density?.equals(device.density) ||
           !this.width?.equals(device.width) ||
           !this.height?.equals(device.height) ||
           !this.virtualKeyboard == device.virtualKeyboard ||
           !this.navbarVisible == device.navbarVisible ||
           !this.nbCpu?.equals(device.nbCpu) ||
           !this.ram?.equals(device.ram)){

            return GMTool.editDevice(this)
        }

        false
    }

    def start(){
        if(start)
            GMTool.startDevice(this)
    }
    def stop(){
        GMTool.stopDevice(this)
    }

    def stopWhenFinish(){
        //if stop is not explicitly disabled, we stop
        if(stopWhenFinish != false)
            stop()
    }

    def deleteWhenFinish(){
        //if stop and delete are not explicitly disabled, we delete
        if(stopWhenFinish != false && deleteWhenFinish != false)
            delete()
    }

    def create(){
        GMTool.createDevice(this)
    }

    def delete(){
        GMTool.deleteDevice(this)
    }

    def flash(){
        GMTool.flashDevice(this, flash)
    }

    def install(){
        GMTool.installToDevice(this, install)
    }

    def pushBefore(){
        GMTool.pushToDevice(this, pushBefore)
    }

    def pullBefore(){
        GMTool.pullFromDevice(this, pullBefore)
    }

    def pushAfter(){
        GMTool.pushToDevice(this, pushAfter)
    }

    def pullAfter(){
        GMTool.pullFromDevice(this, pullAfter)
    }

    def logcat(){
        if(logcat)
            GMTool.routeLogcat(this, logcat)
    }


    static String getRandomName(String extension=null){
        int nameLength = 3
        String name = ""
        Random r = new Random()
        nameLength.times(){
            name += RANDOM_NAMES[r.nextInt(RANDOM_NAMES.size())]
        }
        if(extension)
            name += extension
        name
    }
}
