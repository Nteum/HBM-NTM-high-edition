package com.hbm.blockentity.machine;

import com.hbm.block.machine.generator.BlockPWR;
import com.hbm.blockentity.ModBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PWRBlockEntity extends BlockEntity {
    private static final String KEY_BLOCK = "stored_block";
    private static final String KEY_CORE = "core_pos";

    @Nullable
    private Block storedBlock;
    @Nullable
    private BlockPos corePos;
    @Nullable
    private PWRControllerBlockEntity cachedCore;

    public PWRBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.PWR_BLOCK_ENTITY.get(), pos, state);
    }

    public void setStoredBlock(@Nullable Block block) {
        this.storedBlock = block;
        setChanged();
    }

    public void setCorePos(@Nullable BlockPos pos) {
        this.corePos = pos;
        setChanged();
    }

    @Nullable
    public Block getStoredBlock() {
        return storedBlock;
    }

    @Nullable
    public PWRControllerBlockEntity getCore() {
        if (cachedCore != null && !cachedCore.isRemoved()) {
            return cachedCore;
        }
        if (level == null || corePos == null || !level.hasChunkAt(corePos)) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(corePos);
        if (blockEntity instanceof PWRControllerBlockEntity controller) {
            cachedCore = controller;
            return controller;
        }
        return null;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PWRBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        if (level.getGameTime() % 20 != 0) {
            return;
        }
        if (blockEntity.storedBlock == null || blockEntity.corePos == null) {
            return;
        }
        PWRControllerBlockEntity core = blockEntity.getCore();
        if (core == null || !core.isAssembled()) {
            blockEntity.restoreOriginal(level, pos, state);
        }
    }

    public void restoreOriginal(Level level, BlockPos pos, BlockState state) {
        if (storedBlock == null) {
            return;
        }
        BlockState originalState = storedBlock.defaultBlockState();
        if (!state.is(originalState.getBlock())) {
            level.setBlock(pos, originalState, Block.UPDATE_ALL);
        }
        PWRControllerBlockEntity core = getCore();
        if (core != null) {
            core.setAssembled(false);
        }
    }

    private boolean isPort() {
        BlockState state = getBlockState();
        return state.getBlock() instanceof BlockPWR && state.getValue(BlockPWR.PORT);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (!isPort()) {
            return LazyOptional.empty();
        }
        PWRControllerBlockEntity core = getCore();
        if (core != null) {
            return core.getCapability(cap, side);
        }
        return LazyOptional.empty();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (storedBlock != null && storedBlock != Blocks.AIR) {
            tag.putString(KEY_BLOCK, BuiltInRegistries.BLOCK.getKey(storedBlock).toString());
        }
        if (corePos != null) {
            tag.putIntArray(KEY_CORE, new int[]{corePos.getX(), corePos.getY(), corePos.getZ()});
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        storedBlock = null;
        if (tag.contains(KEY_BLOCK)) {
            ResourceLocation id = new ResourceLocation(tag.getString(KEY_BLOCK));
            Block block = BuiltInRegistries.BLOCK.get(id);
            if (block != Blocks.AIR) {
                storedBlock = block;
            }
        }
        corePos = null;
        if (tag.contains(KEY_CORE)) {
            int[] pos = tag.getIntArray(KEY_CORE);
            if (pos.length == 3) {
                corePos = new BlockPos(pos[0], pos[1], pos[2]);
            }
        }
    }
}
