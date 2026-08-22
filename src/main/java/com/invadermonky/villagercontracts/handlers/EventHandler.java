package com.invadermonky.villagercontracts.handlers;

import com.invadermonky.villagercontracts.init.RegistryVC;
import com.invadermonky.villagercontracts.util.VillagerHelper;
import com.invadermonky.villagercontracts.util.VillagerInfo;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class EventHandler {
    public static final EventHandler INSTANCE = new EventHandler();

    public static Map<String, VillagerInfo> contractMap = new HashMap<>();
    public static Set<String> entityBlacklist = new HashSet<>();

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

                // Modifies the existing entity in-place instead of destroying and recreating it
                // This preserves the UUID, the position, the data from other mods, and avoids
                // the error
                // "Keeping entity that already exists with UUID" (This was a bug during the
                // development of this fork)
                villager.setProfession(villagerInfo.profession);
                villager.careerId = VillagerHelper.getCareerId(villagerInfo.career) + 1;

                // Reset the career level to 1 so that the trades are generated from scratch,
                // just as if the villager were new
                villager.careerLevel = 1;

                // Regenerate the list of trades for the new profession/career.
                // populateBuyingList() clears the existing trades and generates new ones
                villager.populateBuyingList();

                villager.playSound(SoundEvents.ENTITY_VILLAGER_YES, 1.0f, 1.0f);

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