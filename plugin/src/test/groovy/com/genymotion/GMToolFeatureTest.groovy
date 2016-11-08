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

import com.genymotion.tools.GMToolException
import com.genymotion.tools.GMToolFeature
import org.junit.Test

class GMToolFeatureTest extends CleanMetaTest {
    @Test
    public void testOkVersion() {
        GMToolFeature.checkAvailability(GMToolFeature.Feature.DISPOSABLE, GMToolFeature.versionTuple("2.9.0"))
    }

    @Test
    public void testTooOldVersion() {
        try {
            GMToolFeature.checkAvailability(GMToolFeature.Feature.DISPOSABLE, GMToolFeature.versionTuple("2.1.0"))
            fail("Expected GMToolException")
        } catch (GMToolException e) {
            assert e.message == "You need GMTool version 2.9.0 (current version is 2.1.0)"
        }
    }

    @Test
    public void testNotIntVersion() {
        try {
            GMToolFeature.checkAvailability(GMToolFeature.Feature.DISPOSABLE, GMToolFeature.versionTuple("a.b.c"))
            fail("Expected GMToolException")
        } catch (GMToolException e) {
            assert e.message == "Current GMTool version is unknown"
        }
    }

    @Test
    public void testNotTripletVersion() {
        try {
            // current implementation doesn't support this format. However, it should not happen
            GMToolFeature.versionTuple("3.0")
            fail("Expected GMToolException")
        } catch (GMToolException e) {
            assert e.message == "Current GMTool version is unknown"
        }
    }

    @Test
    public void testDevVersion() {
        GMToolFeature.checkAvailability(GMToolFeature.Feature.DISPOSABLE,
                GMToolFeature.versionTuple("2.9.0-310-g595b273"))
    }
}
