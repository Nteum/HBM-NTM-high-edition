package com.hbm.blockentity.weapon;

import com.hbm.HBM;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.config.ConfigBomb;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.IntSupplier;

public class NukeBombCustomEntity extends EntityNukeBomb {
    private static final String PROFILE_TAG = "CustomNukeProfile";
    public static final AABB BOX = AABB.of(new BoundingBox(-1,0,-1,2,1,1));
    private CustomNukeProfile profile = CustomNukeProfile.DEFAULT;

    public NukeBombCustomEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.NUKE_BOMB_CUSTOM_ENTITY.get(),pPos, pBlockState);
        this.items = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    public CustomNukeProfile getProfile() {
        return profile;
    }

    public boolean setProfile(CustomNukeProfile profile) {
        CustomNukeProfile next = profile == null ? CustomNukeProfile.DEFAULT : profile;
        if (this.profile == next) {
            return false;
        }
        this.profile = next;
        setChanged();
        if (level != null && !level.isClientSide) {
            sendUpdatePacket();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        writeProfile(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        readProfile(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        writeProfile(tag);
        return tag;
    }

    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        writeProfile(tag);
        return tag;
    }

    @Override
    public void handleUpdatePacket(CompoundTag tag) {
        super.handleUpdatePacket(tag);
        readProfile(tag);
    }

    private void writeProfile(CompoundTag tag) {
        tag.putString(PROFILE_TAG, profile.getId());
    }

    private void readProfile(CompoundTag tag) {
        if (tag.contains(PROFILE_TAG, CompoundTag.TAG_STRING)) {
            profile = CustomNukeProfile.byId(tag.getString(PROFILE_TAG));
        }
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }

    @Override
    public Component getDefaultName() {
        return null;
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }

//    @Override
//    public AABB getRenderBoundingBox() {
//        return BOX;
//    }

    public enum CustomNukeProfile {
        DEFAULT("custom", "custom_kit", () -> ConfigBomb.manRadius, "block/bomb/custom", false),
        BOY("boy", "boy_kit", () -> ConfigBomb.boyRadius, "block/bomb/boy", true),
        FAT_MAN("fat_man", "man_kit", () -> ConfigBomb.manRadius, "block/bomb/fat_man", false),
        GADGET("gadget", "gadget_kit", () -> ConfigBomb.gadgetRadius, "block/bomb/custom_gadget", false),
        MIKE("mike", "mike_kit", () -> ConfigBomb.mikeRadius, "block/bomb/custom_mike", true),
        TSAR("tsar", "tsar_kit", () -> ConfigBomb.tsarRadius, "block/bomb/custom_tsar", true),
        FLEIJA("fleija", "fleija_kit", () -> ConfigBomb.fleijaRadius, "block/bomb/custom_fleija", false),
        SOLINIUM("solinium", "solinium_kit", () -> ConfigBomb.soliniumRadius, "block/bomb/custom_solinium", false),
        PROTOTYPE("prototype", "prototype_kit", () -> ConfigBomb.prototypeRadius, "block/bomb/custom_prototype", false),
        MULTI("multi", "multi_kit", () -> ConfigBomb.prototypeRadius, "block/bomb/custom_multi", false);

        private final String id;
        private final String kitId;
        private final IntSupplier range;
        private final ResourceLocation model;
        private final boolean thermobaric;

        CustomNukeProfile(String id, String kitId, IntSupplier range, String model, boolean thermobaric) {
            this.id = id;
            this.kitId = kitId;
            this.range = range;
            this.model = HBM.rl(model);
            this.thermobaric = thermobaric;
        }

        public String getId() {
            return id;
        }

        public String getKitId() {
            return kitId;
        }

        public int getRange() {
            return Math.max(0, range.getAsInt());
        }

        public ResourceLocation getModel() {
            return model;
        }

        public boolean isThermobaric() {
            return thermobaric;
        }

        public Component getDisplayName() {
            return Component.translatable("item.hbm." + kitId);
        }

        public static CustomNukeProfile byId(String id) {
            String normalized = id == null ? "" : id.toLowerCase(Locale.ROOT);
            for (CustomNukeProfile profile : values()) {
                if (profile.id.equals(normalized)) {
                    return profile;
                }
            }
            return DEFAULT;
        }

        @Nullable
        public static CustomNukeProfile fromKit(ItemStack stack) {
            if (stack.isEmpty()) {
                return null;
            }
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
            if (itemId == null || !HBM.MODID.equals(itemId.getNamespace())) {
                return null;
            }
            for (CustomNukeProfile profile : values()) {
                if (profile.kitId.equals(itemId.getPath())) {
                    return profile;
                }
            }
            return null;
        }
    }
}
