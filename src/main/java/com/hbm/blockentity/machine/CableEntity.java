package com.hbm.blockentity.machine;

import com.hbm.api.energy.fe.HBMEnergyStorage;
import com.hbm.api.energy.fe.SidedEnergyWrapper;
import com.hbm.api.energy.fe.TransmitHelper;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BasePipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CableEntity extends BasePipeBlockEntity {
    public CableEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.CABLE_ENTITY.get(), pPos, pBlockState);
//        capabilitiesCache.addCapabilityResolver(new SidedEnergyWrapper(new HBMEnergyStorage(10_000)));
    }

    @Override
    protected void onUpdateServer() {
        TransmitHelper.cableTransmit(level,getBlockPos(),getBlockState(),this);
    }
    //    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
//        if (pState.is(ModBlocks.RED_CABLE.get()) && pBlockEntity instanceof CableEntity cableEntity){
//            TransmitHelper.cableTransmit(level,pPos,pState,pBlockEntity);
//            //debug方式：用告示牌展示电量
////            for (Direction direction : Direction.values()) {
////                if (level.getBlockState(pPos.relative(direction)).is(Blocks.OAK_SIGN)) {
////                    cableEntity.getCapability(ForgeCapabilities.ENERGY,null).ifPresent(cap -> {
////                        BlockEntity signEntity = level.getBlockEntity(pPos.relative(direction));
////                        if (signEntity != null && signEntity instanceof SignBlockEntity signBlockEntity){
////                            signBlockEntity.setText(new SignText().setMessage(0, Component.literal(""+((IHBMEnergyStorage) cap).getLongStore())),true);
////                        }
////                    });
////                    break;
////                }
////            }
//        }
//    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
//        capabilitiesCache.invalidateAll();
    }
}
