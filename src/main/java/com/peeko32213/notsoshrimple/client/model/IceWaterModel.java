package com.peeko32213.notsoshrimple.client.model;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.entity.EntityIceWater;
import com.peeko32213.notsoshrimple.common.entity.EntityManeaterShell;
import com.peeko32213.notsoshrimple.common.entity.EntityToxicWater;
import com.peeko32213.notsoshrimple.common.item.ItemSwampBuster;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class IceWaterModel extends AnimatedGeoModel<EntityIceWater> {

    @Override
    public ResourceLocation getModelResource(EntityIceWater object) {
        return new ResourceLocation(NotSoShrimple.MODID, "geo/piss.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EntityIceWater object) {
        return new ResourceLocation(NotSoShrimple.MODID, "textures/entity/poisonpiss.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EntityIceWater animatable) {
        return null;
    }
}
