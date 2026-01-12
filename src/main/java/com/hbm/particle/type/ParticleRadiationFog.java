package com.hbm.particle.type;

import com.hbm.HBM;
import com.hbm.particle.ParticleRenderTypes;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ParticleRadiationFog extends ParticleHBMBase {

	public ParticleRadiationFog(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprites);
		this.setScale(7.5f);
		this.setColor(0,0,0);
		this.lifetime = 100 + pLevel.random.nextInt(40);
		this.friction = 0.9599999785423279f;
		this.gravity = 0;
	}

	@Override
	public void setLifetime(int pParticleLifeTime) {
		this.lifetime = Math.min(pParticleLifeTime, 400);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderTypes.RADIATION_FOG;
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
		// 这个其实是java.util的random，不是mc的random
		Random rand = new Random(50);
		this.rCol = 0.85f;
		this.gCol = 0.9f;
		this.bCol = 0.5f;
		int j = 240;

		for (int i = 0; i < 10; i++) {
			float dX = (float) ((rand.nextGaussian() - 1D) * 2.5D);
			float dY = (float) ((rand.nextGaussian() - 1D) * 0.15D);
			float dZ = (float) ((rand.nextGaussian() - 1D) * 2.5D);
			this.scale(rand.nextFloat());

			float pX = (float) (f + rand.nextGaussian() * 0.5f);
			float pY = (float) (f1 + rand.nextGaussian() * 0.5f);
			float pZ = (float) (f2 + rand.nextGaussian() * 0.5f);

			Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F),
					new Vector3f(-1.0F, 1.0F, 0.0F),
					new Vector3f(1.0F, 1.0F, 0.0F),
					new Vector3f(1.0F, -1.0F, 0.0F)};

			for(int k = 0; k < 4; ++k) {
				Vector3f vector3f = avector3f[k];
				vector3f.rotate(quaternionf);
				vector3f.mul(getQuadSize(pPartialTicks));
				vector3f.add(pX + dX, pY + dY, pZ + dZ);
			}

			float f6 = this.getU0();
			float f7 = this.getU1();
			float f4 = this.getV0();
			float f5 = this.getV1();

			pBuffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
			pBuffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
			pBuffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
			pBuffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		}
	}

	//	@OnlyIn(Dist.CLIENT)
//	public static class Provider implements ParticleProvider<SimpleParticleType> {
//		private final SpriteSet sprites;
//		public Provider(SpriteSet pSprites) {
//			this.sprites = pSprites;
//		}
//
//		@Nullable
//		@Override
//		public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
//			return new ParticleRadiationFog(pLevel, pX, pY, pZ);
//		}
//	}
}