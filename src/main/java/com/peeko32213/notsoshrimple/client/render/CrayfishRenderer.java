package com.peeko32213.notsoshrimple.client.render;


import com.peeko32213.notsoshrimple.client.model.CrayfishModel;
import com.peeko32213.notsoshrimple.common.entity.EntityCrayfish;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CrayfishRenderer extends GeoEntityRenderer<EntityCrayfish> {

    public CrayfishRenderer(EntityRendererProvider.Context context) {
        super(context, new CrayfishModel());
    }
}
