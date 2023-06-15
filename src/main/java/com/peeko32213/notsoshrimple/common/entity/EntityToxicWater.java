package com.peeko32213.notsoshrimple.common.entity;

import com.peeko32213.notsoshrimple.core.registry.NSSParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

public class EntityToxicWater extends AbstractHurtingProjectile implements IAnimatable {

    private Monster owner;
    private int lifeTime;
    private static final ParticleOptions particle = NSSParticles.FOAM_STANDARD.get();
    public boolean isOnFire() {
        return false;
    }
    //@Override
    protected float getInertia() {
        return 0.95F;
    }
    public float damage = 30.0f;
    public double pissspeed = 7.5;
    //piss speed multiplier
    //MAKE SURE THIS IS THE SAME NUMBER AS EntityCrayfish's pissspeed
    public int maxLifeTime = (int) (1500/pissspeed);
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    Vec3 startPos;
    Vec3 deltaPos;
    Vec3 normalDeltaPos;
    double boxRadius = 2.25;
    Vec3 scanBox = new Vec3(50,50,50);

    //hitbox radius

    public EntityToxicWater(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super((EntityType<? extends AbstractHurtingProjectile>) p_37248_, p_37249_);
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.WITCH;
    }

    @Override
    public Monster getOwner() {
        return owner;
    }

    public void setOwner(Monster owner) {
        this.owner = owner;
        /*this.startPos = this.position();
        deltaPos = owner.getTarget().getEyePosition().subtract(startPos);
        normalDeltaPos = deltaPos.normalize();*/
        //System.out.println("start" + this.position());
    }

    public void setTargetPos(Vec3 pos){
        this.startPos = this.position();
        this.deltaPos = pos.subtract(this.position());
        this.normalDeltaPos = deltaPos.normalize();
    }

    /*@Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        //System.out.println("hit" + this.position());
        //Shulker marker = EntityType.SHULKER.create(this.level);
        //marker.moveTo(this.position());


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
    }*/

    protected static float lerpRotation(float p_234614_0_, float p_234614_1_) {
        while (p_234614_1_ - p_234614_0_ < -180.0F) {
            p_234614_0_ -= 360.0F;
        }

        while (p_234614_1_ - p_234614_0_ >= 180.0F) {
            p_234614_0_ += 360.0F;
        }

        return Mth.lerp(0.2F, p_234614_0_, p_234614_1_);
    }


    /*private void addParticlesAroundSelf() {
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

    }*/

