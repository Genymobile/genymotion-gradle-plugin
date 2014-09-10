package main.groovy.com.genymotion

import main.groovy.com.genymotion.GenymotionVirtualDevice

/**
 * Created by eyal on 05/09/14.
 */

public class GenymotionDevices {

    def devices = []

    //TODO add a reference to Project ?

    def add(GenymotionVirtualDevice device) {
        //check the device name exists
        //TODO check if the device exists
        println "add a device"
        println device.toString()
        devices.add(device)
    }

    def all(){
        //check the device name exists
        //TODO check if the device exists

    }


//    def device(
//            start = true
//            String name
//            String template
//            String api "19"
//    def push (String from, String to="/sdcard/Downloads")
//    def pull (String from, String to)
//    def install (def apks)
//    String flash "path.zip"
//    String logcat "output_path"
//    boolean adb = true
//    )

}