package com.hbm.blockentity.base;

import com.hbm.HBMKey;
import com.hbm.api.Mode;
import com.hbm.blockentity.base2.UpdateableBlockEntity;
import com.hbm.capabilities.network.ConnType;
import com.hbm.capabilities.CapabilityCache;
import com.hbm.utils.ItemDataUtils;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基础的管道类，仅需存储管道的连接和模式
 * 暂时不需要考虑能力
 * */
public abstract class BasePipeBlockEntity extends UpdateableBlockEntity {
    // 流体连接状态，借用Mode类，但只用BOTH和NONE，被禁止连接就是NONE
    // 它只表示是否限制方向，不表示实际上是否连接
    public Mode[] connLimit = new Mode[]{Mode.BOTH,Mode.BOTH,Mode.BOTH,Mode.BOTH,Mode.BOTH,Mode.BOTH};
    public BasePipeBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    //=== data load unload
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putIntArray(HBMKey.CONN_LIMIT, Arrays.stream(connLimit).mapToInt(Mode::ordinal).toArray());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        int[] intArray = pTag.getIntArray(HBMKey.CONN_LIMIT);
        if (intArray.length == 6) connLimit = Arrays.stream(intArray).mapToObj(i -> Mode.values()[i]).toArray(Mode[]::new);
    }
    //=== ticker，暂时用不上
    protected void onUpdateClient(){}
    // 服务器更新
    protected void onUpdateServer(){}
    public static void clientTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        ((BasePipeBlockEntity)pBlockEntity).onUpdateClient();
    }
    public static void serverTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        ((BasePipeBlockEntity)pBlockEntity).onUpdateServer();
    }
    public Set<BlockPos> getConnected(){
        Set<BlockPos> set = new HashSet<>();
        for (int i = 0; i < Direction.values().length; i++) {
            if (connLimit[i] == Mode.BOTH){
                set.add(this.getBlockPos().relative(Direction.from3DDataValue(i)));
            }
        }
        return set;
    }
}
