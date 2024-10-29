package com.hbm.block.machine;

import com.hbm.blockentity.machine.AssemblerEntity;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.obj.ObjLoader;
import org.jetbrains.annotations.Nullable;

public class BlockAssembler extends BaseMachineBlock {
//     = Block.box(-16,0,-16,32,16,32)
    public static VoxelShape SHAPE;
    static {
        VoxelShape bottom = Block.box(-24,0,-24,24,8,24);
        VoxelShape body = Block.box(-20,8,-20,20,32,20);
        VoxelShape square1 = Block.box(-32,0,0,-24,16,16);
        VoxelShape square2 = Block.box(24,0,-16,32,16,0);
        VoxelShape wirehead1 = Block.box(6,6,-32,10,10,-24);
        VoxelShape wirehead2 = Block.box(-10,6,-32,-6,10,-24);
        VoxelShape wirehead3 = Block.box(6,6,24,10,10,32);
        VoxelShape wirehead4 = Block.box(-10,6,24,-6,10,32);
        SHAPE = Shapes.or(bottom,body,square1,square2,wirehead1,wirehead2,wirehead3,wirehead4);
    }
    public BlockAssembler(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AssemblerEntity(pPos,pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
}
