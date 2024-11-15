package com.hbm.handler.radiation;

import com.hbm.config.RadiationConfig;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.client.telemetry.events.WorldUnloadEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Most basic implementation of a chunk radiation system: Each chunk has a radiation value which spreads out to its neighbors.
 * @author hbm
 */
public class ChunkRadiationHandlerSimple extends ChunkRadiationHandler {
	
	private HashMap<Level, SimpleRadiationPerWorld> perWorld = new HashMap();
	private static final float maxRad = 100_000F;

	@Override
	public float getRadiation(Level level, BlockPos pos) {
		SimpleRadiationPerWorld radWorld = perWorld.get(level);
		if(radWorld != null) {
			ChunkPos coords = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
			Float rad = radWorld.radiation.get(coords);
			return rad == null ? 0F : Mth.clamp(rad, 0, maxRad);
		}
		
		return 0;
	}

	@Override
	public void setRadiation(Level level, BlockPos pos, float rad) {
		int x = pos.getX();
		int z = pos.getZ();
		SimpleRadiationPerWorld radWorld = perWorld.get(level);
		
		if(radWorld != null) {
			
			if(!level.getBlockState(new BlockPos(x,0,z)).isAir()) {
				
				ChunkPos coords = new ChunkPos(x >> 4, z >> 4);
				radWorld.radiation.put(coords, Mth.clamp(rad, 0, maxRad));
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
	public void decrementRad(Level level, BlockPos pos, float rad) {
		setRadiation(level, pos, Math.max(getRadiation(level, pos) - rad, 0));
	}

	@Override
	public void updateSystem() {
		
		for(Entry<Level, SimpleRadiationPerWorld> entry : perWorld.entrySet()) {
			
			HashMap<ChunkPos, Float> radiation = entry.getValue().radiation;
			HashMap<ChunkPos, Float> buff = new HashMap(radiation);
			radiation.clear();
			Level level = entry.getKey();
			
			for(Entry<ChunkPos, Float> chunk : buff.entrySet()) {
				
				if(chunk.getValue() == 0)
					continue;
				
				ChunkPos coord = chunk.getKey();
				
				for(int i = -1; i <= 1; i++) {
					for(int j = -1; j<= 1; j++) {
						
						int type = Math.abs(i) + Math.abs(j);
						float percent = type == 0 ? 0.6F : type == 1 ? 0.075F : 0.025F;
						ChunkPos newCoord = new ChunkPos(coord.x + i, coord.z + j);
						
						if(buff.containsKey(newCoord)) {
							Float val = radiation.get(newCoord);
							float rad = val == null ? 0 : val;
							float newRad = rad + chunk.getValue() * percent;
							newRad = Mth.clamp(0F, newRad * 0.99F - 0.05F, maxRad);
							radiation.put(newCoord, newRad);
						} else {
							radiation.put(newCoord, chunk.getValue() * percent);
						}
						
						float rad = radiation.get(newCoord);
						if(rad > RadiationConfig.fogRad && level != null && level.random.nextInt(RadiationConfig.fogCh) == 0 && level.hasChunk(coord.x,coord.z)) {
							
							int x = coord.x * 16 + level.random.nextInt(16);
							int z = coord.z * 16 + level.random.nextInt(16);
//							int y = level.getHeightValue(x, z) + level.random.nextInt(5);

//							PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacket(x, y, z, 3), new TargetPoint(world.provider.dimensionId, x, y, z, 100));
						}
					}
				}
			}
		}
	}

	@Override
	public void clearSystem(Level level) {
		SimpleRadiationPerWorld radWorld = perWorld.get(level);
		
		if(radWorld != null) {
			radWorld.radiation.clear();
		}
	}

	@Override
	public void receiveWorldLoad(LevelEvent.Load event) {
		if(!event.getLevel().isClientSide())
			perWorld.put((Level) event.getLevel(), new SimpleRadiationPerWorld());
	}

	@Override
	public void receiveWorldUnload(LevelEvent.Unload event) {
		if(!event.getLevel().isClientSide())
			perWorld.remove((Level) event.getLevel());
	}
	
	private static final String NBT_KEY_CHUNK_RADIATION = "hfr_simple_radiation";

	@Override
	public void receiveChunkLoad(ChunkDataEvent.Load event) {
		
		if(!event.getLevel().isClientSide()) {
			SimpleRadiationPerWorld radWorld = perWorld.get(event.getLevel());
			
			if(radWorld != null) {
				radWorld.radiation.put(event.getChunk().getPos(), event.getData().getFloat(NBT_KEY_CHUNK_RADIATION));
			}
		}
	}

	@Override
	public void receiveChunkSave(ChunkDataEvent.Save event) {
		
		if(!event.getLevel().isClientSide()) {
			SimpleRadiationPerWorld radWorld = perWorld.get(event.getLevel());
			
			if(radWorld != null) {
				Float val = radWorld.radiation.get(event.getChunk().getPos());
				float rad = val == null ? 0F : val;
				event.getData().putFloat(NBT_KEY_CHUNK_RADIATION, rad);
			}
		}
	}

	@Override
	public void receiveChunkUnload(ChunkEvent.Unload event) {
		
		if(!event.getLevel().isClientSide()) {
			SimpleRadiationPerWorld radWorld = perWorld.get(event.getLevel());
			
			if(radWorld != null) {
				radWorld.radiation.remove(event.getChunk());
			}
		}
	}
	
	public static class SimpleRadiationPerWorld {
		
		public HashMap<ChunkPos, Float> radiation = new HashMap();
	}
	//辐射造成环境变化，特定区块变成废土
	@Override
	public void handleWorldDestruction() {
		
		int count = 10;
		int threshold = 10;
		int chunks = 5;
		
		//for all worlds
		for(Entry<Level, SimpleRadiationPerWorld> per : perWorld.entrySet()) {
			
			Level level = per.getKey();
			SimpleRadiationPerWorld list = per.getValue();
			
			Object[] entries = list.radiation.entrySet().toArray();
			
			if(entries.length == 0)
				continue;
			
			//chose this many random chunks
			for(int c = 0; c < chunks; c++) {
				
				Entry<ChunkPos, Float> randEnt = (Entry<ChunkPos, Float>) entries[level.random.nextInt(entries.length)];
				
				ChunkPos coords = randEnt.getKey();
				ServerLevel serv = (ServerLevel) level;
				ChunkSource provider = (ChunkSource) serv.getChunkSource();
				
				//choose this many random locations within the chunk
				for(int i = 0; i < count; i++) {
					
					if(randEnt == null || randEnt.getValue() < threshold)
						continue;
					
					if(provider.hasChunk(coords.x, coords.z)) {
						
						for(int a = 0; a < 16; a++) {
							for(int b = 0; b < 16; b++) {
								
								if(level.random.nextInt(3) != 0)
									continue;
								
								int x = coords.getMiddleBlockX() - 8 + a;
								int z = coords.getMiddleBlockZ() - 8 + b;
//								int y = world.getHeightValue(x, z) - level.random.nextInt(2);
								
//								if(level.getBlockState(new BlockPos(x,y,z)).is(Blocks.GRASS)) {
////									level.setBlock(new BlockPos(x,y,z), ModBlocks.waste_earth);
//
//								} else if(level.getBlockState(new BlockPos(x,y,z)).is(Blocks.TALL_GRASS)) {
//									level.setBlock(new BlockPos(x,y,z), Blocks.AIR.defaultBlockState(),11);
//
//								} else if(level.getBlockState(new BlockPos(x,y,z)).getMaterial() == Material.leaves && !(level.getBlockState(new BlockPos(x,y,z)) == ModBlocks.waste_leaves)) {
//
//									if(level.random.nextInt(7) <= 5) {
////										level.setBlock(new BlockPos(x,y,z), ModBlocks.waste_leaves);
//									} else {
//										level.setBlock(new BlockPos(x,y,z), Blocks.AIR.defaultBlockState(),11);
//									}
//								}
							}
						}
					}
				}
			}
		}
	}
}
