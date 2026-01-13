package com.hbm.particle.type;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DeadLeafParticle extends TextureSheetParticle {
    private int randomId = 0;
    public DeadLeafParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.setSprite(sprites.get(pLevel.random));
        float color = 1 - pLevel.random.nextFloat() * 0.2f;
        setColor(color, color, color);
        scale(0.8f);
        setLifetime(200 + pLevel.random.nextInt(50));
        this.gravity = 0.2f;
        this.randomId = pLevel.random.nextInt(10);
        this.xd = this.yd = this.zd = 0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        if(!this.onGround) {
            this.xd += random.nextGaussian() * 0.002d;
            this.zd += random.nextGaussian() * 0.002d;
            this.yd = Math.max(this.yd, -0.025d);
        }
        super.tick();
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        Vec3 vec3 = pRenderInfo.getPosition();
        float dx = (float)(Mth.lerp((double)pPartialTicks, this.xo, this.x) - vec3.x());
        float dy = (float)(Mth.lerp((double)pPartialTicks, this.yo, this.y) - vec3.y());
        float dz = (float)(Mth.lerp((double)pPartialTicks, this.zo, this.z) - vec3.z());
        Quaternionf quaternionf;
        float scale = this.getQuadSize(pPartialTicks);
        int bright = this.getLightColor(pPartialTicks);
        // begin
        boolean flipU = this.randomId % 2 == 0;
        boolean flipV = this.randomId % 4 < 2;
        // 其实本来this.roll是控制z轴旋转的，但它还需要在tick里规定，太麻烦了，于是我取消了它的使用。
        quaternionf = new Quaternionf(pRenderInfo.rotation());
        if (flipV) quaternionf.rotateZ(Mth.PI);
        if (flipU) quaternionf.rotateY(Mth.PI);
        // end
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(scale);
            vector3f.add(dx, dy, dz);
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        pBuffer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(bright).endVertex();
        pBuffer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(bright).endVertex();
        pBuffer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(bright).endVertex();
        pBuffer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(bright).endVertex();
    }
}