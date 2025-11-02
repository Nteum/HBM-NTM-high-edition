package com.hbm.network.packet.toclient;

import com.google.common.collect.Lists;
import com.hbm.explosion.temp.ExplosionOneOff;
import com.hbm.network.IHBMMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

// 用于ExplosionOneOff
public class S2CExplosionPacket implements IHBMMessage {
    private final double x;
    private final double y;
    private final double z;
    private final float power;
    private final List<BlockPos> toBlow;
    private final float knockbackX;
    private final float knockbackY;
    private final float knockbackZ;

    public S2CExplosionPacket(double pX, double pY, double pZ, float pPower, List<BlockPos> pToBlow, @Nullable Vec3 pKnockback) {
        this.x = pX;
        this.y = pY;
        this.z = pZ;
        this.power = pPower;
        this.toBlow = Lists.newArrayList(pToBlow);
        if (pKnockback != null) {
            this.knockbackX = (float)pKnockback.x;
            this.knockbackY = (float)pKnockback.y;
            this.knockbackZ = (float)pKnockback.z;
        } else {
            this.knockbackX = 0.0F;
            this.knockbackY = 0.0F;
            this.knockbackZ = 0.0F;
        }

    }

    public S2CExplosionPacket(FriendlyByteBuf pBuffer) {
        this.x = pBuffer.readDouble();
        this.y = pBuffer.readDouble();
        this.z = pBuffer.readDouble();
        this.power = pBuffer.readFloat();
        int i = Mth.floor(this.x);
        int j = Mth.floor(this.y);
        int k = Mth.floor(this.z);
        this.toBlow = pBuffer.readList((p_178850_) -> {
            int l = p_178850_.readByte() + i;
            int i1 = p_178850_.readByte() + j;
            int j1 = p_178850_.readByte() + k;
            return new BlockPos(l, i1, j1);
        });
        this.knockbackX = pBuffer.readFloat();
        this.knockbackY = pBuffer.readFloat();
        this.knockbackZ = pBuffer.readFloat();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    @Override
    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeDouble(this.x);
        pBuffer.writeDouble(this.y);
        pBuffer.writeDouble(this.z);
        pBuffer.writeFloat(this.power);
        int i = Mth.floor(this.x);
        int j = Mth.floor(this.y);
        int k = Mth.floor(this.z);
        pBuffer.writeCollection(this.toBlow, (p_178855_, p_178856_) -> {
            int l = p_178856_.getX() - i;
            int i1 = p_178856_.getY() - j;
            int j1 = p_178856_.getZ() - k;
            p_178855_.writeByte(l);
            p_178855_.writeByte(i1);
            p_178855_.writeByte(j1);
        });
        pBuffer.writeFloat(this.knockbackX);
        pBuffer.writeFloat(this.knockbackY);
        pBuffer.writeFloat(this.knockbackZ);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ExplosionOneOff explosion = new ExplosionOneOff(Minecraft.getInstance().level, null, x, y, z, power, toBlow);
            explosion.clientEffect();
            Minecraft.getInstance().player.setDeltaMovement(Minecraft.getInstance().player.getDeltaMovement().add(knockbackX, knockbackY, knockbackZ));
        });
        ctx.get().setPacketHandled(true);
    }
}
