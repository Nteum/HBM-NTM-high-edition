package com.hbm.handler.radiation;

import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.client.telemetry.events.WorldUnloadEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * A slightly more sophisticated version of the ChunkRadiationHandlerSimple, each chunk has 16 radiation values depending on height.
 * The bottom and topmost values extend up to infinity, preventing people from escaping radiation when leaving the build height.
 * @author hbm
 */
public class ChunkRadiationHandler3D extends ChunkRadiationHandler {
	
	private HashMap<Level, ThreeDimRadiationPerWorld> perWorld = new HashMap();

	@Override
//	@Untested
	public float getRadiation(net.minecraft.world.level.Level level, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		ThreeDimRadiationPerWorld radWorld = perWorld.get(level);

		if(radWorld != null) {
			ChunkPos coords = new ChunkPos(x >> 4, z >> 4);

			int yReg = Mth.clamp(y >> 4, 0, 15);
			
			Float rad = radWorld.radiation.get(coords)[yReg]; // this will crash if the coord pair isn't nullchecked
			return rad == null ? 0F : rad;
		}
		
		return 0;
	}

	@Override
	public void setRadiation(net.minecraft.world.level.Level level, BlockPos pos, float rad) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		ThreeDimRadiationPerWorld radWorld = perWorld.get(level);
		
		if(radWorld != null) {
			
			if(!level.getBlockState(new BlockPos(x,0,z)).isAir()) {
				
				ChunkPos coords = new ChunkPos(x >> 4, z >> 4);
				
				int yReg = Mth.clamp(y >> 4, 0, 15);
				
				if(radWorld.radiation.containsKey(coords)) {
					radWorld.radiation.get(coords)[yReg] = rad;
				}

				level.getChunk(x,z).setUnsaved(true);
//				world.getChunkFromBlockCoords(x, z).isModified = true;
			}
		}
	}

	@Override
	public void incrementRad(Level level, BlockPos pos, float rad) {
		setRadiation(level, pos, getRadiation(level, pos) + rad);
	}

	@Override
	public void decrementRad(net.minecraft.world.level.Level level, BlockPos pos, float rad) {
		setRadiation(level, pos, Math.max(getRadiation(level, pos) - rad, 0));
	}

	@Override
//	@Untested //will most definitely crash, for this to work i need to figure out what it even was i wanted to do in the first place
	public void updateSystem() {
		
		for(Entry<Level, ThreeDimRadiationPerWorld> entry : perWorld.entrySet()) {
			
			HashMap<ChunkPos, Float[]> radiation = entry.getValue().radiation;
			HashMap<ChunkPos, Float[]> buff = new HashMap(radiation);
			radiation.clear();
			
			for(Entry<ChunkPos, Float[]> chunk : buff.entrySet()) {
				
				ChunkPos coord = chunk.getKey();
				
				for(int y = 0; y < 16; y++) {
					
					for(int i = -1; i <= 1; i++) {
						for(int j = -1; j <= 1; j++) {
							for(int k = -1; k <= 1; k++) {
								
								int type = Math.abs(i) + Math.abs(j) + Math.abs(k);
								
								if(type == 3)
									continue;
								
								float percent = type == 0 ? 0.6F : type == 1 ? 0.075F : 0.025F;
								
								ChunkPos newCoord = new ChunkPos(coord.x + i, coord.z + k);
								
								if(buff.containsKey(newCoord)) {
									int newY = Mth.clamp(y + j, 0, 15);
									Float[] vals = radiation.get(newCoord); // ????????? but radiation was cleared!
									float newRad = vals[newY] + chunk.getValue()[newY] * percent;
									vals[newY] = Math.max(0F, newRad * 0.999F - 0.05F);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void clearSystem(Level level) {
		ThreeDimRadiationPerWorld radWorld = perWorld.get(level);
		
		if(radWorld != null) {
			radWorld.radiation.clear();
		}
	}

	@Override
	public void receiveWorldLoad(LevelEvent.Load event) {
		if(!event.getLevel().isClientSide())
			perWorld.put((Level) event.getLevel(), new ThreeDimRadiationPerWorld());
	}

	@Override
	public void receiveWorldUnload(LevelEvent.Unload event) {
		if(!event.getLevel().isClientSide())
			perWorld.remove((Level) event.getLevel());
	}
	
	private static final String NBT_KEY_CHUNK_RADIATION = "hfr_3d_radiation_";

	@Override
	public void receiveChunkLoad(ChunkDataEvent.Load event) {

		if(!event.getLevel().isClientSide()) {
			ThreeDimRadiationPerWorld radWorld = perWorld.get(event.getLevel());
			
			if(radWorld != null) {
				
				Float[] vals = new Float[16];
				
				for(int i = 0; i < 16; i++) {
					vals[i] = event.getData().getFloat(NBT_KEY_CHUNK_RADIATION + i);
				}
				
				radWorld.radiation.put(event.getChunk().getPos(), vals);
			}
		}
	}

	@Override
	public void receiveChunkSave(ChunkDataEvent.Save event) {
		
		if(!event.getLevel().isClientSide()) {
			ThreeDimRadiationPerWorld radWorld = perWorld.get(event.getLevel());
			
			if(radWorld != null) {
				Float[] vals = radWorld.radiation.get(event.getChunk().getPos());
				
				for(int i = 0; i < 16; i++) {
					float rad = vals[i] == null ? 0F : vals[i];
					event.getData().putFloat(NBT_KEY_CHUNK_RADIATION + i, rad);
				}
			}
		}
	}

	@Override
	public void receiveChunkUnload(ChunkEvent.Unload event) {
		
		if(!event.getLevel().isClientSide()) {
			ThreeDimRadiationPerWorld radWorld = perWorld.get(event.getLevel());
			
			if(radWorld != null) {
				radWorld.radiation.remove(event.getChunk());
			}
		}
	}
	
	public static class ThreeDimRadiationPerWorld {
		public HashMap<ChunkPos, Float[]> radiation = new HashMap();
	}
}
