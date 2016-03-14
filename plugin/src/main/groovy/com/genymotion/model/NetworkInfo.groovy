package com.genymotion.model

class NetworkInfo {
    public static final String NAT_MODE = "Nat"
    public static final String BRIDGE_MODE = "Bridge"

    String mode
    String bridgeInterface

    NetworkInfo(String mode, String bridgeInterface) {
        this.mode = mode;
        this.bridgeInterface = bridgeInterface;
    }

    static NetworkInfo createNatNetworkInfo() {
        return new NetworkInfo(NAT_MODE, "");
    }

    static NetworkInfo createBridgeNetworkInfo(String networkInterface) {
        return new NetworkInfo(BRIDGE_MODE, networkInterface);
    }
}
