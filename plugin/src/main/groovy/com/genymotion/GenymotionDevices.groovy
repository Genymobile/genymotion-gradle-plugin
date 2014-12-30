package main.groovy.com.genymotion

import main.groovy.com.genymotion.GenymotionVDLaunch
import org.gradle.api.Project

/**
 * Created by eyal on 05/09/14.
 */

public class GenymotionDevices {

    Project project
    def devices = []

    GenymotionDevices(Project project){
        this.project = project
    }

    def add(GenymotionVDLaunch device) {
        this.devices.add(device)
    }

    def all(){
        //TODO implement all() handling
    }

}