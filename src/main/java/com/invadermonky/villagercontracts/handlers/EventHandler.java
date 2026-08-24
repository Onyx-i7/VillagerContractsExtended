package com.invadermonky.villagercontracts.handlers;

import com.invadermonky.villagercontracts.handlers.ConfigHandler.ContractCostType;
import com.invadermonky.villagercontracts.init.RegistryVC;
import com.invadermonky.villagercontracts.util.LogHelper;
import com.invadermonky.villagercontracts.util.VillagerHelper;
import com.invadermonky.villagercontracts.util.VillagerInfo;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class EventHandler {
    public static final EventHandler INSTANCE = new EventHandler();

    public static Map<String, VillagerInfo> contractMap = new HashMap<>();
    public static Set<String> entityBlacklist = new HashSet<>();

    private static Field buyingListField = null;

    private static Field getBuyingListField() {
        if (buyingListField == null) {
            try {
                buyingListField = EntityVillager.class.getDeclaredField("buyingList");
                buyingListField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Failed to access buyingList field", e);
            }
        }
        return buyingListField;
    }

    private static void clearVillagerTrades(EntityVillager villager) {
        try {
            Field field = getBuyingListField();
            MerchantRecipeList list = (MerchantRecipeList) field.get(villager);
            if (list != null) {
                list.clear();
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to clear villager trades", e);
        }
    }

    private static boolean canPayCost(EntityPlayer player) {
        ContractCostType costType = ConfigHandler.contractCostType;
        int costAmount = ConfigHandler.contractCostAmount;

        LogHelper.debug("Cost check - Type: " + costType + ", Amount: " + costAmount);

        if (costType == ContractCostType.EXPERIENCE) {
            LogHelper.debug("Player XP level: " + player.experienceLevel);
            return player.experienceLevel >= costAmount;
        } else if (costType == ContractCostType.EMERALDS) {
            int emeraldCount = countEmeralds(player);
            LogHelper.debug("Player emeralds: " + emeraldCount);
            return emeraldCount >= costAmount;
        }
        // NONE means no cost
        return true;
    }

    private static void payCost(EntityPlayer player) {
        ContractCostType costType = ConfigHandler.contractCostType;
        int costAmount = ConfigHandler.contractCostAmount;

        if (costType == ContractCostType.EXPERIENCE) {
            player.addExperienceLevel(-costAmount);
        } else if (costType == ContractCostType.EMERALDS) {
            consumeEmeralds(player, costAmount);
        }
    }

    private static int countEmeralds(EntityPlayer player) {
        int count = 0;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == Items.EMERALD) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static void consumeEmeralds(EntityPlayer player, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.inventory.getSizeInventory() && remaining > 0; i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == Items.EMERALD) {
                int toRemove = Math.min(remaining, stack.getCount());
                stack.shrink(toRemove);
                remaining -= toRemove;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onVillagerInteract(EntityInteract event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null)
            return;

        ItemStack heldItem = event.getItemStack();
        Entity target = event.getTarget();
        World world = event.getWorld();

        if (world.isRemote || target == null || heldItem.isEmpty())
            return;

        if (target instanceof EntityVillager && !((EntityVillager) target).isChild()
                && !entityBlacklist.contains(target.getEntityString())
                && heldItem.getItem() == RegistryVC.villagerContract) {

            String contractName = heldItem.getDisplayName().toLowerCase(Locale.ROOT);

            if (contractMap.containsKey(contractName)) {
                VillagerInfo villagerInfo = contractMap.get(contractName);
                EntityVillager villager = (EntityVillager) target;

                if (!canPayCost(player)) {
                    villager.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    event.setCanceled(true);
                    return;
                }

                villager.setProfession(villagerInfo.profession);
                villager.careerId = VillagerHelper.getCareerId(villagerInfo.career) + 1;
                villager.careerLevel = 1;
                clearVillagerTrades(villager);
                villager.populateBuyingList();

                villager.playSound(SoundEvents.ENTITY_VILLAGER_YES, 1.0f, 1.0f);

                payCost(player);

                player.swingArm(event.getHand());
                if (ConfigHandler.consumeContractOnUse && !player.isCreative()) {
                    heldItem.shrink(1);
                }
            } else {
                ((EntityVillager) target).playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
            event.setCanceled(true);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onItemRename(KeyboardInputEvent.Pre event) {
        if (ConfigHandler.disableAnvilRenaming) {
            GuiScreen gui = event.getGui();
            if (gui instanceof GuiRepair) {
                ContainerRepair container = ((GuiRepair) gui).anvil;
                ItemStack inputLeft = container.inputSlots.getStackInSlot(0);

                if (inputLeft.getItem() == RegistryVC.villagerContract
                        && !inputLeft.getDisplayName().equals(container.repairedItemName)) {
                    event.setCanceled(true);
                }
            }
        }
    }
}