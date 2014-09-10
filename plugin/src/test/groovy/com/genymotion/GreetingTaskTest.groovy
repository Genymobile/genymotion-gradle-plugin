package com.genymotion

import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class GenymotionTaskTest {
    @Test
    public void canAddTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        def task = project.task('genymotion', type: GenymotionTask)

        assertTrue(task instanceof GenymotionTask)
    }
}
