package com.genymotion

import main.groovy.com.genymotion.GenymotionAdmin
import main.groovy.com.genymotion.GenymotionConfig
import main.groovy.com.genymotion.GenymotionDevices
import main.groovy.com.genymotion.GenymotionVirtualDevice
import org.junit.Before
import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class GenymotionGradlePluginTest {

    Project project

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'genymotion'
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
        assertTrue(project.genymotion.devices instanceof GenymotionVirtualDevice[])
    }

}