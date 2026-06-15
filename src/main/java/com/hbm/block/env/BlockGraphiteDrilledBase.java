package com.hbm.block.env;

import com.hbm.block.interfaces.IToolable;
import com.hbm.block.interfaces.ToolType;
import com.hbm.registries.ModBlocks;
import net.minecraft.world.item.context.UseOnContext;

public class BlockGraphiteDrilledBase extends BlockFireCustom implements IToolable {
    public BlockGraphiteDrilledBase() {
        super(Properties.copy(ModBlocks.BLOCK_GRAPHITE.get()), ((BlockFireCustom) ModBlocks.BLOCK_GRAPHITE.get()).encouragement, ((BlockFireCustom) ModBlocks.BLOCK_GRAPHITE.get()).flammability);
    }

    @Override
    public boolean onScrew(UseOnContext context, ToolType tool) {
        return false;
    }
}
