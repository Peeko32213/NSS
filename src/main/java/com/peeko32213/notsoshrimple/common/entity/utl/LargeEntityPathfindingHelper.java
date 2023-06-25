package com.peeko32213.notsoshrimple.common.entity.utl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.pathfinder.*;

import javax.annotation.Nullable;
import java.util.*;

public class LargeEntityPathfindingHelper{
    public LargeEntityPathfindingHelper(NodeEvaluator pNodeEvaluator, int pMaxVisitedNodes) {
        this.nodeEvaluator = pNodeEvaluator;
        this.maxVisitedNodes = pMaxVisitedNodes;
    }

    private final BinaryHeap openSet = new BinaryHeap();
    private final Node[] neighbors = new Node[32];
    private final int maxVisitedNodes;
    private final NodeEvaluator nodeEvaluator;

    @Nullable
    private Path LargeEntityFindPath(ProfilerFiller pProfiler,
                                     Node pNode,
                                     Map<Target, BlockPos> pTargetPos,
                                     float pMaxRange,
                                     int pAccuracy,
                                     float pSearchDepthMultiplier) {

        pProfiler.push("find_path");
        pProfiler.markForCharting(MetricCategory.PATH_FINDING);
        Set<Target> set = pTargetPos.keySet();
        pNode.g = 0.0F;
        pNode.h = this.getBestH(pNode, set);
        pNode.f = pNode.h;
        this.openSet.clear();
        this.openSet.insert(pNode);
        Set<Node> set1 = ImmutableSet.of();
        int i = 0;
        Set<Target> set2 = Sets.newHashSetWithExpectedSize(set.size());
        int j = (int)((float)this.maxVisitedNodes * pSearchDepthMultiplier);

        while(!this.openSet.isEmpty()) {
            ++i;
            if (i >= j) {
                break;
            }

            Node node = this.openSet.pop();
            node.closed = true;

            for(Target target : set) {
                if (node.distanceManhattan(target) <= (float)pAccuracy) {
                    target.setReached();
                    set2.add(target);
                }
            }

            if (!set2.isEmpty()) {
                break;
            }

            if (!(node.distanceTo(pNode) >= pMaxRange)) {
                int k = this.nodeEvaluator.getNeighbors(this.neighbors, node);

                for(int l = 0; l < k; ++l) {
                    Node node1 = this.neighbors[l];
                    float f = node.distanceTo(node1);
                    node1.walkedDistance = node.walkedDistance + f;
                    float f1 = node.g + f + node1.costMalus;
                    if (node1.walkedDistance < pMaxRange && (!node1.inOpenSet() || f1 < node1.g)) {
                        node1.cameFrom = node;
                        node1.g = f1;
                        node1.h = this.getBestH(node1, set) * 1.5F;
                        if (node1.inOpenSet()) {
                            this.openSet.changeCost(node1, node1.g + node1.h);
                        } else {
                            node1.f = node1.g + node1.h;
                            this.openSet.insert(node1);
                        }
                    }
                }
            }
        }

        Optional<Path> optional = !set2.isEmpty() ? set2.stream().map((p_77454_) -> {
            return this.reconstructPath(p_77454_.getBestNode(), pTargetPos.get(p_77454_), true);
        }).min(Comparator.comparingInt(Path::getNodeCount)) : set.stream().map((p_77451_) -> {
            return this.reconstructPath(p_77451_.getBestNode(), pTargetPos.get(p_77451_), false);
        }).min(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getNodeCount));
        pProfiler.pop();
        return !optional.isPresent() ? null : optional.get();
    }

    private Path reconstructPath(Node pPoint, BlockPos pTargetPos, boolean pReachesTarget) {
        List<Node> list = Lists.newArrayList();
        Node node = pPoint;
        list.add(0, pPoint);

        while(node.cameFrom != null) {
            node = node.cameFrom;
            list.add(0, node);
        }

        return new Path(list, pTargetPos, pReachesTarget);
    }

    private float getBestH(Node pNode, Set<Target> pTargets) {
        float f = Float.MAX_VALUE;

        for(Target target : pTargets) {
            float f1 = pNode.distanceTo(target);
            target.updateBest(f1, pNode);
            f = Math.min(f1, f);
        }

        return f;
    }
}
