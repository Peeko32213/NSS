package com.peeko32213.notsoshrimple.client.render;

import com.peeko32213.notsoshrimple.common.entity.EntityToxicWater;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.peeko32213.notsoshrimple.NotSoShrimple;

public class ToxicWaterRenderer extends EntityRenderer<EntityToxicWater> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(NotSoShrimple.MODID, "textures/entity/poisonpiss.png");

    protected ToxicWaterRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityToxicWater p_114482_) {
        return TEXTURE_LOCATION;
    }
}
