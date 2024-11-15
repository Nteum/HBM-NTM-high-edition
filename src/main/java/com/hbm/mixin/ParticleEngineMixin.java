package com.hbm.mixin;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin implements PreparableReloadListener {
}
