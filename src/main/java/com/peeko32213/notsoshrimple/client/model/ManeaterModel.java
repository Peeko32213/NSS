package com.peeko32213.notsoshrimple.client.model;


import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.entity.mobs.EntityManeaterShell;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

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

