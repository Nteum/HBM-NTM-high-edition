package com.hbm.block.machine;

import com.hbm.block.base.BaseMachineBlock;
import com.hbm.block.base.BedLikeBlock;
import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.TileProxyCombo;
import com.hbm.blockentity.machine.AssemblerEntity;
import com.hbm.blockentity.machine.ChemplantEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.hbm.block.base.BedLikeBlock.square;

public class BlockAssembler extends BlockDummyable {
//    public static final VoxelShape this.shape = Block.box(-32.0,0.0D,-32.0D,32.0D,32.0D,32.0D);
    public BlockAssembler(Properties pProperties) {
        super(pProperties);
        this.shape = Block.box(-32.0,0.0D,-32.0D,32.0D,32.0D,32.0D);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new AssemblerEntity(pPos,pState);
    }
}
