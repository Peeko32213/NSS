package com.peeko32213.notsoshrimple.common.entity;

import com.peeko32213.notsoshrimple.common.entity.utl.*;
import com.peeko32213.notsoshrimple.core.config.NotSoShrimpleConfig;
import com.peeko32213.notsoshrimple.core.registry.NSSEntities;
import com.peeko32213.notsoshrimple.core.registry.NSSSounds;
import com.peeko32213.notsoshrimple.core.registry.NSSTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
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

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }
    public double oldSpeedMod;
    public Vec3 oldPos;
    public Vec3 newPos;
    public Vec3 velocity;
    public double directionlessSpeed;
    public int biomeVariant;
    //0 = swamp, 1 = ice, 2 = blood for biomeVariant;


    public EntityCrayfish(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.maxUpStep = 1.0f;
        this.oldPos = this.position();
        this.newPos = this.position();
    }

    /*@Override
    public void maybeDisableShield(Player pPlayer, ItemStack pMobItemStack, ItemStack pPlayerItemStack) {
        if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD) && this.willItBreak == true) {
            float f = 0.25F + (float) EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
            if (this.random.nextFloat() < f) {
                pPlayer.getCooldowns().addCooldown(Items.SHIELD, 100);
                this.level.broadcastEntityEvent(pPlayer, (byte)30);
            }
        }

    }*/

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ARMOR, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                //0.29 fits the animation, remember to slow it
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10.5D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 70D);
        //health nerfed
        //armour buffed
        //we don't need knockback and damage tbh
        //follow range affect tickrate, thus nerfed
    }



    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new EntityCrayfish.CrayfishMeleeAttackGoal(this, 2F, true));
        //this is pretty much the same as UP's rexMeleeAttackGoal
        this.goalSelector.addGoal(3, new CustomRandomStrollGoal(this, 30, 1.0D, 100, 34));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this){
                    public boolean canUse() {
                        return (this.mob.getLastHurtByMob() instanceof EntityCrayfish);
                    }
            }
        ));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false, entity -> entity.getType().is(NSSTags.CRAYFISH_VICTIMS)));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        //perhaps instead of calculating the whole route each tick, only calculate the amount of blocks to be covered in a single tick and recalculate each tick?
    }

    public boolean canBreatheUnderwater() {
        return true;
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
    public void tick() {

        /*loat speed = (float) (this.getMoveControl().getSpeedModifier() * this.getAttributeValue(Attributes.MOVEMENT_SPEED));
        if (this.isInWater()) {
            speed = speed * (1/this.getWaterSlowDown());
        }
        this.setSpeed(supposedd);*/

        this.oldPos = this.newPos;
        this.newPos = this.position();
        this.velocity = this.newPos.subtract(this.oldPos);
        this.directionlessSpeed = Math.abs(Math.sqrt((velocity.x * velocity.x) + (velocity.z * velocity.z) + (velocity.z * velocity.z)));
        //System.out.println(this.directionlessSpeed);
        super.tick();
    }

    @Override
    protected float getWaterSlowDown() {
        return 1.0f;
        //shrimps are not slowed by water
    }

    @Override
    public void customServerAiStep() {

        if (this.getMoveControl().hasWanted()) {
            this.setSprinting(this.getMoveControl().getSpeedModifier() >= 1.5D);
        } else {
            this.setSprinting(false);
        }
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.horizontalCollision && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) && this.isSprinting()) {
            boolean flag = false;
            AABB axisalignedbb = this.getBoundingBox().inflate(0.2D);
            for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(axisalignedbb.minX), Mth.floor(axisalignedbb.minY), Mth.floor(axisalignedbb.minZ), Mth.floor(axisalignedbb.maxX), Mth.floor(axisalignedbb.maxY), Mth.floor(axisalignedbb.maxZ))) {
                BlockState blockstate = this.level.getBlockState(blockpos);
                if (blockstate.is(NSSTags.CRAYFISH_BREAKABLES)) {
                    flag = this.level.destroyBlock(blockpos, true, this) || flag;
                }
            }
        }

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

    static class CrayfishMeleeAttackGoal extends Goal {
        //max attack cooldown is 20 ticks

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

        private double targetOldX;
        private double targetOldY;
        private double targetOldZ;
        private int meleeRange = 75;
        //the range(blocks) in which the shrimp would stay in melee mode. Adjust until perfect.
        //this is NOT IN BLOCKS. I DO NOT KNOW WHAT UNIT THIS IS.
        //update: this is in squared distance

        Vec3 slamOffSet = new Vec3(0, 0, 4);
        Vec3 pokeOffSet = new Vec3(0, 0.25, 5);
        Vec3 slashOffSet = new Vec3(0, -0.3, 2);
        Vec3 pissOffSet = new Vec3(0, 2, 2);
        //the Y value is at the BOTTOM of the offset, and the hitbox is inflated up.
        //the Z value dictates forwards/backwards.
        //inflation acts on both sides of the thing.


        public CrayfishMeleeAttackGoal(EntityCrayfish theMob, double speedMod, boolean persistentMemory) {
            this.mob = theMob;
            this.speedModifier = speedMod;
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

            if (livingentity == null || livingentity instanceof EntityCrayfish) {
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
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity) || livingentity instanceof EntityCrayfish) {
                this.mob.setTarget(null);
            }
            this.mob.setAnimationState(0);
        }

        /*public void tick() {
            LivingEntity target = this.mob.getTarget();
            double distance = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            int animState = this.mob.getAnimationState();
            //Vec3 aim = this.mob.getLookAngle();
            //Vec2 aim2d = new Vec2((float) (aim.x / (1 - Math.abs(aim.y))), (float) (aim.z / (1 - Math.abs(aim.y))));


            switch (animState) {
                case 21 -> tickRightClawAttack();
                case 22 -> tickLeftClawAttack();
                case 23 -> tickSlamAttack();
                case 24 -> tickPiss();

                default -> {
                    this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
                    this.ticksUntilNextAttack = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
                    this.rangedAttackCD = Math.max(this.rangedAttackCD - 1, 0);
                    this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                    this.doMovement(target, distance);
                    this.checkForCloseRangeAttack(distance, meleeRange);
                    //break;
                }

            }
        }*/

        public void tick() {
            LivingEntity target = this.mob.getTarget();
            double distance = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            //double reach = this.getAttackReachSqr(target);
            int animState = this.mob.getAnimationState();
            //Vec3 aim = this.mob.getLookAngle();

            switch (animState) {
                case 21 -> tickRightClawAttack();
                case 22 -> tickLeftClawAttack();
                case 23 -> tickSlamAttack();
                case 24 -> {
                    this.mob.lookAt(this.mob.getTarget(), 100000, 100000);
                    this.mob.yBodyRot = this.mob.yHeadRot;
                    tickPiss();
                }

                default -> {
                    this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
                    this.ticksUntilNextAttack = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
                    this.rangedAttackCD = Math.max(this.rangedAttackCD - 1, 0);
                    this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                    this.doMovement(target, distance);
                    this.checkForCloseRangeAttack(distance, meleeRange);
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


        protected void checkForCloseRangeAttack ( double distance, double meleeRange){//TODO: LAST LEFT HERE
            if (distance <= meleeRange && this.ticksUntilNextAttack <= 0) {
                int r = (this.mob.getRandom().nextInt(100) + 1);

                //rarity should be equal for stab and sweep but much less for slam
                //23 is slam, 22 is sweep, 21 is stab
                if (Math.abs(this.mob.getTarget().getY() - this.mob.getY()) >= 1){
                    if (r <= 70) {
                        this.mob.setAnimationState(23);
                        //slam
                    } else{
                        this.mob.setAnimationState(24);
                        //piss
                    }
                    //if the mob is above itself, try pissing or slamming
                }

                else if (distance <= 50) {
                    if (r <= 70) {
                        this.mob.setAnimationState(21);
                        //stab
                    } else if (70 < r && r <= 90) {
                        this.mob.setAnimationState(22);
                        //slash
                    } else { //if r > 90
                        this.mob.setAnimationState(23);
                        //slam
                    }
                    //if target is closer than 50, then have a higher chance to slash than stab
                    //70% chance for slash, 20% chance to stab, 10% chance to slam

                } else if (distance <= 100) {
                    if (50 < r && r <= 80) {
                        this.mob.setAnimationState(21);
                        //stab
                    } else if (r <= 50) {
                        this.mob.setAnimationState(22);
                        //slash
                    } else {
                        this.mob.setAnimationState(23);
                        //slam
                    }
                    //if target is closer than 50, then have a higher chance to slash than stab
                    //50% chance for stab, 30% chance to slash, 20% chance to slam

                }
                //originally 400, 1000, 1400 for r

            } else if (this.ticksUntilNextAttack <= 0 && this.rangedAttackCD <= 0) {
                this.mob.setAnimationState(24);
                //piss if target too far
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
            this.mob.getNavigation().stop();
            animTime++;

            if(animTime==16) {
                performLeftClawAttack();
            }

            if(animTime>=24) {
                animTime=0;

                this.mob.setAnimationState(0);
                this.resetAttackCooldown(15);
                this.ticksUntilNextPathRecalculation = 0;

            }

        }

        protected void tickRightClawAttack () {
            if (animTime <= 3) {
                this.mob.lookAt(this.mob.getTarget(), 100000, 100000);
                this.mob.yBodyRot = this.mob.yHeadRot;
            }

            this.mob.getNavigation().stop();
            animTime++;

            /*if(animTime==10) {
                performRightClawAttack();
            }*/

            if(animTime==6) {
                performRightClawAttack();
            }

            if(animTime>=12) {
                animTime=0;
                this.mob.setAnimationState(0);
                this.resetAttackCooldown(5);
                this.ticksUntilNextPathRecalculation = 0;
            }

        }
        protected void tickSlamAttack () {
            animTime++;
            this.mob.getNavigation().stop();

            if (animTime <= 3) {
                this.mob.lookAt(this.mob.getTarget(), 100000, 100000);
                this.mob.yBodyRot = this.mob.yHeadRot;
            }

            if(animTime==19) {
                performSlamAttack();
            }

            if(animTime>=24) {
                animTime=0;
                this.mob.setAnimationState(0);
                this.resetAttackCooldown(20);
                this.ticksUntilNextPathRecalculation = 0;
            }
        }

        protected void tickPiss (){;
            this.mob.getNavigation().stop();
            LivingEntity target = this.mob.getTarget();
            this.mob.lookAt(target, 100000, 100000);
            this.mob.yBodyRot = this.mob.yHeadRot;
            animTime ++;

            if (animTime==10) {
                piss(target);

            } else {
                this.targetOldX = target.getX();
                this.targetOldY = target.getY();
                this.targetOldZ = target.getZ();
                //only update the old coords after confirming they won't be used
            }

            if(animTime>=18) {
                animTime=0;
                this.mob.setAnimationState(0);
                this.resetAttackCooldown(mob.random.nextInt(1, 21));
                this.ticksUntilNextPathRecalculation = 0;
                this.rangedAttackCD = this.mob.getRandom().nextInt(200);;
            }
        }

        //TODO: replace piss particles

        protected void piss(LivingEntity target) {
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0));
            this.mob.getLookControl().setLookAt(target.position());
            this.mob.yBodyRot = this.mob.yHeadRot;
            double pissspeed = 7;
            //MAKE SURE THIS IS THE SAME NUMBER AS EntityToxicWater's pissspeed
            double pissspeedforcalculation = pissspeed - 1;
            //slight delay to tune it
            Vec3 targetVelocity = new Vec3((target.getX() - targetOldX),(target.getY() - targetOldY),(target.getZ() - targetOldZ));

            Vec3 tStartPos = target.position();
            Vec3 tTempPos = tStartPos;

            for (int count = 0; count < 1; count++) {
                double flatDist = Math.sqrt((this.mob.getX() - target.getX())*(this.mob.getX() - target.getX()) + (this.mob.getZ() - target.getZ())*(this.mob.getZ() - target.getZ()));
                double tallDist = Math.sqrt(flatDist*flatDist + (this.mob.getY() + 2 - target.getY())*(this.mob.getY() + 2 - target.getY()));
                //use urine.getwhatevercoordinate if this.mob.getwhatevercoordinate makes it worse
                double pissReachTime = tallDist/pissspeedforcalculation;
                tTempPos = tTempPos.add(targetVelocity.multiply(pissReachTime - (0.1*target.distanceTo(this.mob)), 0, pissReachTime - (0.1*target.distanceTo(this.mob))));

                if (tTempPos.distanceTo(target.position()) <= 1) {
                    break;
                }
            }

            Vec3 finalTargetPos = tTempPos.add(0,target.getEyeHeight()*0.5,0);

            if (this.mob.biomeVariant == 2){
                EntityBloodWater urine = new EntityBloodWater(NSSEntities.BLOODWATER.get(), this.mob.level);
                urine.setOwner(this.mob);
                urine.setInvisible(true);
                urine.moveTo(this.mob.getX(), this.mob.getY() + 2, this.mob.getZ());
                urine.setTargetPos(finalTargetPos);
                this.mob.level.addFreshEntity(urine);

            } else if (this.mob.biomeVariant == 1){
                EntityIceWater urine = new EntityIceWater(NSSEntities.ICEWATER.get(), this.mob.level);
                urine.setOwner(this.mob);
                urine.setInvisible(true);
                urine.moveTo(this.mob.getX(), this.mob.getY() + 2, this.mob.getZ());
                urine.setTargetPos(finalTargetPos);
                this.mob.level.addFreshEntity(urine);

            } else {
                EntityToxicWater urine = new EntityToxicWater(NSSEntities.TOXICWATER.get(), this.mob.level);
                urine.setOwner(this.mob);
                urine.setInvisible(true);
                urine.moveTo(this.mob.getX(), this.mob.getY() + 2, this.mob.getZ());
                urine.setTargetPos(finalTargetPos);
                this.mob.level.addFreshEntity(urine);
            }

            PisslikeHitboxes.PivotedPolyHitCheck(this.mob, this.pissOffSet, 2f, 1.5f, 2f, (ServerLevel)this.mob.getLevel(), 10f, DamageSource.mobAttack(mob), 2f, true);

            this.mob.lookAt(target, 100000, 100000);
            this.mob.yBodyRot = this.mob.yHeadRot;
            //LightningBolt marker = EntityType.LIGHTNING_BOLT.create(this.mob.level);
            //marker.moveTo(finalTargetPos);
            //System.out.println("intent" + pos);
            //System.out.println("target" + target.position());
            //this.mob.level.addFreshEntity(marker);
        }

        protected void performSlamAttack() {
            //Vec3 pos = mob.position();
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0));
            this.mob.playSound(NSSSounds.CRAYFISH_ATTACK.get(), 0.5F, 0.5F);
            //this.mob.willItBreak = true;
            //HitboxHelper.LargeAttack(DamageSource.mobAttack(mob),5.0f, 1.5f, mob, pos,  80.0F, -Math.PI/6, Math.PI/6, -1.0f, 3.0f);
            ServerLevel serverLevel = ((ServerLevel) this.mob.getLevel());
            PisslikeHitboxes.PivotedPolyHitCheck(this.mob, this.slamOffSet, 3f, 3f, 3f, (ServerLevel)this.mob.getLevel(), 15f, DamageSource.mobAttack(mob), 2f, true);

            //TODO: Send a packet for slam particles
            //THIS METHOD IS ONLY RAN ON THE SERVERSIDE.
        }



        protected void performLeftClawAttack () {
            //Vec3 pos = mob.position();
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0));
            this.mob.playSound(NSSSounds.CRAYFISH_ATTACK.get(), 0.5F, 0.5F);
            //this.mob.willItBreak = false;
            PisslikeHitboxes.PivotedPolyHitCheck(this.mob, this.slashOffSet, 5.5f, 1f, 5.5f, (ServerLevel)this.mob.getLevel(), 10f, DamageSource.mobAttack(mob), 3f, false);
            //HitboxHelper.LargeAttack(DamageSource.mobAttack(mob),5.0f, 1.0f, mob, pos,  6.0F, -Math.PI/2, Math.PI/2, -1.0f, 3.0f);

            //TODO: Send a packet for slam particles
            //THIS METHOD IS ONLY RAN ON THE SERVERSIDE.
        }

        protected void performRightClawAttack () {
            //Vec3 pos = mob.position();
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0));
            this.mob.playSound(NSSSounds.CRAYFISH_ATTACK.get(), 0.5F, 0.5F);
            //this.mob.willItBreak = false;
            PisslikeHitboxes.PivotedPolyHitCheck(this.mob, this.pokeOffSet, 0.5f, 2f, 0.5f, (ServerLevel)this.mob.getLevel(), 10f, DamageSource.mobAttack(mob), 0.3f, false);
            //HitboxHelper.LargeAttack(DamageSource.mobAttack(mob),5.0f, 1.0f, mob, pos,  6.0F, -Math.PI/2, Math.PI/2, -1.0f, 3.0f);

            //TODO: Send a packet for slam particles
            //THIS METHOD IS ONLY RAN ON THE SERVERSIDE.
        }


        protected void resetAttackCooldown ( int CD ) {
            this.ticksUntilNextAttack = CD;
        }
        //sets how many ticks until the next attack

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

    /*public boolean canBeCollidedWith() {
        return true;
    }*/

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        boolean blowthrough = false;
        if (source.getDirectEntity() instanceof AbstractArrow) {
            if (((AbstractArrow) source.getDirectEntity()).getPierceLevel() >= 1) {
                blowthrough = true;
            }
        }
        return source == DamageSource.FALL ||
                source == DamageSource.IN_WALL ||
                source == DamageSource.CACTUS ||
                (source.isProjectile() && !blowthrough) ||
                (source.isFire() && this.biomeVariant == 2) ||
                super.isInvulnerableTo(source);
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
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

                case 21:
                    event.getController().setAnimationSpeed(0.9F);
                    event.getController().setAnimation(new AnimationBuilder().playOnce("animation.crayfish.rightclaw"));
                    break;

                case 22:
                    event.getController().setAnimationSpeed(0.8F);
                    event.getController().setAnimation(new AnimationBuilder().playOnce("animation.crayfish.leftclaw"));
                    break;

                case 23:
                    event.getController().setAnimationSpeed(0.8F);
                    event.getController().setAnimation(new AnimationBuilder().playOnce("animation.crayfish.slam"));
                    break;

                case 24:
                    event.getController().setAnimationSpeed(0.8F);
                    event.getController().setAnimation(new AnimationBuilder().playOnce("animation.crayfish.snipe"));
                    break;

                default:
                    if (!(event.getLimbSwingAmount() > -0.06F && event.getLimbSwingAmount() < 0.06F)) {
                        event.getController().setAnimationSpeed(1 + (this.directionlessSpeed/0.24));
                        event.getController().setAnimation(new AnimationBuilder().loop("animation.crayfish.walk"));
                        //default speed is 0.24

                    } else {
                        event.getController().setAnimationSpeed(1.0);
                        event.getController().setAnimation(new AnimationBuilder().loop("animation.crayfish.idle"));
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

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    private void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    private static boolean canSpawnBlood(LevelAccessor worldIn, BlockPos position) {
        return worldIn.getBiome(position).is(NSSTags.SPAWNS_BLOOD_CRAYFISH);
    }

    private static boolean canSpawnIce(LevelAccessor worldIn, BlockPos position) {
        return worldIn.getBiome(position).is(NSSTags.SPAWNS_ICE_CRAYFISH);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        int rarityRoll = (this.getRandom().nextInt(100) + 1);
        //since it uses nextIntBetweenInclusive you just take the max and min texture values and put it in without changing anything
        //i.e. the blood selection in CrayfishModel ranges from 4 - 5, so you put that in

        int i;
        if(canSpawnBlood(worldIn, this.blockPosition())){
            if (rarityRoll >= 100) {
                i = 14;
                //System.out.println("rareblood");
                //1% chance to get a rare crayfish
            } else if (rarityRoll > 90) {
                i = 13;
                //System.out.println("uncblood");
                //9% chance to get an uncommon crayfish
            } else {
                i = this.random.nextIntBetweenInclusive(10, 12);
                //System.out.println("stndblood");
                //90% chance to get a standard crayfish
            }
            this.biomeVariant = 2;
            //2 for blood

        } else if(canSpawnIce(worldIn, this.blockPosition())){
            if (rarityRoll >= 100) {
                i = 9;
                //System.out.println("rareice");
                //1% chance to get a rare crayfish
            } else if (rarityRoll > 90) {
                i = 8;
                //System.out.println("uncice");
                //9% chance to get an uncommon crayfish
            } else {
                i = this.random.nextIntBetweenInclusive(5, 7);
                //System.out.println("stndice");
                //90% chance to get a standard crayfish
            }
            this.biomeVariant = 1;
            //1 for ice;

        } else {
            if (rarityRoll >= 100) {
                i = 4;
                //System.out.println("rareswamp");
                //1% chance to get a rare crayfish
            } else if (rarityRoll > 90) {
                i = 3;
                //System.out.println("uncswamp");
                //9% chance to get an uncommon crayfish
            } else {
                i = this.random.nextIntBetweenInclusive(0, 2);
                //System.out.println("stndswamp");
                //90% chance to get a standard crayfish
            }
            this.biomeVariant = 0;
            //0 for swamp
        }

        this.setVariant(i);
        //System.out.println(i);
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
        return new RexNavigation(this, p_33348_);
        //used to use LargeEntityGroundNavigator
    }

    static class RexNavigation extends GroundPathNavigation {
        public RexNavigation(Mob mob, Level level) {
            super(mob, level);
        }

        protected PathFinder createPathFinder(int maxVisitedNodes) {
            this.nodeEvaluator = new EntityCrayfish.RexNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
        }
    }

    static class RexNodeEvaluator extends WalkNodeEvaluator {
        protected BlockPathTypes evaluateBlockPathType(BlockGetter p_33387_, boolean p_33388_, boolean p_33389_, BlockPos p_33390_, BlockPathTypes p_33391_) {
            return p_33391_ == BlockPathTypes.LEAVES ? BlockPathTypes.OPEN : super.evaluateBlockPathType(p_33387_, p_33388_, p_33389_, p_33390_, p_33391_);
        }
    }

}