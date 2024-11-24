package com.hbm.network.packet.toclient;

import com.hbm.particle.handler.AuxParticleHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AuxParticlePacket {

	double x;
	double y;
	double z;
	int type;

	public AuxParticlePacket()
	{
		
	}

	public AuxParticlePacket(double x, double y, double z, int type)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}

	public AuxParticlePacket(FriendlyByteBuf buf){
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		type = buf.readInt();
	}

	public void toBytes(FriendlyByteBuf buf){
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeInt(type);
	}

	public boolean handle(Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(()->{
			try {
				AuxParticleHandler.particleControl(x,y,z,type);
			} catch(Exception x) { }
		});
		return true;
	}

}
