package com.hbm.item.blockitem;

import com.hbm.item.BlockItemHBM;
import com.hbm.main.ClientEventHanler;
import com.hbm.render.model.Models;
import com.hbm.render.model.armor.ModelArmorAJR;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

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
                return ClientEventHanler.getLazyItemRender();
            }
        });
    }
}
