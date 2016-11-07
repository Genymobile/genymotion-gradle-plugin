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
import com.genymotion.tools.GMToolException
import com.genymotion.tools.GMToolFeature
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

import static org.mockito.Mockito.when


class GMToolFeatureTest extends CleanMetaTest {
    GMTool gmtool
    GMToolFeature gmToolFeature;

    @Before
    public void setUp() {
        this.gmtool = Mockito.mock(GMTool)
        this.gmToolFeature = GMToolFeature.newInstance(gmtool)

    }

    @Test
    public void testOkVersion() {
        when(gmtool.getVersion()).thenReturn("2.9.0")
        gmToolFeature.checkAvailability(GMToolFeature.Feature.DISPOSABLE)
    }

    @Test
    public void testTooOldVersion() {
        when(gmtool.getVersion()).thenReturn("2.1.0")

        try {
            gmToolFeature.checkAvailability(GMToolFeature.Feature.DISPOSABLE)
            fail("Expected GMToolException")
        } catch (GMToolException e) {
            assert e.message == "You need GMTool version 2.9.0 (current version is 2.1.0)"
        }
    }

    @Test
    public void testNotIntVersion() {
        when(gmtool.getVersion()).thenReturn("a.b.c")

        try {
            gmToolFeature.checkAvailability(GMToolFeature.Feature.DISPOSABLE)
            fail("Expected GMToolException")
        } catch (GMToolException e) {
            assert e.message == "Current GMTool version is unknown"
        }
    }

    @Test
    public void testNotTripletVersion() {
        // current implementation doesn't support this format. However, it should not happen
        when(gmtool.getVersion()).thenReturn("3.0")
        try {
            gmToolFeature.checkAvailability(GMToolFeature.Feature.DISPOSABLE)
            fail("Expected GMToolException")
        } catch (GMToolException e) {
            assert e.message == "Current GMTool version is unknown"
        }
    }

    @Test
    public void testDevVersion() {
        when(gmtool.getVersion()).thenReturn("2.9.0-310-g595b273")
        gmToolFeature.checkAvailability(GMToolFeature.Feature.DISPOSABLE)
    }
}
