package com.invadermonky.villagercontracts.handlers;

import com.invadermonky.villagercontracts.VillagerContracts;
import com.invadermonky.villagercontracts.util.LogHelper;
import com.invadermonky.villagercontracts.util.ReferencesVC;
import com.invadermonky.villagercontracts.util.VillagerHelper;
import com.invadermonky.villagercontracts.util.VillagerInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Config(modid = VillagerContracts.MOD_ID)
public class ConfigHandler {

    public enum ContractCostType {
        NONE,
        EXPERIENCE,
        ITEM
    }

    @Comment(ReferencesVC.disableAnvilRenamingComment)
    public static boolean disableAnvilRenaming = false;

    @Comment(ReferencesVC.dumpVillagerInfoComment)
    public static boolean dumpVillagerInfo = false;

    @Comment(ReferencesVC.autoDetectVillagersComment)
    public static boolean autoDetectVillagers = true;

    @Comment(ReferencesVC.generateVillagerAttemptsComment)
    @RangeInt(min = 1, max = 100)
    public static int generateVillagerAttempts = 20;

    @Comment(ReferencesVC.consumeContractOnUseComment)
    public static boolean consumeContractOnUse = true;

    @Comment(ReferencesVC.contractCostTypeComment)
    public static ContractCostType contractCostType = ContractCostType.NONE;

    @Comment(ReferencesVC.contractCostAmountComment)
    @RangeInt(min = 1, max = 100)
    public static int contractCostAmount = 1;

    @Comment(ReferencesVC.contractCostItemComment)
    public static String contractCostItem = "minecraft:emerald";

    @Comment(ReferencesVC.enableCooldownComment)
    public static boolean enableCooldown = false;

    @Comment(ReferencesVC.cooldownTicksComment)
    @RangeInt(min = 100, max = 1728000)
    public static int cooldownTicks = 24000;

    @Comment(ReferencesVC.autoNameVillagersComment)
    public static boolean autoNameVillagers = false;

    @Comment(ReferencesVC.overrideCustomNamesComment)
    public static boolean overrideCustomNames = false;

    @Comment(ReferencesVC.enableGameStagesComment)
    public static boolean enableGameStages = false;

    @Comment(ReferencesVC.requiredGameStageComment)
    public static String requiredGameStage = "contract_master";

    @LangKey("config." + VillagerContracts.MOD_ID + ":validcontracts")
    @Comment(ReferencesVC.validContractsComment)
    public static String[] validContracts = ReferencesVC.defaultContracts;

    @Comment(ReferencesVC.entityBlacklistComment)
    public static String[] entityBlacklist = ReferencesVC.defaultBlacklist;

    private static final Pattern CONTRACT_PATTERN = Pattern.compile("^(.+?)\\s*=\\s*(.+?)\\s*;\\s*(.+)$");

    private static Item cachedCostItem = null;
    private static String cachedCostItemId = null;

    public static Item getCostItem() {
        if (contractCostType != ContractCostType.ITEM) {
            return null;
        }

        if (cachedCostItem != null && contractCostItem.equals(cachedCostItemId)) {
            return cachedCostItem;
        }

        ResourceLocation itemId = new ResourceLocation(contractCostItem);
        cachedCostItem = ForgeRegistries.ITEMS.getValue(itemId);
        cachedCostItemId = contractCostItem;

        if (cachedCostItem == null) {
            LogHelper.error("Configured cost item not found: " + contractCostItem);
        }

        return cachedCostItem;
    }

