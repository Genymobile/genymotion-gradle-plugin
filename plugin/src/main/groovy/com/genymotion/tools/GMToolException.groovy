package main.groovy.com.genymotion.tools

import groovy.transform.CompileStatic

/**
 * Created by eyal on 17/10/14.
 */
@CompileStatic
class GMToolException extends Exception {
    GMToolException(){
        super()
    }

    GMToolException(String message){
        super(message)
    }
}
