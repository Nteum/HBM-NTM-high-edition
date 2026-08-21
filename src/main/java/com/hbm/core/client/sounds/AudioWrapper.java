package com.hbm.core.client.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 循环机器音效的包装器。
 *
 * 对应旧版 hbm 的 AudioWrapperClient / AudioDynamic：让一段录音循环播放，
 * 模拟机器持续运行的声音。相比旧版：
 * 1. 改用 1.20.1 的 HBMUsableSound（AbstractTickableSoundInstance），tick 内自动更新音量/音高
 * 2. 增加 keepAlive 过期机制：机器每 tick 调用 keepAlive()，若机器停止触发
 *    则声音在若干 tick 后自动停止，避免残留
 *
 * 用法（机器客户端 BE）：
 *   AudioWrapper audio = new AudioWrapper(soundEvent, SoundSource.BLOCKS).setRange(16);
 *   // 每 tick：
 *   audio.updatePosition(x, y, z);
 *   if (running) audio.keepAlive(); else audio.stopLoopSound();
 */
public class AudioWrapper {
	public float volume = 1.0f;
	public float pitch = 1.0f;
	public float range = 100.0f;
	public boolean isRepeat = true;
	public boolean isPlaying = false;
	public SoundEvent sound;
	public SoundSource source = SoundSource.NEUTRAL;
	// 当前播放的 tickable 实例
	@OnlyIn(Dist.CLIENT)
	private HBMUsableSound soundInstance;
	// keepAlive 过期 tick 数，超过则自动停止
	private int keepAliveTicks = 40;

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
	public void playLoopSound(Vec3 pPos){
		SoundManager soundManager = Minecraft.getInstance().getSoundManager();
		if (this.soundInstance == null || this.soundInstance.isStopped()){
			this.soundInstance = new HBMUsableSound(sound, source, this);
			this.soundInstance.keepAlive = keepAliveTicks;
			this.soundInstance.shouldExpire = true;
			this.soundInstance.setPosition((float) pPos.x, (float) pPos.y, (float) pPos.z);
			soundManager.play(this.soundInstance);
			this.isPlaying = true;
		}
	}
	/** 兼容旧签名（seed 参数已无意义） */
	@OnlyIn(Dist.CLIENT)
	public void playLoopSound(Vec3 pPos, long pSeed){
		this.playLoopSound(pPos);
	}
	@OnlyIn(Dist.CLIENT)
	public void stopLoopSound(){
		if (this.soundInstance != null){
			Minecraft.getInstance().getSoundManager().stop(this.soundInstance);
			this.soundInstance = null;
			this.isPlaying = false;
		}
	}

	/** 每次机器运行时调用，防止声音过期 */
	public void keepAlive(){
		if (this.soundInstance != null) this.soundInstance.keepAlive();
	}

	public void setKeepAlive(int keepAlive) {
		this.keepAliveTicks = keepAlive;
	}

	public void updatePosition(float x, float y, float z) {
		if (this.soundInstance != null){
			this.soundInstance.setPosition(x, y, z);
		}
	}

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
