package com.hbm.particle.type;

import com.hbm.main.HBMxx;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ParticleContrail extends TextureSheetParticle {

	private static final ResourceLocation texture = new ResourceLocation(HBMxx.MODID + ":textures/particle/contrail.png");
	private TextureManager theRenderEngine;
	private int age;
	private int maxAge;

	protected ParticleContrail(ClientLevel pLevel, double pX, double pY, double pZ) {
		super(pLevel, pX, pY, pZ);
	}


	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_LIT;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;
		public Provider(SpriteSet pSprites) {
			this.sprites = pSprites;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
			return new ParticleContrail(pLevel, pX, pY, pZ);
		}
	}
}
