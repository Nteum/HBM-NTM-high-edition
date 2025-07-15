package com.hbm.network.packet.toclient;

import com.hbm.network.IHBMMessage;
import com.hbm.particle.handler.AuxParticleHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AuxParticlePacket  implements IHBMMessage {

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

	public static AuxParticlePacket decode(FriendlyByteBuf buf){
		return new AuxParticlePacket(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readInt() );
	}
	@Override
	public void encode(FriendlyByteBuf buf){
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeInt(type);
	}
	@Override
	public void handle(Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(()->{
			try {
				AuxParticleHandler.particleControl(x,y,z,type);
			} catch(Exception x) { }
		});
	}

}
