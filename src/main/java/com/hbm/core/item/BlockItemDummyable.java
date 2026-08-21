package com.hbm.core.item;

import com.hbm.core.item.BlockItemCapacities;
import com.hbm.main.ClientEventHandler;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class BlockItemDummyable extends BlockItem {
    protected Vec3i axisLen;
    public BlockItemDummyable(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ClientEventHandler.getLazyItemRender();
            }
        });
    }
}
