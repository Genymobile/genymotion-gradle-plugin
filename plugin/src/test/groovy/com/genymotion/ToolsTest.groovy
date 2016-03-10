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

import com.genymotion.tools.Tools
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

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
        assert result == list

        def single = "ok"
        result = Tools.getStringTable(single)
        assert result == [single]

        def map = ["ok": "er", "nok": "er", "sorry": "er"]
        result = Tools.getStringTable(map)
        assert result == list

        result = Tools.getStringTable(null)
        assert result == []
    }

    @Test
    public void canCheckFileExists() {

        def list = ["src/integTest/res/test/test.txt", "src/integTest/res/test/test2.txt", "src/integTest/res/test/test.zip", "src/integTest/res/test/test2.zip"]
        assert Tools.checkFilesExist(list)

        def single = "src/integTest/res/test/test.txt"
        assert Tools.checkFilesExist(single)

        def map = ["src/integTest/res/test/test.txt": "/sdcard/Downloads/", "src/integTest/res/test/test2.txt": "/sdcard/Downloads/",
                   "src/integTest/res/test/test.zip": "/sdcard/Downloads/", "src/integTest/res/test/test2.zip": "/sdcard/Downloads/"]
        assert Tools.checkFilesExist(map)

        assert Tools.checkFilesExist(null)
    }

    @Test
    public void canCheckFileDoesNotExist() {

        def list = ["src/integTest/res/test/test.txt", "NOPE", "src/integTest/res/test/test.zip", "src/integTest/res/test/test2.zip"]
        assert Tools.checkFilesExist(list) == "NOPE"

        def single = "NOPE"
        assert Tools.checkFilesExist(single) == "NOPE"

        def map = ["src/integTest/res/test/test.txt": "/sdcard/Downloads/", "src/integTest/res/test/test2.txt": "/sdcard/Downloads/",
                   "src/integTest/res/test/test.zip": "/sdcard/Downloads/", "NOPE": "/sdcard/Downloads/"]
        assert Tools.checkFilesExist(map) == "NOPE"
    }

    @After
    public void finishTest() {
    }

}
