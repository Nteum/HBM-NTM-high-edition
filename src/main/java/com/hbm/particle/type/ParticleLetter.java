package com.hbm.particle.type;

import com.hbm.particle.ParticleRenderTypes;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ParticleLetter extends ParticleHBMBase{
    int color;
    char c;
    public ParticleLetter(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprites);
        setLifetime(30);
    }

    public void setChar(char c){
        this.c = c;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderTypes.RADIATION_FOG;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        Vec3 vec3 = renderInfo.getPosition();
        float f = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        Quaternionf quaternionf;
        if (this.roll == 0.0F) {
            quaternionf = renderInfo.rotation();
        } else {
            quaternionf = new Quaternionf(renderInfo.rotation());
            quaternionf.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
        }

        setScale((float) (1 - 1 / Math.pow(Math.E, (age + partialTicks) * 4f / this.lifetime)));
        setAlpha(Math.min(0, 1 - (age + partialTicks) * 4f / this.lifetime));
        // 我知道这只是在乱套Font类的渲染方法，只能先这么写再看看该改什么
        Minecraft.getInstance().font.drawInBatch(String.valueOf(c), f, f1, color, true, new Matrix4f(),
                Minecraft.getInstance().renderBuffers().bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
    }
}