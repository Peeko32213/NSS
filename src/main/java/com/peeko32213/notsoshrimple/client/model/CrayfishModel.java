package com.peeko32213.notsoshrimple.client.model;


import com.google.common.collect.Maps;
import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.entity.EntityCrayfish;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.Map;

public class CrayfishModel extends AnimatedGeoModel<EntityCrayfish> {
    public static final Map<Integer, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), (hashMap) -> {
        hashMap.put(0, new ResourceLocation(NotSoShrimple.MODID, "textures/entity/lobster_brown.png"));
        hashMap.put(1, new ResourceLocation(NotSoShrimple.MODID, "textures/entity/lobster_yellow.png"));
        //brown selection^
        hashMap.put(2, new ResourceLocation(NotSoShrimple.MODID, "textures/entity/lobster_frost.png"));
        hashMap.put(3, new ResourceLocation(NotSoShrimple.MODID, "textures/entity/lobster_blue.png"));
        //frost selection^
        hashMap.put(4, new ResourceLocation(NotSoShrimple.MODID, "textures/entity/lobster_blood.png"));
        hashMap.put(5, new ResourceLocation(NotSoShrimple.MODID, "textures/entity/lobster_red.png"));
        //blood selection^
    });

    @Override
    public ResourceLocation getModelResource(EntityCrayfish object) {
        return new ResourceLocation(NotSoShrimple.MODID, "geo/lobster.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EntityCrayfish object) {
        return TEXTURES.getOrDefault(object.getVariant(), TEXTURES.get(0));
    }

    @Override
    public ResourceLocation getAnimationResource(EntityCrayfish animatable) {
        return new ResourceLocation(NotSoShrimple.MODID, "animations/lobster.animation.json");
    }
}

