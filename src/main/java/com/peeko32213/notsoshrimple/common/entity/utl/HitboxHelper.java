package com.peeko32213.notsoshrimple.common.entity.utl;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class HitboxHelper {

    private static final double d = 0.6f;
    private static final double angleVar = Math.PI/12;

    public static void LargeAttack(DamageSource source, float damage, float knockback, PathfinderMob entityIn, Vec3 pos0, double radius, double angleFirst, double angleLast, double hInf, double hSup){

        Vec2 knockVec = MathHelpers.OrizontalAimVector(
                MathHelpers.AimVector(new Vec3(-entityIn.position().x, -entityIn.position().y, -entityIn.position().z),
                        new Vec3(-entityIn.getTarget().position().x, -entityIn.getTarget().position().y, -entityIn.getTarget().position().z)
                ));

        Vec2 aim = MathHelpers.OrizontalAimVector(entityIn.getLookAngle());
        Level worldIn = entityIn.level;


        for(int i = 0; i<=radius/d; ++i) {


            //double angleVar = Math.asin(1-(1/(2*(i^2)+0.0001)));




            for(int j = 0; j<=(angleLast-angleFirst)/angleVar; ++j) {

                double angle = angleFirst + angleVar*j;

                double x = pos0.x + i*d*(aim.x*Math.cos(angle) - aim.y * Math.sin(angle));
                double z = pos0.z + i*d*(aim.y*Math.cos(angle) + aim.x * Math.sin(angle));

                for(int k = 0; k<=(hSup-hInf)/d; ++k) {

                    double y = pos0.y + hInf + k*d;
                    AABB scanAbove = new AABB(x-d, y - 4*d, z- d, x+ d, y + 2*d, z+ d);
                    List<LivingEntity> entities = new ArrayList<>(worldIn.getEntitiesOfClass(LivingEntity.class, scanAbove));

                    if(!entities.isEmpty()) {
                        for(int n = 0; n < entities.size(); n++) {

                            LivingEntity target = entities.get(n);

                            if(target != entityIn) {
                                //entityIn.doHurtTarget(target);
                                target.hurt(source, damage);
                                target.setLastHurtByMob(entityIn);

                                target.knockback(knockback, knockVec.x, knockVec.y);

                            }
                        }
                    }
					/*
				   if(worldIn instanceof ServerLevel) {


						((ServerLevel) worldIn).sendParticles( ParticleTypes.CRIT, x, y, z, 1,  0, 0, 0, 0.4);


					}
					*/

                }
            }
        }

    }


    public static void LargeAttackWithTargetCheck(DamageSource source, float damage, float knockback, PathfinderMob entityIn, Vec3 pos0, double radius, double angleFirst, double angleLast, double hInf, double hSup){

        Vec2 knockVec = MathHelpers.OrizontalAimVector(
                MathHelpers.AimVector(new Vec3(-entityIn.position().x, -entityIn.position().y, -entityIn.position().z),
                        new Vec3(-entityIn.getTarget().position().x, -entityIn.getTarget().position().y, -entityIn.getTarget().position().z)
                ));

        Vec2 aim = MathHelpers.OrizontalAimVector(entityIn.getLookAngle());
        Level worldIn = entityIn.level;


        for(int i = 0; i<=radius/d; ++i) {


            //double angleVar = Math.asin(1-(1/(2*(i^2)+0.0001)));




            for(int j = 0; j<=(angleLast-angleFirst)/angleVar; ++j) {

                double angle = angleFirst + angleVar*j;

                double x = pos0.x + i*d*(aim.x*Math.cos(angle) - aim.y * Math.sin(angle));
                double z = pos0.z + i*d*(aim.y*Math.cos(angle) + aim.x * Math.sin(angle));

                for(int k = 0; k<=(hSup-hInf)/d; ++k) {

                    double y = pos0.y + hInf + k*d;
                    AABB scanAbove = new AABB(x-d, y - 4d, z- d, x+ d, y + 2d, z+ d);
                    List<LivingEntity> entities = new ArrayList<>(worldIn.getEntitiesOfClass(LivingEntity.class, scanAbove));

                    if(!entities.isEmpty()) {
                        for(int n = 0; n < entities.size(); n++) {

                            LivingEntity target = entities.get(n);

                            if(target == entityIn.getTarget()) {
                                //entityIn.doHurtTarget(target);
                                target.hurt(source, damage);
                                target.setLastHurtByMob(entityIn);

                                target.knockback(knockback, knockVec.x, knockVec.y);

                            }
                        }
                    }
					/*
				   if(worldIn instanceof ServerLevel) {


						((ServerLevel) worldIn).sendParticles( ParticleTypes.CRIT, x, y, z, 1,  0, 0, 0, 0.4);


					}
					*/

                }
            }
        }

    }


    public static void LongAttackWithTargetCheck(DamageSource source, float damage, float knockback, PathfinderMob entityIn, Vec3 pos0, double radius, double edgeS, double edgeR, double hInf, double hSup){

        Vec2 knockVec = MathHelpers.OrizontalAimVector(
                MathHelpers.AimVector(new Vec3(-entityIn.position().x, -entityIn.position().y, -entityIn.position().z),
                        new Vec3(-entityIn.getTarget().position().x, -entityIn.getTarget().position().y, -entityIn.getTarget().position().z)
                ));

        Vec2 aim = MathHelpers.OrizontalAimVector(entityIn.getLookAngle());
        Level worldIn = entityIn.level;


        for(int i = 0; i<=radius/d; ++i) {



            for(int j = Math.round(Math.round(edgeS/d)); j<=edgeR/d; ++j) {

                double angle = edgeR*Math.PI*(2^(-2))/4 + angleVar*j;

                //double x = pos0.x + aim.x*(d*i) + d*(aim.x*Math.cos(angle) - aim.y*Math.sin(angle));
                //double z = pos0.z + aim.y*(d*i) + d*(aim.y*Math.cos(angle) + aim.x * Math.sin(angle));

                double x = pos0.x + aim.x*(d*i + d*j);
                double z = pos0.z + aim.y*(d*i + d*j);

                for(int k = 0; k<=(hSup-hInf)/d; ++k) {

                    double y = pos0.y + hInf + k*d;
                    AABB scanAbove = new AABB(x-d, y - 4d, z- d, x+ d, y + 2d, z+ d);
                    List<LivingEntity> entities = new ArrayList<>(worldIn.getEntitiesOfClass(LivingEntity.class, scanAbove));

                    if(!entities.isEmpty()) {
                        for(int n = 0; n < entities.size(); n++) {

                            LivingEntity target = entities.get(n);

                            if(target == entityIn.getTarget()) {
                                //entityIn.doHurtTarget(target);
                                target.hurt(source, damage);
                                target.setLastHurtByMob(entityIn);

                                target.knockback(knockback, knockVec.x, knockVec.y);

                            }
                        }
                    }
				/*
			   if(worldIn instanceof ServerLevel) {


					((ServerLevel) worldIn).sendParticles( ParticleTypes.CRIT, x, y, z, 1,  0, 0, 0, 0.4);


				}
				*/


                }
            }
        }

    }


}
