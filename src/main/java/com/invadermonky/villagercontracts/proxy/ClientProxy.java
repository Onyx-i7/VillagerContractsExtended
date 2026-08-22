package com.invadermonky.villagercontracts.proxy;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        // Call the common proxy method to register the EventHandler
        super.preInit(event);
    }
}