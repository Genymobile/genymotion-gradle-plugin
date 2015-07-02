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

import org.junit.After
import org.junit.Before

/**
 * It is convenient to mock methods and values for testing by manipulating the metaClass.
 * But, as the classes are not reloaded between the tests, it can include side effects between tests.
 * CleanMetaTest class manages all the metaclass cleaning between tests.
 *
 * This trick is coming from the Grails project : https://jira.grails.org/browse/GRAILS-7293
 *
 * To use it you need extend you test class with this class.
 * It is also highly recommended to call {cleanMetaClass} method at the begining of your @After method
 * to avoid side effects during the tear down.
 */
class CleanMetaTest {
    def emcEvents = []
    def listener

    @Before
    public void setUpCleanMetaTest() {
        monitorMetaClassEvents()
    }

    @After
    public void finishCleanMetaTest() {
        cleanMetaClass()
    }

    protected void cleanMetaClass() {
        GroovySystem.metaClassRegistry.removeMetaClassRegistryChangeEventListener listener
        emcEvents.each { MetaClassRegistryChangeEvent event ->
            GroovySystem.metaClassRegistry.removeMetaClass event.clazz
        }
    }

    protected void monitorMetaClassEvents() {
        this.listener = { MetaClassRegistryChangeEvent event ->
            emcEvents << event
        } as MetaClassRegistryChangeEventListener

        GroovySystem.metaClassRegistry.addMetaClassRegistryChangeEventListener this.listener
    }
}
