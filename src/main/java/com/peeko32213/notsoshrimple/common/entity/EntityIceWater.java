package com.peeko32213.notsoshrimple.common.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class EntityIceWater extends AbstractArrow implements IAnimatable {

    private LivingEntity owner;
    private int lifeTime;
    private static ParticleOptions particle = ParticleTypes.BUBBLE;
    public boolean isOnFire() {
        return false;
    }
    //@Override
    protected float getInertia() {
        return 0.95F;
    }
    public float damage = 15.0f;
    public int maxLifeTime = 1500;
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public EntityIceWater(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super((EntityType<? extends AbstractArrow>) p_37248_, p_37249_);
    }

    @Override
    protected void onHit(HitResult p_37218_) {
        super.onHit(p_37218_);
        if (!this.level.isClientSide) {
            this.discard();
        }

    }

    private ParticleOptions getTrailParticle() {
        return ParticleTypes.BUBBLE;
    }

    @Override
    public void tick() {
        lifeTime++;
        if(lifeTime>=maxLifeTime) {
            lifeTime=0;
            this.discard();
        }
        this.level.addParticle(this.getTrailParticle(), 0,  0.5D, 0, 0.0D, 0.0D, 0.0D);
        super.tick();
    }

    @Override
    protected void onHitEntity(EntityHitResult p_37216_) {
        super.onHitEntity(p_37216_);
        Entity entity1 = this.getOwner();
        Entity entity = p_37216_.getEntity();
        if (entity1 instanceof LivingEntity) {
            ((LivingEntity)entity1).setLastHurtMob(entity);
            p_37216_.getEntity().hurt(DamageSource.mobAttack((LivingEntity) entity1), damage);
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2, 200));
            }
        }

    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().loop("animation.piss.move"));
        event.getController().setAnimationSpeed(1.0F);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.setResetSpeedInTicks(1);
        AnimationController<EntityIceWater> controller = new AnimationController<>(this, "controller", 1, this::predicate);
        data.addAnimationController(controller);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
