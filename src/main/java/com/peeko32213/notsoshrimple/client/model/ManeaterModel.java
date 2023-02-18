package com.peeko32213.notsoshrimple.client.model;


import com.google.common.collect.Maps;
import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.entity.EntityCrayfish;
import com.peeko32213.notsoshrimple.common.entity.EntityManeaterShell;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.Map;

public class ManeaterModel extends AnimatedGeoModel<EntityManeaterShell>{

    @Override
    public ResourceLocation getModelResource(EntityManeaterShell object) {
        return new ResourceLocation(NotSoShrimple.MODID, "geo/maneater.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EntityManeaterShell object) {
        return new ResourceLocation(NotSoShrimple.MODID, "textures/entity/maneater.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EntityManeaterShell animatable) {
        return new ResourceLocation(NotSoShrimple.MODID, "animations/maneater.animation.json");
    }
}

