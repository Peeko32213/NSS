package com.peeko32213.notsoshrimple.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public final ForgeConfigSpec.IntValue crayfishSpawnWeight;
    public final ForgeConfigSpec.IntValue crayfishSpawnRolls;
    public final ForgeConfigSpec.IntValue maneaterWeight;
    public final ForgeConfigSpec.IntValue maneaterRolls;

    public CommonConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("general");

        builder.push("spawning");
        crayfishSpawnWeight = buildInt(builder, "crayfishSpawnWeight", "spawns", NotSoShrimpleConfig.crayfishSpawnWeight, 0, 1000, "Change the spawn weight for Crayfishes. Higher number = higher chance of spawning. 0 = disable spawn");
        crayfishSpawnRolls = buildInt(builder, "crayfishSpawnRolls", "spawns", NotSoShrimpleConfig.crayfishSpawnRolls, 0, Integer.MAX_VALUE, "Change the roll chance for Crayfishes. Higher number = lower chance of spawning");
        maneaterWeight = buildInt(builder, "maneaterWeight", "spawns", NotSoShrimpleConfig.maneaterWeight, 0, 1000, "Change the spawn weight for Maneater Shells. Higher number = higher chance of spawning. 0 = disable spawn");
        maneaterRolls = buildInt(builder, "maneaterRolls", "spawns", NotSoShrimpleConfig.maneaterRolls, 0, Integer.MAX_VALUE, "Change the roll chance for Maneater Shells. Higher number = lower chance of spawning");
    }

    private static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String name, String catagory, int defaultValue, int min, int max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    }
