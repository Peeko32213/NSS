package com.peeko32213.notsoshrimple.common.entity;

import com.peeko32213.notsoshrimple.core.registry.NSSParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

public class EntityBloodWater extends AbstractHurtingProjectile implements IAnimatable {

    private Monster owner;
    public int lifeTime = 0;
    private static final ParticleOptions particle = NSSParticles.FOAM_STANDARD.get();
    public boolean isOnFire() {
        return false;
    }
    //@Override
    protected float getInertia() {
        return 0.95F;
    }
    public float damage = 10.0f;
    public double pissspeed = 7.5;
    //piss speed multiplier
    //MAKE SURE THIS IS THE SAME NUMBER AS EntityCrayfish's pissspeed
    public int maxLifeTime = (int) (1500/pissspeed);
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private static final EntityDataAccessor<Float> DATA_PISS_STARTPOSX = SynchedEntityData.defineId(EntityBloodWater.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PISS_STARTPOSY = SynchedEntityData.defineId(EntityBloodWater.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PISS_STARTPOSZ = SynchedEntityData.defineId(EntityBloodWater.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PISS_DELTAPOSX = SynchedEntityData.defineId(EntityBloodWater.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PISS_DELTAPOSY = SynchedEntityData.defineId(EntityBloodWater.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PISS_DELTAPOSZ = SynchedEntityData.defineId(EntityBloodWater.class, EntityDataSerializers.FLOAT);
    //change these to EntityDataSerializers.VECTOR3 on newer versions(they don't exist yet)
    public Vec3 startPos;
    public Vec3 deltaPos;
    public Vec3 normalDeltaPos;
    public double boxRadius = 2.25;
    public Vec3 scanBox = new Vec3(50,50,50);

    public EntityBloodWater(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super((EntityType<? extends AbstractHurtingProjectile>) p_37248_, p_37249_);

        this.entityData.define(DATA_PISS_STARTPOSX, 0F);
        this.entityData.define(DATA_PISS_STARTPOSY, 0F);
        this.entityData.define(DATA_PISS_STARTPOSZ, 0F);
        this.entityData.define(DATA_PISS_DELTAPOSX, 0F);
        this.entityData.define(DATA_PISS_DELTAPOSY, 0F);
        this.entityData.define(DATA_PISS_DELTAPOSZ, 0F);
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.ENTITY_EFFECT;
    }

    @Override
    public Monster getOwner() {
        return owner;
    }

    public void setOwner(Monster owner) {
        this.owner = owner;
    }

    public void setTargetPos(Vec3 finalTargetPos){
        this.startPos = this.position();
        this.deltaPos = finalTargetPos.subtract(this.position());
        this.normalDeltaPos = deltaPos.normalize();
        this.entityData.set(DATA_PISS_STARTPOSX, (float) startPos.x);
        this.entityData.set(DATA_PISS_STARTPOSY, (float) startPos.y);
        this.entityData.set(DATA_PISS_STARTPOSZ, (float) startPos.z);
        this.entityData.set(DATA_PISS_DELTAPOSX, (float) deltaPos.x);
        this.entityData.set(DATA_PISS_DELTAPOSY, (float) deltaPos.y);
        this.entityData.set(DATA_PISS_DELTAPOSZ, (float) deltaPos.z);
        //NSSPacketHub.INSTANCE.send(PacketDistributor.ALL.with(() -> null,
        //        new ClientboundShrimpTargetingDataInAPacket(this.startPos, this.deltaPos, this.lifeTime, this.getId()));
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
        int timer = this.lifeTime;

        if (this.level.isClientSide && this.lifeTime <= 1) {
            this.startPos = new Vec3(this.entityData.get(DATA_PISS_STARTPOSX), this.entityData.get(DATA_PISS_STARTPOSY), this.entityData.get(DATA_PISS_STARTPOSZ));
            this.deltaPos = new Vec3(this.entityData.get(DATA_PISS_DELTAPOSX), this.entityData.get(DATA_PISS_DELTAPOSY), this.entityData.get(DATA_PISS_DELTAPOSZ));
            this.normalDeltaPos = deltaPos.normalize();
        }

        this.setInvisible(true);
        if (this.lifeTime >= maxLifeTime) {
            //System.out.println("removed(lifetime)");
            this.remove(RemovalReason.DISCARDED);
        }

        if (this.level.isClientSide() && startPos != null) {
            this.normalDeltaPos = deltaPos.normalize();
            Vec3 scaledPos = startPos.add(normalDeltaPos.scale((double)timer*pissspeed));
            //startPos is fine, the process that makes scaledPos broke it

            this.level.addParticle(NSSParticles.FOAM_STANDARD.get(), (scaledPos.x), (scaledPos.y), (scaledPos.z), 0.0D, 0.0D, 0.0D);
            //completely normal particle

            for (int p = 0; p < 6 * (1 + Math.sqrt(0.001 * timer)); ++p) {
                //1 + Math.sqrt(0.001 * timer) makes the piss spawn more particles the farther you are to compensate for the beam getting wider
                double d0 = this.random.nextGaussian() * 0.125D;
                double d1 = this.random.nextGaussian() * 0.125D;
                double d2 = this.random.nextGaussian() * 0.125D;
                double length = this.random.nextDouble();
                this.level.addParticle(ParticleTypes.SMOKE, (scaledPos.x + (d0 * (Math.sqrt(timer)))) + (deltaPos.x * length), (scaledPos.y + (d1 * (Math.sqrt(timer)))) + (deltaPos.y * length), (scaledPos.z + (d2 * (Math.sqrt(timer)))) + (deltaPos.z * length), 0.0D, 0.0D, 0.0D);
                //this foam gets wider over distance^
                this.level.addParticle(ParticleTypes.SMOKE, (scaledPos.x + d0) + (deltaPos.x * length), (scaledPos.y + d1) + (deltaPos.y * length), (scaledPos.z + d2) + (deltaPos.z * length), 0.0D, 0.0D, 0.0D);
                this.level.addParticle(ParticleTypes.SMOKE, (scaledPos.x + d1) + (deltaPos.x * length), (scaledPos.y + d2) + (deltaPos.y * length), (scaledPos.z + d0) + (deltaPos.z * length), 0.0D, 0.0D, 0.0D);
                //these two foams are randomized in range and they will distribute themselves across the length of the pathway
            }
        }

        if (this.getOwner() != null){;
            Vec3 scaledPos = startPos.add(normalDeltaPos.scale((double)timer*pissspeed));
            ServerLevel world = (ServerLevel)owner.level;
            BlockPos center = new BlockPos(scaledPos);
            Block currentBlock = world.getBlockState(center).getBlock();

            AABB checkZone = new AABB(center).inflate(boxRadius + pissspeed);
            //zone to check for a hit
            AABB hitboxbox = new AABB(center).inflate(boxRadius);
            //actual hitbox

            List<LivingEntity> potentialVictims = world.getEntitiesOfClass(LivingEntity.class, checkZone);

            for (int v = 0; v < potentialVictims.size(); v ++){
                LivingEntity victim = potentialVictims.get(v);
                if (victim != owner) {
                    AABB targetbox = getAABB(victim.getX(), victim.getY(), victim.getZ(), victim);
                    if (targetbox.intersects(hitboxbox)) {
                        victim.hurt(DamageSource.mobAttack(owner), damage);
                        if (victim instanceof LivingEntity) {
                            victim.addEffect(new MobEffectInstance(MobEffects.WITHER, 50, 2));
                            //NOTE: THIS IS THE BLOOD PISS, THUS IT MUST INFLICT A NETHER STATUS
                        }
                        double dA = 0.2D * (1.0D - victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                        double dB = 1.0D * (1.0D - victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                        victim.push(normalDeltaPos.x() * dB, normalDeltaPos.y() * dA, normalDeltaPos.z() * dB);
                        //System.out.println();
                    }
                }
            }
            //harms anything it hits

            if (currentBlock.hasCollision == true && !(currentBlock instanceof LeavesBlock)){
                this.remove(RemovalReason.DISCARDED);
                //removes on colliding a block
            }
        }
    }

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
        AnimationController<EntityBloodWater> controller = new AnimationController<>(this, "controller", 1, this::predicate);
        data.addAnimationController(controller);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
