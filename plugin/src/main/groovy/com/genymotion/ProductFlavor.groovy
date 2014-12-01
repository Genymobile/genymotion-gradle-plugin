package main.groovy.com.genymotion

import org.apache.tools.ant.Project

class ProductFlavor {

    String name
    Project project

    ProductFlavor(String name, project){
        this.name = name
        this.project = project
    }

    void device(Map params){
        project.genymotion.device(params, name)
    }
}
