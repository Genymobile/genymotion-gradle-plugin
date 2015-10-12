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

package com.genymotion.model

import com.genymotion.tools.AndroidPluginTools
import com.genymotion.tools.GMTool
import com.genymotion.tools.Log
import com.genymotion.tools.Tools
import org.gradle.api.Project

class GenymotionConfig {

    static final String DEFAULT_GENYMOTION_PATH_MAC = "/Applications/Genymotion.app/Contents/MacOS/"
    static final String DEFAULT_GENYMOTION_PATH_WINDOWS = "C:\\Program Files\\Genymobile\\Genymotion\\"
    static final String DEFAULT_GENYMOTION_PATH_LINUX = ""
    static final String DEFAULT_GENYMOTION_PATH = ""
    static final String VERSION_NOT_SET = "1.0"

    static final String DEPRECATED_ERROR = "is deprecated and has no effect on Genymotion configuration. " +
            "You should not use it anymore."
    static final String STORE_CREDENTIALS_ERROR = "genymotion.config.storeCredentials " + DEPRECATED_ERROR

    //plugin config
    def genymotionPath = ""     //set the Genymotion path to PATH

    //genymotion config

    def fromFile = null         //set config from a file. The content get the priority to the build.gradle file

    def statistics              //enable stats
    def username                //set the login
    def password                //set the password
    def licenseServer           //enable license server
    def licenseServerAddress    //set the license server address
    def license                 //set alphanumeric license
    def proxy                   //enables the proxy
    def proxyAddress            //set the proxy address
    def proxyPort               //set the proxy port
    def proxyAuth               //set the proxy protocol
    def proxyUsername           //set the proxy username
    def proxyPassword           //set the proxy password
    def virtualDevicePath       //set le dossier ou sont stockées les configs
    def androidSdkPath          //set the Android SDK folder
    def useCustomSdk            //enables the use of the Android sdk given by androidSdkPath
    def screenCapturePath       //set le dossier de stockage des screenshots

    @Deprecated
    def storeCredentials        //DEPRECATED: keep the configured account logged in genymotion

    String version = VERSION_NOT_SET      //contains the gmtool version name

    //task configs
    def taskLaunch = AndroidPluginTools.DEFAULT_ANDROID_TASK    //define the task that depends on our launch task
    boolean automaticLaunch = true                              //enable or not the genymotion tasks injection
    int processTimeout = 300000                                 //timeout in ms for processes launched in command line
    boolean verbose = false                                     //verbose mode
    boolean abortOnError = true                                 //abort the task execution if a GMTool error occurs

    protected String CONFIG_PREFIX = "genymotion."
    private Map CONFIG_PROPERTIES = ["genymotionPath"      : String.class,
                                     "statistics"          : Boolean.class,
                                     "username"            : String.class,
                                     "password"            : String.class,
                                     "licenseServer"       : Boolean.class,
                                     "licenseServerAddress": String.class,
                                     "license"             : String.class,
                                     "proxy"               : Boolean.class,
                                     "proxyAddress"        : String.class,
                                     "proxyPort"           : Integer.class,
                                     "proxyAuth"           : Boolean.class,
                                     "proxyUsername"       : String.class,
                                     "proxyPassword"       : String.class,
                                     "virtualDevicePath"   : String.class,
                                     "androidSdkPath"      : String.class,
                                     "useCustomSdk"        : Boolean.class,
                                     "screenCapturePath"   : String.class,
                                     "taskLaunch"          : String.class,
                                     "automaticLaunch"     : Boolean.class,
                                     "processTimeout"      : Integer.class,
                                     "verbose"             : Boolean.class,
                                     "abortOnError"        : Boolean.class]

    GenymotionConfig() {
        genymotionPath = getDefaultGenymotionPath()
    }

    boolean isEmpty() {
        return (statistics == null &&
                username == null &&
                password == null &&
                license == null &&
                licenseServer == null &&
                licenseServerAddress == null &&
                proxy == null &&
                proxyAddress == null &&
                proxyPort == null &&
                proxyAuth == null &&
                proxyUsername == null &&
                proxyPassword == null &&
                virtualDevicePath == null &&
                androidSdkPath == null &&
                useCustomSdk == null &&
                screenCapturePath == null)
    }

    /**
     * Fill the config object following a property file.
     * The file path is given by the fromFile field or the local.properties located at the root of the project.
     *
     * @param project the Project object
     *
     * @return returns true if the object has been field from a file or false otherwise
     */
    def applyConfigFromFile(Project project) {
        def Properties props = new Properties()
        def propFile

        if (fromFile == null && project != null) {
            fromFile = AndroidPluginTools.DEFAULT_PROPERTIES
            propFile = new File(project.rootDir, fromFile)
        } else if (fromFile != null) {
            propFile = new File(fromFile)
        } else {
            return false
        }

        if (propFile.canRead()) {
            try {
                props.load(new FileInputStream(propFile))

                //Reflection could be another solution to fill the object but I prefer to avoid it.
                //This method allows a better control on the data changed and avoid side loading non-allowed values

                //we iterate on the properties list to fill the config object
                CONFIG_PROPERTIES.each { key, value ->
                    def val = props.getProperty(CONFIG_PREFIX + key)

                    if (val != null) {
                        if (value == Boolean.class) {
                            val = GMTool.isOn(val)
                        }

                        this.setProperty(key, val.asType(value))
                    }
                }
            } catch (Exception e) {
                Log.warn("$fromFile file is missing, impossible to load configuration. " + e.message)
                return false
            }
        } else {
            Log.warn("$fromFile file is missing, no configuration to load")
            return false
        }
        return true
    }

    public void setGenymotionPath(String value) {
        if (value != null && value != "" && value.getAt(value.size() - 1) != File.separator) {
            value += File.separator
        }
        genymotionPath = value
    }

    public static String getDefaultGenymotionPath() {
        String os = Tools.getOSName().toLowerCase()

        if (os.contains("mac")) {
            return DEFAULT_GENYMOTION_PATH_MAC

        } else if (os.contains("windows")) {
            return DEFAULT_GENYMOTION_PATH_WINDOWS

        } else if (os.contains("linux")) {
            return DEFAULT_GENYMOTION_PATH_LINUX

        } else {
            return DEFAULT_GENYMOTION_PATH
        }
    }

    /**
     * @deprecated storeCredentials property is now deprecated on GenymotionConfig and should not be used. We override
     * its setter to display a warning.
     * @param value
     */
    @Deprecated
    public void setStoreCredentials(def value) {
        Log.warn(STORE_CREDENTIALS_ERROR)
    }
}
