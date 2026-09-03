package com.invadermonky.villagercontracts.util;

public class ReferencesVC {

        public static final String[] defaultContracts = new String[] {
                        "Farmer=minecraft:farmer;farmer",
                        "Fisherman=minecraft:farmer;fisherman",
                        "Shepherd=minecraft:farmer;shepherd",
                        "Fletcher=minecraft:farmer;fletcher",
                        "Librarian=minecraft:librarian;librarian",
                        "Cartographer=minecraft:librarian;cartographer",
                        "Cleric=minecraft:priest;cleric",
                        "Armorer=minecraft:blacksmith;armor",
                        "Weapon Smith=minecraft:blacksmith;weapon",
                        "Tool Smith=minecraft:blacksmith;tool",
                        "Butcher=minecraft:butcher;butcher",
                        "Leatherworker=minecraft:butcher;leather",
                        "Nitwit=minecraft:nitwit;nitwit"
        };

        public static final String[] defaultBlacklist = new String[] {
                        "iceandfire:snowvillager",
                        "primitivemobs:sheepman",
                        "rats:plague_doctor",
                        "toroquest:toroquest_toro_villager"
        };

        public static final String disableAnvilRenamingComment = "Enabling this will disable contract renaming at an anvil and remove the associated tooltip.\n"
                        +
                        "If this is enabled you will need to create custom recipes for named contracts using CraftTweaker's \"stack.withDisplayName(String name)\" function or something similar.";

        public static final String dumpVillagerInfoComment = "Prints the profession and career names of all registered villagers"
                        +
                        " to the log. This dump will occur on game restart and after the configuration has been changed in-game.";

        public static final String autoDetectVillagersComment = "Automatically detects all professions and careers registered by other mods.\n"
                        +
                        "When enabled, the mod will generate readable contract names for each profession without needing\n"
                        +
                        "to configure them manually. Contracts defined in 'validContracts' take priority.";

        public static final String consumeContractOnUseComment = "Determines whether the Villager Contract is consumed when applied to a villager.\n"
                        +
                        "If enabled (true), the contract disappears after changing the villager's profession.\n" +
                        "If disabled (false), the contract remains in the hand and can be reused.";

        public static final String contractCostTypeComment = "Determines the cost required to apply a contract to a villager.\n"
                        +
                        "Options:\n" +
                        "  NONE - No cost required\n" +
                        "  EXPERIENCE - Requires XP levels\n" +
                        "  ITEM - Requires the item specified in 'contractCostItem'\n" +
                        "Default: NONE";

        public static final String contractCostAmountComment = "The amount of XP levels or items required to apply a contract.\n"
                        +
                        "This setting is ignored if contractCostType is set to NONE.\n" +
                        "For EXPERIENCE: represents the number of XP levels consumed.\n" +
                        "For ITEM: represents the number of items consumed from the player's inventory.";

        public static final String contractCostItemComment = "The item ID to use as cost when contractCostType is set to ITEM.\n"
                        +
                        "Examples: 'minecraft:diamond', 'minecraft:emerald', 'botania:manasteel_ingot'\n" +
                        "Default: minecraft:emerald";

        public static final String enableCooldownComment = "When enabled, villagers will have a cooldown period after a contract is applied.\n"
                        +
                        "During the cooldown, you cannot apply another contract to the same villager.\n" +
                        "This prevents exploiting villagers by rapidly changing their professions.\n" +
                        "Default: false";

        public static final String cooldownTicksComment = "The cooldown duration in ticks after applying a contract to a villager.\n"
                        +
                        "20 ticks = 1 second. 1200 ticks = 1 minute. 72000 ticks = 1 hour. 1728000 ticks = 1 day.\n" +
                        "This setting is ignored if enableCooldown is false.\n" +
                        "Default: 24000 (20 minutes real time)";

        public static final String autoNameVillagersComment = "When enabled, villagers will be automatically renamed when a contract is applied.\n"
                        +
                        "The name will match the contract identifier (which can be customized in the config).\n" +
                        "Example: Applying a 'Librarian' contract will rename the villager to 'Librarian'.\n" +
                        "Default: false";

        public static final String overrideCustomNamesComment = "When enabled, the contract will override any custom name the villager already has\n"
                        +
                        "(including names set with name tags).\n" +
                        "When disabled, villagers with custom names will keep their original names.\n" +
                        "This setting is ignored if autoNameVillagers is false.\n" +
                        "Default: false";

        public static final String validContractsComment = "List of valid contract names and their associated villager careers.\n"
                        +
                        "Format:  name=profession;career\n" +
                        "  name - The name used when renaming the villager contract to specify the desired career. These names are case-insensitive but must be unique, though multiple names can be assigned for the same villager career.\n"
                        +
                        "  profession - The villager profession resource location. Can be found using the \"dumpVillagerInfo\" config option.\n"
                        +
                        "  career - The villager career name. Career must be associated with the profession and can be found using the \"dumpVillagerInfo\" config option.\n\n"
                        +
                        "NOTE:\n" +
                        "  Modded villagers that are not registered with Forge's VillagerProfession registry will not work with these contracts.\n"
                        +
                        "  Villagers with custom models may generate their correct trades, but will default to the farmer texture.";

        public static final String entityBlacklistComment = "Blacklist of any villager entity ids where contract interactions should be disabled.";

        public static final String enableGameStagesComment = "When enabled, players will need a specific Game Stage to apply contracts.\n"
                        +
                        "This requires the Game Stages mod by Darkhax to be installed.\n" +
                        "If Game Stages is not installed, this setting is ignored.\n" +
                        "Default: false";

        public static final String requiredGameStageComment = "The Game Stage required to apply contracts.\n" +
                        "Only used when enableGameStages is true and Game Stages mod is installed.\n" +
                        "Example: 'contract_master', 'advanced_trader'\n" +
                        "Default: contract_master";

        public static final String useModTranslationsComment = "When enabled, the mod will try to use translations from other mods for villager names.\n"
                        +
                        "For example, if Immersive Engineering has Spanish translations, those will be used.\n" +
                        "When disabled, names will be auto-generated from the career ID (e.g., 'weapon_smith' -> 'Weapon Smith').\n"
                        +
                        "This setting is ignored if autoDetectVillagers is false.\n" +
                        "Default: true";

        public static final String professionGameStagesComment =
            "Specific Game Stages required for individual professions\n" +
            "Format: 'profession_id=stage_name'\n" +
            "Example:\n" +
            "  minecraft:farmer=basic_trader\n" +
            "  minecraft:librarian=advanced_trader\n" +
            "  thaumcraft:alchemist=magic_unlocked\n" +
            "Professions not listed here will use the global 'requiredGameStage'\n" +
            "This setting is ignored if enableGameStages is false";

        // Possibly buggy
        public static final String professionCostsComment =
                "Specific costs for individual professions\n" +
                        "Format: 'profession_id=COST_TYPE:cost_value;amount'\n" +
                        "COST_TYPE can be ITEM or EXPERIENCE\n" +
                        "Examples:\n" +
                        "  minecraft:librarian=ITEM:minecraft:book;5\n" +
                        "  minecraft:farmer=ITEM:minecraft:wheat_seeds;3\n" +
                        "  minecraft:priest=EXPERIENCE:0;5\n" +
                        "  thaumcraft:alchemist=ITEM:thaumcraft:phial;2\n" +
                        "For EXPERIENCE type, the cost_value is ignored (use 0)\n" +
                        "Professions not listed here will use the global cost settings\n" +
                        "This setting is ignored if contractCostType is NONE and no specific costs are defined";
}
