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
