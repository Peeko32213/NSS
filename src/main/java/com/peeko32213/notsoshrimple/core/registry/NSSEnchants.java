package com.peeko32213.notsoshrimple.core.registry;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.item.enchantments.Refinement;
import com.peeko32213.notsoshrimple.common.item.enchantments.Reinforcement;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NSSEnchants {

    public static final DeferredRegister<Enchantment> DEF_REG = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, NotSoShrimple.MODID);
    //public static final EnchantmentCategory SMITHING = EnchantmentCategory.create("Smithing", (item -> item instanceof TieredItem));

    //public static final RegistryObject<Enchantment> REFINEMENT = DEF_REG.register("Refinement", () -> new Refinement(Enchantment.Rarity.VERY_RARE, 2, EquipmentSlot.MAINHAND));

}