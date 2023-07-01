package com.peeko32213.notsoshrimple.client.render;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.client.model.BloodWaterModel;
import com.peeko32213.notsoshrimple.client.model.PoisonWaterModel;
import com.peeko32213.notsoshrimple.common.entity.EntityBloodWater;
import com.peeko32213.notsoshrimple.common.entity.EntityToxicWater;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class BloodWaterRenderer extends GeoProjectilesRenderer<EntityBloodWater> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(NotSoShrimple.MODID, "textures/entity/poisonpiss.png");

    public BloodWaterRenderer(EntityRendererProvider.Context context) {
        super(context, new BloodWaterModel());
    }


    @Override
    public ResourceLocation getTextureLocation(EntityBloodWater p_114482_) {
        return TEXTURE_LOCATION;
    }
}
