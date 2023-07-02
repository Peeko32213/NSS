package com.peeko32213.notsoshrimple.common.item.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

public class Reinforcement extends ProtectionEnchantment {
    public Reinforcement(Rarity pRarity, Type pType, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pType, pApplicableSlots);
    }
}
