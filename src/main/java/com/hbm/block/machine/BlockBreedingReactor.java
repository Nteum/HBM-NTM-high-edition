package com.hbm.block.machine;

import com.hbm.blockentity.machine.BreedingReactorEntity;
import com.hbm.core.block.BlockDummyable;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

/**
 * 增殖反应堆（多方块）。移植自旧版 MachineReactorBreeding（3 格高）。
 */
public class BlockBreedingReactor extends BlockDummyable {
    public static MultiblockData MULTIBLOCK_DATA;
    public BlockBreedingReactor(Properties pProperties){
        super(pProperties);
        this.shape = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 48.0D, 1.0D);
    }

    @Nullable
    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new BreedingReactorEntity(pos, state);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTIBLOCK_DATA == null){
            MULTIBLOCK_DATA = new MultiblockData(2, 0, 0, 0, 0, 0)
                    .addCap(new Vec3i(0, 0, -1), ForgeCapabilities.ITEM_HANDLER, net.minecraft.core.Direction.SOUTH)
                    .addCap(new Vec3i(0, 0, 1), ForgeCapabilities.ITEM_HANDLER, net.minecraft.core.Direction.NORTH);
        }
        return MULTIBLOCK_DATA;
    }
}
