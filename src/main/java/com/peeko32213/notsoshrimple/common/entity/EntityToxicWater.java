package com.peeko32213.notsoshrimple.common.entity.projectiles;

import com.peeko32213.notsoshrimple.core.registry.NSSParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class EntityToxicWater extends AbstractHurtingProjectile implements IAnimatable {

    private LivingEntity owner;
    private int lifeTime;
    private static ParticleOptions particle = NSSParticles.FOAM_STANDARD.get();
    public boolean isOnFire() {
        return false;
    }
    //@Override
    protected float getInertia() {
        return 0.95F;
    }
    public float damage = 30.0f;
    public int maxLifeTime = 4000;
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public EntityToxicWater(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super((EntityType<? extends AbstractHurtingProjectile>) p_37248_, p_37249_);
    }

    protected ParticleOptions getTrailParticle() {
        return NSSParticles.FOAM_STANDARD.get();
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        System.out.println("hit" + this.position());
        Shulker marker = EntityType.SHULKER.create(this.level);
        marker.moveTo(this.position());


        HitResult.Type hitresult$type = pResult.getType();
        if (hitresult$type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult)pResult);
            this.level.gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), GameEvent.Context.of(this, (BlockState)null));
        } else if (hitresult$type == HitResult.Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult)pResult;
            //this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level.gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, this.level.getBlockState(blockpos)));
            this.discard();
        }
    }

    protected static float lerpRotation(float p_234614_0_, float p_234614_1_) {
        while (p_234614_1_ - p_234614_0_ < -180.0F) {
            p_234614_0_ -= 360.0F;
        }

        while (p_234614_1_ - p_234614_0_ >= 180.0F) {
            p_234614_0_ += 360.0F;
        }

        return Mth.lerp(0.2F, p_234614_0_, p_234614_1_);
    }


    private void addParticlesAroundSelf() {
        Vec3 vec3 = this.getDeltaMovement();
        for(int i = 0; i < 20; ++i) {
            double d0 = this.random.nextGaussian() * 0.2D;
            double d1 = this.random.nextGaussian() * 0.2D;
            double d2 = this.random.nextGaussian() * 0.2D;
            double length = this.random.nextGaussian();
            //System.out.println(length);
            this.level.addParticle(NSSParticles.FOAM_STANDARD.get(), (this.getX()+d0)+(vec3.x*length),
                    (this.getY()+d1)+(vec3.y*length), (this.getZ()+d2)+(vec3.z*length),
                    this.getDeltaMovement().x*0.02, this.getDeltaMovement().y*0.02,
                    this.getDeltaMovement().z*0.02);
        }

    }

    @Override
    public void tick() {

        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;

        this.updateRotation();
        float f = 0.99F;
        float f1 = 0.06F;

        if (this.level.getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
            this.remove(RemovalReason.DISCARDED);
        } else {
            this.setDeltaMovement(vector3d.scale(1));
            /*if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double)-0.06D, 0.0D));
            }*/

            this.setPos(d0, d1, d2);
        }
        this.addParticlesAroundSelf();
        if (vector3d.x == 0 && vector3d.y == 0 && vector3d.z == 0) {
            System.out.println("hit" + this.position());
            this.remove(RemovalReason.DISCARDED);
        }
        /*vanilla particle code ahead
        Vec3 vec3 = this.getDeltaMovement();
        double d5 = vec3.x;
        double d6 = vec3.y;
        d1 = vec3.z;
        double d7 = this.getX() + d5;
        d2 = this.getY() + d6;
        double d3 = this.getZ() + d1;
        double d4 = vec3.horizontalDistance();
        for(int j = 0; j < 4; ++j) {
            float f2 = 0.25F;
            this.level.addParticle(NSSParticles.FOAM_STANDARD.get(), d7 - d5 * 0.25D, d2 - d6 * 0.25D, d3 - d1 * 0.25D, d5, d6, d1);
        }*/
    }

    @Override
    protected void onHitEntity(EntityHitResult hitData) {
        super.onHitEntity(hitData);
        Entity entity1 = this.getOwner();
        Entity entity = hitData.getEntity();
        if (entity instanceof LivingEntity) {
            hitData.getEntity().hurt(DamageSource.mobAttack((LivingEntity) entity1), damage);
            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, 200, 2));
        }
        if (entity1 instanceof LivingEntity) {
            ((LivingEntity)entity1).setLastHurtMob(entity);
        }
        //this.discard();

    }

    //@Override
    //protected ItemStack getPickupItem() {
   //     return ItemStack.EMPTY;
    //}


    private double horizontalMag(Vec3 vector3d) {
        return vector3d.x * vector3d.x + vector3d.z * vector3d.z;
    }


    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().loop("animation.piss.move"));
        event.getController().setAnimationSpeed(1.0F);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.setResetSpeedInTicks(1);
        AnimationController<EntityToxicWater> controller = new AnimationController<>(this, "controller", 1, this::predicate);
        data.addAnimationController(controller);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
