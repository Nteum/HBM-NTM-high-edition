package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IPlayerProcessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class PlayerProcessorStandard implements IPlayerProcessor {

	@Override
	public void process(ExplosionVNT explosion, Level world, double x, double y, double z, HashMap<Player, Vec3> affectedPlayers) {
		
		for(Map.Entry<Player, Vec3> entry : affectedPlayers.entrySet()) {
			if(entry.getKey() instanceof Player) {
//				PacketDispatcher.wrapper.sendTo(new ExplosionKnockbackPacket(entry.getValue()), (EntityPlayerMP)entry.getKey());
			}
		}
	}
}
