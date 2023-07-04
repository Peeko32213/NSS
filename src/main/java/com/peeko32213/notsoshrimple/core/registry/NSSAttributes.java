package com.peeko32213.notsoshrimple.core.registry;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.item.enchantments.Refinement;
import com.peeko32213.notsoshrimple.common.item.enchantments.Reinforcement;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class NSSAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTEREGISTER = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, NotSoShrimple.MODID);

    public static RegistryObject<Attribute> SMITHING_STONE_EXTRA_DURABILITY (String name, Supplier<Attribute> attribute) {
        return ATTRIBUTEREGISTER.register(name, attribute);
    }

    //public static final RegistryObject<Attribute> SMITHING_STONE_EXTRA_DURABILITY = ATTRIBUTEREGISTER.register("generic.smithing_stone_extra_durability",
    //        (new Attribute("attribute.name.generic.smithing_stone_extra_durability", 0.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    //private static RangedAttribute register(String pId, Attribute pAttribute) {
    //    return ATTRIBUTEREGISTER.register("generic.smithing_stone_extra_durability", new RangedAttribute("attribute.name.generic.smithing_stone_extra_durability", 0.0D, 0.0D, Double.MAX_VALUE));
    //}
}