package com.hbm.item.tool;

import com.hbm.HBMKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.registries.Registries;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class ItemBuildWand extends Item {
    public static final String TAG_BLOCK_STATE = "wand_block_state";
    public static final String TAG_UNDO_ROOT = "wand_undo";
    public static final String TAG_UNDO_DIMENSION = "dimension";
    public static final String TAG_UNDO_LIST = "states";
    public static final String TAG_UNDO_POS = "pos";
    public static final String TAG_UNDO_STATE = "state";

    public ItemBuildWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, java.util.List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.hbm.wand.creative"));
        tooltip.add(Component.translatable("tooltip.hbm.wand.quote"));
        tooltip.add(Component.translatable("tooltip.hbm.wand.hint"));
        BlockPos pos = getStoredPos(stack);
        if (pos != null) {
            tooltip.add(Component.translatable("tooltip.hbm.wand.pos", pos.getX(), pos.getY(), pos.getZ()));
        } else {
            tooltip.add(Component.translatable("tooltip.hbm.wand.pos_missing"));
        }
        BlockState state = getStoredBlockState(stack);
        tooltip.add(Component.translatable("tooltip.hbm.wand.block", state.getBlock().getName()));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos clickedPos = context.getClickedPos();
        if (player.isShiftKeyDown()) {
            BlockState state = level.getBlockState(clickedPos);
            setStoredBlockState(stack, state);
            player.displayClientMessage(Component.translatable("msg.hbm.wand.block_set", state.getBlock().getName()), true);
            return InteractionResult.SUCCESS;
        }

        BlockPos storedPos = getStoredPos(stack);
        if (storedPos == null) {
            setStoredPos(stack, clickedPos);
            player.displayClientMessage(Component.translatable("msg.hbm.wand.pos_set", clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()), true);
            return InteractionResult.SUCCESS;
        }

        BlockState fillState = getStoredBlockState(stack);
        int changed = fillSelection(level, storedPos, clickedPos, fillState, player);
        clearStoredPos(stack);
        player.displayClientMessage(Component.translatable("msg.hbm.wand.fill", changed), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player.isShiftKeyDown()) {
            if (player instanceof ServerPlayer serverPlayer) {
                UndoResult result = undoLast(serverPlayer);
                if (result.status() == UndoStatus.OK) {
                    player.displayClientMessage(Component.translatable("msg.hbm.wand.undo.success", result.count()), true);
                    return InteractionResultHolder.sidedSuccess(stack, false);
                }
                if (result.status() == UndoStatus.MISSING_LEVEL) {
                    player.displayClientMessage(Component.translatable("msg.hbm.wand.undo.dimension_missing", result.dimension()), true);
                    return InteractionResultHolder.sidedSuccess(stack, false);
                }
            }
            setStoredBlockState(stack, Blocks.AIR.defaultBlockState());
            player.displayClientMessage(Component.translatable("msg.hbm.wand.block_set", Blocks.AIR.getName()), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public static UndoResult undoLast(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(TAG_UNDO_ROOT, Tag.TAG_COMPOUND)) {
            return new UndoResult(UndoStatus.EMPTY, 0, null);
        }
        CompoundTag undoData = data.getCompound(TAG_UNDO_ROOT);
        if (!undoData.contains(TAG_UNDO_LIST, Tag.TAG_LIST)) {
            data.remove(TAG_UNDO_ROOT);
            return new UndoResult(UndoStatus.EMPTY, 0, null);
        }
        if (!undoData.contains(TAG_UNDO_DIMENSION, Tag.TAG_STRING)) {
            data.remove(TAG_UNDO_ROOT);
            return new UndoResult(UndoStatus.EMPTY, 0, null);
        }
        String dimensionKey = undoData.getString(TAG_UNDO_DIMENSION);
        if (dimensionKey.isEmpty()) {
            data.remove(TAG_UNDO_ROOT);
            return new UndoResult(UndoStatus.EMPTY, 0, null);
        }
        ResourceLocation dimensionId = new ResourceLocation(dimensionKey);
        ServerLevel level = player.server.getLevel(ResourceKey.create(Registries.DIMENSION, dimensionId));
        if (level == null) {
            data.remove(TAG_UNDO_ROOT);
            return new UndoResult(UndoStatus.MISSING_LEVEL, 0, dimensionId);
        }
        ListTag list = undoData.getList(TAG_UNDO_LIST, Tag.TAG_COMPOUND);
        int restored = 0;
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            BlockPos pos = BlockPos.of(entry.getLong(TAG_UNDO_POS));
            BlockState state = readBlockState(entry.getCompound(TAG_UNDO_STATE));
            level.setBlock(pos, state, Block.UPDATE_ALL);
            restored++;
        }
        data.remove(TAG_UNDO_ROOT);
        return new UndoResult(UndoStatus.OK, restored, dimensionId);
    }

    private static int fillSelection(Level level, BlockPos from, BlockPos to, BlockState fillState, Player player) {
        int minX = Math.min(from.getX(), to.getX());
        int minY = Math.min(from.getY(), to.getY());
        int minZ = Math.min(from.getZ(), to.getZ());
        int maxX = Math.max(from.getX(), to.getX());
        int maxY = Math.max(from.getY(), to.getY());
        int maxZ = Math.max(from.getZ(), to.getZ());

        ListTag undoList = new ListTag();
        int changed = 0;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    cursor.set(x, y, z);
                    BlockState current = level.getBlockState(cursor);
                    if (current.equals(fillState)) {
                        continue;
                    }
                    CompoundTag entry = new CompoundTag();
                    entry.putLong(TAG_UNDO_POS, cursor.asLong());
                    entry.put(TAG_UNDO_STATE, writeBlockState(current));
                    undoList.add(entry);
                    level.setBlock(cursor, fillState, Block.UPDATE_ALL);
                    changed++;
                }
            }
        }

        if (player instanceof ServerPlayer serverPlayer) {
            if (undoList.isEmpty()) {
                serverPlayer.getPersistentData().remove(TAG_UNDO_ROOT);
            } else {
                storeUndo(serverPlayer, level, undoList);
            }
        }

        return changed;
    }

    private static void storeUndo(ServerPlayer player, Level level, ListTag undoList) {
        CompoundTag undo = new CompoundTag();
        undo.putString(TAG_UNDO_DIMENSION, level.dimension().location().toString());
        undo.put(TAG_UNDO_LIST, undoList);
        player.getPersistentData().put(TAG_UNDO_ROOT, undo);
    }

    private static void setStoredPos(ItemStack stack, BlockPos pos) {
        stack.addTagElement(HBMKey.POSITION, NbtUtils.writeBlockPos(pos));
    }

    private static void clearStoredPos(ItemStack stack) {
        stack.removeTagKey(HBMKey.POSITION);
    }

    private static BlockPos getStoredPos(ItemStack stack) {
        CompoundTag posTag = stack.getTagElement(HBMKey.POSITION);
        if (posTag == null) {
            return null;
        }
        return NbtUtils.readBlockPos(posTag);
    }

    private static void setStoredBlockState(ItemStack stack, BlockState state) {
        stack.addTagElement(TAG_BLOCK_STATE, writeBlockState(state));
    }

    private static BlockState getStoredBlockState(ItemStack stack) {
        CompoundTag stateTag = stack.getTagElement(TAG_BLOCK_STATE);
        if (stateTag == null || stateTag.isEmpty()) {
            return Blocks.AIR.defaultBlockState();
        }
        return readBlockState(stateTag);
    }

    private static CompoundTag writeBlockState(BlockState state) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Name", BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
        CompoundTag propsTag = new CompoundTag();
        for (Map.Entry<Property<?>, Comparable<?>> entry : state.getValues().entrySet()) {
            Property<?> property = entry.getKey();
            propsTag.putString(property.getName(), getPropertyValue(property, entry.getValue()));
        }
        if (!propsTag.isEmpty()) {
            tag.put("Properties", propsTag);
        }
        return tag;
    }

    private static BlockState readBlockState(CompoundTag tag) {
        if (tag == null || !tag.contains("Name", Tag.TAG_STRING)) {
            return Blocks.AIR.defaultBlockState();
        }
        Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(tag.getString("Name")));
        BlockState state = block.defaultBlockState();
        if (tag.contains("Properties", Tag.TAG_COMPOUND)) {
            CompoundTag props = tag.getCompound("Properties");
            for (String key : props.getAllKeys()) {
                Property<?> property = block.getStateDefinition().getProperty(key);
                if (property != null) {
                    state = applyProperty(state, property, props.getString(key));
                }
            }
        }
        return state;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static BlockState applyProperty(BlockState state, Property property, String value) {
        Optional<?> parsed = property.getValue(value);
        if (parsed.isPresent()) {
            return state.setValue(property, (Comparable) parsed.get());
        }
        return state;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String getPropertyValue(Property property, Comparable value) {
        return property.getName(value);
    }

    public enum UndoStatus {
        OK,
        EMPTY,
        MISSING_LEVEL
    }

    public record UndoResult(UndoStatus status, int count, @Nullable ResourceLocation dimension) {}
}
