package com.hbm.utils.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AudioUtils {
    @OnlyIn(Dist.CLIENT)
    public static void playLoopSound(Vec3 pPos, AudioWrapper audio, boolean toStop){

        playLoopSound(pPos, audio.sound, audio.source, toStop, audio.volume, audio.pitch, false, Minecraft.getInstance().level == null ? 0 : Minecraft.getInstance().level.random.nextLong());
    }
    @OnlyIn(Dist.CLIENT)
    public static void playLoopSound(Vec3 pPos, SoundEvent pSoundEvent, SoundSource pSource, boolean toStop, float pVolume, float pPitch, boolean pDistanceDelay, long pSeed){
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        SimpleSoundInstance simplesoundinstance = new SimpleSoundInstance(pSoundEvent, pSource, pVolume, pPitch, RandomSource.create(pSeed), pPos.x(), pPos.y(), pPos.z());
        if (!soundManager.isActive(simplesoundinstance)){
            if (!toStop){
                double d0 = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceToSqr(pPos.x(), pPos.y(), pPos.z());
                if (pDistanceDelay && d0 > 100.0D) {
                    double d1 = Math.sqrt(d0) / 40.0D;
                    soundManager.playDelayed(simplesoundinstance, (int)(d1 * 20.0D));
                } else {
                    soundManager.play(simplesoundinstance);
                }
            }
        }else if (toStop){
            soundManager.stop(simplesoundinstance);
        }
    }

}
