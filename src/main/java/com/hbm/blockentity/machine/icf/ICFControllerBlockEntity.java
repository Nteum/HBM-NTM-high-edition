package com.hbm.blockentity.machine.icf;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.HybridEnergyStorage;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.block.base.BaseMachineBlock;
import com.hbm.block.machine.icf.BlockICFReactor;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.registries.HBMCaps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ICFControllerBlockEntity extends BaseMachineBlockEntity {

    private static final long MAX_ENERGY = 50_000_000L;
    private static final long LASER_TRANSFER = 400_000L;
    private static final int MAX_DISTANCE = 48;

    private final BasicEnergyContainer energy = new BasicEnergyContainer(MAX_ENERGY, MAX_ENERGY, LASER_TRANSFER);
    private final HybridEnergyStorage forgeEnergy = new HybridEnergyStorage(energy);
    private BlockPos targetCore;
    private int beamLength;
    private boolean enabled = true;
    private int scanCooldown = 10;

    public ICFControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.ICF_CONTROLLER_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(1, ItemStack.EMPTY);
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this);
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(this.energy));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ENERGY, this.forgeEnergy);
        this.energy.setListener(this);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.level == null) return;
        TransmitUtils.dischargeItem(this, this.items.get(0));

        if (--scanCooldown <= 0 || targetCore == null) {
            rescanTarget(false);
            scanCooldown = 20;
        }

        if (!enabled || targetCore == null) {
            beamLength = 0;
            return;
        }

        BlockEntity target = level.getBlockEntity(targetCore);
        if (!(target instanceof ICFReactorBlockEntity reactor) || !((ICFReactorBlockEntity) target).isFormed) {
            targetCore = null;
            beamLength = 0;
            return;
        }

        if (!isLineClear(reactor)) {
            targetCore = null;
            beamLength = 0;
            return;
        }

        long available = energy.getEnergy();
        if (available <= 0) {
            beamLength = 0;
            return;
        }
        long toSend = Math.min(LASER_TRANSFER, available);
        energy.extract(toSend, false);
        reactor.receiveLaser(toSend, energy.getCapacity());
        beamLength = Math.max(beamLength, 1);
        spawnBeamParticles();
    }

    private boolean isLineClear(ICFReactorBlockEntity reactor) {
        if (level == null) return false;
        Direction facing = this.getBlockState().getValue(BaseMachineBlock.FACING);
        int max = beamLength == 0 ? MAX_DISTANCE : beamLength;
        for (int i = 1; i < max; i++) {
            BlockPos current = worldPosition.relative(facing, i);
            if (current.equals(reactor.getBlockPos())) {
                beamLength = i;
                return true;
            }
            BlockState state = level.getBlockState(current);
            if (state.isAir()) continue;
            Block block = state.getBlock();
            if (block instanceof BlockICFReactor) {
                beamLength = i;
                return true;
            }
            float hardness = state.getDestroySpeed(level, current);
            if (hardness >= 0 && hardness < 10.0F) {
                level.destroyBlock(current, false);
                continue;
            }
            return false;
        }
        return false;
    }

    private void spawnBeamParticles() {
        if (level == null || beamLength <= 0) return;
        Direction facing = this.getBlockState().getValue(BaseMachineBlock.FACING);
        Vec3 start = Vec3.atCenterOf(this.worldPosition);
        if (!(level instanceof ServerLevel server)) {
            return;
        }
        for (int i = 1; i < beamLength; i += 3) {
            Vec3 pos = start.add(facing.getStepX() * i, facing.getStepY() * i, facing.getStepZ() * i);
            server.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y + 0.2D, pos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    public void forceRescan(boolean notify) {
        rescanTarget(true);
        if (notify && level != null && !level.isClientSide) {
            level.playSound(null, worldPosition, net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BELL.value(), net.minecraft.sounds.SoundSource.BLOCKS, 0.4F, 1.2F);
        }
    }

    private void rescanTarget(boolean reset) {
        if (level == null) return;
        if (reset) {
            targetCore = null;
            beamLength = 0;
        }
        Direction facing = this.getBlockState().getValue(BaseMachineBlock.FACING);
        for (int i = 1; i <= MAX_DISTANCE; i++) {
            BlockPos current = worldPosition.relative(facing, i);
            BlockState state = level.getBlockState(current);
            if (!(state.getBlock() instanceof BlockICFReactor reactorBlock)) {
                continue;
            }
            BlockPos core = reactorBlock.getCore(state, level, current);
            if (core != null) {
                targetCore = core;
                beamLength = i;
                return;
            }
        }
    }

    public void toggleEnabled() {
        enabled = !enabled;
        setChanged();
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.energy.deserializeNBT(tag.getCompound(HBMKey.ENERGY));
        if (tag.contains("target")) {
            int[] coords = tag.getIntArray("target");
            if (coords.length == 3) {
                targetCore = new BlockPos(coords[0], coords[1], coords[2]);
            }
        }
        this.enabled = tag.getBoolean("enabled");
        this.beamLength = tag.getInt("beamLength");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(HBMKey.ENERGY, this.energy.serializeNBT());
        if (targetCore != null) {
            tag.putIntArray("target", new int[]{targetCore.getX(), targetCore.getY(), targetCore.getZ()});
        }
        tag.putBoolean("enabled", enabled);
        tag.putInt("beamLength", beamLength);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable(HBMLang.ICF_CONTROLLER.key());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }
}
