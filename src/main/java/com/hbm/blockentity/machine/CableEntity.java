package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.api.energy.IEnergyConductor;
import com.hbm.api.energy.fe.HBMEnergyStorage;
import com.hbm.api.energy.fe.SidedEnergyWrapper;
import com.hbm.api.energy.fe.TransmitHelper;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.capabilities.CapabilityCache;
import com.hbm.lib.ItemDataUtils;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CableEntity extends BlockEntity implements IEnergyConductor {
    protected final CapabilityCache capabilitiesCache = new CapabilityCache();
    public ConnType[] dirType = new ConnType[]{ConnType.ALLOW,ConnType.ALLOW,ConnType.ALLOW,ConnType.ALLOW,ConnType.ALLOW,ConnType.ALLOW};
    public CableEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.CABLE_ENTITY.get(), pPos, pBlockState);
        capabilitiesCache.addCapabilityResolver(new SidedEnergyWrapper(new HBMEnergyStorage(10_000)));
    }
    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (pState.is(ModBlocks.RED_CABLE.get()) && pBlockEntity instanceof CableEntity cableEntity){
            TransmitHelper.cableTransmit(level,pPos,pState,pBlockEntity);
            //debug方式：用告示牌展示电量
//            for (Direction direction : Direction.values()) {
//                if (level.getBlockState(pPos.relative(direction)).is(Blocks.OAK_SIGN)) {
//                    cableEntity.getCapability(ForgeCapabilities.ENERGY,null).ifPresent(cap -> {
//                        BlockEntity signEntity = level.getBlockEntity(pPos.relative(direction));
//                        if (signEntity != null && signEntity instanceof SignBlockEntity signBlockEntity){
//                            signBlockEntity.setText(new SignText().setMessage(0, Component.literal(""+((IHBMEnergyStorage) cap).getLongStore())),true);
//                        }
//                    });
//                    break;
//                }
//            }
        }
    }

    //=============以下是储能相关的信息=============================
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return capabilitiesCache.getCapability(cap,side);
    }
    public static void updateConnCaps(CableEntity cableEntity){
        for (Direction direction : Direction.values()) {
            if (cableEntity.dirType[direction.get3DDataValue()] == ConnType.FORBID)
                cableEntity.capabilitiesCache.invalidate(ForgeCapabilities.ENERGY, direction);
            else if (cableEntity.dirType[direction.get3DDataValue()] == ConnType.ALLOW)
                cableEntity.capabilitiesCache.validate(ForgeCapabilities.ENERGY, direction);
        }
    }
    //=======================================================

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
//        lazyEnergyHandler.invalidate();
        capabilitiesCache.invalidateAll();
    }
    //====================管理数据加载========================
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
    //==================连接方向问题==================
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
    @Override
    public List<BlockPos> getConnection() {
        List<BlockPos> conn = new ArrayList<>();
        BlockPos blockPos = getBlockPos();
        BlockState blockState = getBlockState();
        if (blockState.is(ModBlocks.RED_CABLE.get())){
            PipeBlock.PROPERTY_BY_DIRECTION.forEach((direction, booleanProperty) -> {
                if (!(dirType[direction.get3DDataValue()]==ConnType.FORBID)&&blockState.getValue(booleanProperty)==Boolean.TRUE){
                    conn.add(blockPos.relative(direction));
                }
            });
        }
        return conn;
    }
    public static enum ConnType{
        ALLOW,FORBID,IN,OUT
    }
}
