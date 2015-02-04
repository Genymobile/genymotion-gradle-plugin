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
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GenymotionGradlePlugin.  If not, see <http://www.gnu.org/licenses/>.
 */

package main.groovy.com.genymotion.tools

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Log {
    private static Logger logger = null

    static synchronized Logger checkLogger() {
        if (logger == null)
            logger = LoggerFactory.getLogger("genymotion-logger")
    }

    public static synchronized def error(def message) {
        checkLogger()
        logger.error(normalizeMessage(message))
    }

    public static synchronized def warn(def message) {
        checkLogger()
        logger.warn(normalizeMessage(message))
    }

    public static synchronized def info(def message) {
        checkLogger()
        logger.info(normalizeMessage(message))
    }

    public static synchronized def debug(def message) {
        checkLogger()
        logger.debug(normalizeMessage(message))
    }

    private static String normalizeMessage(def message) {
        if(message instanceof String)
            return message
        else if (message instanceof List<String>) {
            String result = ""
            message.each {
                result += it.toString()
            }
        }
    }
}
