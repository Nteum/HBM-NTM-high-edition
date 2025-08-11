package com.hbm.utils.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * volume
 * pitch
 * range
 * isRepeat
 * isPlaying
 * */
public class AudioWrapper {
	public float volume = 1.0f;
	public float pitch = 1.0f;
	public float range = 100.0f;
	public boolean isRepeat = true;
	public boolean isPlaying = false;
	public SoundEvent sound;
	public SoundSource source = SoundSource.NEUTRAL;
	// 暂时记录播放的instance
	public SimpleSoundInstance soundInstance;

	public AudioWrapper(){}
	public AudioWrapper(SoundEvent sound, SoundSource source){
		this.sound = sound;
		this.source = source;
	}
	public AudioWrapper(SoundEvent sound, float volume, float pitch, float range, boolean isRepeat){
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
		this.range = range;
		this.isRepeat = isRepeat;
	}

	@OnlyIn(Dist.CLIENT)
	public void playLoopSound(Vec3 pPos, long pSeed){
		SoundManager soundManager = Minecraft.getInstance().getSoundManager();
		if (this.soundInstance == null)
			// 我用at修改了构造函数权限，可以正常运行，但不知为什么这里一直显示红线报错，如果没问题可以不用管它
			this.soundInstance =  new SimpleSoundInstance(sound, source, volume, pitch, RandomSource.create(pSeed), true, 0, SoundInstance.Attenuation.NONE, pPos.x(), pPos.y(), pPos.z());
		if (!soundManager.isActive(this.soundInstance)){
			soundManager.play(this.soundInstance);
		}
	}
	@OnlyIn(Dist.CLIENT)
	public void stopLoopSound(){
		SoundManager soundManager = Minecraft.getInstance().getSoundManager();
		soundManager.stop(this.soundInstance);
		this.soundInstance = null;
	}

	public void setKeepAlive(int keepAlive) { }
	public void keepAlive() { }
	
	public void updatePosition(float x, float y, float z) { }

	public void updateVolume(float volume) { this.volume = volume;}
	public void updateRange(float range) { this.range = range;}
	
	public void updatePitch(float pitch) { this.pitch = pitch;}

	public float getVolume() { return volume; }
	public float getRange() { return range; }
	
	public float getPitch() { return pitch; }
	
	public void setDoesRepeat(boolean repeats) { this.isRepeat = repeats;}
	
	public void startSound() { }
	
	public void stopSound() { }
	
	public boolean isPlaying() { return isPlaying; }
}
