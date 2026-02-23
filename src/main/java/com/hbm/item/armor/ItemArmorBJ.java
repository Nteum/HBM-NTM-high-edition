package com.hbm.item.armor;

import com.hbm.item.HBMCombat;
import com.hbm.main.ClientEventHandler;
import com.hbm.render.model.Models;
import com.hbm.render.model.armor.ModelArmorBJ;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ItemArmorBJ extends ItemArmorFSBPowered{
    public ItemArmorBJ(ArmorMaterial pMaterial, Type pType, Properties pProperties, long capacity, long in, long consum, long drain) {
        super(pMaterial, pType, pProperties, capacity, in, consum, drain);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                return ((ModelArmorBJ) Models.getEntityModel(Models.BJ)).adjustWithOrigin(original, equipmentSlot);
            }

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ClientEventHandler.getLazyItemRender();
            }
        });
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (stack.getItem() instanceof ItemArmorRPA armorRPA){
            switch (armorRPA.getType()){
                case HELMET -> {
                    return "hbm:textures/models/armor/bj_eyepatch.png";
                }
                case CHESTPLATE -> {
                    return "hbm:textures/models/armor/bj_chest.png";
                }
                case BOOTS, LEGGINGS -> {
                    return "hbm:textures/models/armor/bj_leg.png";
                }
            }
        }
        return null;
    }

    @Override
    public void renderObjItem(ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ModelArmorBJ model = (ModelArmorBJ) Models.getEntityModel(Models.BJ);
        if (this == HBMCombat.BJ_JETPACK.get()){
            pPoseStack.scale(0.6875f, 0.6875f, 0.6875f);
            model.jetpack.renderStatic(pPoseStack, pBuffer.getBuffer(RenderType.armorCutoutNoCull(ModelArmorBJ.jetpackTex)), pPackedLight, pPackedOverlay);
        } else {
            pPoseStack.scale(0.875f, 0.875f, 0.875f);
            renderStandard(this, model, "bj_eyepatch", "bj_chest", "bj_arm", "bj_leg",pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        }
    }

}
