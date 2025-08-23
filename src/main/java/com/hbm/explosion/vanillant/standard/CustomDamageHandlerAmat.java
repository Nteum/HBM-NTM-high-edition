package com.hbm.explosion.vanillant.standard;

import com.hbm.api.badthing.ContaminationUtil;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.ICustomDamageHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CustomDamageHandlerAmat implements ICustomDamageHandler {
	
	protected float radiation;
	
	public CustomDamageHandlerAmat(float radiation) {
		this.radiation = radiation;
	}
	/**
	 * 为了避免bug，暂时使用旧版辐射系统，以后是需要修改的
	 * */
	@Override
	public void handleAttack(ExplosionVNT explosion, Entity entity, double distanceScaled) {
		if(entity instanceof LivingEntity)
			ContaminationUtil.contaminate((LivingEntity)entity, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, (float) (radiation * (1D - distanceScaled) * explosion.size));
	}
}
