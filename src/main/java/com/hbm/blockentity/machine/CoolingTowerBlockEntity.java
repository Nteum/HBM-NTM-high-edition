package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.blockentity.base.TileProxyBase;
import com.hbm.blockentity.machine.component.CondenserLogic;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.multiblock.MultiblockData;
import com.hbm.api.fluid.VisitRestrictWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Multiblock auxiliary cooling tower that rapidly condenses spent steam.
 */
public class CoolingTowerBlockEntity extends DummyableBlockEntity {

    private static final int STEAM_CAPACITY = 24_000;
    private static final int WATER_CAPACITY = 24_000;
    private static final int CONVERSION_RATE = 800;

    private static final Set<Vec3i> STEAM_PORTS = new HashSet<>();
    private static final Set<Vec3i> WATER_PORTS = new HashSet<>();

    static {
        // default orientation facing south
        registerRing(STEAM_PORTS, 0, 0);
        registerRing(WATER_PORTS, 0, 1);
    }

    private final CondenserLogic logic;

    public CoolingTowerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.COOLING_TOWER_ENTITY.get(), pos, state);
        this.logic = new CondenserLogic(STEAM_CAPACITY, WATER_CAPACITY, CONVERSION_RATE);
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, logic.handler());
        this.multiblockData = MultiblockData.mapping.get(ModBlocks.machine_cooling_tower.get());
        this.isFormed = true;
    }

    private static void registerRing(Set<Vec3i> set, int radiusOffset, int yOffset) {
        int radius = 4 + radiusOffset;
        int inner = 3 + radiusOffset;
        set.add(new Vec3i(radius, yOffset, 0));
        set.add(new Vec3i(-radius, yOffset, 0));
        set.add(new Vec3i(0, yOffset, radius));
        set.add(new Vec3i(0, yOffset, -radius));
        set.add(new Vec3i(inner, yOffset, inner));
        set.add(new Vec3i(-inner, yOffset, inner));
        set.add(new Vec3i(inner, yOffset, -inner));
        set.add(new Vec3i(-inner, yOffset, -inner));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (level != null && logic.tick(level)) {
            setChanged();
            sendUpdatePacket();
        }
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        if (level == null || logic.getWaterTimer() <= 0) {
            return;
        }
        if (level.random.nextInt(4) != 0) {
            return;
        }
        double x = worldPosition.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 2.5;
        double y = worldPosition.getY() + 6 + level.random.nextDouble() * 4;
        double z = worldPosition.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 2.5;
        level.addParticle(ParticleTypes.CLOUD, x, y, z, 0.0D, 0.02D, 0.0D);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(HBMKey.FLUIDS, logic.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains(HBMKey.FLUIDS)) {
            logic.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        }
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        logic.writeSyncTag(tag);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        logic.readSyncTag(tag);
    }

    @Override
    public Component getDisplayName() {
        return getDefaultName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    @Override
    public void giveProxyCapabilities(Vec3i offset, TileProxyBase proxy, Capability<?> cap, Set<Direction> directions) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (STEAM_PORTS.contains(offset)) {
                proxy.capabilitiesContent.addCapability(cap, new VisitRestrictWrapper(logic.handler(), false, 0), directions);
                return;
            }
            if (WATER_PORTS.contains(offset)) {
                proxy.capabilitiesContent.addCapability(cap, new VisitRestrictWrapper(logic.handler(), false, 1), directions);
                return;
            }
        }
        super.giveProxyCapabilities(offset, proxy, cap, directions);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_cooling_tower");
    }
}
