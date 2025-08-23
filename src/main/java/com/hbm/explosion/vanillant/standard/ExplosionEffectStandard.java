package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IExplosionSFX;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExplosionEffectStandard implements IExplosionSFX {

	@Override
	public void doEffect(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {
		
		if(world.isClientSide)
			return;
		
//		world.playSoundEffect(x, y, z, "random.explode", 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
		world.playSound(null, x,y,z, ModSounds.ENTITY_OLD_EXPLOSION.get(), SoundSource.BLOCKS,  4.0F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
		
//		PacketDispatcher.wrapper.sendToAllAround(new ExplosionVanillaNewTechnologyCompressedAffectedBlockPositionDataForClientEffectsAndParticleHandlingPacket(x, y, z, explosion.size, explosion.compat.affectedBlockPositions),  new TargetPoint(world.provider.dimensionId, x, y, z, 250));
	}
	
	public static void performClient(Level world, double x, double y, double z, float size, List affectedBlocks) {
		
		if(size >= 2.0F) {
//			world.addParticle();
//			world.spawnParticle("hugeexplosion", x, y, z, 1.0D, 0.0D, 0.0D);
//			world.addParticle(ParticleTypes.EXPLOSION, x, y, z, 1.0D, 0.0D, 0.0D);
		} else {
//			world.spawnParticle("largeexplode", x, y, z, 1.0D, 0.0D, 0.0D);
//			world.addParticle();
		}

		int count = affectedBlocks.size();

		for(int i = 0; i < count; i++) {

			BlockPos pos = (BlockPos) affectedBlocks.get(i);
			int pX = pos.getX();
			int pY = pos.getY();
			int pZ = pos.getZ();

			double oX = (double) ((float) pX + world.random.nextFloat());
			double oY = (double) ((float) pY + world.random.nextFloat());
			double oZ = (double) ((float) pZ + world.random.nextFloat());
			double dX = oX - x;
			double dY = oY - y;
			double dZ = oZ - z;
			double delta = (double) Math.sqrt(dX * dX + dY * dY + dZ * dZ) / 1D /* hehehe */;
			dX /= delta;
			dY /= delta;
			dZ /= delta;
			double mod = 0.5D / (delta / (double) size + 0.1D);
			mod *= (double) (world.random.nextFloat() * world.random.nextFloat() + 0.3F);
			dX *= mod;
			dY *= mod;
			dZ *= mod;
			world.addParticle(ParticleTypes.EXPLOSION, (oX + x * 1.0D) / 2.0D, (oY + y * 1.0D) / 2.0D, (oZ + z * 1.0D) / 2.0D, dX, dY, dZ);
			world.addParticle(ParticleTypes.SMOKE, oX, oY, oZ, dX, dY, dZ);
//			world.spawnParticle("explode", (oX + x * 1.0D) / 2.0D, (oY + y * 1.0D) / 2.0D, (oZ + z * 1.0D) / 2.0D, dX, dY, dZ);
//			world.spawnParticle("smoke", oX, oY, oZ, dX, dY, dZ);
		}
	}
}
