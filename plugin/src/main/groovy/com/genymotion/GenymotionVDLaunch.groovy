package main.groovy.com.genymotion

import groovy.transform.CompileStatic

@CompileStatic
class GenymotionVDLaunch extends GenymotionVirtualDevice{

    private static String[] RANDOM_NAMES = ["Sam", "Julien", "Dan", "Pascal", "Guillaume", "Damien", "Thomas", "Sylvain", "Philippe", "Cedric", "Charly", "Morgan", "Bruno"]
    private static String INVALID_PARAMETER = "You need to specify an already created device name or a valid template to declare a device"

    protected def templateExists = null
    protected def deviceExists = null
    protected boolean create = false

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

    GenymotionVDLaunch(String name) {
        super(name)
    }

    GenymotionVDLaunch(Map params) {
        super(params)

        //if no params
        if(!params) {
            throw new IllegalArgumentException(INVALID_PARAMETER)
        }

        if(params.stopWhenFinish != null)
            this.stopWhenFinish = params.stopWhenFinish
        if(params.deleteWhenFinish != null)
            this.deleteWhenFinish = params.deleteWhenFinish
        if(params.start != null)
            this.start = params.start
        if(params.template != null && params.template?.toString()?.trim()) //quick fix, safe navigation (?) buggy with groovy 2.3.6 when CompileStatic
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
        if(params.logcat != null && params.logcat?.toString()?.trim()) //quick fix, safe navigation (?) buggy with groovy 2.3.6 when CompileStatic
            this.logcat = params.logcat

        checkParams()
    }

    public void checkParams() {

        deviceExists = GMTool.isDeviceCreated(name)
        templateExists = GMTool.isTemplateExists(template)

        //if name & template are null or not existing
        if (!deviceExists && !templateExists) {
            throw new IllegalArgumentException("On device \"$name\", template: \"$template\". " + INVALID_PARAMETER)
        }

        //if declared device name exists
        else if (deviceExists) {

            //if a template is declared
            if (template != null) {
                println name + " already exists. A new device won't be created before launch and template is ignored"
            }
        }

        //if declared template exists
        else if (templateExists) {
            create = true
        }
    }


    boolean checkAndEdit() {
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
           !this.ram?.equals(device.ram)) {

            return GMTool.editDevice(this)
        }

        false
    }

    protected def start() {
        if(start)
            GMTool.startDevice(this)
    }
    protected def stop() {
        GMTool.stopDevice(this)
    }

    protected def stopWhenFinish() {
        //if stop is not explicitly disabled, we stop
        if(stopWhenFinish != false)
            stop()
    }

    protected def deleteWhenFinish() {
        //if stop and delete are not explicitly disabled, we delete
        if(stopWhenFinish != false && deleteWhenFinish != false)
            delete()
    }

    protected def create() {
        if(templateExists == null)
            checkParams()

        if(template != null && template.toString().trim() && templateExists)
            GMTool.createDevice(this)
    }

    protected def delete() {
        GMTool.deleteDevice(this)
    }

    protected def flash() {
        GMTool.flashDevice(this, flash)
    }

    protected def install() {
        GMTool.installToDevice(this, install)
    }

    protected def pushBefore() {
        GMTool.pushToDevice(this, pushBefore)
    }

    protected def pullBefore() {
        GMTool.pullFromDevice(this, pullBefore)
    }

    protected def pushAfter() {
        GMTool.pushToDevice(this, pushAfter)
    }

    protected def pullAfter() {
        GMTool.pullFromDevice(this, pullAfter)
    }

    protected def logcat() {
        if(logcat)
            GMTool.routeLogcat(this, logcat)
    }

    static String getRandomName(String extension=null) {
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
