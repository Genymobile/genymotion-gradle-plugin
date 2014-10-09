package main.groovy.com.genymotion

import main.groovy.com.genymotion.GenymotionVDLaunch

/**
 * Created by eyal on 05/09/14.
 */

public class GenymotionDevices {

    def devices = []

    //TODO add a reference to Project ?

    def add(GenymotionVDLaunch device) {
        //check the device name exists
        //TODO check if the device exists
        println "add a device"
        println device.toString()
        this.devices.add(device)
    }

    def all(){
        //TODO implement all() handling
    }

}