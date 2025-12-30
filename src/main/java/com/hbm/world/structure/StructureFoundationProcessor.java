package com.hbm.world.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * Simple processor that ensures jigsaw structures spawn with a stone foundation
 * instead of floating in mid‑air. Ported from the Modernized branch.
 */
public class StructureFoundationProcessor extends StructureProcessor {

    public static final Codec<StructureFoundationProcessor> CODEC = Codec.unit(StructureFoundationProcessor::new);

    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructureProcessors.FOUNDATION_PROCESSOR.get();
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level,
                                                             BlockPos pivot,
                                                             BlockPos offset,
                                                             StructureTemplate.StructureBlockInfo templateInfo,
                                                             StructureTemplate.StructureBlockInfo placedInfo,
                                                             StructurePlaceSettings settings) {
        if (level instanceof ServerLevel serverLevel && !placedInfo.state().isAir()) {
            fillFoundationBelow(serverLevel, placedInfo.pos());
        }
        return placedInfo;
    }

    private void fillFoundationBelow(ServerLevel level, BlockPos structurePos) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(structurePos.getX(),
                structurePos.getY() - 1,
                structurePos.getZ());
        final int maxFoundationDepth = 64;
        int filled = 0;

        while (cursor.getY() > level.getMinBuildHeight() + 10 && filled < maxFoundationDepth) {
            BlockState state = level.getBlockState(cursor);
            if (isStableBlock(state)) {
                break;
            }

            if (state.isAir()) {
                level.setBlock(cursor, Blocks.STONE.defaultBlockState(), 3);
                filled++;
            }

            cursor.move(0, -1, 0);
        }
    }

    private static boolean isStableBlock(BlockState state) {
        return state.is(Blocks.STONE) ||
                state.is(Blocks.DIRT) ||
                state.is(Blocks.GRAVEL) ||
                state.is(Blocks.ANDESITE) ||
                state.is(Blocks.DIORITE) ||
                state.is(Blocks.SAND) ||
                state.is(Blocks.SANDSTONE) ||
                state.is(Blocks.DEEPSLATE) ||
                state.is(Blocks.GRANITE);
    }
}
