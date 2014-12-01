package main.groovy.com.genymotion

import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

/**
 * Created by eyal on 27/11/14.
 */
class ProductFlavorFactory implements NamedDomainObjectFactory<ProductFlavor> {

    final Instantiator instantiator
    final Project project

    public ProductFlavorFactory(Instantiator instantiator, Project project) {
        this.instantiator = instantiator
        this.project = project
    }

    @Override
    ProductFlavor create(String name) {
        return instantiator.newInstance(ProductFlavor.class, name, project)
    }
}
