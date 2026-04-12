package com.hbm.block.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public interface IToolable {
    boolean onScrew(UseOnContext context, ToolType tool);

}
