package com.hbm.block.machine;

import com.hbm.blockentity.machine.BigAssTankEntity;
import com.hbm.core.block.BlockDummyable;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

/**
 * 大型储罐（多方块）。移植自旧版 MachineBigAssTank（6 格高）。
 * 1600 万 mB 流体储罐。
 */
public class BlockBigAssTank extends BlockDummyable {
    public static MultiblockData MULTIBLOCK_DATA;
    public BlockBigAssTank(Properties pProperties){
        super(pProperties);
        this.shape = Block.box(-4.0D, 0.0D, -4.0D, 5.0D, 80.0D, 5.0D);
    }

    @Override
    protected int placementOffset() {
        return 6;
    }

    @Nullable
    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new BigAssTankEntity(pos, state);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTIBLOCK_DATA == null){
            MULTIBLOCK_DATA = new MultiblockData(5, 0, 4, 4, 4, 4)
                    .addCap(new Vec3i(0, 0, -4), ForgeCapabilities.FLUID_HANDLER, Direction.SOUTH)
                    .addCap(new Vec3i(0, 0, 4), ForgeCapabilities.FLUID_HANDLER, Direction.NORTH)
                    .addCap(new Vec3i(-4, 0, 0), ForgeCapabilities.FLUID_HANDLER, Direction.EAST)
                    .addCap(new Vec3i(4, 0, 0), ForgeCapabilities.FLUID_HANDLER, Direction.WEST);
        }
        return MULTIBLOCK_DATA;
    }
}
