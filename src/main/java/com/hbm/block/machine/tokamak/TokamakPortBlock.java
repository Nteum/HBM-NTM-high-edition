package com.hbm.block.machine.tokamak;

import com.hbm.blockentity.machine.tokamak.TokamakPortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 接口方块，可用来接入能量/冷却流体/输出等。
 * 具体处理由控制器在结构校验时感知。
 */
public class TokamakPortBlock extends Block implements EntityBlock {
    public TokamakPortBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TokamakPortBlockEntity(pos, state);
    }
}
