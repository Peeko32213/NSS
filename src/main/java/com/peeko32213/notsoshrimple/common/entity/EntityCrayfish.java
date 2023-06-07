package com.peeko32213.notsoshrimple.common.entity;

import com.peeko32213.notsoshrimple.common.entity.projectiles.EntityToxicWater;
import com.peeko32213.notsoshrimple.common.entity.utl.*;
import com.peeko32213.notsoshrimple.core.config.NotSoShrimpleConfig;
import com.peeko32213.notsoshrimple.core.registry.NSSEntities;
import com.peeko32213.notsoshrimple.core.registry.NSSSounds;
import com.peeko32213.notsoshrimple.core.registry.NSSTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.EnumSet;


public class EntityCrayfish extends Monster implements IAnimatable {
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(EntityCrayfish.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMBAT_STATE = SynchedEntityData.defineId(EntityCrayfish.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ENTITY_STATE = SynchedEntityData.defineId(EntityCrayfish.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityCrayfish.class, EntityDataSerializers.INT);
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public EntityCrayfish(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.maxUpStep = 1.0f;
    }

    @Override
    public boolean canDisableShield() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ARMOR, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.29D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10.5D)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 800D);


    }



    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EntityCrayfish.CrayfishMeleeAttackGoal(this, 1.2F, true));
        this.goalSelector.addGoal(3, new CustomRandomStrollGoal(this, 30, 1.0D, 100, 34));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false, entity -> entity.getType().is(NSSTags.CRAYFISH_VICTIMS)));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        //this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        boolean prev = super.canAttack(entity);
        return prev;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public void travel(Vec3 vec3d) {
        super.travel(vec3d);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setVariant(compound.getInt("Variant"));
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
        this.entityData.define(ANIMATION_STATE, 0);
        this.entityData.define(COMBAT_STATE, 0);
        this.entityData.define(ENTITY_STATE, 0);
    }

    @Override
    public void handleEntityEvent(byte pId) {
        super.handleEntityEvent(pId);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.horizontalCollision && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) && this.isAggressive()) {
            boolean flag = false;
            AABB axisalignedbb = this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D);
            for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(axisalignedbb.minX), Mth.floor(axisalignedbb.minY), Mth.floor(axisalignedbb.minZ), Mth.floor(axisalignedbb.maxX), Mth.floor(axisalignedbb.maxY), Mth.floor(axisalignedbb.maxZ))) {
                BlockState blockstate = this.level.getBlockState(blockpos);
                if (blockstate.is(NSSTags.CRAYFISH_BREAKABLES)) {
                    flag = this.level.destroyBlock(blockpos, true, this) || flag;
                }
            }
        }

    }

    public int getMaxHeadYRot() {
        return 45;
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.hasCustomName();
    }

    public boolean removeWhenFarAway(double d) {
        return !this.hasCustomName();
    }
    public int rangedCD = 100;

    public int getAnimationState() {

        return this.entityData.get(ANIMATION_STATE);
    }

    public void setAnimationState(int anim) {

        this.entityData.set(ANIMATION_STATE, anim);
    }

    public int getCombatState() {

        return this.entityData.get(COMBAT_STATE);
    }

    public void setCombatState(int anim) {

        this.entityData.set(COMBAT_STATE, anim);
    }

    public int getEntityState() {

        return this.entityData.get(ENTITY_STATE);
    }

    public void setEntityState(int anim) {

        this.entityData.set(ENTITY_STATE, anim);
    }

    static class CrayfishMeleeAttackGoal extends Goal {

        protected final EntityCrayfish mob;
        private final double speedModifier;
        private final boolean followingTargetEvenIfNotSeen;
        private Path path;
        private double pathedTargetX;
        private double pathedTargetY;
        private double pathedTargetZ;
        private int ticksUntilNextPathRecalculation;
        private int ticksUntilNextAttack;
        private int rangedAttackCD;
        private long lastCanUseCheck;
        private int failedPathFindingPenalty = 0;
        private boolean canPenalize = false;
        private int animTime = 0;
        /*private int targetVelocityTracker = 0;
        private double targetNowX;
        private double targetNowZ;
        private double targetNowY;
        private double targetWasX;
        private double targetWasZ;
        private double targetWasY;
        private double netDeltaX = 0;
        private double netDeltaZ = 0;
        private double netDeltaY = 0;
        private double resetLimit = 0.09;*/
        //speed the player has to be at before resetting the aimbot^

        public CrayfishMeleeAttackGoal(EntityCrayfish p_i1636_1_, double p_i1636_2_, boolean p_i1636_4_) {
            this.mob = p_i1636_1_;
            this.speedModifier = p_i1636_2_;
            this.followingTargetEvenIfNotSeen = p_i1636_4_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            long i = this.mob.level.getGameTime();

            if (i - this.lastCanUseCheck < 20L) {
                return false;
            } else {
                this.lastCanUseCheck = i;
                LivingEntity livingentity = this.mob.getTarget();
                if (livingentity == null) {
                    return false;
                } else if (!livingentity.isAlive()) {
                    return false;
                } else {
                    if (canPenalize) {
                        if (--this.ticksUntilNextPathRecalculation <= 0) {
                            this.path = this.mob.getNavigation().createPath(livingentity, 0);
                            this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                            return this.path != null;
                        } else {
                            return true;
                        }
                    }
                    this.path = this.mob.getNavigation().createPath(livingentity, 0);
                    if (this.path != null) {
                        return true;
                    } else {
                        return this.getAttackReachSqr(livingentity) >= this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                    }
                }
            }


        }



        public boolean canContinueToUse() {

            LivingEntity livingentity = this.mob.getTarget();

            if (livingentity == null) {
                return false;
            }
            else if (!livingentity.isAlive()) {
                return false;
            } else if (!this.followingTargetEvenIfNotSeen) {
                return !this.mob.getNavigation().isDone();
            } else if (!this.mob.isWithinRestriction(livingentity.blockPosition())) {
                return false;
            } else {
                return !(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player) livingentity).isCreative();
            }


        }

        public void start() {
            this.mob.getNavigation().moveTo(this.path, this.speedModifier);
            this.ticksUntilNextPathRecalculation = 0;
            this.ticksUntilNextAttack = 0;
            this.rangedAttackCD = 0;
            this.animTime = 0;
            this.mob.setAnimationState(0);
        }

        public void stop() {
            LivingEntity livingentity = this.mob.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                this.mob.setTarget((LivingEntity) null);
            }
            this.mob.setAnimationState(0);

        }

        public void tick() {


            LivingEntity target = this.mob.getTarget();
            double distance = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            double reach = this.getAttackReachSqr(target);
            int animState = this.mob.getAnimationState();
            Vec3 aim = this.mob.getLookAngle();
            Vec2 aim2d = new Vec2((float) (aim.x / (1 - Math.abs(aim.y))), (float) (aim.z / (1 - Math.abs(aim.y))));


            switch (animState) {
                case 21:
                    tickRightClawAttack();
                    break;
                case 22:
                    tickLeftClawAttack();
                    break;
                case 23:
                    tickSlamAttack();
                    break;
                case 24:
                    tickPiss();
                    break;
                default:
                    this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
                    this.ticksUntilNextAttack = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
                    this.rangedAttackCD = Math.max(this.rangedAttackCD - 1, 0);
                    this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                    this.doMovement(target, distance);
                    this.checkForCloseRangeAttack(distance, reach);
                    break;

            }
        }

        protected void doMovement (LivingEntity livingentity, Double d0){


            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);


            if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(livingentity)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || livingentity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = livingentity.getX();
                this.pathedTargetY = livingentity.getY();
                this.pathedTargetZ = livingentity.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                if (this.canPenalize) {
                    this.ticksUntilNextPathRecalculation += failedPathFindingPenalty;
                    if (this.mob.getNavigation().getPath() != null) {
                        Node finalPathPoint = this.mob.getNavigation().getPath().getEndNode();
                        if (finalPathPoint != null && livingentity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                            failedPathFindingPenalty = 0;
                        else
                            failedPathFindingPenalty += 10;
                    } else {
                        failedPathFindingPenalty += 10;
                    }
                }
                if (d0 > 1024.0D) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (d0 > 256.0D) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                if (!this.mob.getNavigation().moveTo(livingentity, this.speedModifier)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }
            }

        }


        protected void checkForCloseRangeAttack ( double distance, double reach){
            if (distance <= reach && this.ticksUntilNextAttack <= 0) {
                int r = this.mob.getRandom().nextInt(2048);
                if (r <= 600) {
                    this.mob.setAnimationState(21);
                } else if (r <= 800) {
                    this.mob.setAnimationState(22);
                } else if (r <= 1400) {
                    this.mob.setAnimationState(23);
                } else if (r <= 10000 && this.rangedAttackCD <=0) {
                    this.mob.setAnimationState(24);
                }

            } else if (distance > reach && this.ticksUntilNextAttack <= 0 && this.rangedAttackCD <= 0) {
                this.mob.setAnimationState(24);
            }
        }


        protected boolean getRangeCheck () {

            return
                    this.mob.distanceToSqr(this.mob.getTarget().getX(), this.mob.getTarget().getY(), this.mob.getTarget().getZ())
                            <=
                            1.8F * this.getAttackReachSqr(this.mob.getTarget());
        }



        protected void tickLeftClawAttack () {
            this.mob.lookAt(this.mob.getTarget(), 100000, 100000);
            this.mob.yBodyRot = this.mob.yHeadRot;
            animTime++;
            this.mob.getNavigation().stop();
            if(animTime==12) {
                preformLeftClawAttack();
            }
            if(animTime>=13) {
                animTime=0;

                this.mob.setAnimationState(0);
                this.resetAttackCooldown();
                this.ticksUntilNextPathRecalculation = 0;

            }

        }

        protected void tickRightClawAttack () {
            if (animTime <= 3) {
                this.mob.lookAt(this.mob.getTarget(), 100000, 100000);
                this.mob.yBodyRot = this.mob.yHeadRot;
            }
            animTime++;
            if(animTime==12) {
                preformRightClawAttack();
            }
            if(animTime>=16) {
                animTime=0;
                this.mob.setAnimationState(0);
                this.resetAttackCooldown();
                this.ticksUntilNextPathRecalculation = 0;
            }

        }
        protected void tickSlamAttack () {
            if (animTime <= 2) {
                this.mob.lookAt(this.mob.getTarget(), 100000, 100000);
                this.mob.yBodyRot = this.mob.yHeadRot;
            }
            animTime++;
            this.mob.getNavigation().stop();
            if(animTime==15) {
                performSlamAttack();
            }
            if(animTime>=17) {
                animTime=0;
                this.mob.setAnimationState(0);
                this.resetAttackCooldown();
                this.ticksUntilNextPathRecalculation = 0;
            }
        }

        protected void tickPiss (){;
            this.mob.getNavigation().stop();
            LivingEntity target = this.mob.getTarget();
            this.mob.lookAt(target, 100000, 100000);
            this.mob.yBodyRot = this.mob.yHeadRot;
            animTime ++;
//--------------------------------------------------------------------------------------------------------------------------------------
/*
            double distInit = Math.sqrt(Math.pow(target.getX() - this.mob.getX(), 2) + Math.pow(target.getZ() - this.mob.getZ(), 2));

            targetNowX = target.getX();
            targetNowZ = target.getZ();
            targetNowY = target.getY();
            //System.out.println("targetNow" + targetWasX + " " + targetWasZ + " " + targetWasY + " ");

            if (animTime == 1) {
                //if all the coords are 0(i.e. start of the function), store the current coords instead of the past coords and maintain them for a tick
                targetWasX = targetNowX;
                targetWasZ = targetNowZ;
                targetWasY = targetNowY;
                //System.out.println("zeroanimtime");
            }

            double thisDeltaX = Math.abs(targetWasX - targetNowX);
            double thisDeltaZ = Math.abs(targetWasZ - targetNowZ);
            double thisDeltaY = Math.abs(targetWasY - targetNowY);
            //System.out.println("delta " + (targetWasX - targetNowX) + " " + (targetWasZ - targetNowZ) + " " + (targetWasY - targetNowY) + " ");

            targetWasX = targetNowX;
            targetWasZ = targetNowZ;
            targetWasY = targetNowY;
            //System.out.println("targetWas" + targetNowX + " " + targetNowX + " " + targetNowX + " ");

            if (animTime != 1 && (thisDeltaX < resetLimit || target.isAlive() != true)) {
                //reset the calculation if the player stops moving or slows down significantly in any axis (less than resetlimit blocks per sec)
                //System.out.println("resetX" + thisDeltaX);
                targetNowX = target.getX();
                targetWasX = 0;
                netDeltaX = 0;
                thisDeltaX = 0;
                targetNowZ = target.getZ();
                targetWasZ = 0;
                netDeltaZ = 0;
                thisDeltaZ = 0;
                targetNowY = target.getY();
                targetWasY = 0;
                netDeltaY = 0;
                thisDeltaY = 0;
                //System.out.println("resetfull" + thisDeltaX);
            }

            if (animTime != 1 && (thisDeltaZ < resetLimit || target.isAlive() != true)) {
                //reset the calculation if the player stops moving or slows down significantly in any axis (less than .5 blocks per sec)
                //System.out.println("resetZ" + thisDeltaZ);
                targetNowX = target.getX();
                targetWasX = 0;
                netDeltaX = 0;
                thisDeltaX = 0;
                targetNowZ = target.getZ();
                targetWasZ = 0;
                netDeltaZ = 0;
                thisDeltaZ = 0;
                targetNowY = target.getY();
                targetWasY = 0;
                netDeltaY = 0;
                thisDeltaY = 0;
            }

            if (thisDeltaX < resetLimit || target.isAlive() != true) {
                //reset the calculation if the player stops moving or slows down significantly in the X axis (less than .5 blocks per sec)
                targetNowX = 0;
                targetWasX = 0;
                netDeltaX = 0;
                thisDeltaX = 0;
                System.out.println("resetX" + thisDeltaX);
            }

            if (thisDeltaZ < resetLimit || target.isAlive() != true) {
                //reset the calculation if the player stops moving or slows down significantly in the Y axis (less than .5 blocks per sec)
                targetNowZ = 0;
                targetWasZ = 0;
                netDeltaZ = 0;
                thisDeltaZ = 0;
                System.out.println("resetZ" + thisDeltaZ);
            }

            if (animTime != 1 && thisDeltaY < 1) {
                //reset the calculation if the player isn't jumping
                targetNowY = 0;
                targetWasY = 0;
                netDeltaY = 0;
                thisDeltaY = 0;
            }


            netDeltaX = netDeltaX += thisDeltaX;
            netDeltaZ = netDeltaZ += thisDeltaZ;
            netDeltaY = netDeltaY += thisDeltaY;
            //basically, just add all the displacement together and get an average displacement, and then add the displacement to each of the player axis to predict where it will go. If the player stands still for more than resetLimit ticks, reset.
*/
//--------------------------------------------------------------------------------------------------------------------------------------

            if (animTime==8) {
                /*double avgDeltaX = netDeltaX/8;
                double avgDeltaY = netDeltaY/8;
                double avgDeltaZ = netDeltaZ/8;*/

                piss(this.mob.getTarget(), target.getDeltaMovement());
                //System.out.println("net" + netDeltaY + " " + netDeltaX + " " + netDeltaZ);
                //System.out.println(avgDeltaY);

            }
            if(animTime>=12) {
                animTime=0;
                this.mob.setAnimationState(0);
                this.resetAttackCooldown();
                this.ticksUntilNextPathRecalculation = 0;
                this.rangedAttackCD = 20;//this.mob.getRandom().nextInt(100);;
                /*targetNowX = target.getX();
                targetNowY = target.getX();
                targetNowZ = target.getX();
                targetWasX = 0;
                targetWasY = 0;
                targetWasZ = 0;
                netDeltaX = 0;
                netDeltaY = 0;
                netDeltaZ = 0;*/
            }
        }

        //    "minecraft:vindicator",
        //    "minecraft:vex",
        //    "minecraft:illusioner",
        //    "minecraft:wither",
        //    "minecraft_piglin",
        //    "minecraft_piglin_brute"
        //    "minecraft:villager"
        //other victims

        protected void piss(LivingEntity target, Vec3 targetVelocity) {
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0));
            this.mob.yBodyRot = this.mob.yHeadRot;

            EntityToxicWater urine = new EntityToxicWater(NSSEntities.TOXICWATER.get(), this.mob.level);
            urine.setOwner(this.mob);
            urine.moveTo(this.mob.getX(), this.mob.getY()+2, this.mob.getZ());

            double pissspeed = 6;
            //blocks per second^
            Vec3 tStartPos = target.position();
            Vec3 tTempPos = tStartPos;

            for (int count = 0; count < 4; count++) {
                double distFlat = Math.sqrt(Math.pow(tTempPos.x, 2)+Math.pow(tTempPos.z, 2));
                double dist3D = Math.sqrt(Math.pow(distFlat, 2) + Math.pow(tTempPos.y, 2));
                double pissReachTime = distFlat/pissspeed;

                tTempPos = tTempPos.add(targetVelocity.multiply(pissReachTime, pissReachTime, pissReachTime));
            }
            //multiply target speed by time to get target position additive^

            /*double distFlat = Math.sqrt(Math.pow(tTempPos.x - this.mob.getX(), 2) + (Math.pow(tTempPos.z - this.mob.getZ(), 2)));
            double dist3D = Math.sqrt(Math.pow(distFlat, 2) + (Math.pow(tTempPos.y - this.mob.getY(), 2)));
            double pissReachTime = dist3D/pissspeed;
            Vec3 finalTargetPos = tTempPos.add(targetVelocity.multiply(pissReachTime, pissReachTime, pissReachTime));*/
            Vec3 finalTargetPos = tTempPos;

            double dx = finalTargetPos.x() - urine.getX();
            double dy = target.getY() + (target.getEyeHeight()*0.5) - urine.getY();// + finalTargetPos.y - urine.getY();
            double dz = finalTargetPos.z() - urine.getZ();

            //this.mob.lookAt(target, 100000, 100000);
            //this.mob.yBodyRot = this.mob.yHeadRot;
            urine.shoot(dx, dy, dz, (float) pissspeed, 0F);
            System.out.println("xyz" + dx + " " + dy + " " + dz);
            System.out.println("speed" + targetVelocity.x + " " + targetVelocity.y + " " + targetVelocity.z);
            this.mob.level.addFreshEntity(urine);
            //LightningBolt marker = EntityType.LIGHTNING_BOLT.create(this.mob.level);
            //marker.moveTo(finalTargetPos);
            //System.out.println("intent" + pos);
            //System.out.println("target" + target.position());
            //this.mob.level.addFreshEntity(marker);
        }

        protected void performSlamAttack() {
            Vec3 pos = mob.position();

            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0));
            this.mob.playSound(NSSSounds.CRAYFISH_ATTACK.get(), 0.5F, 0.5F);
            HitboxHelper.LargeAttack(DamageSource.mobAttack(mob),30.0f, 2.5f, mob, pos,  20.0F, -Math.PI/6, Math.PI/6, -1.0f, 3.0f);
        }


        protected void preformLeftClawAttack () {
            Vec3 pos = mob.position();
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0));
            this.mob.playSound(NSSSounds.CRAYFISH_ATTACK.get(), 0.5F, 0.5F);
            HitboxHelper.LargeAttack(DamageSource.mobAttack(mob),25.0f, 1.0f, mob, pos,  8.0F, -Math.PI/2, Math.PI/2, -1.0f, 3.0f);
        }

        protected void preformRightClawAttack () {
            Vec3 pos = mob.position();
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0));
            this.mob.playSound(NSSSounds.CRAYFISH_ATTACK.get(), 0.5F, 0.5F);
            HitboxHelper.LargeAttack(DamageSource.mobAttack(mob),15.0f, 1.0f, mob, pos,  8.0F, -Math.PI/2, Math.PI/2, -1.0f, 3.0f);
        }


        protected void resetAttackCooldown () {
            this.ticksUntilNextAttack = 5;
        }

        protected boolean isTimeToAttack () {
            return this.ticksUntilNextAttack <= 0;
        }

        protected int getTicksUntilNextAttack () {
            return this.ticksUntilNextAttack;
        }

        protected int getAttackInterval () {
            return 5;
        }

        protected double getAttackReachSqr(LivingEntity p_179512_1_) {
            return (double)(this.mob.getBbWidth() * 2.5F * this.mob.getBbWidth() * 1.8F + p_179512_1_.getBbWidth());
        }
    }


    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.FALL || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || source == DamageSource.CACTUS || source.isProjectile() || source.isFire() || super.isInvulnerableTo(source);
        //gec - removed void and magic damage immunity
    }

    protected SoundEvent getAmbientSound() {
        return NSSSounds.CRAYFISH_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return NSSSounds.CRAYFISH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return NSSSounds.CRAYFISH_DEATH.get();
    }

    protected void playStepSound(BlockPos p_28301_, BlockState p_28302_) {
        this.playSound(NSSSounds.CRAYFISH_SCUTTLE.get(), 0.3F, 1.0F);
    }

    /*
    public static void pointTowards(Vec3 targetcoords, LivingEntity entityinquestion){
        double xdiff = targetcoords.x - entityinquestion.getX();
        double zdiff = targetcoords.z - entityinquestion.getZ();
        double rvalue = 0;

        if (xdiff == 0) {
            if (zdiff == 0 || zdiff > 0) {
                rvalue = 0;
            } else if (zdiff < 0) {
                rvalue = 180;
            }
            entityinquestion.setYRot((float) rvalue);
            return;

        } else if (xdiff < 0) {
            if (zdiff == 0) {
                rvalue = 0;
            } else if (zdiff < 0) {
                rvalue = Math.toDegrees(Math.atan(xdiff/zdiff));
            } else if (zdiff > 0) {
                rvalue = 180 - Math.toDegrees(Math.atan(xdiff/zdiff));
            }

        } else if (xdiff > 0) {
            if (zdiff == 0) {
                rvalue = 0;
            } else if (zdiff < 0) {
                rvalue = -(Math.toDegrees(Math.atan(xdiff/zdiff)));
            } else if (zdiff > 0) {
                rvalue = -(180 - Math.toDegrees(Math.atan(xdiff/zdiff)));
            }
        }
        entityinquestion.setYRot((float) -(Math.toRadians(180 + rvalue)));
        //System.out.println("pointing" + (180 + rvalue));
        //System.out.println(Math.toDegrees(entityinquestion.getYRot()));
    }
    */

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        int animState = this.getAnimationState();
        {
            switch (animState) {

                case 21:
                    event.getController().setAnimation(new AnimationBuilder().playOnce("animation.lobster.rightclaw"));
                    break;

                case 22:
                    event.getController().setAnimation(new AnimationBuilder().playOnce("animation.lobster.leftclaw"));
                    break;

                case 23:
                    event.getController().setAnimation(new AnimationBuilder().playOnce("animation.lobster.slam"));
                    break;

                case 24:
                    event.getController().setAnimation(new AnimationBuilder().playOnce("animation.lobster.snipe"));
                    break;

                default:
                    if (!(event.getLimbSwingAmount() > -0.06F && event.getLimbSwingAmount() < 0.06F)) {
                        event.getController().setAnimation(new AnimationBuilder().loop("animation.lobster.walk"));

                    } else {
                        event.getController().setAnimation(new AnimationBuilder().loop("animation.lobster.idle"));
                        event.getController().setAnimationSpeed(1.0F);

                    }
                    break;

            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.setResetSpeedInTicks(1);
        AnimationController<EntityCrayfish> controller = new AnimationController<>(this, "controller", 3, this::predicate);
        data.addAnimationController(controller);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    private void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        int i;
        if(reason == MobSpawnType.SPAWN_EGG){
            i = this.getRandom().nextInt(4);
        }else{
            i = this.getRandom().nextInt(3);
        }
        this.setVariant(i);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }


    public static boolean canSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        return worldIn.getBlockState(pos.below()).canOcclude();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return NSSEntities.rollSpawn(NotSoShrimpleConfig.crayfishSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }



    protected PathNavigation createNavigation(Level p_33348_) {
        return new EntityCrayfish.RexNavigation(this, p_33348_);
    }

    static class RexNavigation extends GroundPathNavigation {
        public RexNavigation(Mob p_33379_, Level p_33380_) {
            super(p_33379_, p_33380_);
        }

        protected PathFinder createPathFinder(int p_33382_) {
            this.nodeEvaluator = new EntityCrayfish.RexNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, p_33382_);
        }
    }

    static class RexNodeEvaluator extends WalkNodeEvaluator {
        protected BlockPathTypes evaluateBlockPathType(BlockGetter p_33387_, boolean p_33388_, boolean p_33389_, BlockPos p_33390_, BlockPathTypes p_33391_) {
            return p_33391_ == BlockPathTypes.LEAVES ? BlockPathTypes.OPEN : super.evaluateBlockPathType(p_33387_, p_33388_, p_33389_, p_33390_, p_33391_);
        }
    }

}