package com.peeko32213.notsoshrimple.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public final ForgeConfigSpec.IntValue crayfishSpawnWeight;
    public final ForgeConfigSpec.IntValue crayfishSpawnRolls;

    public CommonConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("general");

        builder.push("spawning");
        crayfishSpawnWeight = buildInt(builder, "crayfishSpawnWeight", "spawns", NotSoShrimpleConfig.crayfishSpawnWeight, 0, 1000, "Change the spawn weight for Crayfish. Higher number = higher chance of spawning. 0 = disable spawn");
        crayfishSpawnRolls = buildInt(builder, "crayfishSpawnRolls", "spawns", NotSoShrimpleConfig.crayfishSpawnRolls, 0, Integer.MAX_VALUE, "Change the roll chance for Crayfish. Higher number = lower chance of spawning");
    }

    private static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String name, String catagory, int defaultValue, int min, int max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    }
