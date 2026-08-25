package com.invadermonky.villagercontracts.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Method;

import com.invadermonky.villagercontracts.util.LogHelper;

public class GameStageIntegration {

    private static final String GAMESTAGES_MOD_ID = "gamestages";
    private static boolean isGameStagesInstalled = false;
    private static Method hasStageMethod = null;
    private static boolean reflectionInitialized = false;

    public static void initialize() {
        isGameStagesInstalled = Loader.isModLoaded(GAMESTAGES_MOD_ID);
        
        if (isGameStagesInstalled) {
            try {
                Class<?> gameStageHelper = Class.forName("net.darkhax.gamestages.GameStageHelper");
                hasStageMethod = gameStageHelper.getMethod("hasStage", EntityPlayer.class, String.class);
                reflectionInitialized = true;
                LogHelper.info("Game Stages detected! Integration enabled.");
            } catch (Exception e) {
                LogHelper.warn("Game Stages is installed but reflection failed: " + e.getMessage());
                isGameStagesInstalled = false;
                reflectionInitialized = false;
            }
        } else {
            LogHelper.info("Game Stages not detected. Skipping integration.");
        }
    }

    public static boolean isAvailable() {
        return isGameStagesInstalled && reflectionInitialized;
    }

    public static boolean hasRequiredStage(EntityPlayer player, String stageName) {
        if (!isAvailable()) {
            return true;
        }

        try {
            Object result = hasStageMethod.invoke(null, player, stageName);
            return result instanceof Boolean && (Boolean) result;
        } catch (Exception e) {
            LogHelper.error("Failed to check Game Stage: " + e.getMessage());
            return false;
        }
    }
}