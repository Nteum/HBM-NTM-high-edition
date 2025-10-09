package com.hbm.item.armor;

import com.hbm.item.HBMCombat;
import com.hbm.main.ClientSetup;
import com.hbm.render.model.Models;
import com.hbm.render.model.armor.ModelArmorAJR;
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

public class ItemArmorAJR extends ItemArmorFSBPowered{
    public String nameStr = "ajr";
    public ItemArmorAJR(ArmorMaterial pMaterial, Type pType, Properties pProperties, long capacity, long in, long consum, long drain) {
        super(pMaterial, pType, pProperties, capacity, in, consum, drain);
    }
    public ItemArmorAJR setName(String str){
        this.nameStr = str;
        return this;
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                return ((ModelArmorAJR) Models.getEntityModel(Models.AJR)).name(nameStr).adjustWithOrigin(original, equipmentSlot);
            }

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ClientSetup.getLazyItemRender();
            }
        });
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (stack.getItem() instanceof ItemArmorAJR armorAJR){
            switch (armorAJR.getType()){
                case HELMET -> {
                    return "hbm:textures/models/armor/" + nameStr + "_helmet.png";
                }
                case CHESTPLATE -> {
                    return "hbm:textures/models/armor/" + nameStr + "_chest.png";
                }
                case BOOTS, LEGGINGS -> {
                    return "hbm:textures/models/armor/" + nameStr + "_leg.png";
                }
            }
        }
        return null;
    }

    @Override
    public void renderObjItem(ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        renderStandard(this, (ModelArmorT51)Models.getEntityModel(Models.T51), nameStr + "_helmet", nameStr + "_chest", nameStr + "_arm", nameStr + "_leg",pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
    }
}
