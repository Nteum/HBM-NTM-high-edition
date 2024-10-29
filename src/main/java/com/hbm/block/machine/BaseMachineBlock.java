package com.hbm.block.machine;

import com.hbm.blockentity.machine.BaseMachineBlockEntity;
import com.hbm.blockentity.machine.DifurnaceEntity;
//import com.hbm.handler.MoltiblockHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
/**
 * 大部分机器的父类：
 * 1. 有四个方向
 * 2. 放置时会根据玩家位置调整方向
 *
 * 参考：
 * （HBM）BlockDummable
 * */
public abstract class BaseMachineBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    protected BaseMachineBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    /**
     * 添加状态，不继承这个函数方块状态会被视为不存在
     * */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
        createBlockStateDefinition2(pBuilder);
    }

    public void createBlockStateDefinition2(StateDefinition.Builder<Block, BlockState> pBuilder){}
    /** 根据玩家放置的方向决定方向 */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
    /** 当方块被移除调用 */
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())){
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BaseMachineBlockEntity){
                if (pLevel instanceof ServerLevel){
                    /** 掉落方块中的物品 */
                    Containers.dropContents(pLevel,pPos,(BaseMachineBlockEntity) blockEntity);
                }
                pLevel.updateNeighbourForOutputSignal(pPos,this);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }
    //必须规定为MODEL，否则渲染不出来
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
    /// 多方块格式放置相关 ///
    public static boolean safeRem = false;
    //机器在坐标轴上的偏移量
//    public abstract Vec3i getOffset();
    //获取机器从放置点各个方向延申的距离
//    public abstract int[] getDimensions();
//    @Override
//    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
//        if(!(pPlacer instanceof Player))
//            return;
//
//        safeRem = true;
//        pLevel.removeBlock(pPos,true);
//        safeRem = false;
//
//        Player player = (Player) pPlacer;
//        //获取玩家方向
//        Direction dir = player.getDirection();
//
//        //如果发现空间不足，则不会建立方块，直接返还物品
//        if(!checkRequirement(pLevel, pPos,getDimensions(),getOffset(), dir)) {
//            if(!player.isCreative()) {
//                ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
//
//                if(stack.isEmpty()) {
//                    player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(this));
//                } else {
//                    if(stack.is(stack.getItem()) || stack.getCount() == stack.getMaxStackSize()) {
//                        player.addItem(new ItemStack(this));
//                    } else {
//                        player.getItemInHand(InteractionHand.MAIN_HAND).grow(1);
//                    }
//                }
//            }
//            return;
//        }
        //放置机器方块
//        if(!pLevel.isClientSide) {
//            //this is separate because the multiblock rotation and the final meta might not be the same
//            int meta = getMetaForCore(world, x + dir.offsetX * o, y + dir.offsetY * o, z + dir.offsetZ * o, (EntityPlayer) player, dir.ordinal() + offset);
//            //lastCore = new BlockPos(x + dir.offsetX * o, y + dir.offsetY * o, z + dir.offsetZ * o);
//            world.setBlock(x + dir.offsetX * o, y + dir.offsetY * o, z + dir.offsetZ * o, this, meta, 3);
//            IPersistentNBT.restoreData(world, x + dir.offsetX * o, y + dir.offsetY * o, z + dir.offsetZ * o, itemStack);
//            fillSpace(world, x, y, z, dir, o);
//        }
//        y -= getHeightOffset();
//        world.scheduleBlockUpdate(x, y, z, this, 1);
//        world.scheduleBlockUpdate(x, y, z, this, 2);


//        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
//    }
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }



//    //判断多方块机器是否可被放在某个位置。
//    protected boolean checkRequirement(Level world, BlockPos pos,int[] dim, Vec3i offset, Direction dir){
//        return MoltiblockHandler.checkSpace(world,pos,dim,offset,dir);
//    }

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
