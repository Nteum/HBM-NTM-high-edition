package com.hbm.block.machine;

import com.hbm.blockentity.machine.SolarBoilerEntity;
import com.hbm.blockentity.machine.SolarPanelEntity;
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
 * 太阳能锅炉（多方块）。移植自旧版 MachineSolarBoiler（3 格高）。
 * 利用太阳功率把水转化为蒸汽。
 */
public class BlockSolarBoiler extends BlockDummyable {
    public static MultiblockData MULTIBLOCK_DATA;
    public BlockSolarBoiler(Properties pProperties){
        super(pProperties);
        this.shape = Block.box(-1.0D, 0.0D, -1.0D, 2.0D, 48.0D, 2.0D);
    }

    @Override
    protected int placementOffset() {
        return 1;
    }

    @Nullable
    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new SolarBoilerEntity(pos, state);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTIBLOCK_DATA == null){
            MULTIBLOCK_DATA = new MultiblockData(2, 0, 1, 1, 1, 1)
                    .addCap(new Vec3i(0, 0, -1), ForgeCapabilities.FLUID_HANDLER, Direction.SOUTH)
                    .addCap(new Vec3i(0, 0, 1), ForgeCapabilities.FLUID_HANDLER, Direction.NORTH)
                    .addCap(new Vec3i(-1, 0, 0), ForgeCapabilities.FLUID_HANDLER, Direction.EAST)
                    .addCap(new Vec3i(1, 0, 0), ForgeCapabilities.FLUID_HANDLER, Direction.WEST);
        }
        return MULTIBLOCK_DATA;
    }
}
