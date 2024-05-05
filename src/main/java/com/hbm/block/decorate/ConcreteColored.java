package com.hbm.block.decorate;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConcreteColored extends Block {
    public static final IntProperty concrete_color = IntProperty.of("concrete_color",0,15);
    public ConcreteColored(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(concrete_color,8));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(concrete_color);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item handitem = player.getStackInHand(player.getActiveHand()).getItem();
        if (handitem instanceof DyeItem){
//            DyeColor color = ((DyeItem) handitem).getColor();
            switch (((DyeItem) handitem).getColor()){
                case WHITE -> world.setBlockState(pos,state.with(concrete_color,0));
                case ORANGE -> world.setBlockState(pos,state.with(concrete_color,1));
                case MAGENTA -> world.setBlockState(pos,state.with(concrete_color,2));
                case LIGHT_BLUE -> world.setBlockState(pos,state.with(concrete_color,3));
                case YELLOW -> world.setBlockState(pos,state.with(concrete_color,4));
                case LIME -> world.setBlockState(pos,state.with(concrete_color,5));
                case PINK -> world.setBlockState(pos,state.with(concrete_color,6));
                case GRAY -> world.setBlockState(pos,state.with(concrete_color,7));
                case LIGHT_GRAY -> world.setBlockState(pos,state.with(concrete_color,8));
                case CYAN -> world.setBlockState(pos,state.with(concrete_color,9));
                case PURPLE -> world.setBlockState(pos,state.with(concrete_color,10));
                case BLUE -> world.setBlockState(pos,state.with(concrete_color,11));
                case BROWN -> world.setBlockState(pos,state.with(concrete_color,12));
                case GREEN -> world.setBlockState(pos,state.with(concrete_color,13));
                case RED -> world.setBlockState(pos,state.with(concrete_color,14));
                case BLACK -> world.setBlockState(pos,state.with(concrete_color,15));
                default -> world.setBlockState(pos,state.with(concrete_color,8));
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}
