package test.groovy.com.genymotion

import main.groovy.com.genymotion.*
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.*

class GenymotionConfigTest {

    Project project

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

}