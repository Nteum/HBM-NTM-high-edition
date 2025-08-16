package com.hbm.block.weapon;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.TileProxyCombo;
import com.hbm.blockentity.weapon.NukeBombCustomEntity;
import com.hbm.blockentity.weapon.NukeBombEntity;
import com.hbm.blockentity.weapon.NukeBombFatEntity;
import com.hbm.utils.MultipartUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class NukeFat extends NukeBomb{
//    public static final VoxelShape SHAPE = Block.box(-14,0,-16,14,24,30);
    public NukeFat(Properties pProperties,int range) {
        super(pProperties,range);
        SHAPE = Block.box(-14,0,-16,14,24,30);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new NukeBombFatEntity(pPos,pState);
    }
//
//    @Override
//    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
//        return pState.getValue(IS_CORE) ? SHAPE : Shapes.block();
//    }
}
