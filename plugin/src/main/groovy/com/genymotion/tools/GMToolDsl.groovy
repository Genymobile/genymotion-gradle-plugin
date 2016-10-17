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

package com.genymotion.tools

class GMToolDsl {

    //@formatter:off
    public static final String GMTOOL        = "gmtool"
    public static final String OPT_CLOUD     = "--cloud"
    public static final String VERBOSE       = "--verbose"
    public static final String SOURCE        = "--source"
    public static final String SOURCE_GRADLE = SOURCE + "=gradle"

    //root actions
    public static final String LOGZIP        = "logzip"
    public static final String HELP          = "help"
    public static final String VERSION       = "version"
    //admin actions
    public static final String ADMIN                       = "admin"
    public static final String LIST                        = "list"
    public static final String TEMPLATES                   = "templates"
    public static final String CREATE                      = "create"
    public static final String EDIT                        = "edit"
    public static final String DELETE                      = "delete"
    public static final String CLONE                       = "clone"
    public static final String DETAILS                     = "details"
    public static final String START                       = "start"
    public static final String RESTART                     = "restart"
    public static final String STOP                        = "stop"
    public static final String STOPALL                     = "stopall"
    public static final String FACTORY_RESET               = "factoryreset"
    public static final String START_DISPOSABLE            = "startdisposable"
    public static final String STOP_DISPOSABLE             = "stopdisposable"
    //device actions
    public static final String DEVICE        = "device"
    public static final String PUSH          = "push"
    public static final String PULL          = "pull"
    public static final String INSTALL       = "install"
    public static final String FLASH         = "flash"
    public static final String LOGCAT_CLEAR  = "logcatclear"
    public static final String LOGCAT_DUMP   = "logcatdump"
    public static final String ADBDISCONNECT = "adbdisconnect"
    public static final String ADBCONNECT    = "adbconnect"
    //config actions
    public static final String CONFIG        = "config"
    public static final String PRINT         = "print"
    public static final String RESET         = "reset"
    public static final String CLEARCACHE    = "clearcache"
    //license
    public static final String LICENSE       = "license"
    public static final String INFO          = "info"
    public static final String REGISTER      = "register"
    public static final String COUNT         = "count"
    public static final String VERIFY        = "verify"
    public static final String VALIDITY      = "validity"
    //options
    public static final String OPT_RUNNING                 = "--running"
    public static final String OPT_OFF                     = "--off"
    public static final String OPT_FULL                    = "--full"
    public static final String OPT_DENSITY                 = '--density='
    public static final String OPT_WIDTH                   = '--width='
    public static final String OPT_HEIGHT                  = '--height='
    public static final String OPT_VIRTUAL_KEYBOARD        = '--virtualkeyboard='
    public static final String OPT_NAVBAR                  = '--navbar='
    public static final String OPT_NBCPU                   = '--nbcpu='
    public static final String OPT_RAM                     = "--ram="
    public static final String OPT_NAME                    = "-n="
    public static final String OPT_NETWORK_MODE            = "--network-mode="
    public static final String OPT_BRIDGE_INTERFACE        = "--bridge-if="
    public static final String OPT_STATISTICS              = "statistics="
    public static final String OPT_USERNAME_CONFIG         = "username="
    public static final String OPT_PASSWORD_CONFIG         = "password="
    public static final String OPT_LICENSE_SERVER          = "license_server="
    public static final String OPT_LICENSE_SERVER_ADDRESS  = "license_server_address="
    public static final String OPT_PROXY                   = "proxy="
    public static final String OPT_PROXY_ADDRESS           = "proxy_address="
    public static final String OPT_PROXY_PORT              = "proxy_port="
    public static final String OPT_PROXY_AUTH              = "proxy_auth="
    public static final String OPT_PROXY_USERNAME          = "proxy_username="
    public static final String OPT_PROXY_PASSWORD          = "proxy_password="
    public static final String OPT_VIRTUAL_DEVICE_PATH     = "virtual_device_path="
    public static final String OPT_SDK_PATH                = "sdk_path="
    public static final String OPT_USE_CUSTOM_SDK          = "use_custom_sdk="
    public static final String OPT_SCREEN_CAPTURE_PATH     = "screen_capture_path="

    public static final String OPTION_ON                = "on"
    public static final String OPTION_OFF               = "off"

    /**
     * valid input for --network-mode
     */
    public static final String NAT_MODE                 = "nat"
    public static final String BRIDGE_MODE              = "bridge"

    //Minimum gmtool version for each feature after first release
    /**
    * Adding --source to gmtool commands
     */
    public static final String FEATURE_SOURCE_PARAM             = "2.5.1"

    /**
    * Adding license_server & license_server_address config options
     */
    public static final String FEATURE_ONSITE_LICENSE_CONFIG    = "2.6"

    //code returned by gmtool or command line
    public static final int RETURN_NO_ERROR                = 0
    public static final int RETURN_NO_SUCH_ACTION          = 1
    public static final int RETURN_BAD_PARAM_VALUE         = 2
    public static final int RETURN_COMMAND_FAILED          = 3
    public static final int RETURN_VMENGINE_ERROR          = 4
    public static final int RETURN_DEVICE_NOT_FOUND        = 5
    public static final int RETURN_CANT_LOGIN              = 6
    public static final int RETURN_CANT_REGISTER_LICENSE   = 7
    public static final int RETURN_CANT_ACTIVATE_LICENSE   = 8
    public static final int RETURN_NO_ACTIVATED_LICENSE    = 9
    public static final int RETURN_INVALID_LICENSE         = 10
    public static final int RETURN_MISSING_ARGUMENTS       = 11
    public static final int RETURN_VM_NOT_STOPPED          = 12
    public static final int RETURN_LICENSE_REQUIRED        = 13
    public static final int RETURN_COMMAND_NOT_FOUND_UNIX  = 127
    public static final int RETURN_SIGTERM                 = 143
    //@formatter:on

    /**
     * Translate a on/off value coming from gmtool to a boolean.
     * If the value is not known as a gmtool value the method returns value.toBoolean
     *
     * @param value
     * @return returns true when
     */
    static boolean isOn(String value) {
        return value == OPTION_ON || value.toBoolean()
    }
}
