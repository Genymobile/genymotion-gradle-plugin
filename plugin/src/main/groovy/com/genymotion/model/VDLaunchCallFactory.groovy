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

import main.groovy.com.genymotion.model.VDLaunchCall
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

/**
 * Created by eyal on 27/11/14.
 */
class VDLaunchCallFactory implements NamedDomainObjectFactory<VDLaunchCall> {

    final Instantiator instantiator
    final Project project

    public VDLaunchCallFactory(Instantiator instantiator, Project project) {
        this.instantiator = instantiator
        this.project = project
    }

    @Override
    VDLaunchCall create(String name) {
        return instantiator.newInstance(VDLaunchCall.class, name)
    }
}
