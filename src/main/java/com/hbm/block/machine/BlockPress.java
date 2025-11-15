package com.hbm.block.machine;

import com.hbm.block.base.BaseMachineBlock;
import com.hbm.block.base.BlockMachineBase;
import com.hbm.block.interfaces.ICustomBlockItemModel;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.DifurnaceEntity;
import com.hbm.blockentity.machine.PressEntity;
import com.hbm.item.ItemBlockCustomModel;
import com.hbm.render.model.Models;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 火力锻压机
 * */
public class BlockPress extends BlockMachineBase implements ICustomBlockItemModel {
    public VoxelShape SHAPE = Block.box(2,0,2,14,48,14);
    public BlockPress(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PressEntity(pPos,pState);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public BakedModel[] getModels() {
        return new BakedModel[]{Models.get(Models.PRESS_BODY), Models.get(Models.PRESS_HEAD)};
    }
    /*
    * 需要确保为ENTITYBLOCK_ANIMATED才能独立渲染物品
    * */
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
