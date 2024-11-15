package com.hbm.handler.radiation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;

public abstract class ChunkRadiationHandler {

	/**
	 * Updates the radiation system, i.e. all worlds.
	 * Doesn't need parameters because it governs the ENTIRE system.
	 */
	public abstract void updateSystem();
	public abstract float getRadiation(Level level, BlockPos pos);
	public abstract void setRadiation(Level level, BlockPos pos, float rad);
	public abstract void incrementRad(Level level, BlockPos pos, float rad);
	public abstract void decrementRad(Level level, BlockPos pos, float rad);
	public abstract void clearSystem(Level level);

	/*
	 * Proxy'd event handlers
	 */
	public void receiveWorldLoad(LevelEvent.Load event) { }
	public void receiveWorldUnload(LevelEvent.Unload event) { }
	public void receiveWorldTick(TickEvent.ServerTickEvent event) { }

	public void receiveChunkLoad(ChunkDataEvent.Load event) { }
	public void receiveChunkSave(ChunkDataEvent.Save event) { }
	public void receiveChunkUnload(ChunkEvent.Unload event) { }
	
	public void handleWorldDestruction() { }
}