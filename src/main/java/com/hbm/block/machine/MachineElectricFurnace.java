package com.hbm.block.machine;

import com.hbm.entity.blockentity.MachineElectricFurnaceEntity;
import com.hbm.entity.blockentity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/*
* HBM的电炉
* 特点：有状态、有朝向、有GUI、会存储物品、消耗电力
* 参考原版熔炉的做法
* */
public class MachineElectricFurnace extends BlockWithEntity implements BlockEntityProvider{
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty LIT = Properties.LIT;
    public MachineElectricFurnace(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(LIT, false));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING,LIT);
    }
    // 创建方块中的实体
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MachineElectricFurnaceEntity(pos, state);
    }

    @Override   // 返回一下渲染类型是MODEL，否则默认会当作透明
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    // 继承 BlockEntityProvider
    //获得方块实体的Ticker
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntities.electric_furnace_entity)return null;
        return MachineElectricFurnaceEntity::tick;
    }
    //右键点击：展示GUI
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
//            BlockEntity blockEntity = world.getBlockEntity(pos);
//            if (blockEntity instanceof FurnaceBlockEntity) {
//                player.openHandledScreen((NamedScreenHandlerFactory)((Object)blockEntity));
//                player.incrementStat(Stats.INTERACT_WITH_FURNACE);
//            }
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null){
                //让服务器请求客户端开启和时段Screen handler
                player.openHandledScreen(screenHandlerFactory);
//                player.incrementStat(Stats.INTERACT_WITH_FURNACE);  //似乎还需要传个stats，暂时先复制furnaceblock的
                return ActionResult.CONSUME;
            }
        }
        return ActionResult.SUCCESS;
    }
    //获取当前方块状态
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    //方块被放置时的行为
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
    }
    //当方块状态被替换？（以下这么写可以让方块被破坏时返回内部所有东西）
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MachineElectricFurnaceEntity){
//                ItemScatterer.spawn(world,pos,);  但这里还没有完成，它的第三个参数需要返回inventory，我感觉可能需要在实体里面添加返回物品的函数
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    //输出是否课比较（似乎是和红石检测有关）
    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }
    //输出可比较的值（需要和hasComparatorOutput一同继承）
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }
    //随机刻发生的事情
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
    }

    public void setLit(boolean lit){
        this.setDefaultState(this.getDefaultState().with(LIT,lit));
    }
}
