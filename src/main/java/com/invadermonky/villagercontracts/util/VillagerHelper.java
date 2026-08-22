package com.invadermonky.villagercontracts.util;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VillagerHelper {

    private static final Map<String, VillagerProfession> PROFESSION_CACHE = new HashMap<>();
    private static final Map<VillagerProfession, List<VillagerCareer>> CAREERS_CACHE = new HashMap<>();
    private static Field careersFieldCache = null;

    public static boolean doesVillagerExist(String professionName, String careerName) {
        return doesProfessionExist(professionName) && getCareer(getProfession(professionName), careerName) != null;
    }

    public static boolean doesProfessionExist(String professionName) {
        return getProfession(professionName) != null;
    }

    @Nullable
    public static VillagerProfession getProfession(String professionName) {
        return PROFESSION_CACHE.computeIfAbsent(professionName.toLowerCase(), name -> {
            for (VillagerProfession profession : ForgeRegistries.VILLAGER_PROFESSIONS) {
                if (getProfessionName(profession).equalsIgnoreCase(name)) {
                    return profession;
                }
            }
            return null;
        });
    }

    public static String getProfessionName(VillagerProfession profession) {
        if (profession == null)
            return "";
        ResourceLocation regName = profession.getRegistryName();
        return regName != null ? regName.toString() : "";
    }

    @Nullable
    public static List<VillagerCareer> getProfessionCareers(VillagerProfession profession) {
        if (profession == null)
            return null;

        return CAREERS_CACHE.computeIfAbsent(profession, p -> {
            try {
                if (careersFieldCache == null) {
                    careersFieldCache = p.getClass().getDeclaredField("careers");
                    careersFieldCache.setAccessible(true);
                }
                return (List<VillagerCareer>) careersFieldCache.get(p);
            } catch (Exception e) {
                LogHelper.error("Error obtaining the courses for the profession: " + getProfessionName(profession));
                return null;
            }
        });
    }

    @Nullable
    public static VillagerCareer getCareer(VillagerProfession profession, String careerName) {
        List<VillagerCareer> careers = getProfessionCareers(profession);
        if (careers != null) {
            for (VillagerCareer career : careers) {
                if (getCareerName(career).equalsIgnoreCase(careerName)) {
                    return career;
                }
            }
        }
        return null;
    }

    public static String getCareerName(VillagerCareer career) {
        return career.getName();
    }

    public static int getCareerId(VillagerCareer career) {
        try {
            // Use reflection to obtain the race ID, as Forge 1.12.2 does not publicly
            // expose this method
            Field idField = career.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return (int) idField.get(career);
        } catch (Exception e) {
            LogHelper.error("Error obtaining the career ID: " + getCareerName(career));
            return 0;
        }
    }

    public static EntityVillager createVillager(@Nullable VillagerProfession profession,
            @Nullable VillagerCareer career, World world) {
        if (profession != null && career != null) {
            EntityVillager villager = new EntityVillager(world);
            villager.setProfession(profession);

            // Assign the careerId directly thanks to the Access Transformer
            // (villagercontracts_at.cfg)
            villager.careerId = getCareerId(career) + 1;

            // Generate the list of businesses for the new profession/career
            villager.populateBuyingList();

            return villager;
        }
        return new EntityVillager(world);
    }
}