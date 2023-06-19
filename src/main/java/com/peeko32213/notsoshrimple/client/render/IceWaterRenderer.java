package com.peeko32213.notsoshrimple.client.render;

import com.peeko32213.notsoshrimple.client.model.IceWaterModel;
import com.peeko32213.notsoshrimple.common.entity.ArchivedProjectileWater;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.peeko32213.notsoshrimple.NotSoShrimple;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class IceWaterRenderer extends GeoProjectilesRenderer<ArchivedProjectileWater> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(NotSoShrimple.MODID, "textures/entity/poisonpiss.png");

    public IceWaterRenderer(EntityRendererProvider.Context context) {
        super(context, new IceWaterModel());
    }


    @Override
    public ResourceLocation getTextureLocation(ArchivedProjectileWater p_114482_) {
        return TEXTURE_LOCATION;
    }
}