package com.hbm.block.base;

import com.hbm.blockentity.base.DummibleBlockEntity;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** 多方块联动破坏的机器
 *
 * */
public abstract class BedLikeBlock extends BaseMachineBlock {
    protected BedLikeBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING,Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext).setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())){
            //核心方块被移除时联动移除填充方块
            List<Vec3i> offsets = transOffsets(getOffsets(), pState.getValue(FACING));
            for (Vec3i offset : offsets) {
                BlockPos pos = pPos.offset(offset);
                if (pLevel.getBlockState(pos).is(ModBlocks.DUMMIBLE.get())){
                    pLevel.removeBlock(pos,false);
                }
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    /** 被放置后的动作 */
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (!pLevel.isClientSide){
            Direction direction = pState.getValue(FACING);
            //判断多方块结构是被会被阻挡
            if (!checkRequirement(pLevel,pPos,direction,getOffsets())){
                //方块掉落
                pLevel.removeBlock(pPos,false);
                Containers.dropItemStack(pLevel,pPos.getCenter().x,pPos.getCenter().y,pPos.getCenter().z,pStack.getItem().getDefaultInstance());
                return;
            }
            //放置方块
            fillSpace(pLevel, pPos, ModBlocks.DUMMIBLE.get().defaultBlockState(), direction, getOffsets());
        }
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }
    /** 获取物品占用的所有位置（相对于放置点的位置） */
    protected List<Vec3i> getOffsets(){return List.of(new Vec3i(0,0,0));}
    /** 检查方块是否可以放得下 */
    protected boolean checkRequirement(Level level, BlockPos blockPos, Direction dir, List<Vec3i> offsets){
        List<Vec3i> offsets2 = transOffsets(offsets, dir);
        for (Vec3i offset : offsets2) {
            if (!level.getBlockState(blockPos.offset(offset)).canBeReplaced())return false;
        }
        return true;
    }
    /** 填充实体的方块 */
    protected void fillSpace(Level level, BlockPos blockPos,BlockState blockState, Direction dir, List<Vec3i> offsets){
        List<Vec3i> offsets2 = transOffsets(offsets, dir);
        for (Vec3i offset : offsets2) {
            level.setBlock(blockPos.offset(offset),blockState,3);
            BlockEntity blockEntity = level.getBlockEntity(blockPos.offset(offset));
            if (blockEntity instanceof DummibleBlockEntity dummibleBlockEntity){
                dummibleBlockEntity.corePos = new BlockPos(blockPos);
            }
        }
    }
    protected void clearSpace(Level level, BlockPos blockPos, Direction dir, List<Vec3i> offsets){
        List<Vec3i> offsets2 = transOffsets(offsets, dir);
        for (Vec3i offset : offsets2) {
            level.setBlockAndUpdate(blockPos.offset(offset), Blocks.AIR.defaultBlockState());
        }
    }
    /** 将offset根据方向进行旋转。
     * 默认方向是南方，其他方向按照南方进行旋转（因为南方两个坐标都是正的）
     * （本以为会有现成方法的，但好像确实没有）
     * */
    public static List<Vec3i> transOffsets(List<Vec3i> offsets, Direction dir){
        List<Vec3i> result = new ArrayList<>(offsets);
        int[] trans;
        switch (dir){
            case NORTH -> trans = new int[]{-1,0,0,-1};
            case EAST -> trans = new int[]{0,-1,1,0};
            case SOUTH -> trans = new int[]{1,0,0,1};
            case WEST -> trans = new int[]{0,1,-1,0};
            default -> trans = new int[]{1,0,0,1};
        }
        for (int i = 0; i < result.size(); i++) {
            Vec3i v1 = result.get(i);
            Vec3i v2 = new Vec3i(v1.getX() * trans[0] + v1.getZ() * trans[2], v1.getY(), v1.getX() * trans[1] + v1.getZ() * trans[3]);
            result.set(i, v2);
        }
        return result;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    //=====================获取offset的工具函数===================
    /** 工具函数，用于计算立方体型空间的偏移量 */
    public static List<Vec3i> square(int[] dim){
        List<Vec3i> offsets = new ArrayList<>();
        for (int i = -dim[4]; i <= dim[5]; i++) {
            for (int j = -dim[1]; j <= dim[0]; j++) {
                for (int k = -dim[2]; k <= dim[3]; k++) {
                    if (!(i==0&&j==0&&k==0))
                        offsets.add(new Vec3i(i,j,k));
                }
            }
        }
        return offsets;
    }
    //工具函数，返回一个立方体的偏移
    public static Vec3i square(int n){
        return new Vec3i(n,n,n);
    }
    //工具函数，返回一个底面为柱体的偏移
    public static Vec3i pillar(int width,int height){
        return new Vec3i(width,height,width);
    }
    //机器在Y轴上的偏移量
    public int getHeightOffset() {
        return 0;
    }
}
