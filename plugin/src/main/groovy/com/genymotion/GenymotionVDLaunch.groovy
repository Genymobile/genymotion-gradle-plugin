package main.groovy.com.genymotion

class GenymotionVDLaunch extends GenymotionVirtualDevice{

    private static RANDOM_NAMES = ["Sam", "Julien", "Dan", "Pascal", "Guillaume", "Damien", "Thomas", "Sylvain", "Philippe", "Cedric", "Charly", "Morgan", "Bruno"]

    boolean start = true
    String template
    def pushBefore
    def pullBefore
    def pushAfter
    def pullAfter
    def install
    def flash
    String logcat
    boolean deleteWhenFinish = true
    private boolean create = false


    def invalidParameterException = new IllegalArgumentException("You need to specify a valid name and/or template to declare a device")


    GenymotionVDLaunch(Map params) {
        super(params)

        //if no params
        if(!params){
            throw invalidParameterException
        }

        boolean deviceExists = GMTool.isDeviceCreated(params.name)
        boolean templateExists = GMTool.isTemplateExists(params.template)

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
        if(params.logcat?.trim())
            this.logcat = params.logcat
    }


    boolean checkAndEdit(){
        if(!GMTool.isDeviceCreated(this.name))
            return false

        GenymotionVirtualDevice device = new GenymotionVirtualDevice(this.name, true)

        //if the configuration is different from the created device
        if(!dpi?.equals(device.dpi) ||
           !width?.equals(device.width) ||
           !height?.equals(device.height) ||
           !virtualKeyboard == device.virtualKeyboard ||
           !navbarVisible == device.navbarVisible ||
           !nbCpu?.equals(device.nbCpu) ||
           !ram?.equals(device.ram)){

            return GMTool.editDevice(this)
        }

        false
    }

    def create(){
        if(create)
            GMTool.createDevice(this)
        this.create = false
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
