//package com.hbm.api.multiblock.mek;
//
//import com.hbm.HBMKey;
//import com.hbm.utils.NBTUtils;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.ListTag;
//import net.minecraft.nbt.NbtUtils;
//import net.minecraft.nbt.Tag;
//
//import java.util.Collection;
//
//public interface IValveHandler {
//
//    default void writeValves(CompoundTag updateTag) {
//        ListTag valves = new ListTag();
//        for (ValveData valveData : getValveData()) {
//            if (valveData.activeTicks > 0) {
//                CompoundTag valveNBT = new CompoundTag();
//                valveNBT.put(HBMKey.POSITION, NbtUtils.writeBlockPos(valveData.location));
//                NBTUtils.writeEnum(valveNBT, HBMKey.SIDE, valveData.side);
//                valves.add(valveNBT);
//            }
//        }
//        updateTag.put(HBMKey.VALVE, valves);
//    }
//
//    default void readValves(CompoundTag updateTag) {
//        getValveData().clear();
//        if (updateTag.contains(HBMKey.VALVE, Tag.TAG_LIST)) {
//            ListTag valves = updateTag.getList(HBMKey.VALVE, Tag.TAG_COMPOUND);
//            for (int i = 0; i < valves.size(); i++) {
//                CompoundTag valveNBT = valves.getCompound(i);
//                NBTUtils.setBlockPosIfPresent(valveNBT, HBMKey.POSITION, pos -> {
//                    Direction side = Direction.from3DDataValue(valveNBT.getInt(HBMKey.SIDE));
//                    getValveData().add(new ValveData(pos, side));
//                });
//            }
//        }
//    }
//
//    default void triggerValveTransfer(IMultiblock<?> multiblock) {
//        if (multiblock.getMultiblock().isFormed()) {
//            for (ValveData data : getValveData()) {
//                if (multiblock.getTilePos().equals(data.location)) {
//                    data.onTransfer();
//                    break;
//                }
//            }
//        }
//    }
//
//    Collection<ValveData> getValveData();
//
//    class ValveData {
//
//        public final BlockPos location;
//        public final Direction side;
//
//        // 活跃状态倒计时，倒计时到0激活。
//        public int activeTicks;
//        // 是否处于未激活状态，activeTicks==0时转为false
//        public boolean prevActive;
//
//        public ValveData(BlockPos location, Direction side) {
//            this.location = location;
//            this.side = side;
//        }
//
//        public void onTransfer() {
//            activeTicks = 30;
//        }
//
//        @Override
//        public int hashCode() {
//            int code = 1;
//            code = 31 * code + side.ordinal();
//            code = 31 * code + location.hashCode();
//            return code;
//        }
//
//        @Override
//        public boolean equals(Object obj) {
//            return obj instanceof ValveData other && other.side == side && other.location.equals(location);
//        }
//    }
//}
