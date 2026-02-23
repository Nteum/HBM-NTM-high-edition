package com.hbm.item.blockitem;

import com.hbm.item.BlockItemHBM;
import com.hbm.main.ClientEventHandler;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class BlockItemDummyable extends BlockItemHBM {
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
