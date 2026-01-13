package com.hbm.particle.type;

import com.hbm.particle.ParticleRenderTypes;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ParticleContrail extends ParticleHBMBase {
    double[] gaussianRands;
    public ParticleContrail(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprites);
        this.rCol = this.bCol = this.gCol = 0;
        this.lifetime = 100 + pLevel.random.nextInt(40);
        this.gaussianRands = new double[6];
        for (int i = 0; i < 6; i++) {
            gaussianRands[i] = pLevel.getRandom().nextGaussian();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderTypes.SHOCK_WAVE;
    }

    @Override
    public void tick() {
        this.alpha = 1 - (float) age / lifetime;
        super.tick();
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        Vec3 vec3 = pRenderInfo.getPosition();
        float f = (float)(Mth.lerp(pPartialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp(pPartialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp(pPartialTicks, this.zo, this.z) - vec3.z());

        Quaternionf quaternionf;
        if (this.roll == 0.0F) {
            quaternionf = pRenderInfo.rotation();
        } else {
            quaternionf = new Quaternionf(pRenderInfo.rotation());
            quaternionf.rotateZ(Mth.lerp(pPartialTicks, this.oRoll, this.roll));
        }

        for (int i = 0; i < 6; i++) {
            float mod = random.nextFloat() * 0.2F + 0.2F;
            this.setColor(this.rCol + mod, this.bCol + mod, this.gCol + mod);
            int brightness = 240;
            this.quadSize = (float) ((this.alpha + 0.5) * this.quadSize);
            float pX = (float) (f + (gaussianRands[i] - 1D) * 0.2F * quadSize);
            float pY = (float) (f1 + (gaussianRands[(i + 1) % 10] - 1D) * 0.5F * quadSize);
            float pZ = (float) (f2 + (gaussianRands[(i + 2) % 10] - 1D) * 0.2F * quadSize);

            Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F),
                    new Vector3f(-1.0F, 1.0F, 0.0F),
                    new Vector3f(1.0F, 1.0F, 0.0F),
                    new Vector3f(1.0F, -1.0F, 0.0F)};

            for(int k = 0; k < 4; ++k) {
                Vector3f vector3f = avector3f[k];
                vector3f.rotate(quaternionf);
                vector3f.mul(quadSize);
                vector3f.add(pX, pY, pZ);
            }

            float f6 = this.getU0();
            float f7 = this.getU1();
            float f4 = this.getV0();
            float f5 = this.getV1();

            pBuffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(brightness).endVertex();
            pBuffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(brightness).endVertex();
            pBuffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(brightness).endVertex();
            pBuffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(brightness).endVertex();
        }
    }
}