package com.hbm.item.armor;

import com.hbm.HBM;
import com.hbm.item.HBMCombat;
import com.hbm.main.ClientSetup;
import com.hbm.render.model.Models;
import com.hbm.render.model.armor.ModelArmorBismuth;
import com.hbm.render.model.armor.ModelArmorDesh;
import com.hbm.render.model.armor.ModelArmorT51;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemArmorDesh extends ItemArmorFSBFueled{
    public ItemArmorDesh(ArmorMaterial pMaterial, Type pType, Properties pProperties, Fluid fuel, int maxFuel, int fillRate, int consumption, int drain) {
        super(pMaterial, pType, pProperties, fuel, maxFuel, fillRate, consumption, drain);
    }

    public ItemArmorDesh(ArmorMaterial pMaterial, Type pType, Properties pProperties, Fluid fuel, int maxFuel, int fillRate, int consumption, int drain, Supplier<HBMCombat.Suit> suit) {
        super(pMaterial, pType, pProperties, fuel, maxFuel, fillRate, consumption, drain, suit);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                return ((ModelArmorDesh) Models.getEntityModel(Models.DESH)).adjustWithOrigin(original, equipmentSlot);
            }
//
//            @Override
//            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
//                return ClientSetup.getLazyItemRender();
//            }
        });
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        switch (slot){
            case HEAD -> {
                return HBM.rl("textures/models/armor/steamsuit_helmet.png").toString();
            }
            case CHEST -> {
                return HBM.rl("textures/models/armor/steamsuit_chest.png").toString();
            }
            case LEGS, FEET -> {
                return HBM.rl("textures/models/armor/steamsuit_leg.png").toString();
            }
        }
        return null;
    }
}