    @Mod.EventBusSubscriber(modid = VillagerContracts.MOD_ID)
    public static class ConfigChangeListener {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(VillagerContracts.MOD_ID)) {
                ConfigManager.sync(VillagerContracts.MOD_ID, Type.INSTANCE);
                syncConfigValues();
            }
        }

        public static void syncConfigValues() {
            if (dumpVillagerInfo) {
                dumpVillagerInfo();
            }

            EventHandler.entityBlacklist.clear();
            EventHandler.entityBlacklist.addAll(Arrays.asList(entityBlacklist));

            EventHandler.contractMap.clear();

            for (String configStr : validContracts) {
                if (configStr != null && !configStr.trim().isEmpty()) {
                    parseConfiguredVillager(configStr.trim());
                }
            }

            if (autoDetectVillagers) {
                autoDetectAllVillagers();
            }
        }

        public static void dumpVillagerInfo() {
            LogHelper.info("Villager Info Dump: STARTING");

            for (VillagerProfession profession : ForgeRegistries.VILLAGER_PROFESSIONS) {
                String professionName = VillagerHelper.getProfessionName(profession);
                List<VillagerCareer> careers = VillagerHelper.getProfessionCareers(profession);

                if (careers == null)
                    continue;

                for (VillagerCareer career : careers) {
                    String careerName = VillagerHelper.getCareerName(career);
                    LogHelper.info("\tProfession: " + professionName + ", Career: " + careerName);
                    LogHelper.info("\t\t=" + professionName + ";" + careerName);
                }
            }

            LogHelper.info("Villager Info Dump: FINISHED");
        }

        public static void parseConfiguredVillager(String configStr) {
            Matcher matcher = CONTRACT_PATTERN.matcher(configStr);
            if (!matcher.matches()) {
                LogHelper.error("Invalid contract format. Expected: 'Name=Profession;Career'. Received: " + configStr);
            } else {
                String identifier = matcher.group(1).trim();
                String professionName = matcher.group(2).trim();
                String careerName = matcher.group(3).trim();

                VillagerProfession profession = VillagerHelper.getProfession(professionName);
                if (profession == null) {
                    LogHelper.error("Invalid profession: " + professionName + " in contract: " + configStr);
                } else {
                    VillagerCareer career = VillagerHelper.getCareer(profession, careerName);
                    if (career == null) {
                        LogHelper.error("Invalid career: " + careerName + " for profession " + professionName);
                    } else {
                        VillagerInfo info = new VillagerInfo(identifier, profession, career);
                        EventHandler.contractMap.put(identifier.toLowerCase(Locale.ROOT), info);
                    }
                }
            }
        }

        public static void autoDetectAllVillagers() {
            Map<String, Integer> nameUsageCount = new HashMap<>();

            for (VillagerProfession profession : ForgeRegistries.VILLAGER_PROFESSIONS) {
                List<VillagerCareer> careers = VillagerHelper.getProfessionCareers(profession);
                if (careers == null)
                    continue;

                for (VillagerCareer career : careers) {
                    String careerName = VillagerHelper.getCareerName(career);
                    String prettyName = getLocalizedOrPrettyName(career, profession);
                    nameUsageCount.merge(prettyName.toLowerCase(Locale.ROOT), 1, Integer::sum);
                }
            }

            for (VillagerProfession profession : ForgeRegistries.VILLAGER_PROFESSIONS) {
                List<VillagerCareer> careers = VillagerHelper.getProfessionCareers(profession);
                if (careers == null)
                    continue;

                String modId = extractModId(profession);

                for (VillagerCareer career : careers) {
                    String careerName = VillagerHelper.getCareerName(career);
                    String prettyName = getLocalizedOrPrettyName(career, profession);
                    String lowerName = prettyName.toLowerCase(Locale.ROOT);

                    String contractName;
                    if (nameUsageCount.getOrDefault(lowerName, 0) > 1) {
                        String prettyModName = prettifyName(modId);
                        contractName = prettyModName + " " + prettyName;
                    } else {
                        contractName = prettyName;
                    }

                    String lowerContract = contractName.toLowerCase(Locale.ROOT);
                    if (!EventHandler.contractMap.containsKey(lowerContract)) {
                        VillagerInfo info = new VillagerInfo(contractName, profession, career);
                        EventHandler.contractMap.put(lowerContract, info);
                    }
                }
            }

            LogHelper.info("Auto-detected " + EventHandler.contractMap.size() + " villager contracts in total.");
        }

        private static String getLocalizedOrPrettyName(VillagerCareer career, VillagerProfession profession) {
            String careerName = VillagerHelper.getCareerName(career);

            String modId = extractModId(profession);

            String[] translationKeys = {
                    "entity.Villager." + careerName,
                    "entity.villager." + careerName,
                    "entity." + modId + "." + careerName,
                    "entity." + modId + ".villager." + careerName,
                    "profession." + careerName,
                    "profession." + modId + "." + careerName,
                    modId + "." + careerName,
                    careerName + ".name",
                    careerName
            };

            for (String key : translationKeys) {
                try {
                    String translated = I18n.format(key);
                    if (translated != null && !translated.equals(key) && !translated.isEmpty()) {
                        return translated;
                    }
                } catch (Exception e) {
                }
            }

            return prettifyName(careerName);
        }

        private static String prettifyName(String rawName) {
            if (rawName == null || rawName.isEmpty())
                return rawName;

            String name = rawName;

            if (name.contains(":")) {
                name = name.split(":")[1];
            } else if (name.contains(".")) {
                String[] parts = name.split("\\.");
                if (parts.length > 1) {
                    name = parts[parts.length - 1];
                }
            }

            name = name.replace('_', ' ').replace('-', ' ');

            StringBuilder pretty = new StringBuilder();
            for (String word : name.split("\\s+")) {
                if (!word.isEmpty()) {
                    if (pretty.length() > 0)
                        pretty.append(' ');
                    pretty.append(Character.toUpperCase(word.charAt(0)));
                    if (word.length() > 1)
                        pretty.append(word.substring(1).toLowerCase(Locale.ROOT));
                }
            }
            return pretty.toString();
        }

        private static String extractModId(VillagerProfession profession) {
            if (profession.getRegistryName() == null)
                return "minecraft";
            String regName = profession.getRegistryName().toString();
            return regName.contains(":") ? regName.split(":")[0] : "minecraft";
        }
    }
}