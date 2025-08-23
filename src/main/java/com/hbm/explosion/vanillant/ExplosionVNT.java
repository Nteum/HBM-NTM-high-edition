package com.hbm.explosion.vanillant;

import com.google.common.collect.Maps;
import com.hbm.explosion.vanillant.interfaces.*;
import com.hbm.explosion.vanillant.standard.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * HBM参考原版爆炸逻辑做出来的爆炸类型。
 * 注意他参考的是1.7.10的爆炸逻辑，也许之后需要将其改造的更加现代化。
 * */
public class ExplosionVNT {

	//explosions only need one of these, in the unlikely event that we do need to combine different types we can just write a wrapper that acts as a chainloader
	private IBlockAllocator blockAllocator;
	private IEntityProcessor entityProcessor;
	private IBlockProcessor blockProcessor;
	private IPlayerProcessor playerProcessor;
	//since we want to reduce each effect to the bare minimum (sound, particles, etc. being separate) we definitely need multiple most of the time
	private IExplosionSFX[] sfx;
	
	public Level world;
	public double posX;
	public double posY;
	public double posZ;
	// 可以理解为爆炸半径
	public float size;
	// 引发爆炸的实体，可能是苦力怕之类的
	public Entity exploder;
	// 受冲击波影响的玩家
	private Map<Player, Vec3> compatPlayers = Maps.newHashMap();
	// 内部维护一个Explosion实例，主要是因为很多接口获得爆炸抗性需要传入一个爆炸实例

	public Explosion innerInstance;
	
	public ExplosionVNT(Level world, double x, double y, double z, float size) {
		this(world, x, y, z, size, null);
	}
	
	public ExplosionVNT(Level world, double x, double y, double z, float size, Entity exploder) {
		this.world = world;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.size = size;
		this.exploder = exploder;

		this.innerInstance = new Explosion(world, exploder, x, y, z, size, false, Explosion.BlockInteraction.DESTROY){
			@Override
			public Map<Player, Vec3> getHitPlayers() {
				return ExplosionVNT.this.compatPlayers;
			}
		};
	}

	public Map<Player, Vec3> getHitPlayers() {
		return ExplosionVNT.this.compatPlayers;
	}
	
	public void explode() {
		boolean processBlocks = blockAllocator != null && blockProcessor != null;
		boolean processEntities = entityProcessor != null && playerProcessor != null;
		
		HashSet<BlockPos> affectedBlocks = null;
		HashMap<Player, Vec3> affectedPlayers = null;
		
		//allocation
		if(processBlocks) affectedBlocks = blockAllocator.allocate(this, world, posX, posY, posZ, size);
		if(processEntities) affectedPlayers = entityProcessor.process(this, world, posX, posY, posZ, size);
		
		//serverside processing
		if(processBlocks) blockProcessor.process(this, world, posX, posY, posZ, affectedBlocks);
		if(processEntities) playerProcessor.process(this, world, posX, posY, posZ, affectedPlayers);
		
		//compat
		if(processBlocks) this.innerInstance.getToBlow().addAll(affectedBlocks);
		if(processEntities) this.compatPlayers.putAll(affectedPlayers);
		
		if(sfx != null) {
			for(IExplosionSFX fx : sfx) {
				fx.doEffect(this, world, posX, posY, posZ, size);
			}
		}
	}
	
	public ExplosionVNT setBlockAllocator(IBlockAllocator blockAllocator) {
		this.blockAllocator = blockAllocator;
		return this;
	}
	public ExplosionVNT setEntityProcessor(IEntityProcessor entityProcessor) {
		this.entityProcessor = entityProcessor;
		return this;
	}
	public ExplosionVNT setBlockProcessor(IBlockProcessor blockProcessor) {
		this.blockProcessor = blockProcessor;
		return this;
	}
	public ExplosionVNT setPlayerProcessor(IPlayerProcessor playerProcessor) {
		this.playerProcessor = playerProcessor;
		return this;
	}
	public ExplosionVNT setSFX(IExplosionSFX... sfx) {
		this.sfx = sfx;
		return this;
	}
	
	public ExplosionVNT makeStandard() {
		this.setBlockAllocator(new BlockAllocatorStandard());
		this.setBlockProcessor(new BlockProcessorStandard());
		this.setEntityProcessor(new EntityProcessorStandard());
		this.setPlayerProcessor(new PlayerProcessorStandard());
		this.setSFX(new ExplosionEffectStandard());
		return this;
	}
	
	public ExplosionVNT makeAmat() {
		this.setBlockAllocator(new BlockAllocatorStandard(this.size < 15 ? 16 : 32));
		this.setBlockProcessor(new BlockProcessorStandard()
				.setNoDrop());
		this.setEntityProcessor(new EntityProcessorStandard()
				.withRangeMod(2F)
				.withDamageMod(new CustomDamageHandlerAmat(50F)));
		this.setPlayerProcessor(new PlayerProcessorStandard());
		this.setSFX(new ExplosionEffectAmat());
		return this;
	}
}
