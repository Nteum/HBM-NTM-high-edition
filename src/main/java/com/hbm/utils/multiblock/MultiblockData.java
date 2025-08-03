package com.hbm.utils.multiblock;

import com.hbm.block.HBMMachine;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiblockData {
    public static final Map<Block, MultiblockData> mapping = new HashMap<>();
    static {
        mapping.put(ModBlocks.machine_assembler.get(), new MultiblockData(1, 0, 2 ,1 ,2 ,1));
        mapping.put(HBMMachine.CHEMPLANT.get(), new MultiblockData(2, 0, 2 ,1 ,2 ,1));
    }

    MultiblockData(List<Vec3i> offsets, int[] dirOffsets){
        this.offsets = offsets;
        this.dirOffsets = dirOffsets;
    }
    MultiblockData(int ... dirOffsets){
        this.dirOffsets = dirOffsets;
        this.offsets = square(dirOffsets);
    }
    /**
     * 注意：offset不包括核心方块，它记录的是所有填充方块的位置。
     * */
    public List<Vec3i> offsets;
    public int[] dirOffsets;

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
}
