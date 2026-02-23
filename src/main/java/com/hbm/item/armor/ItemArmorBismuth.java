package com.hbm.item.armor;

import com.hbm.HBM;
import com.hbm.item.HBMCombat;
import com.hbm.main.ClientEventHandler;
import com.hbm.render.model.Models;
import com.hbm.render.model.armor.ModelArmorBismuth;
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
import java.util.function.Supplier;

public class ItemArmorBismuth extends ItemArmorFSB {
	
	public ItemArmorBismuth(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
		super(pMaterial, pType, pProperties);
	}
	public ItemArmorBismuth(ArmorMaterial pMaterial, Type pType, Properties pProperties, Supplier<HBMCombat.Suit> suit) {
		super(pMaterial, pType, pProperties);
		this.suit = suit;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
				return ((ModelArmorBismuth) Models.getEntityModel(Models.BISMUTH)).adjustWithOrigin(original, equipmentSlot);
			}

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return ClientEventHandler.getLazyItemRender();
			}
		});
	}

	@Override
	public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return HBM.rl("textures/models/armor/bismuth.png").toString();
	}

	@Override
	public void renderObjItem(ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		renderStandard(this, (ModelArmorT51)Models.getEntityModel(Models.T51), "bismuth", "bismuth", "bismuth", "bismuth",
				pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
	}
}
