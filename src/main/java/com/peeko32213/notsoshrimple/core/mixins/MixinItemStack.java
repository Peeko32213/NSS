package com.peeko32213.notsoshrimple.core.mixins;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.function.Consumer;

//A mixin to ItemStack
@Mixin(ItemStack.class)
public abstract class MixinItemStack{
    //remember, if a method in a mixin is identical to multiple methods in its hiearchy(method x with y parameters that exists in both the superclasses and in the class to be mixed into), it will always inject into the one in the mixed in class(the class in @Mixin).

    /*@Inject(
            method = "hurtAndBreak",  // the method's signature, or just its name
            at = @At("HEAD"),  // signal that this void should be run at the method HEAD, meaning the first opcode
            cancellable = true
    )
    public <T extends LivingEntity> void hurtAndBreak(int pAmount, T pEntity, Consumer<T> pOnBroken) {
        ItemStack itemStack = (ItemStack)(Object)this;
        CompoundTag stackTags = itemStack.getOrCreateTag();

        if (!pEntity.level.isClientSide && (!(pEntity instanceof Player) || !((Player)pEntity).getAbilities().instabuild)) {
            if (itemStack.isDamageableItem()) {
                if (stackTags.contains("smithing_stone_durability_bonus")) {
                    int baseHealthBuff = stackTags.getInt("smithing_stone_durability_bonus");

                    if (baseHealthBuff >= pAmount) {
                        stackTags.putInt("smithing_stone_durability_bonus", baseHealthBuff - pAmount);
                        return;
                    } else {
                        int leftoverDmg = pAmount - baseHealthBuff;
                        pAmount = leftoverDmg;
                    }
                }
            }
        }

    }*/

    @Inject(
            method = "hurt",  // the method's signature, or just its name
            at = @At("HEAD")  // signal that this void should be run at the method HEAD, meaning the first opcode
    )
    public void NSS_hurt(int pAmount, RandomSource pRandom, ServerPlayer pUser, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = (ItemStack)(Object)this;
        CompoundTag stackTags = itemStack.getOrCreateTag();
        System.out.println("yeahitworks");

        if (itemStack.isDamageableItem()) {
            if (stackTags.contains("smithing_stone_durability_bonus")) {
                int baseHealthBuff = stackTags.getInt("smithing_stone_durability_bonus");

                if (baseHealthBuff >= pAmount) {
                    stackTags.putInt("smithing_stone_durability_bonus", baseHealthBuff - pAmount);
                    pAmount = 0;
                } else {
                    int leftoverDmg = pAmount - baseHealthBuff;
                    pAmount = leftoverDmg;
                }
            }
        }

    }


}
