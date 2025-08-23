package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IExplosionSFX;
import com.hbm.registries.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;


public class ExplosionEffectAmat implements IExplosionSFX {

	@Override
	public void doEffect(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {

		if(size < 15)
			world.playSound(null, x,y,z, ModSounds.ENTITY_OLD_EXPLOSION.get(), SoundSource.BLOCKS, 4.0f, (1.4F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
//			world.playSoundEffect(x, y, z, "random.explode", 4.0F, (1.4F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
		else
			world.playSound(null, x,y,z, ModSounds.WEAPON_MUKE_EXPLOSION.get(), SoundSource.BLOCKS, 15.0F, 1.0F);
//			world.playSoundEffect(x, y, z, "hbm:weapon.mukeExplosion", 15.0F, 1.0F);

		CompoundTag data = new CompoundTag();
		data.putString("type", "amat");
		data.putFloat("scale", size);
//		PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(data, x, y, z), new TargetPoint(world.provider.dimensionId, x, y, z, 200));
	}

}
