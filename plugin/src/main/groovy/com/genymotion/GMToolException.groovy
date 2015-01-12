package main.groovy.com.genymotion

import groovy.transform.CompileStatic

import java.lang.Exception

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
