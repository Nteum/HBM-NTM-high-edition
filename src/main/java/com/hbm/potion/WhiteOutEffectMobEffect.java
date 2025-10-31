package net.mcreator.nuclearcraft.potion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.mcreator.nuclearcraft.procedures.WhiteOutEffectEffectStartedappliedProcedure;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/potion/WhiteOutEffectMobEffect.class */
public class WhiteOutEffectMobEffect extends MobEffect {
    public WhiteOutEffectMobEffect() {
        super(MobEffectCategory.HARMFUL, -1);
    }

    public List<ItemStack> getCurativeItems() {
        ArrayList<ItemStack> cures = new ArrayList<>();
        return cures;
    }

    public void m_6385_(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        WhiteOutEffectEffectStartedappliedProcedure.execute(entity.m_9236_(), entity.m_20185_(), entity.m_20186_(), entity.m_20189_());
    }

    public boolean m_6584_(int duration, int amplifier) {
        return true;
    }

    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() { // from class: net.mcreator.nuclearcraft.potion.WhiteOutEffectMobEffect.1
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
