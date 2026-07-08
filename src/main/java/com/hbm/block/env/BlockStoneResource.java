package com.hbm.block.env;

import com.hbm.block.BlockEnums;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStoneResource extends Block {
    public BlockStoneResource(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        // 💡 关键判断：只有当旧方块和新方块不是同一种方块时，才触发（避免 BlockState 改变时重复触发，比如换个朝向）
        if (!state.is(newState.getBlock())) {

            // 确保只在服务端运行逻辑，避免客户端同步错乱
            if (!level.isClientSide && state.is(ModBlocks.STONE_RESOURCE.get(BlockEnums.EnumStoneType.ASBESTOS).get())) {

                // 💡 替换为你模组的气体方块（这里假设你的气体也是一种 Block）
                // 如果你的气体是流体（Fluid），可以使用 ModFluids.GAS.get().defaultFluidState().createLegacyBlock()
                BlockState gasState = ModBlocks.GAS_RADON_ASBESTOS.get().defaultBlockState();

                // 在原地放置气体方块
                // 3 是常规更新标志（Flags.BLOCK_UPDATE | Flags.NOTIFY_CLIENT）
                level.setBlock(pos, gasState, 3);
            }
        }

        // 💡 必须调用父类方法，否则会破坏方块实体（BlockEntity）的清理和常规逻辑
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
