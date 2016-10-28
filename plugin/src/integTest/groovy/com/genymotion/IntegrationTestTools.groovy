/*
 * Copyright (C) 2015 Genymobile
 *
 * This file is part of GenymotionGradlePlugin.
 *
 * GenymotionGradlePlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version
 *
 * GenymotionGradlePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenymotionGradlePlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.genymotion

import com.genymotion.model.DeviceLocation
import com.genymotion.model.GenymotionConfig
import com.genymotion.model.GenymotionVirtualDevice
import com.genymotion.tools.GMTool
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class IntegrationTestTools {
    public static final String[] RANDOM_NAMES = ["Sam", "Julien", "Dan", "Pascal", "Guillaume", "Damien", "Thomas",
                                                 "Sylvain", "Philippe", "Cedric", "Charly", "Morgan", "Bruno"]

    public static String TEMP_PATH = "temp" + File.separator
    public static String PULLED_PATH = TEMP_PATH + "pulled" + File.separator

    public static def DEVICES = [
            "Nexus7-junit" : "Google Nexus 7 - 4.1.1 - API 16 - 800x1280",
            "Nexus10-junit": "Google Nexus 10 - 4.4.4 - API 19 - 2560x1600",
            "Nexus4-junit" : "Google Nexus 4 - 4.3 - API 18 - 768x1280"
    ]

    static def init(DeviceLocation deviceLocation = DeviceLocation.LOCAL) {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'genymotion'
        setDefaultGenymotionPath(project)

        GMTool.DEFAULT_CONFIG = project.genymotion.config
        GMTool gmtool = GMTool.newInstance()
        gmtool.deviceLocation = deviceLocation
        gmtool.getConfig(project.genymotion.config)
        project.genymotion.config.verbose = true

        return [project, gmtool]
    }

    private static void setDefaultGenymotionPath(Project project, String defaultPath = null) {
        String path = getDefaultConfig()?.genymotionPath
        if (path) {
            project.genymotion.config.genymotionPath = path
        } else if (defaultPath) {
            project.genymotion.config.genymotionPath = defaultPath
        } else {
            project.genymotion.config.genymotionPath = GenymotionConfig.getDefaultGenymotionPath()
        }
    }

    private static def getRandomTemplateAndName() {
        Random rand = new Random()
        int index = rand.nextInt(DEVICES.size())

        String[] keys = DEVICES.keySet() as String[]
        String name = keys[index]

        return [DEVICES[name], name]
    }

    static void deleteAllDevices(GMTool gmtool) {
        DEVICES.each() { key, value ->
            gmtool.deleteDevice(key)
        }
    }

    static void createAllDevices(GMTool gmtool) {
        DEVICES.each() { key, value ->
            gmtool.createDevice(value, key)
        }
    }

    static String createADevice(GMTool gmtool) {
        def (template, name) = getRandomTemplateAndName()
        gmtool.createDevice(template, name)

        return name
    }

    static String startADisposableDevice(GMTool gmtool) {
        def (template, name) = getRandomTemplateAndName()
        gmtool.startDisposableDevice(template, name)

        return name
    }

    static def declareADetailedDevice(Project project, boolean stop = true) {
        String vdName = getRandomName("-junit")
        String densityName = "mdpi"
        int heightInt = 480
        int widthInt = 320
        int ramInt = 2048
        int nbCpuInt = 1
        boolean delete = true

        project.genymotion.devices {
            "$vdName" {
                template "Google Nexus 7 - 4.1.1 - API 16 - 800x1280"
                density densityName
                width widthInt
                height heightInt
                virtualKeyboard false
                navbarVisible false
                nbCpu nbCpuInt
                ram ramInt
                deleteWhenFinish delete
                stopWhenFinish stop
            }
        }

        return [vdName, densityName, widthInt, heightInt, nbCpuInt, ramInt, delete]
    }

    static void cleanAfterTests(GMTool gmtool) {
        println "Cleaning after tests"

        gmtool.getConfig()

        try {
            def devices = gmtool.getAllDevices(false, false)
            def pattern = ~/^.+?\-junit$/
            println devices

            devices.each() {
                if (pattern.matcher(it.name).matches()) {
                    println "Removing $it.name"
                    gmtool.updateDevice(it)
                    if (it.isRunning()) {
                        gmtool.stopDevice(it.name)
                    }
                    gmtool.deleteDevice(it.name)
                }
            }
        } catch (Exception e) {
            println e
        }

        new File("temp").deleteDir()
    }

    static void recreatePulledDirectory() {
        File tempDir = new File(PULLED_PATH)
        if (tempDir.exists()) {
            if (tempDir.isDirectory()) {
                tempDir.deleteDir()
            } else {
                tempDir.delete()
            }
        }
        tempDir.mkdirs()
    }

    static GenymotionConfig getDefaultConfig(String path = "src/integTest/res/test/default.properties") {
        GenymotionConfig config = new GenymotionConfig()
        config.fromFile = path

        if (config.applyConfigFromFile(null)) {
            return config
        }

        def error = "No default.properties file found, add one or supply needed properties via commandline arguments"
        throw new FileNotFoundException(error)
    }

    static void setDefaultUser(registerLicense = false, GMTool gmtool) {
        gmtool.resetConfig()
        GenymotionConfig config = getDefaultConfig()
        config.version = gmtool.getVersion()
        gmtool.genymotionConfig.version = config.version

        if (!config) {
            return
        }

        if (config.username && config.password) {
            gmtool.setConfig(config)

            if (config.license && registerLicense) {
                gmtool.setLicense(config.license)
            }
        }
    }

    static String getRandomName(String extension = null) {
        int nameLength = 3
        String name = ""
        Random r = new Random()
        nameLength.times() {
            name += RANDOM_NAMES[r.nextInt(RANDOM_NAMES.size())]
        }
        if (extension) {
            return name += extension
        }

        return name
    }

    static Project getAndroidProject() {
        Project project = ProjectBuilder.builder()
                                        .withProjectDir(new File("src/integTest/res/test/android-app"))
                                        .build();

        project.apply plugin: 'com.android.application'
        project.apply plugin: 'genymotion'

        project.android {
            compileSdkVersion 21
            buildToolsVersion "21.1.2"
        }
        project.genymotion.config.genymotionPath = IntegrationTestTools.getDefaultConfig().genymotionPath

        project.afterEvaluate {
            println "TASKS AFTER " + project.tasks
        }

        return project
    }

    static List runAndCheckLogcat(Project project, GMTool gmtool, String deviceName, String path) {
        project.evaluate()
        project.tasks.genymotionLaunch.exec()

        GenymotionVirtualDevice device = gmtool.getDevice(deviceName)

        //we add a line into logcat
        String uniqueString = "GENYMOTION ROCKS DU PONEY " + System.currentTimeMillis()
        gmtool.cmd(["tools/adb", "-s", device.adbSerial, "shell", "log $uniqueString"])

        project.tasks.genymotionFinish.exec()

        //we reach the file created
        File file = new File(path)

        boolean clearedAfterBoot = true
        boolean logcatDumped = false

        file.eachLine {
            if (it.contains(">>>>>> AndroidRuntime START com.android.internal.os.ZygoteInit <<<<<<")) {
                clearedAfterBoot = false
            }
            if (it.contains(uniqueString)) {
                logcatDumped = true
            }
        }
        [clearedAfterBoot, logcatDumped]
    }
}
