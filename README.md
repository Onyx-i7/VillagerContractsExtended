<h1 align="center">Villager Contracts Extended</h1>

<div align="center">

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)](LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.12.2-green.svg?style=flat-square)](https://minecraft.net)
[![Forge](https://img.shields.io/badge/Forge-14.23.5.2847+-red.svg?style=flat-square)](https://files.minecraftforge.net/)
[![Version](https://img.shields.io/badge/Version-1.3.1-orange.svg?style=flat-square)](https://www.curseforge.com/minecraft/mc-mods/villager-contracts-extended/files)

**An improved [Villager Contracts](https://www.curseforge.com/minecraft/mc-mods/villager-contracts) fork by Invadermonky**

[Installation](#installation) • [Features](#features) • [Configuration](#configuration) • [Compatibility](#mod-integrations)

</div>

---

## Overview

**Villager Contracts Extended** is a modernized and optimized fork for Minecraft 1.12.2. It rewrites and overhauls the villager profession changing system to fix severe incompatibilities in heavy modpacks. This fork focuses on improvements such as:

* **Dedicated Graphical User Interface (GUI)** to eliminate problematic anvil mechanics when using large amounts of mods.
* **Universal compatibility** with custom villagers added by any other mod.
* **Bug fixes** and internal performance optimizations.
* **Removal of legacy code** (obsolete tooltips and outdated mechanics).

---

## Features

### Interactive Interface (GUI)

* **No More Anvils**: You no longer need to rename contracts in an anvil.
* **Visual Menu**: Right-clicking while holding a contract opens a clean interface where you can clearly select any profession and career available in the game.

### Universal Compatibility

* **Global Mod Support**: Automatically works out-of-the-box with any villager correctly registered through Minecraft Forge, regardless of which mod adds it.

### Clean & Modernized System

* **Tooltip Cleanup**: Completely removed the old tooltip system and anvil logic that caused bloated, oversized tooltips during development when loading large modpacks with many villager professions.
* **Profession Reset**: Changes the villager's career and resets their trades cleanly and instantly *(currently slightly buggy)*.

---

## Mod Integrations

Thanks to the new detection system, this mod is **compatible out-of-the-box with any 1.12.2 villager mod**, including (and tested with):

* Vanilla Minecraft
* Quark
* Ice and Fire: ROTH Edition
* Thaumcraft
* Immersive Engineering
* *And any other mod that properly registers villagers via Forge.*

---

## Configuration

The configuration file allows you to tweak the mod's behavior to fit your modpack's needs:

* **`dumpVillagerInfo`**: When enabled, dumps all registered professions and careers into the log (useful for modpack creators).
* **`entityBlacklist`**: A blacklist of entity IDs for which contract interactions are disabled.
* **`validContracts`**: A whitelist to filter allowed names, professions, and careers.
* **`autoDetectVillagersComment`**: Automatically detects all villager professions and careers registered by other mods.
* **`consumeContractOnUseComment`**: Determines whether the Villager Contract is consumed upon applying it to a villager.

---

## Installation

### Requirements

* **Minecraft**: 1.12.2
* **Forge**: 14.23.5.2847 or higher
* **Java**: 8

### Steps

1. **Download** the latest release from GitHub Releases, CurseForge, or Modrinth.
2. **Locate** your Minecraft `mods` folder:
* **Windows**: `%appdata%\.minecraft\mods`
* **Linux**: `~/.minecraft/mods`


3. **Copy** the downloaded `.jar` file into the `mods` folder.
4. **Launch** Minecraft using your Forge profile.

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

This project is distributed under the **MIT License**.

### **Authors & Contributors**

* **Invadermonky**: Creator of the original *Villager Contracts* mod.
* **Onyx_i7**: Fork developer, performance optimizations, GUI, and maintenance
