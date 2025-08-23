package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.ICustomDamageHandler;
import com.hbm.explosion.vanillant.interfaces.IEntityProcessor;
import com.hbm.explosion.vanillant.interfaces.IEntityRangeMutator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.HashMap;
import java.util.List;
/**
 * 对应Explosion中对实体造成伤害的部分
 * */
public class EntityProcessorStandard implements IEntityProcessor {

	protected IEntityRangeMutator range;
	protected ICustomDamageHandler damage;
	protected boolean allowSelfDamage = false;

	@Override
	public HashMap<Player, Vec3> process(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {

		HashMap<Player, Vec3> affectedPlayers = new HashMap();

		size *= 2.0F;
		
		if(range != null) {
			size = range.mutateRange(explosion, size);
		}
		
		double minX = x - (double) size - 1.0D;
		double maxX = x + (double) size + 1.0D;
		double minY = y - (double) size - 1.0D;
		double maxY = y + (double) size + 1.0D;
		double minZ = z - (double) size - 1.0D;
		double maxZ = z + (double) size + 1.0D;

		List<Entity> list = world.getEntities(allowSelfDamage ? null : explosion.exploder, new AABB(minX, minY, minZ, maxX, maxY, maxZ));
//		List list = world.getEntitiesWithinAABBExcludingEntity(allowSelfDamage ? null : explosion.exploder, AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ));
		
		ForgeEventFactory.onExplosionDetonate(world, explosion.innerInstance, list, size);
		Vec3 vec3 = new Vec3(x, y, z);

		for(int index = 0; index < list.size(); ++index) {
			
			Entity entity = (Entity) list.get(index);
			double distanceScaled = entity.distanceToSqr(x, y, z) / size;

			if(distanceScaled <= 1.0D) {
				
				double deltaX = entity.getX() - x;
				double deltaY = entity.getY() + entity.getEyeHeight() - y;
				double deltaZ = entity.getZ() - z;
				double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

				if(distance != 0.0D) {
					
					deltaX /= distance;
					deltaY /= distance;
					deltaZ /= distance;

					/**
					 * 高版本没有对应的对密度的计算方式，其他实现方式比较复杂，暂时直接规定为1.0
					 * */
//					world.clip(new ClipContext(explosion.exploder.position(), entity.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).
//					double density = world.getBlockDensity(vec3, entity.boundingBox);
					double density = 1.0;
					double knockback = (1.0D - distanceScaled) * density;

					entity.hurt(setExplosionSource(explosion.innerInstance), calculateDamage(distanceScaled, density, knockback, size));
//					entity.attackEntityFrom(setExplosionSource(explosion.innerInstance), calculateDamage(distanceScaled, density, knockback, size));
//					double enchKnockback = EnchantmentProtection.func_92092_a(entity, knockback);
					double enchKnockback = 0;
					if (entity instanceof LivingEntity)
						enchKnockback = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity) entity, knockback);

					entity.addDeltaMovement(new Vec3(deltaX * enchKnockback, deltaY * enchKnockback, deltaZ * enchKnockback));

					if(entity instanceof Player) {
						affectedPlayers.put((Player) entity, new Vec3(deltaX * knockback, deltaY * knockback, deltaZ * knockback));
					}
					
					if(damage != null) {
						damage.handleAttack(explosion, entity, distanceScaled);
					}
				}
			}
		}

		return affectedPlayers;
	}
	
	public float calculateDamage(double distanceScaled, double density, double knockback, float size) {
		return (float) ((int) ((knockback * knockback + knockback) / 2.0D * 8.0D * size + 1.0D));
	}

	public static DamageSource setExplosionSource(Explosion explosion) {
		return null;
//		return explosion != null && explosion.getExplosivePlacedBy() != null ?
//				(new EntityDamageSource("explosion.player", explosion.getExplosivePlacedBy())).setExplosion() :
//					(new DamageSource("explosion")).setExplosion();
	}
	
	public EntityProcessorStandard withRangeMod(float mod) {
		range = new IEntityRangeMutator() {
			@Override
			public float mutateRange(ExplosionVNT explosion, float range) {
				return range * mod;
			}
		};
		return this;
	}
	
	public EntityProcessorStandard withDamageMod(ICustomDamageHandler damage) {
		this.damage = damage;
		return this;
	}
	
	public EntityProcessorStandard allowSelfDamage() {
		this.allowSelfDamage = true;
		return this;
	}
}
