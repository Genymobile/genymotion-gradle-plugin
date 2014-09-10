package com.genymotion

import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class GenymotionGradlePluginTest {
    @Test
    public void genymotionPluginAddsGreetingTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'genymotion'

        assertTrue(project.tasks.hello instanceof GreetingTask)
    }
}