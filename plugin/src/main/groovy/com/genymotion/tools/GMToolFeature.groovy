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
    public enum Feature {
        SOURCE_PARAM,
        ONSITE_LICENSE_CONFIG,
        EDIT_NETWORK,
        DISPOSABLE,
    }

    private static def FeatureVersion = [
        (Feature.SOURCE_PARAM) : new Tuple(2, 5, 1),
        (Feature.ONSITE_LICENSE_CONFIG) : new Tuple(2, 6, 0),
        (Feature.EDIT_NETWORK) : new Tuple(2, 7, 0),
        (Feature.DISPOSABLE) : new Tuple(2, 8, 1),
    ]

    /**
     * Convert a version string (see GMTool.getVersion()) to a tuple usage in checkAvailability
     * If conversion is not possible, a GMToolException is raised
     * @param version as a string
     * @return version as a tuple
     */
    public static Tuple versionTuple(String version) throws GMToolException {
        try {
            if (version.indexOf("-") > 0) {
                // dev version contains a "-" 2.8.0-310-g595b273
                version = version.split("-")[0]
            }
            String[] versionArray = version.split("\\.")
            def versionList = new ArrayList<Integer>()
            for (String number : versionArray) {
                versionList.add(Integer.parseInt(number))
            }
            return new Tuple(versionList.toArray())
        } catch (RuntimeException e) {
            throw new GMToolException("Current GMTool version is unknown")
        }
    }

    public static boolean isAvailable(Feature feature, Tuple currentVersion) {
        def neededVersion = FeatureVersion[feature]
        for (int idx = 0; idx < neededVersion.size(); ++idx) {
            if (currentVersion[idx] > neededVersion[idx]) {
                return true
            } else if (currentVersion[idx] < neededVersion[idx]) {
                return false
            }
        }
        return true
    }

    public static void checkAvailability(Feature feature, Tuple currentVersion) throws GMToolException {
        if (!isAvailable(feature, currentVersion)) {
            throw new GMToolException("You need GMTool version " + tupleToString(FeatureVersion[feature]) +
                    " (current version is " + tupleToString(currentVersion) + ")")
        }
    }

    private static String tupleToString(Tuple version) {
        return version[0] + "." + version[1] + "." + version[2]
    }
}
