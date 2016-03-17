package com.genymotion.model

class NetworkInfo {
    public static final String NAT_MODE = "nat"
    public static final String BRIDGE_MODE = "bridge"

    private static final String EMPTY_INTERFACE = ""
    private static final String NETWORK_DETAILS_SEPARATOR = " "
    private static final String NETWORK_DETAILS_BRIDGE = "Bridged"

    String mode
    String bridgeInterface

    NetworkInfo(String networkMode, String networkInterface) {
        this.mode = networkMode
        this.bridgeInterface = networkInterface
    }

    static NetworkInfo createNatNetworkInfo() {
        return new NetworkInfo(NAT_MODE, EMPTY_INTERFACE);
    }

    static NetworkInfo createBridgeNetworkInfo(String networkInterface) {
        return new NetworkInfo(BRIDGE_MODE, networkInterface);
    }

    static boolean isNetworkModeValid(String mode) {
        return mode == NAT_MODE || mode == BRIDGE_MODE
    }

    static NetworkInfo fromGMtoolDeviceDetails(String deviceNetworkDetails) {
        if (deviceNetworkDetails.contains(NETWORK_DETAILS_SEPARATOR)) {
            int separatorPosition = deviceNetworkDetails.indexOf(NETWORK_DETAILS_SEPARATOR)
            String networkModeFromDetails = deviceNetworkDetails.subSequence(0, separatorPosition)
            String networkInterface = deviceNetworkDetails.subSequence(separatorPosition+1, deviceNetworkDetails.size())
            String networkMode = getNetworkModeFromDetails(networkModeFromDetails)

            return new NetworkInfo(networkMode, networkInterface)
        } else {
            String networkMode = getNetworkModeFromDetails(deviceNetworkDetails)
            return new NetworkInfo(networkMode, EMPTY_INTERFACE)
        }
    }

    private static String getNetworkModeFromDetails(String details) {
        if (details.equals(NETWORK_DETAILS_BRIDGE)) {
            return BRIDGE_MODE
        } else {
            return NAT_MODE
        }
    }
}
