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



package test.groovy.com.genymotion

import main.groovy.com.genymotion.tools.Tools
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class ToolsTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void canGetStringTable() {

        def list = ["ok", "nok", "sorry"]
        def result = Tools.getStringTable(list)
        assertEquals(list, result)

        def single = "ok"
        result = Tools.getStringTable(single)
        assertEquals([single], result)

        def map = ["ok":"er", "nok":"er", "sorry":"er"]
        result = Tools.getStringTable(map)
        assertEquals(list, result)

        result = Tools.getStringTable(null)
        assertEquals([], result)
    }

    @Test
    public void canCheckFileExists() {

        def list = ["res/test/test.txt", "res/test/test2.txt", "res/test/test.zip", "res/test/test2.zip"]
        assertTrue(Tools.checkFilesExist(list))

        def single = "res/test/test.txt"
        assertTrue(Tools.checkFilesExist(list))

        def map = ["res/test/test.txt":"/sdcard/Downloads/", "res/test/test2.txt":"/sdcard/Downloads/",
                   "res/test/test.zip":"/sdcard/Downloads/", "res/test/test2.zip":"/sdcard/Downloads/"]
        assertTrue(Tools.checkFilesExist(list))

        assertTrue(Tools.checkFilesExist(null))
    }

    @Test
    public void canCheckFileDoesNotExists() {

        def list = ["res/test/test.txt", "NOPE", "res/test/test.zip", "res/test/test2.zip"]
        assertEquals("NOPE", Tools.checkFilesExist(list))

        def single = "NOPE"
        assertEquals("NOPE", Tools.checkFilesExist(list))

        def map = ["res/test/test.txt":"/sdcard/Downloads/", "res/test/test2.txt":"/sdcard/Downloads/",
                   "res/test/test.zip":"/sdcard/Downloads/", "NOPE":"/sdcard/Downloads/"]
        assertEquals("NOPE", Tools.checkFilesExist(list))
    }

    @After
    public void finishTest() {
    }

}
