package com.peeko32213.notsoshrimple.client.render;

import com.peeko32213.notsoshrimple.client.model.PoisonWaterModel;
import com.peeko32213.notsoshrimple.common.entity.EntityToxicWater;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.peeko32213.notsoshrimple.NotSoShrimple;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class ToxicWaterRenderer extends GeoProjectilesRenderer<EntityToxicWater> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(NotSoShrimple.MODID, "textures/entity/poisonpiss.png");

    public ToxicWaterRenderer(EntityRendererProvider.Context context) {
        super(context, new PoisonWaterModel());
    }


    @Override
    public ResourceLocation getTextureLocation(EntityToxicWater p_114482_) {
        return TEXTURE_LOCATION;
    }
}
