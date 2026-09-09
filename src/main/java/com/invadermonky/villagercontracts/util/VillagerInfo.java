package com.invadermonky.villagercontracts.util;

import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class VillagerInfo {
    public final String identifier;
    public final VillagerProfession profession;
    public final VillagerCareer career;

    public VillagerInfo(String identifier, VillagerProfession profession, VillagerCareer career) {
        this.identifier = identifier;
        this.profession = profession;
        this.career = career;
    }

    // Facilita la depuracion y lectura de los logs al usar dumpVillagerInfo
    @Override
    public String toString() {
        return String.format("VillagerInfo{identifier='%s', profession='%s', career='%s'}",
                identifier,
                VillagerHelper.getProfessionName(profession),
                VillagerHelper.getCareerName(career));
    }
}