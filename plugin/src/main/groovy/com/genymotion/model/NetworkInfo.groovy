package com.genymotion.model

import com.genymotion.tools.GMToolDsl

/**
 * Represents the details of the network configuration of a local device
 *
 * A local device can be connected either in NAT or Bridged mode. If it is connected in Bridged mode, then the bridge
 * interface must be defined.
 */
class NetworkInfo {
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
        return new NetworkInfo(GMToolDsl.NAT_MODE, EMPTY_INTERFACE);
    }

    static NetworkInfo createBridgeNetworkInfo(String networkInterface) {
        return new NetworkInfo(GMToolDsl.BRIDGE_MODE, networkInterface);
    }

    static boolean isNetworkModeValid(String mode) {
        return mode == GMToolDsl.NAT_MODE || mode == GMToolDsl.BRIDGE_MODE
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
            return GMToolDsl.BRIDGE_MODE
        } else {
            return GMToolDsl.NAT_MODE
        }
    }
}
