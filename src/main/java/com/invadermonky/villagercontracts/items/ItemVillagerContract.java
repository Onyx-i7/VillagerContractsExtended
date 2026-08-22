package com.invadermonky.villagercontracts.items;

import com.invadermonky.villagercontracts.VillagerContracts;
import com.invadermonky.villagercontracts.client.gui.GuiVillagerContracts;
import com.invadermonky.villagercontracts.handlers.ConfigHandler;
import com.invadermonky.villagercontracts.handlers.EventHandler;
import com.invadermonky.villagercontracts.init.RegistryVC;
import com.invadermonky.villagercontracts.util.StringHelper;
import com.invadermonky.villagercontracts.util.VillagerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class ItemVillagerContract extends Item {

    public ItemVillagerContract(String unlocName) {
        setRegistryName(unlocName);
        setCreativeTab(RegistryVC.TAB_VILLAGER_CONTRACTS);
        setTranslationKey(VillagerContracts.MOD_ID + "." + unlocName);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if (worldIn.isRemote && !playerIn.isSneaking()) {
            RayTraceResult ray = this.rayTrace(worldIn, playerIn, false);
            if (ray == null || ray.typeOfHit != RayTraceResult.Type.ENTITY) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiVillagerContracts());
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        if (ConfigHandler.disableAnvilRenaming)
            return;

        if (stack.hasDisplayName()) {
            String contractName = stack.getDisplayName().toLowerCase(Locale.ROOT);
            VillagerInfo info = EventHandler.contractMap.get(contractName);

            if (info != null) {
                String regName = info.profession.getRegistryName() != null
                        ? info.profession.getRegistryName().toString()
                        : "minecraft:?";
                String modId = regName.contains(":") ? regName.split(":")[0] : "minecraft";
                tooltip.add(TextFormatting.GREEN + I18n.format(StringHelper.getLanguageKey("valid", "tooltip")));
                tooltip.add(TextFormatting.GRAY + " -> Mod: " + TextFormatting.BLUE + modId);
            } else {
                tooltip.add(TextFormatting.RED + I18n.format(StringHelper.getLanguageKey("invalid", "tooltip")));
            }
        } else {
            tooltip.add(I18n.format(StringHelper.getLanguageKey("desc", "tooltip")));
            tooltip.add(I18n.format(StringHelper.getLanguageKey("rightclick", "tooltip")));
        }
    }
}