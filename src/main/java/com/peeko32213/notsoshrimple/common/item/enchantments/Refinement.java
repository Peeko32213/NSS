package com.peeko32213.notsoshrimple.common.item.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;

public class Refinement extends DamageEnchantment {
    public Refinement(Enchantment.Rarity pRarity, int pType, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pType, pApplicableSlots);
    }

    /*@Override
    public int getMinCost(int pEnchantmentLevel) {
        return 0;
        //no xp needed for smithing
    }

    @Override
    public int getMaxCost(int pEnchantmentLevel) {
        return 0;
        //no xp needed for smithing
    }

    @Override
    public float getDamageBonus(int pLevel, MobType pCreatureType) {
        return pLevel;
        //each level boosts damage by 1
    }

    @Override
    public int getMaxLevel() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canEnchant(ItemStack pStack) {
        return pStack.getItem() instanceof TieredItem ? true : super.canEnchant(pStack);
    }*/


}

