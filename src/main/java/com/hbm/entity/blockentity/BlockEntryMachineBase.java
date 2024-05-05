package com.hbm.entity.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

//AbstractFurnaceBlockEntity：原版所有炉子实体的基类
//    int burnTime; —— 燃料当前可燃烧时间
//    int fuelTime; —— 燃料可燃烧的总时间
//    int cookTime; —— 物品被烹饪的时间
//    int cookTimeTotal; —— 这个配方需要的时间
//ContainerLock：容器锁，用于和itemsteck的customName比较，如果相同就返回true
//LockableContainerBlockEntity：貌似应该是原版所有带物品格的静止方块的基类，可以设置锁来让玩家打不开
//Inventory：接口，所有有格子的实体都需要继承这个接口
//Inventories：一些便于操作格子的工具函数
//Recipe：接口，所有配方的总接口
//RecipeManager：配方的一些工具函数
//RecipeType：配方种类
//RecipeSerializer：配方的序列化器接口，可以在里面找到配方的序列化器
//Codec
public class BlockEntryMachineBase extends BlockEntity implements SidedInventory {
    private ContainerLock lock = ContainerLock.EMPTY;
    @Nullable
    private Text customName;
    public BlockEntryMachineBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return null;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {

    }
}
