package com.invadermonky.villagercontracts.network;

import com.invadermonky.villagercontracts.VillagerContracts;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Central network packet handler
 * Logs all the packets from the mod
 */
public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE
            .newSimpleChannel(VillagerContracts.MOD_ID);

    public static void init() {
        // Register the package to rename contracts (client -> server)
        INSTANCE.registerMessage(PacketApplyContractName.Handler.class, PacketApplyContractName.class, 0, Side.SERVER);
    }
}