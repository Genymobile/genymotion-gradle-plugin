package com.genymotion

import main.groovy.com.genymotion.GenymotionAdmin
import main.groovy.com.genymotion.GenymotionConfig
import main.groovy.com.genymotion.GenymotionDevices
import main.groovy.com.genymotion.GenymotionVirtualDevice
import org.gradle.api.Project


class GenymotionPluginExtension {

    final Project project
    def genymotionConfig = new GenymotionConfig()
    def genymotionAdmin = new GenymotionAdmin()
    def genymotionDevices = new GenymotionDevices()


    GenymotionPluginExtension(Project project) {
        this.project = project
    }

    void device(
            Map params
//            String name = "",
//            String apiLevel = "",
//            String template = "",
//            String dpi = 0,
//            int width = 0,
//            int height = 0,
//            boolean physicalButton = true,
//            boolean navbar = true,
//            int nbCpu = 0,
//            int ram = 0
    ){
        GenymotionVirtualDevice device = new GenymotionVirtualDevice(params)
        this.genymotionDevices.add(device)
    }

    def getDevices(){
        genymotionDevices.devices
    }
}