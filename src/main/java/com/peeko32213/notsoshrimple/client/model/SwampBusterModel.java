package com.peeko32213.notsoshrimple.client.model;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.item.ItemSwampBuster;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SwampBusterModel extends AnimatedGeoModel<ItemSwampBuster> {

    @Override
    public ResourceLocation getModelResource(ItemSwampBuster object) {
        return new ResourceLocation(NotSoShrimple.MODID, "geo/swampbuster.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ItemSwampBuster object) {
        return new ResourceLocation(NotSoShrimple.MODID, "textures/item/swampbusterblade.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ItemSwampBuster animatable) {
        return new ResourceLocation(NotSoShrimple.MODID, "animations/sword.animation.json");
    }
}
