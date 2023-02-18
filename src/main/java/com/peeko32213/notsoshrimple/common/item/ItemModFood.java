package com.peeko32213.notsoshrimple.common.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ItemModFood {

    //Raw
    public static final FoodProperties RAW_PRAWN = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).effect(new MobEffectInstance(MobEffects.WEAKNESS, 250, 1), 1.0F).meat().build();

    //Cooked
    public static final FoodProperties COOKED_PRAWN = (new FoodProperties.Builder()).nutrition(10).saturationMod(1.9F).effect(new MobEffectInstance(MobEffects.ABSORPTION, 500, 1), 1.0F).meat().build();


}
