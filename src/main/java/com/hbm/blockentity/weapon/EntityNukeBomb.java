package com.hbm.blockentity.weapon;

import com.hbm.block.weapon.NukeBomb;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class EntityNukeBomb extends DummyableBlockEntity {
    public boolean ready = false;
    public boolean explode = false;

    public EntityNukeBomb(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.multiblockData = MultiblockData.mapping.get(pBlockState.getBlock());
    }

    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (pBlockEntity instanceof EntityNukeBomb && pState.is(ModBlocks.BOMB_FAT_MAN.get())){
            EntityNukeBomb entity = (EntityNukeBomb) pBlockEntity;
            NukeBomb block = (NukeBomb) pState.getBlock();
            entity.explode = block.explode;
        }
    }
    //服务端发送数据包
    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(this);
        return packet;
    }
    //客户端接收数据包（注意：服务端和客户端的实体时不一样的，比如客户端的实体地址24999，服务端可以是25068，虽然同一个类，但有两个实例）
    //方块实体渲染器调用的就是客户端
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        CompoundTag tag = pkt.getTag();
        ItemStack itemStack = ItemStack.of(tag);
        explode = tag.getBoolean("explode");
    }
    //方块被载入时同步数据用
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = super.getUpdateTag();
        updateTag.putBoolean("explode",explode);
        return updateTag;
    }
    public boolean isReady(){return this.ready;}
}
