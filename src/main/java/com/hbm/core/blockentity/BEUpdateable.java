package com.hbm.core.blockentity;

import com.hbm.HBM;
import com.hbm.blockentity.HBMTiles;
import com.hbm.core.network.HBMNetwork;
import com.hbm.network.packet.toclient.S2CSyncTileMessage;
import com.hbm.core.client.sounds.AudioWrapper;
import com.hbm.registries.RegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Extension of TileEntity that adds various helpers we use across the majority of our Tiles even those that are not an instance of TileEntityMekanism. Additionally, we
 * improve the performance of markDirty by not firing neighbor updates unless the markDirtyComparator method is overridden.
 */
public abstract class BEUpdateable extends BlockEntity {

    public BEUpdateable(BlockPos pos, BlockState state) {
        super(HBMTiles.getTypeById(BEMachineBase.getId(state)), pos, state);
    }

    /** 保留旧的三参构造器，供仍显式传入 BE 类型的子类使用 */
    public BEUpdateable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * 命名默认就是方块自身的名字
     */
    protected static String getId(BlockState blockState){
        return RegistryHelper.blockRL(blockState.getBlock()).getPath();
    }
    /* 同步逻辑 */

    /**
     * 服务端 -> 客户端的同步信息
     *
     * @return 同步的信息
     */
    @NotNull
    public CompoundTag getReducedUpdateTag() {
        //Add the base update tag information
        return super.getUpdateTag();
    }

    /**
     * 服务端 -> 客户端同步中，客户端处理信息
     *
     * @param tag 客户端需要处理的信息
     */
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        handleUpdateTag(tag);
    }


    /**
     * @return 客户端 -> 服务端同步的信息
     */
    public CompoundTag getClientSyncTag(){
        return new CompoundTag();
    }

    /**
     * 服务端处理客户端同步的信息
     *
     * @param tag 客户端 —> 服务端同步的信息
     */
    public void handleClientPacket(@NotNull CompoundTag tag) {
    }
    // 发送更新方块实体数据包
    public void sendUpdatePacket() {
        sendUpdatePacket(this);
    }
    // 通过TARACKING方式更新数据包
    public void sendUpdatePacket(BlockEntity tracking) {
        if (this.level.isClientSide) {
            HBM.LOGGER.warn("Update packet call requested from client side", new IllegalStateException());
        } else if (isRemoved()) {
            HBM.LOGGER.warn("Update packet call requested for removed tile", new IllegalStateException());
        } else {
            HBMNetwork.sendToAll(new S2CSyncTileMessage(this));
        }
    }

    /**
     * 更新逻辑
     */
    // 客户端更新
    protected void onUpdateClient(){}
    // 服务器更新
    protected void onUpdateServer(){}
    public static void clientTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (pBlockEntity instanceof BEUpdateable BEUpdateable) BEUpdateable.onUpdateClient();
    }
    public static void serverTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (pBlockEntity instanceof BEUpdateable BEUpdateable) BEUpdateable.onUpdateServer();
    }
    //
    public AudioWrapper createAudioLoop() { return null; }
    public AudioWrapper rebootAudio(AudioWrapper wrapper) {
        wrapper.stopSound();
        AudioWrapper audio = createAudioLoop();
        audio.startSound();
        return audio;
    }

    public AudioWrapper getLoopedSound(SoundEvent sound, float x, float y, float z, float volume, float range, float pitch) {

        AudioWrapper audio = new AudioWrapper(sound, SoundSource.BLOCKS);
        audio.updatePosition(x, y, z);
        audio.updateVolume(volume);
        audio.updateRange(range);
        return audio;
    }

    public AudioWrapper getLoopedSound(SoundEvent sound, float x, float y, float z, float volume, float range, float pitch, int keepAlive) {
        AudioWrapper audio = getLoopedSound(sound, x, y, z, volume, range, pitch);
        audio.setKeepAlive(keepAlive);
        return audio;
    }

    public AudioWrapper getLoopedSound(SoundEvent sound, Entity entity, float volume, float range, float pitch, int keepAlive) {
        AudioWrapper audio = new AudioWrapper(sound, SoundSource.BLOCKS);
        audio.updateVolume(volume);
        audio.updateRange(range);
        audio.setKeepAlive(keepAlive);
        return audio;
    }
}