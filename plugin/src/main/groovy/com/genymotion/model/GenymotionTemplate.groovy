package main.groovy.com.genymotion.model

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
}
