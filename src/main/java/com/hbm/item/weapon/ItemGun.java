package com.hbm.item.weapon;

import com.hbm.main.ClientEventHandler;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ItemGun extends Item {
    public ItemGun(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @OnlyIn(value = Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ClientEventHandler.getLazyItemRender();
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void renderGun(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay){
        float scale = 0.08f;
        float xRot = -45;
        float yRot = 225;
        float xOffset = 0.5f;
        float yOffset = 0.35f;
        float zOffSet = 0.5f;
        if (pDisplayContext != ItemDisplayContext.GUI){
            xRot = 0; scale = 0.04f;
            yOffset = 0.5f;
            if (pDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND){
                yRot = 135;
            }else if (pDisplayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
                yRot = 45;
            }else if (pDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND){
                yRot = -45;
            }
        }

        pPoseStack.pushPose();

        pPoseStack.translate(xOffset, yOffset, zOffSet);
        pPoseStack.mulPose(Axis.XN.rotationDegrees(xRot));
        pPoseStack.mulPose(Axis.YN.rotationDegrees(yRot));
        pPoseStack.scale(scale, scale, scale);
        BakedModel model = Models.get(BuiltInRegistries.ITEM.getKey(pStack.getItem()));
        RenderUtils.renderModel(model, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());

        pPoseStack.popPose();
    }
}
