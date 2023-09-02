package com.peeko32213.notsoshrimple.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Game;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

import java.util.Collection;
import java.util.Iterator;

public class ItemPurgingStone extends Item {
    public ItemPurgingStone(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 1;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level pLevel, LivingEntity pEntityLiving) {
        Iterator<MobEffectInstance> allStatus = pEntityLiving.getActiveEffects().iterator();
        boolean removedAny = false;
        //boolean to check if any statuses exist

        if (!pLevel.isClientSide) {
            while (allStatus.hasNext()) {
                MobEffectInstance effect = allStatus.next();
                //iterates through all the effect instances and removes anything that's considered negative
                if (!effect.getEffect().isBeneficial()) {
                    //removes the status
                    pEntityLiving.onEffectRemoved(effect);
                    allStatus.remove();
                    removedAny = true;
                }
            }

            if (pEntityLiving instanceof ServerPlayer serverplayer && removedAny == true) {
                Minecraft.getInstance().gameRenderer.displayItemActivation(itemStack);
                pEntityLiving.playSound(SoundEvents.AMETHYST_BLOCK_BREAK, 1.0F, 1.0F);
                //plays animation and sound

                CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, itemStack);
                serverplayer.awardStat(Stats.ITEM_USED.get(this));
                itemStack.shrink(1);
                //consumes the item if it has removed negative
            }
        }

        return itemStack;
    }

}
