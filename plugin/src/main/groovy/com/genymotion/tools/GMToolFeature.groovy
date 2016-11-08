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

package com.genymotion.tools

/**
 * Check if a feature is available in the current gmtool
 */
public class GMToolFeature {
    private GMTool gmtool
    private String version

    public enum Feature {
        DISPOSABLE,
        EDIT_NETWORK,
    }

    private static def FeatureVersion = [
        (Feature.EDIT_NETWORK) : "2.7.0",
        (Feature.DISPOSABLE): "2.9.0",
    ]

    static GMToolFeature newInstance(GMTool gmtool) {
        GMToolFeature gmtoolFeature = new GMToolFeature()
        gmtoolFeature.gmtool = gmtool

        return  gmtoolFeature
    }

    private void initVersion() {
        this.version = gmtool.getVersion()
    }

    private static def versionTuple(String version) {
        if (version.indexOf("-") > 0) {
            // dev version contains a "-" 2.8.0-310-g595b273
            version = version.split("-")[0]
        }
        String[] versionArray = version.split("\\.")

        return [ Integer.parseInt(versionArray[0]), Integer.parseInt(versionArray[1]), Integer.parseInt(versionArray[2]) ]
    }

    void checkAvailability(Feature feature) throws GMToolException {
        if (!version || version.isEmpty()) {
            initVersion()
        }

        try {
            def currentVersion = versionTuple(this.version)
            def neededVersion = versionTuple(FeatureVersion[feature])

            if (currentVersion[0] < neededVersion[0] ||
                    currentVersion[1] < neededVersion[1] ||
                    currentVersion[2] < neededVersion[2]) {
                throw new GMToolException("You need GMTool version " + FeatureVersion[feature] + " (current version is " +
                        version + ")")
            }
        } catch (RuntimeException e) {
            throw new GMToolException("Current GMTool version is unknown")
        }
    }
}
