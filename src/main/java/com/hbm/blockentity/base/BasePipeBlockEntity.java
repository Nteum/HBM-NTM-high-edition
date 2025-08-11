package com.hbm.blockentity.base;

import com.hbm.HBMKey;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BasePipeBlockEntity extends BlockEntity {
    protected final CapabilityCache capabilitiesCache = new CapabilityCache();
    public ConnType[] dirType = new ConnType[]{ConnType.NORMAL, ConnType.NORMAL, ConnType.NORMAL, ConnType.NORMAL, ConnType.NORMAL, ConnType.NORMAL};
    public BasePipeBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    // === capabilities
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return capabilitiesCache.getCapability(cap,side);
    }
    public static void updateConnCaps(BasePipeBlockEntity pipeEntity){
        for (Direction direction : Direction.values()) {
            if (pipeEntity.dirType[direction.get3DDataValue()] == ConnType.FORBID)
                pipeEntity.capabilitiesCache.invalidate(ForgeCapabilities.ENERGY, direction);
            else if (pipeEntity.dirType[direction.get3DDataValue()] == ConnType.NORMAL)
                pipeEntity.capabilitiesCache.validate(ForgeCapabilities.ENERGY, direction);
        }
    }
    //=== conn type
    public ConnType getConnType(Direction direction){
        return dirType[direction.get3DDataValue()];
    }
    public static int[] dirTypeToInt(ConnType[] dirType){
        return Arrays.stream(dirType).mapToInt(ConnType::ordinal).toArray();
    }
    public static ConnType[] stringToDirType(int[] intDirs){
        ConnType[] connTypes = new ConnType[6];
        for (int i = 0; i < intDirs.length; i++) {
            connTypes[i] = ConnType.values()[intDirs[i]];
        }
        return connTypes;
        //我尝试用stream转换，但老是报错。
//        return (ConnType[]) Arrays.stream(intDirs).mapToObj(value -> ConnType.values()[value]).map(obj -> (ConnType)obj).toArray();
    }

    public List<BlockPos> getConnection() {
        List<BlockPos> conn = new ArrayList<>();
        BlockPos blockPos = getBlockPos();
        BlockState blockState = getBlockState();
        if (blockState.is(ModBlocks.RED_CABLE.get())){
            PipeBlock.PROPERTY_BY_DIRECTION.forEach((direction, booleanProperty) -> {
                if (!(dirType[direction.get3DDataValue()]== ConnType.FORBID)&&blockState.getValue(booleanProperty)==Boolean.TRUE){
                    conn.add(blockPos.relative(direction));
                }
            });
        }
        return conn;
    }
    //=== data load unload
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putIntArray(HBMKey.FORBID_DIR,dirTypeToInt(dirType));
        CompoundTag dataMap = ItemDataUtils.getDataMap(pTag);
        dataMap.put(HBMKey.CAPS, capabilitiesCache.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        dirType = stringToDirType(pTag.getIntArray(HBMKey.FORBID_DIR));
        CompoundTag dataMap = ItemDataUtils.getDataMapIfPresent(pTag);
        if (dataMap!=null && dataMap.contains(HBMKey.CAPS))
            capabilitiesCache.deserializeNBT((CompoundTag) dataMap.get(HBMKey.CAPS));
    }
    //=== ticker
    protected void onUpdateClient(){}
    // 服务器更新
    protected void onUpdateServer(){}
    public static void clientTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        ((BasePipeBlockEntity)pBlockEntity).onUpdateClient();
    }
    public static void serverTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        ((BasePipeBlockEntity)pBlockEntity).onUpdateServer();
    }
}
