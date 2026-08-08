package com.hbm.core.blockentity;

import com.hbm.HBM;
import com.hbm.core.network.HBMNetwork;
import com.hbm.network.packet.toclient.S2CSyncTileMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class BEUpdateable extends BlockEntity {
    public BEUpdateable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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

}