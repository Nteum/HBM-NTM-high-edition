package net.mcreator.nuclearcraft.potion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.mcreator.nuclearcraft.procedures.ScreenShakeEffectActiveTickConditionProcedure;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/potion/ScreenShakeEffectMobEffect.class */
public class ScreenShakeEffectMobEffect extends MobEffect {
    public ScreenShakeEffectMobEffect() {
        super(MobEffectCategory.NEUTRAL, -1);
    }

    public List<ItemStack> getCurativeItems() {
        ArrayList<ItemStack> cures = new ArrayList<>();
        return cures;
    }

    public void m_6742_(LivingEntity entity, int amplifier) {
        ScreenShakeEffectActiveTickConditionProcedure.execute(entity);
    }

    public boolean m_6584_(int duration, int amplifier) {
        return true;
    }

    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() { // from class: net.mcreator.nuclearcraft.potion.ScreenShakeEffectMobEffect.1
            public boolean isVisibleInInventory(MobEffectInstance effect) {
                return false;
            }

            public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                return false;
            }

            public boolean isVisibleInGui(MobEffectInstance effect) {
                return false;
            }
        });
    }
}
