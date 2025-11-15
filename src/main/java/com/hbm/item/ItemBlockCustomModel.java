package com.hbm.item;

import com.hbm.block.interfaces.ICustomBlockItemModel;
import com.hbm.main.ClientEventHanler;
import com.hbm.render.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

/**
 * 可以自定义模型的方块物品，用于一些通过obj渲染的方块在gui中的显示
 * */
public class ItemBlockCustomModel extends BlockItemHBM {
    // 特殊物品的尺度，用于规定模型的缩放程度。
    float size = 1.0f;
    public ItemBlockCustomModel(Block pBlock, Properties pProperties, float size) {
        super(pBlock, pProperties);
        this.size = size;
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

    @OnlyIn(Dist.CLIENT)
    public void renderItemModel(ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay){
        float scale = 1 / size;
        float xRot = -45;
        float yRot = 225;
        float xOffset = 0f;
        float yOffset = 0f;
        float zOffSet = 0f;
        if (pDisplayContext != ItemDisplayContext.GUI){
            xRot = 0; scale = scale / 2;
            yOffset = 0.5f;
            if (pDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND){
                yRot = 135;
            }else if (pDisplayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
                yRot = 45;
            }else if (pDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND){
                yRot = -45;
            }
        }

        pPoseStack.translate(xOffset, yOffset, zOffSet);
        pPoseStack.mulPose(Axis.XN.rotationDegrees(xRot));
        pPoseStack.mulPose(Axis.YN.rotationDegrees(yRot));
        pPoseStack.scale(scale, scale, scale);
        if (getBlock() instanceof ICustomBlockItemModel customBlockItemModel){
            BakedModel[] models = customBlockItemModel.getModels();
            for (BakedModel model : models) {
                RenderUtils.renderModel(model, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            }
        }
    }
}
