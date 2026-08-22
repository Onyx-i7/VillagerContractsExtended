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

        public static final String disableAnvilRenamingComment = "Enabling this option will disable contract renaming in the forge and remove the associated tooltip.\n"
                        +
                        "If this is enabled, you will need to create custom recipes for named contracts using CraftTweaker (stack.withDisplayName) or similar.";

        public static final String dumpVillagerInfoComment = "Print the names of the profession and career of all the villagers registered in the log (latest.log).\n"
                        +
                        "Very useful for discovering the correct names of villagers added by other mods.\n" +
                        "This dump will occur when restarting the game and every time you change the settings from the game menu.";

        public static final String generateVillagerAttemptsComment = "[OBSOLETE] This value is no longer used thanks to recent code optimizations, but it is maintained for compatibility with old configurations.";

        public static final String validContractsComment = "List of valid contract names and their associated villager careers.\n"
                        +
                        "Format: name=profession;career\n" +
                        "  name - The exact name you must give to the contract in the forge (case insensitive).\n"
                        +
                        "  profession - The registration ID of the profession (e.g., minecraft:farmer). Find these IDs using 'dumpVillagerInfo'.\n"
                        +
                        "  career - The name of the career associated with the profession.  Find these names using 'dumpVillagerInfo'.\n\n"
                        +
                        "NOTE: Villagers from other mods that do not use the standard Forge registry will not work with these contracts.";

        public static final String entityBlacklistComment = "Blacklist of villager entity IDs with which the contract MUST NOT interact.\n"
                        +
                        "Useful for avoiding conflicts with entities that appear to be villagers but are not (e.g., Plague Doctors from the Rats mod).";

        public static final String autoDetectVillagersComment = "Automatically detects all villager professions and careers registered by other mods.\n"
                        +
                        "When enabled, the mod will generate readable contract names for each profession without the need\n"
                        +
                        "to configure them manually.  The contracts defined in 'validContracts' take precedence.";
        public static final String consumeContractOnUseComment = "Determine if the Villager Contract is consumed when applied to a villager.\n"
                        +
                        "If it is enabled (true), the contract disappears after changing the villager's profession.\n"
                        +
                        "If it is disabled (false), the contract remains in hand and can be reused.";
}