package main.groovy.com.genymotion

import main.groovy.com.genymotion.*

class GenymotionVDLaunch extends GenymotionVirtualDevice{

    private static RANDOM_NAMES = ["Sam", "Julien", "Dan", "Pascal", "Guillaume", "Damien", "Thomas", "Sylvain", "Philippe", "Cedric", "Charly", "Morgan", "Bruno"]

    boolean start = true
    String template
    def push
    def pull
    def install
    def flash
    String logcat
    boolean deleteWhenFinish = false
    private boolean create = false


    def invalidParameterException = new IllegalArgumentException("You need to specify a valid name and/or template to declare a device")


    GenymotionVDLaunch(Map params) {
        super(params)

        println "params "+params

        //if no params
        if(!params){
            throw invalidParameterException
        }

        boolean deviceExists = GenymotionTool.isDeviceCreated(params.name)
        boolean templateExists = GenymotionTool.isTemplateExists(params.template)

        //if name & template are null or not existing
        if(!deviceExists && !templateExists){
            throw invalidParameterException
        }

        //if declared device name exists
        else if(deviceExists){

            //if a template is declared
            if(params.template != null){
                println params.name + " already exists. A new device won't be created before launch and template is ignored"
                params.template = null
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

        if(params.deleteWhenFinish != null)
            this.deleteWhenFinish = params.deleteWhenFinish
        if(params.start != null)
            this.start = params.start
        if(params.template?.trim())
            this.template = params.template
        if(params.push)
            this.push = params.push
        if(params.pull)
            this.pull = params.pull
        if(params.install)
            this.install = params.install
        if(params.flash)
            this.flash = params.flash
        if(params.logcat?.trim())
            this.logcat = params.logcat
    }


    boolean checkAndUpdate(){
        if(!GenymotionTool.isDeviceCreated(this.name))
            return false

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(this.name, true)

        //if the configuration is different from the created device
        if(!dpi?.equals(device.dpi) ||
           !width?.equals(device.width) ||
           !height?.equals(device.height) ||
           !physicalButton == device.physicalButton ||
           !navbar == device.navbar ||
           !nbCpu?.equals(device.nbCpu) ||
           !ram?.equals(device.ram)){

            return GenymotionTool.updateDevice(this)
        }

        false
    }

    def create(){
        if(create)
            GenymotionTool.createDevice(this)
        this.create = false
    }


    def flash(){
        GenymotionTool.flashDevice(this, flash)
    }

    def install(){
        GenymotionTool.installToDevice(this, install)
    }

    def push(){
        GenymotionTool.pushToDevice(this, push)
    }

    def pull(){
        GenymotionTool.pullFromDevice(this, pull)
    }


    static String getRandomName(){
        int nameLength = 3
        String name = ""
        Random r = new Random()
        nameLength.times(){
            name += RANDOM_NAMES[r.nextInt(RANDOM_NAMES.size())]
        }
        name
    }
}
