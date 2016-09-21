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

import com.genymotion.model.LocalVDLaunchDsl
import com.genymotion.tools.GMTool
import org.gradle.api.Project
import org.junit.After
import org.junit.Test

import static org.mockito.Mockito.when

class LocalVDLaunchDslTest extends CleanMetaTest {
    static GMTool gmtool

    @Test
    public void deleteWhenFinish() {

        def (Project project, GMTool gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdTemplate = "templateName"
        when(gmtool.templateExists(vdTemplate)).thenReturn(true)

        project.genymotion.devices {
            "test-fdfdsfd" {
                stopWhenFinish false
                template vdTemplate
            }
        }
        project.genymotion.checkParams()

        assert project.genymotion.devices != null
        assert project.genymotion.devices[0].stopWhenFinish == false
        assert project.genymotion.devices[0].deleteWhenFinish == true
        assert vdTemplate == project.genymotion.devices[0].template
        assert project.genymotion.devices[0].templateExists
        assert project.genymotion.devices[0].deviceExists == false
    }

    @Test
    public void setStopWhenFinish() {

        def (Project project, GMTool gmtool) = TestTools.init()
        GMTool.metaClass.static.newInstance = { gmtool }

        String vdTemplate = "templateName"
        when(gmtool.templateExists(vdTemplate)).thenReturn(true)

        project.genymotion.devices {
            "test-fdfqd" {
                deleteWhenFinish true
                template vdTemplate
            }
        }
        project.genymotion.checkParams()

        assert project.genymotion.devices != null
        assert project.genymotion.devices[0].stopWhenFinish == true
        assert project.genymotion.devices[0].deleteWhenFinish == true
        assert vdTemplate == project.genymotion.devices[0].template
        assert project.genymotion.devices[0].templateExists
        assert project.genymotion.devices[0].deviceExists == false
    }

    @Test
    public void canSetProductFlavor() {
        LocalVDLaunchDsl vd = new LocalVDLaunchDsl("device")

        vd.productFlavors "NONO"
        assert vd.productFlavors == ["NONO"]

        vd.productFlavors = ["NONO", "NINI"]
        assert vd.productFlavors == ["NONO", "NINI"]

        vd.productFlavors "NONO", "NINI"
        assert vd.productFlavors == ["NONO", "NINI"]

        vd.productFlavors = null
        assert vd.productFlavors == []
    }

    @After
    public void finishTest() {
        cleanMetaClass()
    }
}
