package com.hbm.blockentity.base;

import com.hbm.blockentity.ModBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummibleBlockEntity extends BlockEntity {
    public BlockPos corePos;
//    public Map<Capability<?>, List<Tuple<Direction, LazyOptional<?>>>> caps = new HashMap<>();
    public Map<Capability<?>, Map<Direction, LazyOptional<?>>> caps = new HashMap<>();
    public DummibleBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.DUMMIBLEBLOCK.get(), pPos, pBlockState);

    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putIntArray("core_pos",new int[]{corePos.getX(),corePos.getY(),corePos.getZ()});
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        int[] pos = pTag.getIntArray("core_pos");
        corePos = new BlockPos(pos[0],pos[1],pos[2]);
    }

    public <T>void setCaps(Capability<T> cap, T handler, Direction ... directions){
//        List<Tuple<Direction, LazyOptional<?>>> list = new ArrayList<>();
        Map<Direction, LazyOptional<?>> map = new HashMap<>();
        for (Direction direction : directions) {
//            list.add(new Tuple<>(direction, LazyOptional.of(()->handler)));
            map.put(direction,LazyOptional.of(()->handler));
        }
        caps.put(cap,map);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (caps.containsKey(cap)&&caps.get(cap).containsKey(side)){
            return caps.get(cap).get(side).cast();
        }
        return super.getCapability(cap, side);
    }
}
