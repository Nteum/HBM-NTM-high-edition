package com.hbm.network.packet;

import com.hbm.particle.ModParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;
/** 从客户端接收HBM模组渲染爆炸信息的包
 * type - 1 - smoke
 * mode - 1 - cloud
 * */
public class C2SExplosionEffectPacket {
    private final double x;
    private final double y;
    private final double z;
    public final int Efftype;
    public final int mode;
    public final int Effcount;

    public C2SExplosionEffectPacket(double pX, double pY, double pZ,int type, int mode, int count){
        x = pX;
        y = pY;
        z = pZ;
        Efftype = type;
        this.mode = mode;
        Effcount = count;
    }
    //从缓冲区读取数据初始化的阶段
    public C2SExplosionEffectPacket(FriendlyByteBuf buf){
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        Efftype = buf.readInt();
        mode = buf.readInt();
        Effcount = buf.readInt();
    }
    //将数据包写入缓冲区
    public void toBytes(FriendlyByteBuf buf){
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(Efftype);
        buf.writeInt(mode);
        buf.writeInt(Effcount);
    }
    //收到数据包后执行的逻辑
    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(()->{
            ClientLevel pLevel = Minecraft.getInstance().level;
            //渲染爆炸产生的烟雾
            if (Efftype == 1 && mode == 1){
                assert pLevel != null;
                RandomSource rand = pLevel.getRandom();
                for(int i = 0; i < Effcount; i++) {
                    double motionY = rand.nextGaussian() * (1 + ((double) Effcount / 100));
                    double motionX = rand.nextGaussian() * (1 + ((double) Effcount / 150));
                    double motionZ = rand.nextGaussian() * (1 + ((double) Effcount / 150));
                    if(rand.nextBoolean()) motionY = Math.abs(motionY);
                    pLevel.addParticle(ModParticleTypes.HBM_SMOKE.get(),x,y,z,motionX,motionY,motionZ);
                }
            }
        });
        return true;
    }
}
