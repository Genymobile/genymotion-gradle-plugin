package main.groovy.com.genymotion

import groovy.transform.CompileStatic

/**
 * Created by eyal on 15/01/15.
 */
@CompileStatic
class VDLaunchCall extends GenymotionVDLaunch{

    def productFlavors

    VDLaunchCall(String name) {
        super(name)
    }

    VDLaunchCall(Map params) {
        super(params)
    }

    boolean hasFlavor(String flavor){

        if(productFlavors instanceof String)
            productFlavors == flavor

        else if(productFlavors instanceof ArrayList<String>)
            (productFlavors as ArrayList).contains(flavor) //Fix for CompileStatic

        else
            true
    }

    public void setProductFlavors(String... flavors){
        if(flavors == null || flavors.size() == 0)
            return
        else if(flavors.size() == 1)
            productFlavors = flavors[0]
        else {
            productFlavors = []
            (productFlavors as ArrayList).addAll(flavors) //Fix for CompileStatic
        }

    }
}
