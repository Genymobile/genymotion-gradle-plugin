package com.genymotion.model

class NetworkInfo {
    String mode
    String bridgeInterface

    NetworkInfo(String mode, String bridgeInterface) {
        this.mode = mode;
        this.bridgeInterface = bridgeInterface;
    }
}
