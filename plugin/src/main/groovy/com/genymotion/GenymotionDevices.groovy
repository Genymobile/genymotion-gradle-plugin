package main.groovy.com.genymotion

import main.groovy.com.genymotion.GenymotionVDLaunch
import org.gradle.api.Project

/**
 * Created by eyal on 05/09/14.
 */

public class GenymotionDevices {

    Project project
    def devices = []

    def add(GenymotionVDLaunch device) {
        if (project.genymotion.config.verbose){
            println "Add a device"
            println device.toString()
        }
        this.devices.add(device)
    }

    def all(){
        //TODO implement all() handling
    }

}