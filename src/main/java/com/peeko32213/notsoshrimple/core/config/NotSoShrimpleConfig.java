package com.peeko32213.notsoshrimple.core.config;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import static com.peeko32213.notsoshrimple.NotSoShrimple.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class NotSoShrimpleConfig {
    public static int crayfishSpawnWeight = 1;
    public static int crayfishSpawnRolls = 1;
    public static int maneaterWeight = 10;
    public static int maneaterRolls = 1;
    public static int somberCap = 5;
    public static int smithingCap = 5;

    public static void bake(ModConfig config) {
        try {
            crayfishSpawnWeight = ConfigHolder.COMMON.crayfishSpawnWeight.get();
            crayfishSpawnRolls = ConfigHolder.COMMON.crayfishSpawnRolls.get();
            maneaterWeight = ConfigHolder.COMMON.maneaterWeight.get();
            maneaterRolls = ConfigHolder.COMMON.maneaterRolls.get();
            somberCap = ConfigHolder.COMMON.somberCap.get();
            smithingCap = ConfigHolder.COMMON.smithingCap.get();

        } catch (Exception e) {
            NotSoShrimple.LOGGER.warn("An exception was caused trying to load the config for Not So Shrimple.");
            e.printStackTrace();
        }
    }
}
