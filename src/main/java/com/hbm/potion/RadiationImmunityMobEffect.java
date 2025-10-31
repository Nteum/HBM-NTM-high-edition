package net.mcreator.nuclearcraft.potion;

import java.util.ArrayList;
import java.util.List;
import net.mcreator.nuclearcraft.procedures.RadiationImmunityOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/potion/RadiationImmunityMobEffect.class */
public class RadiationImmunityMobEffect extends MobEffect {
    public RadiationImmunityMobEffect() {
        super(MobEffectCategory.HARMFUL, -256);
    }

    public List<ItemStack> getCurativeItems() {
        ArrayList<ItemStack> cures = new ArrayList<>();
        return cures;
    }

    public void m_6742_(LivingEntity entity, int amplifier) {
        RadiationImmunityOnEffectActiveTickProcedure.execute(entity);
    }

    public boolean m_6584_(int duration, int amplifier) {
        return true;
    }
}
