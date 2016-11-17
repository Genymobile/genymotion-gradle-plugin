/*
 * Copyright (C) 2015 Genymobile
 *
 * This file is part of GenymotionGradlePlugin.
 *
 * GenymotionGradlePlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version
 *
 * GenymotionGradlePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenymotionGradlePlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.genymotion

import com.genymotion.tools.GMTool
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import static org.mockito.Mockito.mock

class TestTools {

    public static final String[] RANDOM_NAMES = ["Sam", "Julien", "Dan", "Pascal", "Guillaume", "Damien", "Thomas",
                                                 "Sylvain", "Philippe", "Cedric", "Charly", "Morgan", "Bruno"]

    static def init(def gmtool = mock(GMTool)) {

        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'genymotion'

        GMTool.DEFAULT_CONFIG = project.genymotion.config

        project.genymotion.config.verbose = true

        [project, gmtool]
    }

    static def getAndroidProject(def gmtool = null) {

        Project project = ProjectBuilder.builder().withProjectDir(new File("src/integTest/res/test/android-app")).build();

        project.apply plugin: 'com.android.application'
        project.apply plugin: 'genymotion'

        project.android {
            compileSdkVersion 23
            buildToolsVersion "25"
        }

        if (gmtool == null) {
            gmtool = mock(GMTool)
        }

        [project, gmtool]
    }
}
