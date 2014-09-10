package com.genymotion

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CmdTask extends DefaultTask {

    @TaskAction
    def exec() {
        def process = "ls -l".execute()
        println process.text
    }

}
