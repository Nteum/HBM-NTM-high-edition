package com.hbm.utils;

import com.hbm.blockentity.base.DummyableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class MultipartUtils {

    private MultipartUtils() {
    }

    /* taken from MCMP */
    public static RayTraceVectors getRayTraceVectors(Entity entity) {
        float pitch = entity.getXRot();
        float yaw = entity.getYRot();
        Vec3 start = entity.getEyePosition();
        float f1 = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = -Mth.cos(-pitch * 0.017453292F);
        float lookY = Mth.sin(-pitch * 0.017453292F);
        float lookX = f2 * f3;
        float lookZ = f1 * f3;
        double reach = 5.0D;
        if (entity instanceof Player player) {
            reach = player.getBlockReach();
        }
        Vec3 end = start.add(lookX * reach, lookY * reach, lookZ * reach);
        return new RayTraceVectors(start, end);
    }

    public static AdvancedRayTraceResult collisionRayTrace(Entity entity, BlockPos pos, Collection<VoxelShape> boxes) {
        RayTraceVectors vecs = getRayTraceVectors(entity);
        return collisionRayTrace(pos, vecs.start(), vecs.end(), boxes);
    }

    public static AdvancedRayTraceResult collisionRayTrace(BlockPos pos, Vec3 start, Vec3 end, Collection<VoxelShape> boxes) {
        double minDistance = Double.POSITIVE_INFINITY;
        AdvancedRayTraceResult hit = null;
        int i = -1;
        for (VoxelShape shape : boxes) {
            if (shape != null) {
                BlockHitResult result = shape.clip(start, end, pos);
                if (result != null) {
                    AdvancedRayTraceResult advancedResult = new AdvancedRayTraceResult(result, shape, i);
                    double d = advancedResult.squareDistanceTo(start);
                    if (d < minDistance) {
                        minDistance = d;
                        hit = advancedResult;
                    }
                }
            }
            i++;
        }
        return hit;
    }

    public record RayTraceVectors(Vec3 start, Vec3 end) {
    }

    public static class AdvancedRayTraceResult {

        public final VoxelShape bounds;
        public final HitResult hit;
        public final int subHit;

        public AdvancedRayTraceResult(HitResult mop, VoxelShape shape, int subHit) {
            hit = mop;
            bounds = shape;
            this.subHit = subHit;
        }

        public boolean valid() {
            return hit != null && bounds != null;
        }

        public double squareDistanceTo(Vec3 vec) {
            return hit.getLocation().distanceToSqr(vec);
        }
    }

    /** 检查方块是否可以放得下 */
    public static boolean checkRequirement(Level level, BlockPos blockPos, Direction dir, List<Vec3i> offsets){
        List<Vec3i> offsets2 = transOffsets(offsets, dir);
        for (Vec3i offset : offsets2) {
            if (!level.getBlockState(blockPos.offset(offset)).canBeReplaced())return false;
        }
        return true;
    }
    /** 填充实体的方块 */
    public static void fillSpace(Level level, BlockPos blockPos, BlockState blockState, Direction dir, List<Vec3i> offsets){
        List<Vec3i> offsets2 = transOffsets(offsets, dir);
        for (Vec3i offset : offsets2) {
            level.setBlock(blockPos.offset(offset),blockState,3);
            BlockEntity blockEntity = level.getBlockEntity(blockPos.offset(offset));
            if (blockEntity instanceof DummyableBlockEntity multiPartBlockEntity){
                //填充方块实体记录中心点位
//                multiPartBlockEntity.isCore = false;
//                multiPartBlockEntity.corePos = new BlockPos(blockPos);
            }
        }
    }
    public static void clearSpace(Level level, BlockPos blockPos, BlockState blockState, Direction direction, List<Vec3i> offsets){
        List<Vec3i> offsets2 = MultipartUtils.transOffsets(offsets, direction);
        for (Vec3i offset : offsets2) {
            BlockPos pos = blockPos.offset(offset);
            if (level.getBlockState(pos).is(blockState.getBlock())){
                level.removeBlock(pos,false);
            }
        }
    }
    /** 将offset根据方向进行旋转。
     * 默认方向是南方，其他方向按照南方进行旋转（因为南方两个坐标都是正的）
     * （本以为会有现成方法的，但好像确实没有）
     * */
    public static List<Vec3i> transOffsets(List<Vec3i> offsets, Direction dir){
        List<Vec3i> result = new ArrayList<>(offsets);
        int[] trans;
        switch (dir){
            case NORTH -> trans = new int[]{-1,0,0,-1};
            case EAST -> trans = new int[]{0,-1,1,0};
            case SOUTH -> trans = new int[]{1,0,0,1};
            case WEST -> trans = new int[]{0,1,-1,0};
            default -> trans = new int[]{1,0,0,1};
        }
        for (int i = 0; i < result.size(); i++) {
            Vec3i v1 = result.get(i);
            Vec3i v2 = new Vec3i(v1.getX() * trans[0] + v1.getZ() * trans[2], v1.getY(), v1.getX() * trans[1] + v1.getZ() * trans[3]);
            result.set(i, v2);
        }
        return result;
    }
    /** 工具函数，用于计算立方体型空间的偏移量
     * 输入数组的方向：U  D  N  S  W  E
     * 别问我为什么，我也想知道bob为什么这么干
     * */
    public static List<Vec3i> square(int[] dim){
        List<Vec3i> offsets = new ArrayList<>();
        for (int i = -dim[4]; i <= dim[5]; i++) {
            for (int j = -dim[1]; j <= dim[0]; j++) {
                for (int k = -dim[2]; k <= dim[3]; k++) {
                    if (!(i==0&&j==0&&k==0))
                        offsets.add(new Vec3i(i,j,k));
                }
            }
        }
        return offsets;
    }
    //工具函数，返回一个立方体的偏移
    public static Vec3i square(int n){
        return new Vec3i(n,n,n);
    }
    //工具函数，返回一个底面为柱体的偏移
    public static Vec3i pillar(int width,int height){
        return new Vec3i(width,height,width);
    }

    //======================BlockDummyable==========================

}