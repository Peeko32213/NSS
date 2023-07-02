package com.peeko32213.notsoshrimple.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.client.render.SwampBusterRenderer;
import com.peeko32213.notsoshrimple.common.entity.utl.MathHelpers;
import com.peeko32213.notsoshrimple.common.entity.utl.PisslikeHitboxes;
import com.peeko32213.notsoshrimple.core.event.CommonForgeEvents;
import com.peeko32213.notsoshrimple.core.registry.NSSItemTiers;
import com.peeko32213.notsoshrimple.core.registry.NSSItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ItemClawblade extends SwordItem implements IAnimatable{
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);


    public Vec3 slamOffset = new Vec3(0, -2, 0);
    public int slamDmg = 5;
    public double arthropodBonus = 0.3;
    public int animationState = 0;

    public ItemClawblade(Tier tier, int attackDamage, float attackSpeed) {
        super(tier, attackDamage, attackSpeed, new Properties()
                .stacksTo(1)
                .defaultDurability(tier.getUses())
                .tab(NotSoShrimple.SHRIMPLE)
        );
        //TODO: Add particles to the slam with a standard distribution, maybe add poison?

    }

    public void switchAnimationState(int value) { this.animationState = value; };

    public Rarity getRarity(ItemStack pStack) {
        return Rarity.EPIC;
        //epic rarity on default
    }

    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        return false;
        //stops it from randomly breaking shit
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker)
    {
        return true;
        //banana slamma
    }

    public AABB getSweepHitBox(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity target)
    {
        return target.getBoundingBox().inflate(0.0D, 0.0D, 0.0D);
        //The weapon cannot sweep.
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity){
        super.onLeftClickEntity(stack, player, entity);

        /*if (player.getMainHandItem().getItem() instanceof ItemClawblade && player.fallDistance > 0 && player.isCrouching() && !player.level.isClientSide() && !player.getCooldowns().isOnCooldown(NSSItems.GREAT_PRAWN_CLAWBLADE.get())) {
            this.switchAnimationState(1);
            //you have to be falling and crouching and have the weapon in your main hand to be slamming

            if (entity instanceof LivingEntity) {
                LivingEntity victim = (LivingEntity) entity;
                Vec2 knockVec = MathHelpers.OrizontalAimVector(
                        MathHelpers.AimVector(new Vec3(-player.position().x, -player.position().y, -player.position().z),
                                new Vec3(-victim.position().x, -victim.position().y, -victim.position().z)
                        ));
                victim.knockback(1.5, knockVec.x, knockVec.y);
            }
            //knock the target back without the aoe if target is alive

            PisslikeHitboxes.PivotedPolyHitCheck(player, slamOffset, 4.0, 2, 4.0, (ServerLevel) player.getLevel(), 5, DamageSource.playerAttack(player), 1.5f, false);
            //creates aoe
            stack.hurtAndBreak(50, player, (p_29910_) -> {
                p_29910_.broadcastBreakEvent(player.getUsedItemHand());
            });
            //damages the sword by 50
            player.getCooldowns().addCooldown(NSSItems.GREAT_PRAWN_CLAWBLADE.get(), 20*3);
            //cooldown of 3 seconds
        }*/
        return false;
    }

    /*@Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if (this.allowedIn(tab)) {
            ItemStack stack = new ItemStack(this);
            stack.enchant(Enchantments.BANE_OF_ARTHROPODS, 5);
            stack.enchant(Enchantments.SWEEPING_EDGE, 3);
            stack.enchant(Enchantments.KNOCKBACK, 3);
            list.add(stack);
        }
    }


    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        stack.serializeNBT();
        stack.enchant(Enchantments.BANE_OF_ARTHROPODS, 5);
        stack.enchant(Enchantments.SWEEPING_EDGE, 3);
        stack.enchant(Enchantments.KNOCKBACK, 3);
    }*/

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final SwampBusterRenderer renderer = new SwampBusterRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimationData data) {
            data.addAnimationController(new AnimationController(this, "controller",
                    0, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {

        switch(animationState) {
            case 0:
                event.getController().setAnimation(new AnimationBuilder().loop("idle"));
            case 1:
                event.getController().setAnimation(new AnimationBuilder().loop("idle"));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
