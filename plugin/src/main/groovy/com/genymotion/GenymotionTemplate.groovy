package main.groovy.com.genymotion

/**
 * Created by eyal on 05/09/14.
 */
class GenymotionTemplate {

    String name
    String apiLevel
    String vdName
    int dpi =0
    int width = 0
    int height = 0
    boolean physicalButton = true
    boolean navbar = true
    int nbCpu = 0
    int ram = 0

    GenymotionTemplate(String name) {
        this.name = name
    }

    GenymotionTemplate(String name, String apiLevel, String vdName, int dpi, int width, int height, boolean physicalButton, boolean navbar, int nbCpu, int ram) {
        this.name = name
        this.apiLevel = apiLevel
        this.vdName = vdName
        this.dpi = dpi
        this.width = width
        this.height = height
        this.physicalButton = physicalButton
        this.navbar = navbar
        this.nbCpu = nbCpu
        this.ram = ram
    }
}
