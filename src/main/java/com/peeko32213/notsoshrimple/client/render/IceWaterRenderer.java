package com.peeko32213.notsoshrimple.client.render;

import com.peeko32213.notsoshrimple.client.model.CrayfishModel;
import com.peeko32213.notsoshrimple.client.model.IceWaterModel;
import com.peeko32213.notsoshrimple.client.model.PoisonWaterModel;
import com.peeko32213.notsoshrimple.common.entity.EntityIceWater;
import com.peeko32213.notsoshrimple.common.entity.EntityToxicWater;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.peeko32213.notsoshrimple.NotSoShrimple;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class IceWaterRenderer extends GeoProjectilesRenderer<EntityIceWater> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(NotSoShrimple.MODID, "textures/entity/poisonpiss.png");

    public IceWaterRenderer(EntityRendererProvider.Context context) {
        super(context, new IceWaterModel());
    }


    @Override
    public ResourceLocation getTextureLocation(EntityIceWater p_114482_) {
        return TEXTURE_LOCATION;
    }
}