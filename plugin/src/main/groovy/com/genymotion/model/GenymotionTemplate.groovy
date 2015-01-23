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

package main.groovy.com.genymotion.model

/**
 * Created by eyal on 05/09/14.
 */
class GenymotionTemplate {

    String name
    String uuid
    String description
    String androidVersion
    String genymotionVersion
    int width = 0
    int height = 0
    String density = 0
    int dpi = 0
    int nbCpu = 0
    int ram = 0
    int internalStorage = 0
    boolean telephony = true
    boolean virtualKeyboard = true
    boolean navbarVisible = true

    GenymotionTemplate() {
    }
}
