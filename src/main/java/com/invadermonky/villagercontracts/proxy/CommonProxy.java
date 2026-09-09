package com.invadermonky.villagercontracts.proxy;

import com.invadermonky.villagercontracts.handlers.ConfigHandler;
import com.invadermonky.villagercontracts.handlers.EventHandler;
import com.invadermonky.villagercontracts.util.LogHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        // Registra el EventHandler.java en el bus de eventos de Forge para detectar la accion del jugador con el aldeano
        MinecraftForge.EVENT_BUS.register(EventHandler.INSTANCE);
        LogHelper.debug("EventHandler registered correctly on the event bus");
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
        ConfigHandler.ConfigChangeListener.syncConfigValues();

        if (ConfigHandler.dumpVillagerInfo) {
            LogHelper.info("Starting villager information dump (dumpVillagerInfo)...");
            ConfigHandler.ConfigChangeListener.dumpVillagerInfo();
        }
    }
}