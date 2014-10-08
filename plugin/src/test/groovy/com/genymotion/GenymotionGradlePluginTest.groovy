package test.groovy.com.genymotion

import main.groovy.com.genymotion.GenymotionTask

import java.util.List
import main.groovy.com.genymotion.GenymotionAdmin
import main.groovy.com.genymotion.GenymotionConfig
import main.groovy.com.genymotion.GenymotionTool
import main.groovy.com.genymotion.GenymotionVirtualDevice
import main.groovy.com.genymotion.GenymotionPluginExtension
import org.junit.Before
import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project


import static org.junit.Assert.*
import static org.junit.Assert.assertNotNull

class GenymotionGradlePluginTest {

    static def GENYMOTION_PATH = "/home/eyal/genymotion/genymotion-softs/build/"

    Project project

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'genymotion'

        project.genymotion.config.genymotionPath = GENYMOTION_PATH
        //we set the config inside the GenymotionTool
        GenymotionTool.GENYMOTION_CONFIG = project.genymotion.config
    }

    @Test
    public void canAddsTaskToProject() {
        assertTrue(project.tasks.genymotionLaunch instanceof GenymotionTask)
    }

    @Test
    public void canAddExtensionToProject() {
        assertTrue(project.genymotion instanceof GenymotionPluginExtension)
        assertTrue(project.genymotion.config instanceof GenymotionConfig)
        assertTrue(project.genymotion.admin instanceof GenymotionAdmin)
        assertTrue(project.genymotion.devices instanceof List)
    }

}