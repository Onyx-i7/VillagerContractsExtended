# Villager Contracts Extended Changelog

## Version 1.3.5

- Fixed a bug where translations made by other mods were not updated correctly when changing the game language
- Improved the English translations
- Added the `useModTranslations` option to configure whether to use the translations from the mods or their original English names

**This version will serve as the basis for porting this mod to 1.16.5. This means it will no longer receive updates that add new features only compatibility fixes and bug fixes until the 1.16.5 version is complete, which is currently 30% ready**

## Version 1.3.4

- Added the GUI texture
- Improved the search system it can now find mods and villager professions previously, it only searched for mods
- Now, when you perform a search, select the result that best matches what you're looking for
- The GUI now includes localizations for mods' villagers based on the mods' original translations

## Version 1.3.3

- feat: New `contractCostItem` option you are no longer limited to selecting only emeralds or only experience, you can now enter an item ID. It also works with mods
- feat: New `enableCooldownComment` option to configure the cooldown time to prevent players from changing trades until they get the desired one villagers also display messages when they are tired due to this cooldown (Currently there are only a few, but more will be added over time)
- feat: New `autoNameVillagersComment` option for villager names it simply helps make the messages make more sense, but it's still in development and isn't finished yet (though I recommend using the Villager Names Mod created by Serilum instead, since it does a better job with this feature)
- feat: Added compatibility with **GameStage**, as requested by small_raman88

## Version 1.3.2

- feat: A cost has now been added so that hiring a villager requires XP or Emeralds. This is optional and can be configured in the mod settings
- feat(config)!: Added `contractCostTypeComment` and `contractCostAmountComment` to the configuration file
- style: Added the mod's logo to mcmod.info
- docs: Updated the repository's Readme.md using a better translator

## Version 1.3.1

- Fix: Double-clicking in the GUI to select something on the right side
- Fix: Villagers were not properly clearing out their shops

## Version 1.3.0

- Initial version of the fork

## Version 1.12.2-1.2.1

- Fixed crash on dedicated servers

## Version 1.12.2-1.2.0

- Added config option to disable Anvil renaming
- Fixed a rare infinite villager spawn bug

## Version 1.12.2-1.1.0

- Added entity blacklist to configuration
- Changed info dump format for easier copy-pasting

## Version 1.12.2-1.0.0

- Initial Release
