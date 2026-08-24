<h1 align="center">Villager Contracts Extended</h1>

<div align="center">

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)](LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.12.2-green.svg?style=flat-square)](https://minecraft.net)
[![Forge](https://img.shields.io/badge/Forge-14.23.5.2847+-red.svg?style=flat-square)](https://files.minecraftforge.net/)
[![Version](https://img.shields.io/badge/Version-1.3.1-orange.svg?style=flat-square)](https://www.curseforge.com/minecraft/mc-mods/villager-contracts-extended/files)

**A fork of [VillagerContract](https://www.curseforge.com/minecraft/mc-mods/villager-contracts) with some improvements**

[Installation](#installation) • [Features](#features) • [Configuration](#configuration) • [Compatibility](#mod-integrations)

</div>

---

## Description

**Villager Contracts Extended** is a fork of Villager Contracts created by Invadermonky that adds several improvements to the original mod, such as general compatibility without having to configure the mod and an interface that makes it easy to select a villager's profession when there are a lot of mods installed

---

## Features

### GUI
***Texture currently under development***
* **No More Anvil**: The tooltip and anvil system was a bit tedious when there were a lot of mods, causing it to sometimes disappear off the screen
* **Menu**: Right-clicking while holding a contract opens a simple interface where you can easily select any profession or career available in the game

### Universal Compatibility

* **Global mod compatibility**: Works automatically right from the start with any villager properly registered through Minecraft Forge, regardless of which mod added them. This can be disabled via the blacklist or the mod's settings

### Clean and Modernized System

* **Cleanup of tooltips**: The old tooltip system and the anvil logic which caused tooltips to become too large and cumbersome when loading large mod packs with many villager professions during the development of this fork have been completely removed
* **Reset Profession**: Changes the villager's profession and resets their trades

---

## Mod Integrations

Thanks to the new detection system, this mod is **immediately compatible with any villager mod for version 1.12.2**, including (and tested with):

* Vanilla Minecraft
* Quark
* Ice and Fire: ROTH Edition
* Thaumcraft
* Immersive Engineering
* *And any other mod that registers villagers through Forge*

---

## Configuration

The configuration file allows you to adjust the mod's behavior to suit the needs of your modpack:

* **`dumpVillagerInfo`**: When enabled, it logs all professions and careers listed in the registry (useful for modpack creators)
* **`entityBlacklist`**: A blacklist of entity identifiers for which contractual interactions have been disabled. This is useful if you want to prevent a villager from a specific mod from being hired, this is accompanied by the previously mentioned configuration
* **`validContracts`**: A whitelist for filtering allowed names, professions, and careers
* **`autoDetectVillagersComment`**: Automatically detects all villager professions and careers registered by other mods. This is useful when you don't want to configure a lot of villagers
* **`consumeContractOnUseComment`**: Determines whether the villager contract is consumed when applied to a villager

---

## Future Roadmap:
* [ ] Port to future versions such as 1.16 or 1.21
* [ ] Add more features, such as the experience cost of hiring a villager
* [ ] And other features that will be added to this list

---

## Building From Source

### Prerequisites

* Java Development Kit (JDK) 25
* Git

### Build Steps

```text
# Clone the repository
git clone https://github.com/Onyx-i7/VillagerContractsExtended.git
cd VillagerContractsExtended

# Build the mod
./gradlew build

# For Windows users
gradlew.bat build
```

---

## License & Credits

### **License**

This project is distributed entirely under the **MIT License** and is based on the code from the base version of *Villager Contracts* by **Invadermonky**

Since the original project was legitimately published under the MIT License, this fork remains fully open source and is permitted. Any subsequent forks or modifications of this project must continue to retain the original copyright notices and comply with the terms of the MIT License attached to this repository

### **Authors & Contributors**

* **Invadermonky**: Creator of the original *Villager Contracts* mod
* **Onyx_i7**: Developer of this fork, responsible for performance optimizations, the GUI, and maintenance