package com.genymotion

import com.genymotion.model.NetworkInfo
import org.junit.Test

class NetworkInfoTest {
    @Test
    public void givenNatNetworkingDetailsThenAProperNetworkInfoIsCreated() {
        String natNetworkDetails = "Nat"

        NetworkInfo networkInfo = NetworkInfo.fromGMtoolDeviceDetails(natNetworkDetails)

        assert networkInfo.mode.equals(NetworkInfo.NAT_MODE)
        assert networkInfo.bridgeInterface.equals("")
    }

    @Test
    public void givenBridgeNetworkingDetailsThenAProperNetworkInfoIsCreated() {
        String natNetworkDetails = "Bridged eth0"
        String expectedInterface = "eth0"

        NetworkInfo networkInfo = NetworkInfo.fromGMtoolDeviceDetails(natNetworkDetails)

        assert networkInfo.mode.equals(NetworkInfo.BRIDGE_MODE)
        assert networkInfo.bridgeInterface.equals(expectedInterface)
    }

    @Test
    public void givenNetworkDetailsWithWindowsStyleInterfaceThenAProperNetworkInfoIsCreated() {
        String natNetworkDetails = "Bridged my network interface"

        NetworkInfo networkInfo = NetworkInfo.fromGMtoolDeviceDetails(natNetworkDetails)

        assert networkInfo.mode.equals(NetworkInfo.BRIDGE_MODE)
        assert networkInfo.bridgeInterface.equals("my network interface")
    }
}
