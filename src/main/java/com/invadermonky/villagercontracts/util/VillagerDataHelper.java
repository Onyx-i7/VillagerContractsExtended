package com.invadermonky.villagercontracts.util;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Helper class for managing villager NBT data related to the contract system.
 */
public class VillagerDataHelper {

    private static final String NBT_LAST_CONTRACT_TIME = "vc_last_contract_time";

    public static long getLastContractTime(EntityVillager villager) {
        NBTTagCompound nbt = villager.getEntityData();
        if (nbt.hasKey(NBT_LAST_CONTRACT_TIME)) {
            return nbt.getLong(NBT_LAST_CONTRACT_TIME);
        }
        return -1;
    }

    public static void setLastContractTime(EntityVillager villager, long currentTime) {
        villager.getEntityData().setLong(NBT_LAST_CONTRACT_TIME, currentTime);
    }

    public static boolean isOnCooldown(EntityVillager villager, long currentTime, long cooldownTicks) {
        long lastTime = getLastContractTime(villager);
        if (lastTime < 0) {
            return false;
        }
        return (currentTime - lastTime) < cooldownTicks;
    }

    public static long getRemainingCooldown(EntityVillager villager, long currentTime, long cooldownTicks) {
        long lastTime = getLastContractTime(villager);
        if (lastTime < 0) {
            return 0;
        }
        long elapsed = currentTime - lastTime;
        return Math.max(0, cooldownTicks - elapsed);
    }

    public static String formatCooldownTime(long remainingTicks) {
        if (remainingTicks <= 0) {
            return "0s";
        }

        long totalSeconds = remainingTicks / 20;
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(days).append("d ");
        }
        if (hours > 0 || days > 0) {
            result.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            result.append(minutes).append("m ");
        }
        if (seconds > 0 || result.length() == 0) {
            result.append(seconds).append("s");
        }

        return result.toString().trim();
    }
}