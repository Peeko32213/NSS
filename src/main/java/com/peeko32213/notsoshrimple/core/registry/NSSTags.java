package com.peeko32213.notsoshrimple.core.registry;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public class NSSTags {

    public static final TagKey<Block> CRAYFISH_BREAKABLES = registerBlockTag("crayfish_breakables");
    public static final TagKey<Block> CRAYFISH_SPAWNS = registerBlockTag("crayfish_spawns");


    public static final TagKey<EntityType<?>> CRAYFISH_VICTIMS = registerEntityTag("crayfish_victims");


    private static TagKey<Block> registerBlockTag(String name) {
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(NotSoShrimple.MODID, name));
    }

    private static TagKey<EntityType<?>> registerEntityTag(String name) {
        return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(NotSoShrimple.MODID, name));
    }

}
