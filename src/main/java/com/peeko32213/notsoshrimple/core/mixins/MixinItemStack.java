package com.peeko32213.notsoshrimple.core.mixins;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

//A mixin to ItemStack
@Mixin(ItemStack.class)
public class MixinItemStack{
    //remember, if a method in a mixin is identical to multiple methods in its hiearchy(method x with y parameters that exists in both the superclasses and in the class to be mixed into), it will always inject into the one in the mixed in class(the class in @Mixin).

    /*@Inject(
            method = "hurtAndBreak",  // the method's signature, or just its name
            at = @At("HEAD")  // signal that this void should be run at the method HEAD, meaning the first opcode
    )
    public <T extends LivingEntity> void hurtAndBreak(int pAmount, T pEntity, Consumer<T> pOnBroken, CallbackInfo ci) {
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


    /*@Nullable
    @Inject(
            method = "hurt",  // the method's signature, or just its name
            at = @At("HEAD")  // signal that this void should be run at the method HEAD, meaning the first opcode
    )
    public void NSS_hurt(int pAmount, RandomSource pRandom, ServerPlayer pUser, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemstack = (ItemStack) (Object) this;
        //CompoundTag stackTags = itemStack.getOrCreateTag();
        //System.out.println("yeahitworks");

        System.out.println(pAmount);

        if (itemstack.isDamageableItem()) {
            CompoundTag itemTags = itemstack.getOrCreateTag().copy();

            if (itemTags.contains("SmithingDurabilityBuff")) {
                int currentDurabilityBuff = itemTags.getInt("SmithingDurabilityBuff");

                if (pAmount >= currentDurabilityBuff) {
                    itemTags.putInt("SmithingDurabilityBuff", 0);
                    System.out.println(0);
                    itemstack.setTag(itemTags);
                    pAmount = pAmount - currentDurabilityBuff;

                    //if the amount is more than the buff, subtract the buff from the amount and set buff to 0
                } else {
                    itemTags.putInt("SmithingDurabilityBuff", currentDurabilityBuff - pAmount);
                    System.out.println(currentDurabilityBuff - pAmount);
                    itemstack.setTag(itemTags);

                    pAmount = 0;
                    //if the amount is less than the buff, just subtract the amount from the buff and return since pAmount is 0
                }
                //if the buff already exists decrease it, otherwise just pass it on
            }
            //otherwise just pass it on

        }
    }*/

    @Inject(
            method = "hurt",  // the method's signature, or just its name
            at = @At("TAIL")  // signal that this void should be run at the method HEAD, meaning the first opcode
    )
    public void otherhurt(int pAmount, RandomSource pRandom, ServerPlayer pUser, CallbackInfoReturnable<Boolean> cir) {
        System.out.println("pAmount" + pAmount);
    }

    @ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/world/item/ItemStack.getDamageValue()I"), method = "hurt", argsOnly = true, ordinal = 0)
    public int interceptedAmount(int pAmounttobeModified, int amountCopy) {
        ItemStack itemstack = (ItemStack) (Object) this;

        if (itemstack.isDamageableItem()) {
            CompoundTag itemTags = itemstack.getOrCreateTag().copy();

            if (itemTags.contains("SmithingDurabilityBuff")) {
                int currentDurabilityBuff = itemTags.getInt("SmithingDurabilityBuff");

                if (pAmounttobeModified >= currentDurabilityBuff) {
                    itemTags.putInt("SmithingDurabilityBuff", 0);
                    System.out.println(0);
                    itemstack.setTag(itemTags);
                    pAmounttobeModified = pAmounttobeModified - currentDurabilityBuff;

                    //if the amount is more than the buff, subtract the buff from the amount and set buff to 0
                } else {
                    itemTags.putInt("SmithingDurabilityBuff", currentDurabilityBuff - pAmounttobeModified);
                    System.out.println(currentDurabilityBuff - pAmounttobeModified);
                    itemstack.setTag(itemTags);
                    pAmounttobeModified = 0;
                    //if the amount is less than the buff, just subtract the amount from the buff and return since pAmount is 0
                }
                //if the buff already exists decrease it, otherwise just pass it on
            }
            //otherwise just pass it on

        }

        System.out.println("amountcopy" + amountCopy);
        System.out.println("amount" + pAmounttobeModified);

        return pAmounttobeModified;
    }

        /*if (itemStack.isDamageableItem()) {
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
        }*/



}
