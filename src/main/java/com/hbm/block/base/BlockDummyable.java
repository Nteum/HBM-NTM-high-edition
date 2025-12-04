package com.hbm.block.base;

import com.hbm.HBM;
import com.hbm.block.HBMBlockProperties;
import com.hbm.block.interfaces.ICustomBlockHighlight;
import com.hbm.blockentity.base2.DummyableBlockEntity;
import com.hbm.blockentity.base2.TileProxyBase;
import com.hbm.blockentity.base2.TileProxyCombo;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.multiblock.DummableHelper;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHighlightEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//所有多方块结构的父类
//主要处理可以协同破坏和恢复的多方块机器
public abstract class BlockDummyable extends BlockMachineBase implements ICustomBlockHighlight {
    public static boolean doShapeRot = false;
    public static VoxelShape SHAPE;
    // 某个方块是否为核心，如果是核心，建立功能性方块实体，否则只是代理方块实体。
    public static final BooleanProperty IS_CORE = HBMBlockProperties.IS_CORE;
    public BlockDummyable(Properties pProperties) {
        super(pProperties.noOcclusion().isViewBlocking(BlockDummyable::never));
        this.registerDefaultState(this.getStateDefinition().any().setValue(IS_CORE, Boolean.TRUE));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(IS_CORE);
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
//            pState.setValue(IS_CORE, Boolean.TRUE);     // 设为中心方块
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
    // 搜寻机器中心
    public BlockPos getCore(BlockState pState, LevelReader pLevel, BlockPos pPos){
        if (!pState.getValue(IS_CORE)){
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof TileProxyBase tileProxy){
                return tileProxy.cachedPos;
            }
        }
        return pPos;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && !pPlayer.getPose().equals(Pose.CROUCHING)){
            BlockState coreState = pState;
            BlockPos core = getCore(pState, pLevel, pPos);
            
//            // 先找到核心点位
//            if (!pState.getValue(IS_CORE)){
//                BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
//                if (blockEntity instanceof TileProxyBase tileProxy){
//                    core = tileProxy.cachedPos;
//                }
//            }
            // 右键相当于直接对核心点位右键
            if (pLevel.getBlockEntity(core) instanceof DummyableBlockEntity entity){
//                entity.onLeftClick(pState, pLevel, pPos, pPlayer, pHand, pHit);
                coreState = pLevel.getBlockState(core);
                // 但我还是觉得保留原本的触发位置可能是有必要的，因此在DummyableBlockEntity留了一个对应接口
                entity.onLeftClick(pState, pLevel, pPos, pPlayer, pHand, pHit);
                super.use(coreState,pLevel,core,pPlayer,pHand,pHit);
            }else {
                if (coreState.getValue(IS_CORE))
                    return coreState.getBlock().use(coreState,pLevel,core,pPlayer,pHand,pHit);
                else {
                    HBM.LOGGER.warn("Dummy block's core lost, at " + pPos.toShortString());
                    return InteractionResult.CONSUME;
                }
            }
        }else {
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }
        return InteractionResult.SUCCESS;
    }
    /**
     * 对红石信号作出反应还得是neighborChanged，onNeighborChange函数不行
     * */
    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (!pLevel.isClientSide){

            if (pState.getValue(IS_CORE)){
                super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
            } else {
                BlockState coreState = pLevel.getBlockState(getCore(pState, pLevel, pPos));
                coreState.neighborChanged(pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
            }
        }
    }

    //    public static Block[] multiBlockList = new Block[]{ModBlocks.machine_crucible.get(), ModBlocks.machine_assembler.get(), ModBlocks.machine_cracking_tower.get(), HBMMachine.CHEMPLANT.get()};
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public int[] getDimensions(){
        return MultiblockData.mapping.get(this).dirOffsets;
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return pState.getValue(IS_CORE) ? mainBlockEntity(pPos,pState) : new TileProxyCombo(pPos, pState);
    }
    /** 获得核心方块的方块实体 */
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState){return null;};

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(IS_CORE) ? (doShapeRot ? DirectionUtils.voxelShapeRot(SHAPE, pState.getValue(FACING)) : SHAPE) : Shapes.block();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDrawHighlight(Level world, BlockPos pPos) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawHighlight(RenderHighlightEvent event, Level world, BlockPos pPos) {

    }

    private static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {
        return false;
    }
}
