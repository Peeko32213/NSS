package com.peeko32213.notsoshrimple.common.entity;

import com.peeko32213.notsoshrimple.common.entity.utl.*;
import com.peeko32213.notsoshrimple.core.config.NotSoShrimpleConfig;
import com.peeko32213.notsoshrimple.core.registry.NSSEntities;
import com.peeko32213.notsoshrimple.core.registry.NSSSounds;
import com.peeko32213.notsoshrimple.core.registry.NSSTags;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.ShipwreckStructure;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.data.ForgeBiomeTagsProvider;
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
import java.util.Random;

public class EntityManeaterShell extends Monster implements IAnimatable, SemiAquatic {
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(EntityManeaterShell.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMBAT_STATE = SynchedEntityData.defineId(EntityManeaterShell.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ENTITY_STATE = SynchedEntityData.defineId(EntityManeaterShell.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityCrayfish.class, EntityDataSerializers.INT);
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public Vec3 oldPos;
    public Vec3 newPos;
    public Vec3 velocity;
    public double directionlessSpeed;

//    public MobType getMobType() {
//        return MobType.WATER;
//    }
    //may be needed later

    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }
    //necessary for water spawning


    public EntityManeaterShell(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.maxUpStep = 1.0f;
        this.oldPos = this.position();
        this.newPos = this.position();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 2D)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 50D);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new EntityManeaterShell.ClamMeleeAttackGoal(this, 1F, true));
        this.goalSelector.addGoal(1, new FindWaterGoal(this));
        this.goalSelector.addGoal(1, new LeaveWaterGoal(this));
        this.goalSelector.addGoal(3, new BottomRoamGoal(this, 1.0D, 10, 50));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false, entity -> entity.getType().is(NSSTags.CRAYFISH_VICTIMS)));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        boolean prev = super.canAttack(entity);
        return prev;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public void tick() {
        //System.out.println("yesIexist");

        this.oldPos = this.newPos;
        this.newPos = this.position();
        this.velocity = this.newPos.subtract(this.oldPos);
        this.directionlessSpeed = Math.abs(Math.sqrt((velocity.x * velocity.x) + (velocity.z * velocity.z) + (velocity.z * velocity.z)));
        //calculates the clam's net speed
        super.tick();
    }


    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            if(this.jumping){
                this.setDeltaMovement(this.getDeltaMovement().scale(1.4D));
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.72D, 0.0D));
            }else{
                this.setDeltaMovement(this.getDeltaMovement().scale(0.4D));
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.08D, 0.0D));
            }

        } else {
            super.travel(travelVector);
        }

    }

    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        return worldIn.getFluidState(pos.below()).isEmpty() && worldIn.getFluidState(pos).is(FluidTags.WATER) ? 10.0F : super.getWalkTargetValue(pos, worldIn);
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
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setVariant(compound.getInt("Variant"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", getVariant());
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

    @Override
    public boolean shouldEnterWater() {
        return true;
    }

    @Override
    public boolean shouldLeaveWater() {
        return false;
    }

    @Override
    public boolean shouldStopMoving() {
        return false;
    }

    @Override
    public int getWaterSearchRange() {
        return 5;
    }


    static class ClamMeleeAttackGoal extends Goal {

        protected final EntityManeaterShell mob;
        private final int meleeRange = 350;
        private double speedModifier;
        private double baseModifier;
        private double chargeModifier = 2;
        private final boolean followingTargetEvenIfNotSeen;
        private Path path;
        private double pathedTargetX;
        private double pathedTargetY;
        private double pathedTargetZ;
        private int ticksUntilNextPathRecalculation;
        private int ticksUntilNextAttack;
        private long lastCanUseCheck;
        private int failedPathFindingPenalty = 0;
        private boolean canPenalize = false;
        private int animTime = 0;
        private double chargeSpeedScale = 0.01;

        private int chargeCD;
        private Vec3 chargeMotion = new Vec3(0,0,0);

        Vec3 biteOffSet = new Vec3(0, 1, 2);
        Vec3 strikeOffSet = new Vec3(0, 1.5, 3);
        Vec3 chargeOffSet = new Vec3(0, 1, 2);
        //z is fore - back, x is side - side



        public ClamMeleeAttackGoal(EntityManeaterShell theMob, double speedmod, boolean persistentMemory) {
            this.mob = theMob;
            this.speedModifier = speedmod;
            this.baseModifier = speedModifier;
            this.followingTargetEvenIfNotSeen = persistentMemory;
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
            } else if (this.mob.distanceToSqr(livingentity) >= 70){
                return false;
            } else if (!livingentity.isAlive()) {
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
            this.animTime = 0;
            this.chargeCD = 0;
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
                case 21 -> tickStrikeAttack();
                case 22 -> tickBiteAttack();
                case 23 -> tickCharge();
                default -> {
                    this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
                    this.ticksUntilNextAttack = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
                    this.chargeCD = Math.max(this.chargeCD - 1, 0);
                    this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                    this.doMovement(target, distance);
                    this.checkForCloseRangeAttack(distance);
                }

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


        protected void checkForCloseRangeAttack (double distance){
            if (distance <= meleeRange && this.ticksUntilNextAttack <= 0) {
                int r = (this.mob.getRandom().nextInt(100) + 1);
                //21 is strike, 22 is bite, 23 is charge

                if (distance <= 40) {
                    if (r <= 60) {
                        this.mob.setAnimationState(22);
                        //bite
                    } else if (60 < r && r <= 80) {
                        this.mob.setAnimationState(21);
                        //strike
                    } else if (r >= 80) {
                        this.mob.setAnimationState(23);
                        //strike
                    }
                    //if target is closer than 50, then have a higher chance to bite than strike
                    //60% chance for bite, 20% chance to strike, 20% chance to charge

                } else {
                    if (r <= 40) {
                        this.mob.setAnimationState(21);
                        //strike
                    } else if (40 < r && r <= 70) {
                        this.mob.setAnimationState(22);
                        //bite
                    } else if (r > 70) {
                        this.mob.setAnimationState(23);
                        //charge
                    }
                    //if target is further than 50, then have a higher chance to strike than bite, and can occasionally charge
                    //40% chance for strike, 30% chance to bite, 30% chance to charge
                }
            } else if (this.ticksUntilNextAttack <= 0 && this.chargeCD <= 0 && this.mob.isOnGround()) {
                this.mob.setAnimationState(23);
            }
        }


        protected boolean getRangeCheck () {

            return
                    this.mob.distanceToSqr(this.mob.getTarget().getX(), this.mob.getTarget().getY(), this.mob.getTarget().getZ())
                            <=
                            1.8F * this.getAttackReachSqr(this.mob.getTarget());
        }



        protected void tickBiteAttack () {
            animTime++;
            //this.mob.getNavigation().stop();
            LivingEntity target = this.mob.getTarget();
            this.mob.lookAt(target, 100000, 100000);
            this.mob.yBodyRot = this.mob.yHeadRot;

            if(animTime==14) {
                //System.out.println("bite");
                preformBiteAttack();
            }
            if(animTime>=26) {
                animTime=0;

                this.mob.setAnimationState(0);
                this.resetAttackCooldown();
                this.ticksUntilNextPathRecalculation = 0;
            }
            //Note: Due to the animations being layered, if the conclusion time of the tickBiteAttack function is not concurrent with the conclusion of the bite animation,
            //the entity will wait for the end of the function instead of the end of the animation to continue doing something else, creating a cooldown or stun - like effect

        }

        protected void tickStrikeAttack () {
            animTime++;
            //this.mob.getNavigation().stop();
            LivingEntity target = this.mob.getTarget();
            this.mob.lookAt(target, 100000, 100000);
            this.mob.yBodyRot = this.mob.yHeadRot;

            if(animTime==9) {
                //System.out.println("strike");
                preformStrikeAttack();
            }
            if(animTime>=10) {
                animTime=0;
                this.mob.setAnimationState(0);
                this.resetAttackCooldown();
                this.ticksUntilNextPathRecalculation = 0;
            }

        }

        protected void tickCharge () {
            animTime++;
            this.mob.getNavigation().stop();
            this.mob.lookAt(this.mob.getTarget(), 100000, 100000);
            this.mob.yBodyRot = this.mob.yHeadRot;

            if (animTime == 1) {
                this.mob.lookAt(this.mob.getTarget(), 100000, 100000);
                this.mob.yBodyRot = this.mob.yHeadRot;
                //look at the target
                Entity target = this.mob.getTarget();
                Vec3 targetPos = (target.position());
                //gets target data

                double x = -((this.mob.position().x - targetPos.x));
                double z = -((this.mob.position().z - targetPos.z));
                this.chargeMotion = new Vec3(x, this.mob.getDeltaMovement().y, z).normalize();
                //Find the direction of the charge and sets charge speed with speed scale. chargeMotion contains the speed and direction. the multiply scales the charge to be not too fast.
            }

            if(animTime >= 8 && animTime < 20) {
                this.mob.setDeltaMovement(chargeMotion.x/2, this.mob.getDeltaMovement().y, chargeMotion.z/2);
                //Only start moving after tick 8(after it's charged up).

                PisslikeHitboxes.PivotedPolyHitCheck(this.mob, this.chargeOffSet, 1.2, 3, 1.2, (ServerLevel) this.mob.getLevel(), 14,
                        DamageSource.mobAttack(this.mob), 4, false);
                //keep hitting as long as it's moving
            }

            if(animTime>=24) {
                animTime=0;
                this.mob.setAnimationState(0);
                this.resetAttackCooldown();
                this.ticksUntilNextPathRecalculation = 0;
                this.chargeCD = this.mob.getRandom().nextInt(200);;
            }

        }

        protected void preformBiteAttack () {
            Vec3 pos = mob.position();
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0));
            this.mob.playSound(NSSSounds.CRAYFISH_ATTACK.get(), 0.5F, 0.5F);
            PisslikeHitboxes.PivotedPolyHitCheck(this.mob, this.biteOffSet, 2, 1, 2, (ServerLevel)this.mob.getLevel(), 12,
                    DamageSource.mobAttack(this.mob), 1, false);
        }

        protected void preformStrikeAttack () {
            Vec3 pos = mob.position();
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0));
            this.mob.playSound(NSSSounds.CRAYFISH_ATTACK.get(), 0.5F, 0.5F);
            PisslikeHitboxes.PivotedPolyHitCheck(this.mob, this.strikeOffSet, 1.2, 2, 1.2, (ServerLevel)this.mob.getLevel(), 8,
                    DamageSource.mobAttack(this.mob), 1.5, false);
        }


        protected void resetAttackCooldown () {
            this.ticksUntilNextAttack = 0;
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

        protected double getAttackReachSqr(LivingEntity entity) {
            return (double)(this.mob.getBbWidth() * 2.5F * this.mob.getBbWidth() * 1.8F + entity.getBbWidth());
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {

        if (source.getDirectEntity() instanceof AbstractArrow) {
            AbstractArrow hitter = (AbstractArrow) source.getDirectEntity();

            if (this.getAnimationState() == 22) {
                return false;
            }

            if (hitter.position().y >= this.position().y() + 2) {
                return true;
            }
            //checks whether an arrow hits the entity above its armour(the +2 at the end indicates how high up the armour is) and returns true if it is
        }

        return source == DamageSource.FALL ||
                source == DamageSource.IN_WALL ||
                source == DamageSource.FALLING_BLOCK ||
                source == DamageSource.CACTUS ||
                super.isInvulnerableTo(source);
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

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        int animState = this.getAnimationState();
        {
            switch (animState) {

                default:
                    if (!(event.getLimbSwingAmount() > -0.06F && event.getLimbSwingAmount() < 0.06F) && (!this.isUnderWater() || (this.isUnderWater() && this.isOnGround()))) {
                        event.getController().setAnimationSpeed(1 + (this.directionlessSpeed/0.20));
                        //scales the animation speed by dividing net speed by base speed
                        event.getController().setAnimation(new AnimationBuilder().loop("animation.maneater_shell.walk"));

                    } else {
                        event.getController().setAnimation(new AnimationBuilder().loop("animation.maneater_shell.idle"));
                        event.getController().setAnimationSpeed(1.0F);

                    }
                    break;

            }
        }
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState atkState(AnimationEvent<E> event) {
        int animState = this.getAnimationState();
        {
            switch (animState) {

                case 21:
                    event.getController().setAnimation(new AnimationBuilder().playOnce("animation.maneater_shell.strike"));
                    event.getController().setAnimationSpeed(1F);
                    break;
                case 22:
                    event.getController().setAnimation(new AnimationBuilder().playOnce("animation.maneater_shell.bite"));
                    event.getController().setAnimationSpeed(1F);
                    break;
                case 23:
                    event.getController().setAnimation(new AnimationBuilder().playOnce("animation.maneater_shell.ram"));
                    event.getController().setAnimationSpeed(1F);
                    break;
                default:
                    event.getController().setAnimation(new AnimationBuilder().playOnce("animation.maneater_shell.empty"));
                    event.getController().setAnimationSpeed(1F);
                    break;

            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.setResetSpeedInTicks(1);
        AnimationController<EntityManeaterShell> controller = new AnimationController<>(this, "controller", 1, this::predicate);
        AnimationController<EntityManeaterShell> atkController = new AnimationController<>(this, "atkcontroller", 1, this::atkState);
        data.addAnimationController(controller);
        data.addAnimationController(atkController);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
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
        int rarityRoll = (this.getRandom().nextInt(100) + 1);
        //since it uses nextIntBetweenInclusive you just take the max and min texture values and put it in without changing anything
        //i.e. the blood selection in CrayfishModel ranges from 4 - 5, so you put that in

        int i;
        if (rarityRoll > 90) {
            i = 9;
        } else if (rarityRoll > 80 && rarityRoll <= 90) {
            i = 8;
        } else if (rarityRoll > 70 && rarityRoll <= 80) {
            i = 7;
        } else if (rarityRoll > 60 && rarityRoll <= 70) {
            i = 6;
        } else if (rarityRoll > 50 && rarityRoll <= 60) {
            i = 5;
        } else if (rarityRoll > 40 && rarityRoll <= 50) {
            i = 4;
        } else if (rarityRoll > 30 && rarityRoll <= 40) {
            i = 3;
        } else if (rarityRoll > 20 && rarityRoll <= 30) {
            i = 2;
        } else if (rarityRoll > 10 && rarityRoll <= 20) {
            i = 1;
        } else {
            i = 0;
        }

        if(reason == MobSpawnType.STRUCTURE){
            this.restrictTo(this.blockPosition(), 15);
        }

        this.setVariant(i);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        //TODO: implement proper spawning(spawns around shipwrecks and swamp huts)
    }

    public static boolean canSpawn(EntityType<EntityManeaterShell> type, ServerLevelAccessor levelAccessor, MobSpawnType p_32352_, BlockPos pos, RandomSource random) {

        Boolean biomeRight = ((LevelAccessor)levelAccessor).getBiome(pos).is(NSSTags.MANEATERSPAWNS);

        if (!levelAccessor.getFluidState(pos.below()).is(FluidTags.WATER)) {
            return false;
        } else {
            System.out.println(biomeRight);
            System.out.println(levelAccessor.getBiome(pos));
            System.out.println(NSSTags.MANEATERSPAWNS);
            return levelAccessor.getDifficulty() != Difficulty.PEACEFUL && levelAccessor.getFluidState(pos).is(FluidTags.WATER) && biomeRight == true;
        }
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return NSSEntities.rollSpawn(NotSoShrimpleConfig.maneaterRolls, this.getRandom(), spawnReasonIn);
    }

    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }


}
