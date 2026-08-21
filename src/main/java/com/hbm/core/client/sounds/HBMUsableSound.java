package com.hbm.core.client.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 现代化的循环机器音效实例。
 *
 * 相比旧 hbm 的 AudioDynamic（基于 1.7.10 MovingSound），改用 1.20.1 的
 * AbstractTickableSoundInstance：每个客户端 tick 调用 tick() 更新音量/位置，
 * 并通过 keepAlive 机制自动过期停止，避免机器被拆除后声音残留。
 *
 * 用法：机器客户端 BE 持有 AudioWrapper，每 tick 调用 audio.keepAlive()；
 * 当 keepAlive 计数超过阈值（机器不再触发），自动停止。
 */
@OnlyIn(Dist.CLIENT)
public class HBMUsableSound extends AbstractTickableSoundInstance {
    private final AudioWrapper owner;
    private final RandomSource random;
    // keepAlive 过期机制
    public int keepAlive;
    public int timeSinceKA;
    public boolean shouldExpire = false;

    public HBMUsableSound(SoundEvent sound, SoundSource source, AudioWrapper owner){
        super(sound, source, RandomSource.create());
        this.owner = owner;
        this.random = RandomSource.create();
        this.looping = true;
        this.delay = 0;
        this.volume = owner.getVolume();
        this.relative = false;
    }

    @Override
    public void tick(){
        // 位置/音量每 tick 同步
        this.volume = owner.getVolume();
        this.pitch = owner.getPitch();

        // keepAlive 过期：如果超过阈值没有续命则停止
        if (this.shouldExpire){
            if (this.timeSinceKA > this.keepAlive){
                this.stop();
            }
            this.timeSinceKA++;
        }
    }

    /** 更新声音位置（旧版 AudioDynamic.setPosition 的对应实现） */
    public void setPosition(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** 续命，防止声音过期 */
    public void keepAlive(){
        this.timeSinceKA = 0;
    }

    @Override
    public boolean canStartSilent() {
        return false;
    }

    @Override
    public float getVolume(){
        return owner != null ? owner.getVolume() : super.getVolume();
    }
}
