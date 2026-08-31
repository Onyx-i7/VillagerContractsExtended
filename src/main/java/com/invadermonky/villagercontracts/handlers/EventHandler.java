package com.invadermonky.villagercontracts.handlers;

import com.invadermonky.villagercontracts.compat.GameStageIntegration;
import com.invadermonky.villagercontracts.handlers.ConfigHandler.*;
import com.invadermonky.villagercontracts.init.RegistryVC;
import com.invadermonky.villagercontracts.util.LogHelper;
import com.invadermonky.villagercontracts.util.VillagerDataHelper;
import com.invadermonky.villagercontracts.util.VillagerHelper;
import com.invadermonky.villagercontracts.util.VillagerInfo;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class EventHandler {
    public static final EventHandler INSTANCE = new EventHandler();

    public static Map<String, VillagerInfo> contractMap = new HashMap<>();
    public static Set<String> entityBlacklist = new HashSet<>();

    private static Field buyingListField = null;
    private static final Random random = new Random();

    private static final String[] COOLDOWN_MESSAGES = {
            "message.villagercontracts.cooldown_slavery",
            "message.villagercontracts.cooldown_rest",
            "message.villagercontracts.cooldown_tired",
            "message.villagercontracts.cooldown_union",
            "message.villagercontracts.cooldown_boss"
    };

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

        if (costType == ContractCostType.EXPERIENCE) {
            return player.experienceLevel >= costAmount;
        } else if (costType == ContractCostType.ITEM) {
            Item costItem = ConfigHandler.getCostItem();
            if (costItem == null) {
                return false;
            }
            int itemCount = countItem(player, costItem);
            return itemCount >= costAmount;
        }
        return true;
    }

    private static void payCost(EntityPlayer player) {
        ContractCostType costType = ConfigHandler.contractCostType;
        int costAmount = ConfigHandler.contractCostAmount;

        if (costType == ContractCostType.EXPERIENCE) {
            player.addExperienceLevel(-costAmount);
        } else if (costType == ContractCostType.ITEM) {
            Item costItem = ConfigHandler.getCostItem();
            if (costItem != null) {
                consumeItem(player, costItem, costAmount);
            }
        }
    }

    private static int countItem(EntityPlayer player, Item item) {
        int count = 0;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static void consumeItem(EntityPlayer player, Item item, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.inventory.getSizeInventory() && remaining > 0; i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
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
                long currentTime = world.getTotalWorldTime();

                // Check Game Stages requirement (profession-specific or global)
                if (ConfigHandler.enableGameStages && GameStageIntegration.isAvailable()) {
                    String requiredStage = ConfigHandler.getRequiredStageForProfession(villagerInfo.profession);
                    
                    if (!GameStageIntegration.hasRequiredStage(player, requiredStage)) {
                        TextComponentTranslation msg = new TextComponentTranslation(
                                "message.villagercontracts.stage_required_specific", 
                                villagerInfo.identifier, 
                                requiredStage);
                        msg.getStyle().setColor(TextFormatting.RED);
                        player.sendMessage(msg);
                        villager.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                        event.setCanceled(true);
                        return;
                    }
                }

                if (ConfigHandler.enableCooldown
                        && VillagerDataHelper.isOnCooldown(villager, currentTime, ConfigHandler.cooldownTicks)) {
                    long remaining = VillagerDataHelper.getRemainingCooldown(villager, currentTime,
                            ConfigHandler.cooldownTicks);
                    String formattedTime = VillagerDataHelper.formatCooldownTime(remaining);

                    String messageKey = COOLDOWN_MESSAGES[random.nextInt(COOLDOWN_MESSAGES.length)];
                    TextComponentTranslation msg = new TextComponentTranslation(messageKey, formattedTime);
                    msg.getStyle().setColor(TextFormatting.RED);
                    player.sendMessage(msg);

                    villager.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    event.setCanceled(true);
                    return;
                }

                if (!canPayCost(player)) {
                    TextComponentTranslation msg = new TextComponentTranslation(
                            "message.villagercontracts.insufficient_funds");
                    msg.getStyle().setColor(TextFormatting.RED);
                    player.sendMessage(msg);
                    villager.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    event.setCanceled(true);
                    return;
                }

                villager.setProfession(villagerInfo.profession);
                villager.careerId = VillagerHelper.getCareerId(villagerInfo.career) + 1;
                villager.careerLevel = 1;
                clearVillagerTrades(villager);
                villager.populateBuyingList();

                if (ConfigHandler.autoNameVillagers) {
                    boolean shouldRename = !villager.hasCustomName() || ConfigHandler.overrideCustomNames;
                    if (shouldRename) {
                        villager.setCustomNameTag(villagerInfo.identifier);
                        villager.setAlwaysRenderNameTag(true);
                    }
                }

                if (ConfigHandler.enableCooldown) {
                    VillagerDataHelper.setLastContractTime(villager, currentTime);
                }

                villager.playSound(SoundEvents.ENTITY_VILLAGER_YES, 1.0f, 1.0f);

                payCost(player);

                String villagerDisplayName = villager.hasCustomName() ? villager.getCustomNameTag() : villagerInfo.identifier;
                TextComponentTranslation successMsg = new TextComponentTranslation(
                        "message.villagercontracts.contract_applied", villagerDisplayName, villagerInfo.identifier);
                successMsg.getStyle().setColor(TextFormatting.GREEN);
                player.sendMessage(successMsg);

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