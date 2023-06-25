package com.peeko32213.notsoshrimple.common.entity.utl;

import com.peeko32213.notsoshrimple.core.registry.NSSParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class PisslikeHitboxes {
    //Essentially, this makes the most basic hitbox system, where if the root of the player is within a certain radius to the hit point, it hits them.

    public static void PivotedRadialHitCheck(PathfinderMob source, Vec3 boxOffset, double attackRadius, ServerLevel world, float damage, DamageSource damageSource, float knockback, boolean disableShield) {
        //attackRadius is in blocks


        Vec2 knockVec = MathHelpers.OrizontalAimVector(
                MathHelpers.AimVector(new Vec3(-source.position().x, -source.position().y, -source.position().z),
                        new Vec3(-source.getTarget().position().x, -source.getTarget().position().y, -source.getTarget().position().z)
                ));

        Vec3 sourcePos = source.position();
        double entityAngle = (source.getYRot());
        Vec3 truePos = sourcePos.add(boxOffset);
        double[] trueXZ = {truePos.x, truePos.z};

        AffineTransform.getRotateInstance(Math.toRadians(entityAngle), sourcePos.x, sourcePos.z).transform(trueXZ, 0, trueXZ, 0, 1);
        double[] transformedTrueXY = trueXZ;
        Vec3 rotatedPos = new Vec3(transformedTrueXY[0], truePos.y, transformedTrueXY[1]);



        BlockPos finalPos = new BlockPos(rotatedPos);
        AABB Hitbox = new AABB(finalPos).inflate(attackRadius);
        hitboxOutline(Hitbox, world);
        world.sendParticles(ParticleTypes.EXPLOSION, rotatedPos.x,rotatedPos.y,rotatedPos.z, 1, 0, 0, 0, 0);
        List<LivingEntity> victims = new ArrayList<>(world.getEntitiesOfClass(LivingEntity.class, Hitbox));

        for (int i = 0; i<victims.size(); i++) {
            LivingEntity victim = victims.get(i);

            if(victim != source) {
                //entityIn.doHurtTarget(target);
                if (victim instanceof Player && disableShield == true) {
                    disableShield((Player)victim, victim.getMainHandItem(), victim.getOffhandItem(), source);
                }

                victim.hurt(damageSource, damage);
                victim.setLastHurtByMob(source);

                victim.knockback(knockback, knockVec.x, knockVec.y);

            }
        }
    }

    public static void PivotedPolyHitCheck(PathfinderMob source, Vec3 boxOffset, double attackWidth, double attackHeight, double attackLength, ServerLevel world, float damage, DamageSource damageSource, float knockback, boolean disableShield) {
        //attackRadius is in blocks


        Vec2 knockVec = MathHelpers.OrizontalAimVector(
                MathHelpers.AimVector(new Vec3(-source.position().x, -source.position().y, -source.position().z),
                        new Vec3(-source.getTarget().position().x, -source.getTarget().position().y, -source.getTarget().position().z)
                ));

        Vec3 sourcePos = source.position();
        double entityAngle = (source.getYRot());
        Vec3 truePos = sourcePos.add(boxOffset);
        double[] trueXZ = {truePos.x, truePos.z};

        AffineTransform.getRotateInstance(Math.toRadians(entityAngle), sourcePos.x, sourcePos.z).transform(trueXZ, 0, trueXZ, 0, 1);
        double[] transformedTrueXY = trueXZ;
        Vec3 rotatedPos = new Vec3(transformedTrueXY[0], truePos.y, transformedTrueXY[1]);


        BlockPos finalPos = new BlockPos(rotatedPos);
        AABB Hitbox = new AABB(finalPos).inflate(attackWidth, attackHeight, attackLength);
        hitboxOutline(Hitbox, world);
        world.sendParticles(ParticleTypes.EXPLOSION, rotatedPos.x, rotatedPos.y, rotatedPos.z, 1, 0, 0, 0, 0);
        List<LivingEntity> victims = new ArrayList<>(world.getEntitiesOfClass(LivingEntity.class, Hitbox));

        for (int i = 0; i < victims.size(); i++) {
            LivingEntity victim = victims.get(i);

            if (victim != source) {
                //entityIn.doHurtTarget(target);
                if (victim instanceof Player && disableShield == true) {
                    disableShield((Player)victim, victim.getMainHandItem(), victim.getOffhandItem(), source);
                }

                victim.hurt(damageSource, damage);
                victim.setLastHurtByMob(source);

                victim.knockback(knockback, knockVec.x, knockVec.y);

            }
        }
    }

    public static void disableShield(Player pPlayer, ItemStack mainHand, ItemStack offHand, PathfinderMob source) {
        //System.out.println("Shatter " + (!mainHand.isEmpty() && mainHand.is(Items.SHIELD)));

        if (!mainHand.isEmpty() && mainHand.is(Items.SHIELD) && pPlayer.isBlocking()) {
            //float f = 0.25F + (float) EnchantmentHelper.getBlockEfficiency(source) * 0.05F;
            pPlayer.getCooldowns().addCooldown(Items.SHIELD, 100);
            source.level.broadcastEntityEvent(pPlayer, (byte)30);

        } else if (!offHand.isEmpty() && offHand.is(Items.SHIELD) && pPlayer.isBlocking()) {
            //float f = 0.25F + (float) EnchantmentHelper.getBlockEfficiency(source) * 0.05F;
            pPlayer.getCooldowns().addCooldown(Items.SHIELD, 100);
            source.level.broadcastEntityEvent(pPlayer, (byte)30);
        }

    }

    public static void hitboxOutline (AABB box, ServerLevel world) {
        world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, (box.maxX), (box.maxY), (box.maxZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, (box.maxX), (box.minY), (box.minZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, (box.maxX), (box.minY), (box.maxZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, (box.maxX), (box.maxY), (box.minZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);

        world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, (box.minX), (box.maxY), (box.maxZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, (box.minX), (box.minY), (box.minZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, (box.minX), (box.minY), (box.maxZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, (box.minX), (box.maxY), (box.minZ), 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

}
