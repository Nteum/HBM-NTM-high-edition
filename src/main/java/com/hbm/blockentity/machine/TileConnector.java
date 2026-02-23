package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.block.logistic.BlockConnector;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.CapabilityBlockEntity;
import com.hbm.blockentity.interfaces.IConnector;
import com.hbm.utils.NBTUtils;
import com.hbm.utils.transport_net.EnergyNetwork;
import com.hbm.utils.transport_net.EnergyNetworkSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileConnector extends CapabilityBlockEntity implements IConnector {
    protected EnergyNetwork network;
    protected Set<BlockPos> connectedPos;
    protected BlockPos tempPos = null;
    public TileConnector(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.TILE_CONNECTOR.get(), pPos, pBlockState);
        connectedPos = new HashSet<>();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.hasLevel() && !this.getLevel().isClientSide){
            EnergyNetworkSystem.getOrCreate(this.level).join(this);
            if (level != null && !level.isClientSide) sendUpdatePacket();
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.hasLevel() && !this.getLevel().isClientSide){
            EnergyNetworkSystem.getOrCreate(this.level).leave(this);
        }
    }

    public void neighbourChanged(){
        if (this.hasLevel() && !this.getLevel().isClientSide){
            EnergyNetworkSystem.getOrCreate(this.level).refreshNeighbour(this.getBlockPos(), this.getBlockState().getValue(BlockStateProperties.FACING).getOpposite());
        }
    }

    // 连接器链接的点位
    @Override
    public List<Direction> getAttached() {
        return List.of(this.getBlockState().getValue(BlockConnector.FACING).getOpposite());
    }

    @Override
    public EnergyNetwork getNetwork() {
        return network;
    }

    @Override
    public void setNetwork(EnergyNetwork network) {
        this.network = network;
    }

    public Set<BlockPos> getConnected(){
        return connectedPos;
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        if (tempPos != null) tag.put(HBMKey.POSITION, NbtUtils.writeBlockPos(tempPos));
        else {
            NBTUtils.savePositions(tag, connectedPos);
        }
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        if (tag.contains(HBMKey.POSITION, Tag.TAG_COMPOUND)){
            BlockPos pos = NbtUtils.readBlockPos(tag.getCompound(HBMKey.POSITION));
            connectedPos.add(pos);
            this.setChanged();
        }
    }

    public void addConnected(BlockPos blockPos){
        connectedPos.add(new BlockPos(blockPos));
        tempPos = blockPos;
        EnergyNetworkSystem.getOrCreate(this.level).link(this.getBlockPos(), blockPos);
        this.setChanged();
        sendUpdatePacket();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        connectedPos.addAll(NBTUtils.loadPositions(nbt));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        NBTUtils.savePositions(pTag, connectedPos);
    }


}
