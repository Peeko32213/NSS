package com.peeko32213.notsoshrimple.common.entity.utl;

import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class MathHelpers {

    public static Vec2 OrizontalAimVector(Vec3 aim){

        Vec2 vec = new Vec2((float)(aim.x/(1-Math.abs(aim.y))), (float)(aim.z/(1-Math.abs(aim.y))));

        return vec;
    }

    public static Vec3 AimVector(Vec3 pos, Vec3 targetPos){

        double d = Math.sqrt((targetPos.x - pos.x)*(targetPos.x - pos.x) + (targetPos.z - pos.z)*(targetPos.z - pos.z) + (targetPos.y - pos.y)*(targetPos.y - pos.y));


        Vec3 aim = new Vec3((targetPos.x - pos.x)/d, (targetPos.y - pos.y)/d, (targetPos.z - pos.z)/d);


        return aim;


    }

    public static boolean isInStructure(ServerLevel serverLevel, BlockPos pos, ResourceKey<Structure> structure) {
        return LocationPredicate.inStructure(structure).matches(serverLevel, pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean checkSurfaceWaterMobSpawnRules(EntityType<? extends PathfinderMob> entityType, LevelAccessor level, MobSpawnType mobSpawnType, BlockPos pos, RandomSource rand) {
        int seaLevel = level.getSeaLevel();
        int belowSeaLevel = seaLevel - 2;
        return pos.getY() >= belowSeaLevel && pos.getY() <= seaLevel && level.getFluidState(pos.below()).is(FluidTags.WATER);
    }


}