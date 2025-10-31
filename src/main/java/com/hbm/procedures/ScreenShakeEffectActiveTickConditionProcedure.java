package net.mcreator.nuclearcraft.procedures;

import net.mcreator.nuclearcraft.network.BigExplosivesModVariables;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/ScreenShakeEffectActiveTickConditionProcedure.class */
public class ScreenShakeEffectActiveTickConditionProcedure {
    public static void execute(Entity entity) {
        if (entity == null) {
            return;
        }
        double _setval = ((BigExplosivesModVariables.PlayerVariables) entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new BigExplosivesModVariables.PlayerVariables())).ScreenShake + 1.0d;
        entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).ifPresent(capability -> {
            capability.ScreenShake = _setval;
            capability.syncPlayerVariables(entity);
        });
        if (((BigExplosivesModVariables.PlayerVariables) entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new BigExplosivesModVariables.PlayerVariables())).ScreenShake == 0.0d) {
            entity.m_146922_(entity.m_146908_() + 1.0f);
            entity.m_146926_(entity.m_146909_() + 1.0f);
            entity.m_5618_(entity.m_146908_());
            entity.m_5616_(entity.m_146908_());
            entity.f_19859_ = entity.m_146908_();
            entity.f_19860_ = entity.m_146909_();
            if (entity instanceof LivingEntity) {
                LivingEntity _entity = (LivingEntity) entity;
                _entity.f_20884_ = _entity.m_146908_();
                _entity.f_20886_ = _entity.m_146908_();
            }
        } else if (((BigExplosivesModVariables.PlayerVariables) entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new BigExplosivesModVariables.PlayerVariables())).ScreenShake == 1.0d) {
            entity.m_146922_(entity.m_146908_() - 2.0f);
            entity.m_146926_(entity.m_146909_() - 2.0f);
            entity.m_5618_(entity.m_146908_());
            entity.m_5616_(entity.m_146908_());
            entity.f_19859_ = entity.m_146908_();
            entity.f_19860_ = entity.m_146909_();
            if (entity instanceof LivingEntity) {
                LivingEntity _entity2 = (LivingEntity) entity;
                _entity2.f_20884_ = _entity2.m_146908_();
                _entity2.f_20886_ = _entity2.m_146908_();
            }
        } else if (((BigExplosivesModVariables.PlayerVariables) entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new BigExplosivesModVariables.PlayerVariables())).ScreenShake == 2.0d) {
            entity.m_146922_(entity.m_146908_() + 1.0f);
            entity.m_146926_(entity.m_146909_() + 1.0f);
            entity.m_5618_(entity.m_146908_());
            entity.m_5616_(entity.m_146908_());
            entity.f_19859_ = entity.m_146908_();
            entity.f_19860_ = entity.m_146909_();
            if (entity instanceof LivingEntity) {
                LivingEntity _entity3 = (LivingEntity) entity;
                _entity3.f_20884_ = _entity3.m_146908_();
                _entity3.f_20886_ = _entity3.m_146908_();
            }
        } else if (((BigExplosivesModVariables.PlayerVariables) entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new BigExplosivesModVariables.PlayerVariables())).ScreenShake == 3.0d) {
            entity.m_146922_(entity.m_146908_() + 1.0f);
            entity.m_146926_(entity.m_146909_() - 1.0f);
            entity.m_5618_(entity.m_146908_());
            entity.m_5616_(entity.m_146908_());
            entity.f_19859_ = entity.m_146908_();
            entity.f_19860_ = entity.m_146909_();
            if (entity instanceof LivingEntity) {
                LivingEntity _entity4 = (LivingEntity) entity;
                _entity4.f_20884_ = _entity4.m_146908_();
                _entity4.f_20886_ = _entity4.m_146908_();
            }
        } else if (((BigExplosivesModVariables.PlayerVariables) entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new BigExplosivesModVariables.PlayerVariables())).ScreenShake == 4.0d) {
            entity.m_146922_(entity.m_146908_() + 0.0f);
            entity.m_146926_(entity.m_146909_() + 1.0f);
            entity.m_5618_(entity.m_146908_());
            entity.m_5616_(entity.m_146908_());
            entity.f_19859_ = entity.m_146908_();
            entity.f_19860_ = entity.m_146909_();
            if (entity instanceof LivingEntity) {
                LivingEntity _entity5 = (LivingEntity) entity;
                _entity5.f_20884_ = _entity5.m_146908_();
                _entity5.f_20886_ = _entity5.m_146908_();
            }
        }
        if (((BigExplosivesModVariables.PlayerVariables) entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new BigExplosivesModVariables.PlayerVariables())).ScreenShake > 4.0d) {
            double _setval2 = 0.0d;
            entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).ifPresent(capability2 -> {
                capability2.ScreenShake = _setval2;
                capability2.syncPlayerVariables(entity);
            });
        }
    }
}
