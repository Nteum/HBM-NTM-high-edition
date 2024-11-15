package com.hbm.handler.radiation;

import com.hbm.config.RadiationConfig;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ChunkRadiationManager {
	
	public static ChunkRadiationHandler proxy = new ChunkRadiationHandlerSimple();

	@SubscribeEvent
	public void onWorldLoad(LevelEvent.Load event) {
		if(RadiationConfig.enableChunkRads) proxy.receiveWorldLoad(event);
	}
	
	@SubscribeEvent
	public void onWorldUnload(LevelEvent.Unload event) {
		if(RadiationConfig.enableChunkRads) proxy.receiveWorldUnload(event);
	}

	@SubscribeEvent
	public void onChunkLoad(ChunkDataEvent.Load event) {
		if(RadiationConfig.enableChunkRads) proxy.receiveChunkLoad(event);
	}
	
	@SubscribeEvent
	public void onChunkSave(ChunkDataEvent.Save event) {
		if(RadiationConfig.enableChunkRads) proxy.receiveChunkSave(event);
	}
	
	@SubscribeEvent
	public void onChunkUnload(ChunkEvent.Unload event) {
		if(RadiationConfig.enableChunkRads) proxy.receiveChunkUnload(event);
	}
	
	int eggTimer = 0;
	
	@SubscribeEvent
	public void updateSystem(TickEvent.ServerTickEvent event) {
		
		if(RadiationConfig.enableChunkRads && event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
			
			eggTimer++;
			
			if(eggTimer >= 20) {
				proxy.updateSystem();
				eggTimer = 0;
			}
			
			if(RadiationConfig.worldRadEffects) {
				proxy.handleWorldDestruction();
			}
			
			proxy.receiveWorldTick(event);
		}
	}
}
