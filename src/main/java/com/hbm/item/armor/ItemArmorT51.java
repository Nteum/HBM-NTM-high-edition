package com.hbm.item.armor;

import com.hbm.main.ClientEventHandler;
import com.hbm.render.model.Models;
import com.hbm.render.model.armor.ModelArmorT51;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
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

public class ItemArmorT51 extends ItemArmorFSBPowered{
    public ItemArmorT51(ArmorMaterial pMaterial, Type pType, Properties pProperties, long capacity, long in, long consum, long drain) {
        super(pMaterial, pType, pProperties, capacity, in, consum, drain);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                return ((ModelArmorT51) Models.getEntityModel(Models.T51)).adjustWithOrigin(original, equipmentSlot);
            }

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ClientEventHandler.getLazyItemRender();
            }
        });
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (stack.getItem() instanceof ItemArmorT51 armorT51){
            switch (armorT51.type){
                case HELMET -> {
                    return "hbm:textures/models/armor/t51_helmet.png";
                }
                case CHESTPLATE -> {
                    return "hbm:textures/models/armor/t51_chest.png";
                }
                case BOOTS, LEGGINGS -> {
                    return "hbm:textures/models/armor/t51_leg.png";
                }
            }
        }
        return null;
    }

    @Override
    public void renderObjItem(ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        renderStandard(this, (ModelArmorT51)Models.getEntityModel(Models.T51), "t51_helmet", "t51_chest", "t51_arm", "t51_leg",pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
    }
}
