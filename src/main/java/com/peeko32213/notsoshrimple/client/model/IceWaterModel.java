package com.peeko32213.notsoshrimple.client.model;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.entity.ArchivedProjectileWater;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class IceWaterModel extends AnimatedGeoModel<ArchivedProjectileWater> {

    @Override
    public ResourceLocation getModelResource(ArchivedProjectileWater object) {
        return new ResourceLocation(NotSoShrimple.MODID, "geo/piss.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArchivedProjectileWater object) {
        return new ResourceLocation(NotSoShrimple.MODID, "textures/entity/poisonpiss.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ArchivedProjectileWater animatable) {
        return new ResourceLocation(NotSoShrimple.MODID, "animations/piss.animation.json");
    }
}
