package com.hbm.block.env;

import com.hbm.block.HBMBlockProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class GlyphidBlock extends Block {
    /*
    * 0 - base
    * 1 - infected
    * 2 - radiation
    * */
    public static final IntegerProperty VARIANT = HBMBlockProperties.VARIANT3;
    public GlyphidBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(VARIANT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(VARIANT);
    }
}
