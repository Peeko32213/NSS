package com.peeko32213.notsoshrimple.common.entity.utl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class LargeEntityGroundNavigator extends GroundPathNavigation {

    public LargeEntityGroundNavigator(Mob pMob, Level pLevel) {
        super(pMob, pLevel);
    }
    static final float EPSILON = 1.0E-8F;

    @Override
    protected MowziesPathfinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        //this.nodeEvaluator.setCanPassDoors(true);
        return new MowziesPathfinder(this.nodeEvaluator, maxVisitedNodes);
    }

    @Override
    protected void followThePath() {
        Path path = Objects.requireNonNull(this.path);
        Vec3 entityPos = this.getTempMobPos();
        int pathLength = path.getNodeCount();
        //path, current mob position, how long the rest of the path is, blah blah

        if (!path.isDone()) {
            for (int i = path.getNextNodeIndex(); i < path.getNodeCount(); i++) {
                if (path.getNode(i).y != Math.floor(entityPos.y)) {
                    pathLength = i;
                    break;
                    //find the index of the next node the entity has to arrive to.
                    //only change pathLength if the entity is not at that node. If the path has been complete, the next node is its own node.
                }
            }
        }

        final Vec3 base = entityPos.add(-this.mob.getBbWidth() * 0.5F, 0.0F, -this.mob.getBbWidth() * 0.5F);
        final Vec3 max = base.add(this.mob.getBbWidth(), this.mob.getBbHeight(), this.mob.getBbWidth());
        //maximum and base distances the entity can be from the target node before it is considered reached.

        if (this.tryShortcut(path, new Vec3(this.mob.getX(), this.mob.getY(), this.mob.getZ()), pathLength, base, max)) {
            //checks if a shortcut can be taken to find an easier path
            if (this.isAt(path, 0.5F) || this.atElevationChange(path) && this.isAt(path, this.mob.getBbWidth() * 0.5F)) {
                path.setNextNodeIndex(path.getNextNodeIndex() + 1);
                //if a shortcut cannot be made, it checks for whether the entity is of a certain distance from the next node, or whether the>>
                //>>path has an elevation change and if the entity is over the next node.
            }
        }
        this.doStuckDetection(entityPos);
    }

    /*@Override
    protected void followThePath() {
        Vec3 entityPos = this.getTempMobPos();
        this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
        // if this.mob.getBbWidth() > 0.75F, then this variable is this.mob.getBbWidth() / 2.0F, else 0.75F - this.mob.getBbWidth() / 2.0F.
        //finds the farthest a mob could be to the waypoint(next location)

        Vec3i vec3i = this.path.getNextNodePos();
        //gets the position of the next node

        double d0 = Math.abs(this.mob.getX() - ((double)vec3i.getX() + (this.mob.getBbWidth() + 1) / 2D)); //Forge: Fix MC-94054
        double d1 = Math.abs(this.mob.getY() - (double)vec3i.getY());
        double d2 = Math.abs(this.mob.getZ() - ((double)vec3i.getZ() + (this.mob.getBbWidth() + 1) / 2D)); //Forge: Fix MC-94054
        //the distance between the mob's current position and the target node's position plus offset
        //offset is just how close the mob need to get to a node before it's considered to be "over" it

        boolean flag = d0 <= (double)this.maxDistanceToWaypoint && d2 <= (double)this.maxDistanceToWaypoint && d1 < 1.0D; //Forge: Fix MC-94054
        //flags true if the entity's position is within the distance required to be "over" the target node

        if (flag || this.mob.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(entityPos)) {
            this.path.advance();
        }
        //if the mob can move to the next node one way or another and it is over the current node then advance the path to the next node

        this.doStuckDetection(entityPos);
    }*/

    private boolean isAt(Path path, /*Vec3 pathPos,*/ float threshold) {
        //threashold is how close the mob can get before it's considered to be over the node.
        final Vec3 pathPos = path.getNextEntityPos(this.mob);
        //final Vec3 pathPos = path.getEntityPosAtNode(this.mob, path.getNextNodeIndex());

        return Mth.abs((float) (this.mob.getX() - pathPos.x)) < threshold &&
                Mth.abs((float) (this.mob.getZ() - pathPos.z)) < threshold &&
                Math.abs(this.mob.getY() - pathPos.y) < 1.0D;
        //returns whether the distance the mob is from its current node is smaller than the threshold.
    }

    private boolean atElevationChange(Path path) {
        final int curr = path.getNextNodeIndex();
        final int end = Math.min(path.getNodeCount(), curr + Mth.ceil(this.mob.getBbWidth() * 0.5F) + 1);
        final int currY = path.getNode(curr).y;

        for (int i = curr + 1; i < end; i++) {
            if (path.getNode(i).y != currY) {
                return true;
                //detects if any elevation change is within the mob's path and returns true/false
            }
        }
        return false;
    }

    private boolean shouldTargetNextNodeInDirection(Vec3 pVec) {
        //should the mob be targeting the node after the next one it will arrive at
        if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
            return false;
            //if the path ends return false
        } else {
            Vec3 vec3 = Vec3.atBottomCenterOf(this.path.getNextNodePos());
            //center the position of the next node
            if (!pVec.closerThan(vec3, 2.0D)) {
                return false;
                //if the mob is not close enough keep targeting the next node instead of the node after the next
            } else if (this.canMoveDirectly(pVec, this.path.getNextEntityPos(this.mob))) {
                return true;
            } else {
                Vec3 vec31 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
                Vec3 vec32 = vec31.subtract(vec3);
                Vec3 vec33 = pVec.subtract(vec3);
                return vec32.dot(vec33) > 0.0D;
            }
        }
    }

    private boolean tryShortcut(Path path, Vec3 entityPos, int pathLength, Vec3 base, Vec3 max) {
        for (int i = pathLength; --i > path.getNextNodeIndex(); ) {
            final Vec3 vec = path.getEntityPosAtNode(this.mob, i).subtract(entityPos);
            if (this.sweep(vec, base, max)) {
                path.setNextNodeIndex(i);
                return false;
            }
        }
        return true;
    }

    private boolean sweep(Vec3 mobPos, Vec3 base, Vec3 max) {
        float t = 0.0F;
        float max_t = (float) mobPos.length();
        //length of the location vector for the mob from 0,0 to current pos

        //if (max_t < EPSILON) return true;
        final float[] tr = new float[3];
        final int[] ldi = new int[3];
        final int[] tri = new int[3];
        final int[] step = new int[3];
        final float[] tDelta = new float[3];
        final float[] tNext = new float[3];
        final float[] normed = new float[3];
        //declare a bunch of triple length arrays

        for (int i = 0; i < 3; i++) {
            float coordComponent = element(mobPos, i);
            //checks the x, y, and z component of mobPos

            boolean dir = coordComponent >= 0.0F;
            //checks whether the value isn't 0

            step[i] = dir ? 1 : -1;
            float lead = element(dir ? max : base, i);
            //if this axis isn't 0, set lead to max, else it is base(only affects the same component as coordComponent)

            tr[i] = element(dir ? base : max, i);
            //set each coordinate component in tri to base or max if the axis isn't or is 0

            ldi[i] = leadEdgeToInt(lead, step[i]);
            tri[i] = trailEdgeToInt(tr[i], step[i]);
            normed[i] = coordComponent / max_t;
            tDelta[i] = Mth.abs(max_t / coordComponent);
            float dist = dir ? (ldi[i] + 1 - lead) : (lead - ldi[i]);
            tNext[i] = tDelta[i] < Float.POSITIVE_INFINITY ? tDelta[i] * dist : Float.POSITIVE_INFINITY;
        }

        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        do {
            //
            int axis = (tNext[0] < tNext[1]) ?
                    ((tNext[0] < tNext[2]) ? 0 : 2) :
                    ((tNext[1] < tNext[2]) ? 1 : 2);
            float dt = tNext[axis] - t;
            t = tNext[axis];
            ldi[axis] += step[axis];
            tNext[axis] += tDelta[axis];
            for (int i = 0; i < 3; i++) {
                tr[i] += dt * normed[i];
                tri[i] = trailEdgeToInt(tr[i], step[i]);
            }
            // checkCollision
            int stepx = step[0];
            int x0 = (axis == 0) ? ldi[0] : tri[0];
            int x1 = ldi[0] + stepx;
            int stepy = step[1];
            int y0 = (axis == 1) ? ldi[1] : tri[1];
            int y1 = ldi[1] + stepy;
            int stepz = step[2];
            int z0 = (axis == 2) ? ldi[2] : tri[2];
            int z1 = ldi[2] + stepz;
            for (int x = x0; x != x1; x += stepx) {
                for (int z = z0; z != z1; z += stepz) {
                    for (int y = y0; y != y1; y += stepy) {
                        BlockState block = this.level.getBlockState(pos.set(x, y, z));
                        if (!block.isPathfindable(this.level, pos, PathComputationType.LAND)) return false;
                    }
                    BlockPathTypes below = this.nodeEvaluator.getBlockPathType(this.level, x, y0 - 1, z, this.mob, 1, 1, 1, true, true);
                    if (below == BlockPathTypes.WATER || below == BlockPathTypes.LAVA || below == BlockPathTypes.OPEN) return false;
                    BlockPathTypes in = this.nodeEvaluator.getBlockPathType(this.level, x, y0, z, this.mob, 1, y1 - y0, 1, true, true);
                    float priority = this.mob.getPathfindingMalus(in);
                    if (priority < 0.0F || priority >= 8.0F) return false;
                    if (in == BlockPathTypes.DAMAGE_FIRE || in == BlockPathTypes.DANGER_FIRE || in == BlockPathTypes.DAMAGE_OTHER) return false;
                }
            }
        } while (t <= max_t);
        return true;
    }

    static int leadEdgeToInt(float coord, int step) {
        return Mth.floor(coord - step * EPSILON);
    }

    static int trailEdgeToInt(float coord, int step) {
        return Mth.floor(coord + step * EPSILON);
    }

    static float element(Vec3 v, int i) {
        switch (i) {
            case 0: return (float) v.x;
            case 1: return (float) v.y;
            case 2: return (float) v.z;
            default: return 0.0F;
        }
    }

}
