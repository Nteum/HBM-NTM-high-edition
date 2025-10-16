package com.hbm.blockentity;

import net.minecraft.world.entity.Entity;

public interface IRadarCommandReceiver {

	public boolean sendCommandPosition(int x, int y, int z);
	public boolean sendCommandEntity(Entity target);
}
