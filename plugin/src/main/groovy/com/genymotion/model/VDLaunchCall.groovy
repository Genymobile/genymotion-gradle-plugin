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

import groovy.transform.CompileStatic
import main.groovy.com.genymotion.model.GenymotionVDLaunch

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
