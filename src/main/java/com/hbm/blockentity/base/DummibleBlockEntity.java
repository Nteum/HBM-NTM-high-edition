package com.hbm.blockentity.base;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.capabilities.CapabilityCache;
import com.hbm.capabilities.resolver.ICapabilityResolver;
import com.hbm.capabilities.resolver.SidedCapabilityWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummibleBlockEntity extends BlockEntity {
    public BlockPos corePos;
//    public Map<Capability<?>, Map<Direction, LazyOptional<?>>> caps = new HashMap<>();
    protected final CapabilityCache capabilitiesCache = new CapabilityCache();
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

//    public <T>void setCaps(Capability<?> capability, LazyOptional<T> handler, Direction ... directions){
////        Map<Direction, LazyOptional<?>> map = new HashMap<>();
////        for (Direction direction : directions) {
////            map.put(direction,LazyOptional.of(()->handler));
////        }
////        caps.put(cap,map);
//    }

    public <T>void setCaps(Capability<?> capability, ICapabilityResolver handler, Direction direction) {
        Object clone = null;
        if (handler instanceof SidedCapabilityWrapper<?> wrapper){
            try {
                clone = wrapper.clone();
                ((SidedCapabilityWrapper)clone).setAllowDirection(direction);
            }catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
        }
        capabilitiesCache.addCapabilityResolver(clone == null ? handler : (ICapabilityResolver) clone);
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//        if (caps.containsKey(cap)&&caps.get(cap).containsKey(side)){
//            return caps.get(cap).get(side).cast();
//        }
//        return super.getCapability(cap, side);
        return this.capabilitiesCache.getCapability(cap,side);
    }

//    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity){
//
//    }
}