    @Override
    public void tick() {
        super.tick();
        lifeTime++;

        this.setInvisible(true);
        if (this.lifeTime >= maxLifeTime) {
            //System.out.println("removed(lifetime)");
            this.remove(RemovalReason.DISCARDED);
        }
        /*Vec3 vector3d = this.getDeltaMovement();
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
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double)-0.06D, 0.0D));
            }

            this.setPos(d0, d1, d2);
        }
        this.addParticlesAroundSelf();
        if (vector3d.x == 0 && vector3d.y == 0 && vector3d.z == 0) {
            System.out.println("hit" + this.position());
            this.remove(RemovalReason.DISCARDED);
        }*/

        /*vanilla particle code below
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

        //---------------------------------------------------------------
        //original code^
        if (this.getOwner() != null){
            int timer = this.lifeTime;

            Vec3 scaledPos = startPos.add(normalDeltaPos.scale((double)timer*pissspeed));
            //System.out.println(scaledPos);
            //System.out.println(lifeTime);
            ServerLevel world = (ServerLevel)owner.level;
            BlockPos center = new BlockPos(scaledPos);
            if (world.getBlockState(center).getBlock().hasCollision == true){
                //System.out.println("removed(hitting smthin)");
                //System.out.println("blockstate" + center);
                //TODO: Check if this fix works(do shrimprojectiles pierce cobweb)
                this.remove(RemovalReason.DISCARDED);
            }

            world.sendParticles(NSSParticles.FOAM_STANDARD.get(), (scaledPos.x), (scaledPos.y), (scaledPos.z), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            AABB checkZone = new AABB(center).inflate(boxRadius + pissspeed);

            //Vec3 hitbox = new Vec3 ((boxRadius), (boxRadius), (boxRadius));
            AABB hitboxbox = new AABB(center).inflate(boxRadius);
            //hitboxOutline(scaledPos, hitbox.x, hitbox.y, hitbox.z, world);

            List<LivingEntity> potentialVictims = world.getEntitiesOfClass(LivingEntity.class, checkZone);
            //System.out.println(potentialVictims);
            //System.out.println(scaledPos);

            for (int v = 0; v < potentialVictims.size(); v ++){
                LivingEntity victim = potentialVictims.get(v);
                if (victim != owner) {
                    AABB targetbox = getAABB(victim.getX(), victim.getY(), victim.getZ(), victim);
                    if (targetbox.intersects(hitboxbox)) {
                        victim.hurt(DamageSource.mobAttack(owner), 10.0F);
                        double dA = 0.2D * (1.0D - victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                        double dB = 1.0D * (1.0D - victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                        victim.push(normalDeltaPos.x() * dB, normalDeltaPos.y() * dA, normalDeltaPos.z() * dB);
                        System.out.println(victim);
                    }
                }
            }

            //world.sendParticles(NSSParticles.FOAM_STANDARD.get(), scaledPos.x, scaledPos.y, scaledPos.z,  1, 0.0D, 0.0D, 0.0D, 0.0D);

            for(int p = 0; p < 6 * (1 + Math.sqrt(0.001 * timer)); ++p) {
                //System.out.println(length);
                if (this.level.isClientSide) {
                    double d0 = this.random.nextGaussian() * 0.125D;
                    double d1 = this.random.nextGaussian() * 0.125D;
                    double d2 = this.random.nextGaussian() * 0.125D;
                    double length = this.random.nextDouble();
                    this.level.addParticle(NSSParticles.FOAM_STANDARD.get(), (scaledPos.x + (d0 * (Math.sqrt(timer)))) + (deltaPos.x * length), (scaledPos.y + (d1 * (Math.sqrt(timer)))) + (deltaPos.y * length), (scaledPos.z + (d2 * (Math.sqrt(timer)))) + (deltaPos.z * length), 0.0D, 0.0D, 0.0D);
                    this.level.addParticle(NSSParticles.FOAM_STANDARD.get(), (scaledPos.x + d0) + (deltaPos.x * length), (scaledPos.y + d1) + (deltaPos.y * length), (scaledPos.z + d2) + (deltaPos.z * length), 0.0D, 0.0D, 0.0D);
                    this.level.addParticle(NSSParticles.FOAM_STANDARD.get(), (scaledPos.x + d1) + (deltaPos.x * length), (scaledPos.y + d2) + (deltaPos.y * length), (scaledPos.z + d0) + (deltaPos.z * length), 0.0D, 0.0D, 0.0D);
                    //owner.level.addParticle(NSSParticles.FOAM_STANDARD.get(), (scaledPos.x+d0) + (deltaPos.x*length), (scaledPos.y+d1) + (deltaPos.y*length), (scaledPos.z+d2) + (deltaPos.z*length), 0.0D, 0.0D, 0.0D);
                }
            }
        }

        //------------------------------------------------------------------
        //^warden stuff

            /*if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double)-0.06D, 0.0D));
            }

            this.setPos(d0, d1, d2);
        }
//        this.addParticlesAroundSelf();
        if (vector3d.x == 0 && vector3d.y == 0 && vector3d.z == 0) {
            System.out.println("hit" + this.position());
            this.remove(RemovalReason.DISCARDED);*/
    }


    /*@Override
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

    }*/

    //@Override
    //protected ItemStack getPickupItem() {
   //     return ItemStack.EMPTY;
    //}


    private double horizontalMag(Vec3 vector3d) {
        return vector3d.x * vector3d.x + vector3d.z * vector3d.z;
    }

    public void hitboxOutline (Vec3 pos, double rX, double rY, double rZ, ServerLevel world) {
        world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, (pos.x + rX), (pos.y + rY), (pos.z + rZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(ParticleTypes.END_ROD, (pos.x + rX), (pos.y - rY), (pos.z + rZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(NSSParticles.FOAM_STANDARD.get(), (pos.x + rX), (pos.y + rY), (pos.z - rZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(ParticleTypes.HAPPY_VILLAGER, (pos.x + rX), (pos.z - rY), (pos.z - rZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);

        world.sendParticles(ParticleTypes.HAPPY_VILLAGER, (pos.x - rX), (pos.y + rY), (pos.z + rZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(NSSParticles.FOAM_STANDARD.get(), (pos.x - rX), (pos.y - rY), (pos.z + rZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(ParticleTypes.END_ROD, (pos.x - rX), (pos.y + rY), (pos.z - rZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, (pos.x - rX), (pos.z - rY), (pos.z - rZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    public static AABB getAABB(double pX, double pY, double pZ, LivingEntity entity) {
        float f = entity.getBbWidth() / 2.0F;
        return new AABB(pX - (double)f, pY, pZ - (double)f, pX + (double)f, pY + (double)entity.getBbHeight(), pZ + (double)f);
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
