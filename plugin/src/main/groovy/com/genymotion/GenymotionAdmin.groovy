package main.groovy.com.genymotion
/**
 * Created by eyal on 05/09/14.
 */

class GenymotionAdmin {

    /**
     * List available templates
     */
    List<GenymotionTemplate> getTemplates(){
        //TODO
    }

    /**
     * create a virtual device following a template
     *
     * @param template
     * @param apiLevel
     * @param vdName
     * @param dpi
     * @param width
     * @param height
     * @param physicalButton
     * @param navbar
     * @param nbCpu
     * @param ram
     */
    void create(String template, String apiLevel, String vdName, int dpi=0, int width=0, int height=0, physicalButton=true, navbar=true, int nbCpu=0, int ram=0){
        //TODO
    }

    /**
     * cpdate a VD config
     *
     * @param vdName
     * @param dpi
     * @param width
     * @param height
     * @param physicalButton
     * @param navbar
     * @param nbCpu
     * @param ram
     */
    void update(String vdName, int dpi=0, int width=0, int height=0, physicalButton=true, navbar=true, int nbCpu=0, int ram=0){
        //TODO
    }

    /**
     * clone a virtual device
     *
     * @param vdName
     * @param newVdName
     */
    void clone (String vdName, String newVdName){
        //TODO
    }

    /**
     * get the created virtual devices
     *
     * @param filter
     */
    void getDevices(String filter="all"){
        //TODO
    }

    /**
     * Start a virtual device
     * @param vdName
     */
    void start(String vdName){
        //TODO
    }

    /**
     * Restart a virtual device
     * @param vdName
     */
    void restart(String vdName){
        //TODO
    }

    /**
     * Stop a virtual device
     * @param vdName
     */
    void stop(String vdName){
        //TODO
    }

    /**
     * Stop all running devices
     */
    void stopall(){
        //TODO
    }

    /**
     * Reset a device
     *
     * @param vdName
     */
    void reset(String vdName){
        //TODO
    }

    /**
     * Start automatically a new device following a template and an api level
     *
     * @param template
     * @param apiLevel
     */
    void startauto(String template, String apiLevel){
        //TODO
    }

}
