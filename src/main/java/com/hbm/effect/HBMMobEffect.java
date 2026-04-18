package com.hbm.effect;

import com.hbm.addational_data.entity.EntityEffectHandler;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class HBMMobEffect extends MobEffect {
    protected HBMMobEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }
    // 重写此方法以决定效果是否在每个tick都被应用
    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        if (this == ModEffects.RADIATION.get()){
            return scaleDuration(25, pDuration, pAmplifier);
        }
        return true;
    }
    public static boolean scaleDuration(int scale, int pDuration, int pAmplifier){
        int j = scale >> pAmplifier;
        if (j > 0) {
            return pDuration % j == 0;
        } else {
            return true;
        }
    }
    // 重写此方法来实现效果的具体逻辑（在服务端执行）
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide()){
            Level level = entity.level();
            if (this == ModEffects.RADIATION.get()){
                if (amplifier == 4){
                    if(level.random.nextInt(300) == 0) entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 30, 0));
                    if(level.random.nextInt(300) == 0) entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10 * 20, 2));
                    if(level.random.nextInt(300) == 0) entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 20, 2));
                    if(level.random.nextInt(500) == 0) entity.addEffect(new MobEffectInstance(MobEffects.POISON, 3 * 20, 2));
                    if(level.random.nextInt(700) == 0) entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 3 * 20, 1));
                }else if (amplifier == 3){
                    if(level.random.nextInt(300 / 3) == 0) entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 30, 0));
                    if(level.random.nextInt(300 / 3) == 0) entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10 * 20, 2));
                    if(level.random.nextInt(300 / 3) == 0) entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 20, 2));
                    if(level.random.nextInt(500 / 3) == 0) entity.addEffect(new MobEffectInstance(MobEffects.POISON, 3 * 20, 1));
                }else if (amplifier == 2){
                    if(level.random.nextInt(300 / 6) == 0) entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 30, 0));
                    if(level.random.nextInt(500 / 6) == 0) entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5 * 20, 0));
                    if(level.random.nextInt(300 / 6) == 0) entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 5 * 20, 1));
                }else if (amplifier == 1){
                    if(level.random.nextInt(300 / 12) == 0) entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 20, 0));
                    if(level.random.nextInt(500 / 12) == 0) entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 5 * 20, 0));
                }
            }
        }
    }
}
