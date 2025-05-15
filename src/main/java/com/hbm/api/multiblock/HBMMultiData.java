package com.hbm.api.multiblock;

import com.hbm.block.base.BedLikeBlock;
import com.hbm.lib.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.capabilities.Capability;

import java.util.*;

public class HBMMultiData {
    //转换之前相对中心方块的位置和方向
    public Map<Capability<?>,List<Tuple<Vec3i, Direction>>> beforeTrans = new IdentityHashMap<>();
    //转换之后在世界中绝对的位置和方向
    public Map<Capability<?>,List<Tuple<BlockPos, Direction>>> afterTrans;
    public void put(Capability<?> capability, List<Tuple<Vec3i, Direction>> posDir){
        beforeTrans.put(capability,posDir);
    }
    public HBMMultiData put(Capability<?> capability, Object ...objs){
        int[] relativePos = new int[4];
        List<Tuple<Vec3i, Direction>> list = new ArrayList<>();
        for (Object obj : objs) {
            if (obj instanceof Integer integer){
                relativePos[relativePos[3]] = integer;
                relativePos[3] = (relativePos[3] + 1) % 3;
            }
            else{
                list.add(new Tuple<>(new Vec3i(relativePos[0],relativePos[1],relativePos[2]),(Direction)obj));
            }
        }
        beforeTrans.put(capability, list);
        return this;
    }
    public void transDirection(BlockPos pos, Direction facing){
        if (afterTrans == null)
            afterTrans = new IdentityHashMap<>();
        for (Map.Entry<Capability<?>, List<Tuple<Vec3i, Direction>>> entry : beforeTrans.entrySet()) {
            List<Tuple<BlockPos, Direction>> list = new ArrayList<>();
            for (Tuple<Vec3i, Direction> tuple : entry.getValue()) {
                list.add(new Tuple<>(pos.offset(BedLikeBlock.transOffsets(List.of(tuple.getA()), facing).get(0)), DirectionUtils.horizRot(Direction.SOUTH, facing, tuple.getB())));
            }
            afterTrans.put(entry.getKey(),list);
        }
    }
}
