package com.hbm.particle.type;

import com.hbm.HBM;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ParticleRadiationFog extends TextureSheetParticle {

	private static final ResourceLocation texture = new ResourceLocation(HBM.MODID + ":textures/particle/fog.png");
	private TextureManager theRenderEngine;
	private int maxAge;

	public ParticleRadiationFog(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
		this.setSprite(sprites.get(pLevel.random));
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_LIT;
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
