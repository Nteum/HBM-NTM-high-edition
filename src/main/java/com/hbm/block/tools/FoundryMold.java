package com.hbm.block.tools;

import com.hbm.blockentity.tools.TileFoundryMold;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FoundryMold extends FoundryCastingBase{
    public FoundryMold(Properties properties) {
        super(properties);
        SHAPE = Shapes.or(
                Block.box(0, 0, 0, 16, 2, 16),
                Block.box(0, 2, 0, 14, 8, 2),
                Block.box(0, 2, 2, 2, 8, 16),
                Block.box(2, 2, 14, 16, 8, 16),
                Block.box(14, 2, 0, 16, 8, 14)
        );

    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileFoundryMold(pPos, pState);
    }

    @Override
    public List<Component> getDesc(Level level, BlockPos pos) {
        return List.of(
                Component.translatable(this.getDescriptionId()).withStyle(ChatFormatting.BLUE),
                Component.literal("Insert your mold.").withStyle(ChatFormatting.YELLOW)
        );
    }
}
