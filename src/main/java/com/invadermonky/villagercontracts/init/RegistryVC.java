package com.invadermonky.villagercontracts.init;

import com.invadermonky.villagercontracts.VillagerContracts;
import com.invadermonky.villagercontracts.items.ItemVillagerContract;
import com.invadermonky.villagercontracts.util.LogHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = VillagerContracts.MOD_ID)
public class RegistryVC {
    public static Item villagerContract;

    // Create a CreativeTab. When there are many mods installed, the "Misc" tab
    // becomes unmanageable since having a dedicated tab greatly facilitates finding
    // the item in creative mode
    public static final CreativeTabs TAB_VILLAGER_CONTRACTS = new CreativeTabs(VillagerContracts.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            // Use the contract item if it is already registered, or a backup item (Paper)
            // to avoid NPE during the initial rendering
            if (villagerContract != null) {
                return new ItemStack(villagerContract);
            }
            return new ItemStack(Items.PAPER);
        }
    };

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        // Instantiate the item and assign it the CreativeTab
        villagerContract = new ItemVillagerContract("contract").setCreativeTab(TAB_VILLAGER_CONTRACTS);
        registry.register(villagerContract);

        LogHelper.debug("Item Villager Contract registered correctly in its own tab");
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerItemRenders(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(villagerContract.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(villagerContract, 0, loc);

        LogHelper.debug("Rendering of the registered item");
    }
}