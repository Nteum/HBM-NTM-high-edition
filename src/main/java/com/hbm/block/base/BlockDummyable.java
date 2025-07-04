package com.hbm.block.base;

import com.hbm.block.interfaces.ICustomBlockHighlight;
import com.hbm.interfaces.ICopiable;
import com.hbm.utils.multiblock.DummableHelper;
import com.hbm.utils.multiblock.MultiblockData;
import com.hbm.world.gen.INBTTransformable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//所有多方块结构的父类
//主要处理可以协同破坏和恢复的多方块机器
public abstract class BlockDummyable extends BlockMachineBase implements ICustomBlockHighlight, ICopiable, INBTTransformable {
    public BlockDummyable(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (!pLevel.isClientSide){
            List<Vec3i> offsets = MultiblockData.mapping.get(this).offsets;
            Direction direction = pState.getValue(FACING);
            //判断多方块结构是被会被阻挡
            if (!DummableHelper.checkRequirement(pLevel,pPos,direction,offsets)){
                //方块掉落
                pLevel.removeBlock(pPos,false);
                Containers.dropItemStack(pLevel,pPos.getCenter().x,pPos.getCenter().y,pPos.getCenter().z,pStack.getItem().getDefaultInstance());
                return;
            }
            //放置方块
            DummableHelper.fillSpace(pLevel, pPos, pState, direction, offsets);
        }
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())){
            //核心方块被移除时联动移除填充方块
            DummableHelper.clearSpace(pLevel,pPos,pState,pState.getValue(FACING));
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }
}
