package main.groovy.com.genymotion

/**
 * Created by eyal on 05/09/14.
 */
class GenymotionTemplate {

    String name
    String uuid
    String description
    String androidVersion
    String genymotionVersion
    int width = 0
    int height = 0
    String density = 0
    int dpi = 0
    int nbCpu = 0
    int ram = 0
    int internalStorage = 0
    boolean telephony = true
    boolean virtualKeyboard = true
    boolean navbarVisible = true

    GenymotionTemplate() {
    }

    GenymotionTemplate(String name, String apiLevel, String vdName, int dpi, int width, int height, boolean virtualKeyboard, boolean navbarVisible, int nbCpu, int ram) {
        this.name = name
        this.apiLevel = apiLevel
        this.vdName = vdName
        this.dpi = dpi
        this.width = width
        this.height = height
        this.virtualKeyboard = virtualKeyboard
        this.navbarVisible = navbarVisible
        this.nbCpu = nbCpu
        this.ram = ram
    }
}
