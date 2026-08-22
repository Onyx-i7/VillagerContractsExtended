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
        // Register the EventHandler on the Forge event bus to capture interactions with
        // villagers
        MinecraftForge.EVENT_BUS.register(EventHandler.INSTANCE);
        LogHelper.debug("EventHandler registered correctly on the event bus");
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
        // Run the configuration sync
        // This ensures that all other mods have registered their professions and
        // careers before validating them
        ConfigHandler.ConfigChangeListener.syncConfigValues();

        // If the user enabled the information dump, it runs it when starting the game
        if (ConfigHandler.dumpVillagerInfo) {
            LogHelper.info("Starting villager information dump (dumpVillagerInfo)...");
            ConfigHandler.ConfigChangeListener.dumpVillagerInfo();
        }
    }
}