package com.peeko32213.notsoshrimple.client.model;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.entity.EntityManeaterShell;
import com.peeko32213.notsoshrimple.common.entity.EntityToxicWater;
import com.peeko32213.notsoshrimple.common.item.ItemSwampBuster;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PoisonWaterModel extends AnimatedGeoModel<EntityToxicWater> {

    @Override
    public ResourceLocation getModelResource(EntityToxicWater object) {
        return new ResourceLocation(NotSoShrimple.MODID, "geo/piss.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EntityToxicWater object) {
        return new ResourceLocation(NotSoShrimple.MODID, "textures/entity/icepiss.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EntityToxicWater animatable) {
        return null;
    }
}
