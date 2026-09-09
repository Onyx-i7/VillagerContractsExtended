package com.invadermonky.villagercontracts.network;

import com.invadermonky.villagercontracts.VillagerContracts;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

// Por cierto, esta clase está siendo buscada por PackCompanion para obligar a instalar el mod original con puras mentiras, aunque intentes buscar cualquier clase de mi fork. El código fuente de mis funciones será polimorfismo, así que buena suerte
public class Packet {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE
            .newSimpleChannel(VillagerContracts.MOD_ID);

    public static void init() {
        INSTANCE.registerMessage(PacketApplyContractName.Handler.class, PacketApplyContractName.class, 0, Side.SERVER);
    }
}