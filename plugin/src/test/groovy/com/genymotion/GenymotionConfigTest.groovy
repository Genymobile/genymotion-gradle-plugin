package test.groovy.com.genymotion

import main.groovy.com.genymotion.tools.GMTool
import main.groovy.com.genymotion.model.GenymotionConfig
import org.gradle.api.Project
import org.junit.After
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.*

class GenymotionConfigTest {

    Project project
    boolean changedUser = false

    @Test
    public void isEmptyWhenEmpty() {
        GenymotionConfig config = new GenymotionConfig()

        assertTrue("Config should be empty", config.isEmpty())
    }

    @Test
    public void isNotEmptyWhenNotEmpty() {

        ["statistics",
         "username",
         "password",
         "storeCredentials",
         "license",
         "proxy",
         "proxyAddress",
         "proxyPort",
         "proxyAuth",
         "proxyUsername",
         "proxyPassword",
         "virtualDevicePath",
         "sdkPath",
         "useCustomSdk",
         "screenCapturePath"].each {
            testEmptyFromValue(it, "notNull")
        }
    }

    private testEmptyFromValue(String valueName, def value){
        GenymotionConfig config = new GenymotionConfig()
        config.setProperty(valueName, value)
        assertFalse("Should not be empty, $valueName not tested", config.isEmpty())
    }

    @Test
    public void canConfigFromFile(){

        project = TestTools.init()
        GenymotionConfig config = GMTool.getConfig(true)

        project.genymotion.config.fromFile = "res/test/config.properties"

        //we set the user as changed to set it again after the test
        changedUser = true

        //we set the config file
        project.tasks.genymotionLaunch.exec()

        assertEquals("statistics not loaded from file",         false,          project.genymotion.config.statistics)
        assertEquals("username not loaded from file",           "testName",     project.genymotion.config.username)
        assertEquals("password not loaded from file",           "testPWD",      project.genymotion.config.password)
        assertEquals("storeCredentials not loaded from file",   true,           project.genymotion.config.storeCredentials)
        assertEquals("license not loaded from file",            "testLicense",  project.genymotion.config.license)
        assertEquals("proxy not loaded from file",              false,           project.genymotion.config.proxy)
        assertEquals("proxyAddress not loaded from file",       "testAddress",  project.genymotion.config.proxyAddress)
        assertEquals("proxy not loaded from file",              false,           project.genymotion.config.proxy)
        assertEquals("proxyPort not loaded from file",          12345,          project.genymotion.config.proxyPort)
        assertEquals("proxyAuth not loaded from file",          true,           project.genymotion.config.proxyAuth)
        assertEquals("proxyUsername not loaded from file",      "testUsername", project.genymotion.config.proxyUsername)
        assertEquals("proxyPassword not loaded from file",      "testPWD",      project.genymotion.config.proxyPassword)
        assertEquals("virtualDevicePath not loaded from file",  "testPath",     project.genymotion.config.virtualDevicePath)
        assertEquals("sdkPath not loaded from file",            "testPath",     project.genymotion.config.sdkPath)
        assertEquals("useCustomSdk not loaded from file",       true,           project.genymotion.config.useCustomSdk)
        assertEquals("screenCapturePath not loaded from file",  "testPath",     project.genymotion.config.screenCapturePath)
        assertEquals("taskLaunch not loaded from file",         "testTask",     project.genymotion.config.taskLaunch)
        assertEquals("automaticLaunch not loaded from file",    true,           project.genymotion.config.automaticLaunch)
        assertEquals("processTimeout not loaded from file",     500000,         project.genymotion.config.processTimeout)
        assertEquals("verbose not loaded from file",            true,           project.genymotion.config.verbose)
        assertEquals("persist not loaded from file",            true,           project.genymotion.config.persist)
        assertEquals("abortOnError not loaded from file",       false,          project.genymotion.config.abortOnError)

        //we set the last config back
        GMTool.setConfig(config, true)

        //we set the default config credentials
        GMTool.setConfig(TestTools.getDefaultConfig(), true)
    }

    @After
    public void finishTest(){

        if(changedUser){
            TestTools.setDefaultUser(true)
            changedUser = false
        }
    }
}