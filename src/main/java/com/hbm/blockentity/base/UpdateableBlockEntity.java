package com.hbm.blockentity.base;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.api.Chunk3D;
import com.hbm.api.Coord4D;
import com.hbm.api.interferences.ITileWrapper;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toclient.S2CSyncTileMessage;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Extension of TileEntity that adds various helpers we use across the majority of our Tiles even those that are not an instance of TileEntityMekanism. Additionally, we
 * improve the performance of markDirty by not firing neighbor updates unless the markDirtyComparator method is overridden.
 */
public abstract class UpdateableBlockEntity extends BlockEntity implements ITileWrapper {

    @Nullable
    private Coord4D cachedCoord;
    private boolean cacheCoord;
    private long lastSave;
    // 代表机器是否倍东西挡住，挡住的话声音传不过来，似乎是用在这个上的
    public boolean muffled;
    public boolean shouldSync = false;     // 是否应当同步，这个是新加的，可以用也可以不用

    public UpdateableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    //============方块实体更新==============
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (isRemote() && net.getDirection() == PacketFlow.CLIENTBOUND) {
            //Handle the update tag when we are on the client
            CompoundTag tag = pkt.getTag();
            if (tag != null) {
                handleUpdatePacket(tag);
            }
        }
    }
//    // 主要提供更新内容的函数
//    @NotNull
//    @Override
//    public CompoundTag getUpdateTag() {
//        return getReducedUpdateTag();
//    }

    // 自定义更新内容的函数，内容是需要更新到客户端的数据，不是全部数据
    // 尽管把getUpdateTag内容也定义成它了，但getUpdateTag走的是原版流程，而模组更新是靠自己发送数据更新的。
    @NotNull
    public CompoundTag getReducedUpdateTag() {
        //Add the base update tag information
        return super.getUpdateTag();
    }
    // 可自定义的处理内容函数，处理getReducedUpdateTag发送的内容
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        handleUpdateTag(tag);
    }
    public CompoundTag getClientSyncTag(){
        return new CompoundTag();
    }
    public void handleClientPacket(@NotNull CompoundTag tag) {
    }
    // 发送更新方块实体数据包
    public void sendUpdatePacket() {
        sendUpdatePacket(this);
    }
    // 通过TARACKING方式更新数据包
    public void sendUpdatePacket(BlockEntity tracking) {
        if (isRemote()) {
            HBM.LOGGER.warn("Update packet call requested from client side", new IllegalStateException());
        } else if (isRemoved()) {
            HBM.LOGGER.warn("Update packet call requested for removed tile", new IllegalStateException());
        } else {
            //Note: We use our own update packet/channel to avoid chunk trashing and minecraft attempting to rerender
            // the entire chunk when most often we are just updating a TileEntityRenderer, so the chunk itself
            // does not need to and should not be redrawn
//            ModMessages.sendToAllTracking(new UpdateTileMessage(this), tracking);
            ModMessages.sendToAll(new S2CSyncTileMessage(this));
        }
    }
    // 1.7.10版本HBM更新机制，我在这里复现了它
    // 但我认为新版本的更新是能完成客户端服务器同步的任务的
    // 希望不要用到它
    S2CSyncTileMessage lastUpdateMsg;
    public void networkPackNT(int range) {
        if(level.isClientSide) return;

        S2CSyncTileMessage updateMsg = new S2CSyncTileMessage(this);

        if(!updateMsg.equals(lastUpdateMsg) || this.level.getGameTime() % 20 != 0) return;

        this.lastUpdateMsg = updateMsg.copy();

        ModMessages.sendToAllAround(updateMsg, new PacketDistributor.TargetPoint(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), range, level.dimension()));
    }
    @NotNull
    protected Level getWorldNN() {
        return Objects.requireNonNull(getLevel(), "getWorldNN called before world set");
    }

    public boolean isRemote() {
        return getWorldNN().isClientSide();
    }
    @Override
    public Level getTileWorld() {
        return level;
    }

    @Override
    public BlockPos getTilePos() {
        return worldPosition;
    }
    //更新方块模型信息
    protected void updateModelData() {
        requestModelDataUpdate();
        WorldUtils.updateBlock(getLevel(), getBlockPos(), getBlockState());
    }

    //在方块被移动的情况下调用
    public void blockRemoved() {
    }

    //检查是否需要更新comparator
    public void markDirtyComparator() {
    }

    @Override
    public final void setChanged() {
        setChanged(true);
    }

    public final void markForSave() {
        setChanged(false);
    }
    // 对markDirty的扩充
    protected void setChanged(boolean updateComparator) {
        //Copy of the base impl of markDirty in TileEntity, except only updates comparator state when something changed
        // and if our block supports having a comparator signal, instead of always doing it
        if (level != null) {
            long time = level.getGameTime();
            if (lastSave != time) {
                //Only mark the chunk as dirty at most once per tick
                WorldUtils.markChunkDirty(level, worldPosition);
                lastSave = time;
            }
            if (updateComparator && !isRemote()) {
                markDirtyComparator();
            }
        }
    }

    // 客户端更新
    protected void onUpdateClient(){}
    // 服务器更新
    protected void onUpdateServer(){}
    public static void clientTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (pBlockEntity instanceof UpdateableBlockEntity updateableBlockEntity) updateableBlockEntity.onUpdateClient();
    }
    public static void serverTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (pBlockEntity instanceof UpdateableBlockEntity updateableBlockEntity) updateableBlockEntity.onUpdateServer();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        updateCoord();
        this.muffled = nbt.contains(HBMKey.MUFFLED);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.muffled)
            pTag.putByte(HBMKey.MUFFLED, (byte) 0);
    }

    @Override
    public void setLevel(@NotNull Level world) {
        super.setLevel(world);
        updateCoord();
    }
    // 记录坐标的函数，cacheCoord在载入之后应该只用一次，表明坐标是否已经被记录了。
    protected void cacheCoord() {
        //Mark that we want to cache the coord and then update the coord if needed
        cacheCoord = true;
        updateCoord();
    }

    private void updateCoord() {
        if (cacheCoord && level != null) {
            cachedCoord = new Coord4D(worldPosition, level);
        }
    }

    @Override
    public Coord4D getTileCoord() {
        return cacheCoord && cachedCoord != null ? cachedCoord : ITileWrapper.super.getTileCoord();
    }

    @Override
    public Chunk3D getTileChunk() {
        if (cacheCoord && cachedCoord != null) {
            return new Chunk3D(cachedCoord);
        }
        BlockPos pos = getTilePos();
        return new Chunk3D(getTileWorld().dimension(), SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
    }

    public float getVolume(float baseVolume) {
        return muffled ? baseVolume * 0.1F : baseVolume;
    }
}