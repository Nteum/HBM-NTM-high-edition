package com.hbm.reactor.rbmk;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Per-level RBMK bookkeeping. This is intentionally light-weight: it keeps
 * track of all registered reactor columns and exposes a few helpers for block
 * entities to mutate lid state or query structural properties. Heating, neutron
 * simulation, etc. will hook into this context later once the rest of the port
 * is ready.
 */
public final class RBMKLevelContext {

    private static final double TIME_STEP = 1.0D / 20.0D;
    private static final Direction[] TRANSFER_DIRECTIONS = new Direction[]{Direction.EAST, Direction.SOUTH};

    private final ServerLevel level;
    private final Map<BlockPos, RBMKColumnState> columns = new HashMap<>();
    private double cachedControlRodInsertion;

    RBMKLevelContext(final ServerLevel level) {
        this.level = level;
    }

    public ServerLevel level() {
        return level;
    }

    public Map<BlockPos, RBMKColumnState> snapshot() {
        return Collections.unmodifiableMap(columns);
    }

    public double controlRodAverage() {
        return cachedControlRodInsertion;
    }

    public RBMKColumnState registerColumn(final BlockPos corePos, final RBMKSettings settings) {
        final BlockPos key = corePos.immutable();
        RBMKColumnState state = new RBMKColumnState(key, settings, RBMKLidType.NONE);
        RBMKColumns.populateState(level, state);
        columns.put(key, state);
        return state;
    }

    public void removeColumn(final BlockPos corePos) {
        columns.remove(corePos);
    }

    public Optional<RBMKColumnState> column(final BlockPos corePos) {
        return Optional.ofNullable(columns.get(corePos));
    }

    public boolean addHeat(final BlockPos corePos, final double heat) {
        final RBMKColumnState state = columns.get(corePos);
        if (state == null) {
            return false;
        }
        state.addHeat(heat);
        return true;
    }

    public boolean setLidState(final BlockPos corePos, final RBMKLidType lidType) {
        final RBMKColumnState state = columns.get(corePos);
        if (state == null) {
            return false;
        }
        state.setLidType(lidType);
        return true;
    }

    public boolean setControlRodInsertion(final BlockPos corePos, final double insertion) {
        final RBMKColumnState state = columns.get(corePos);
        if (state == null) {
            return false;
        }
        state.setControlRodInsertion(insertion);
        return true;
    }

    public void tick() {
        if (columns.isEmpty()) {
            cachedControlRodInsertion = 0.0D;
            return;
        }

        final List<BlockPos> positions = new ArrayList<>(columns.keySet());

        int controlCount = 0;
        double controlSum = 0.0D;
        for (BlockPos pos : positions) {
            final RBMKColumnState state = columns.get(pos);
            if (state == null) {
                continue;
            }
            RBMKColumns.populateState(level, state);
            if (state.columnType() == RBMKColumnType.CONTROL || state.columnType() == RBMKColumnType.CONTROL_AUTO) {
                controlSum += state.controlRodInsertion();
                controlCount++;
            }
        }
        cachedControlRodInsertion = controlCount > 0 ? controlSum / controlCount : 0.0D;

        propagateFlux(positions);
        final Map<BlockPos, Double> transferDeltas = new HashMap<>();

        for (BlockPos pos : positions) {
            final RBMKColumnState state = columns.get(pos);
            if (state == null) {
                continue;
            }
            final double heat = state.heat();

            for (Direction direction : TRANSFER_DIRECTIONS) {
                final BlockPos neighborPos = pos.relative(direction);
                final RBMKColumnState neighbor = columns.get(neighborPos);
                if (neighbor == null) {
                    continue;
                }

                final double flow = Math.min(state.settings().columnHeatFlow(), neighbor.settings().columnHeatFlow());
                if (flow <= 0.0D) {
                    continue;
                }

                final double diff = heat - neighbor.heat();
                final double transfer = diff * 0.5D * flow * TIME_STEP;
                if (transfer == 0.0D) {
                    continue;
                }

                transferDeltas.merge(pos, -transfer, Double::sum);
                transferDeltas.merge(neighborPos, transfer, Double::sum);
            }
        }

        for (Map.Entry<BlockPos, Double> entry : transferDeltas.entrySet()) {
            final RBMKColumnState state = columns.get(entry.getKey());
            if (state != null) {
                state.addHeat(entry.getValue());
            }
        }

        for (BlockPos pos : positions) {
            final RBMKColumnState state = columns.get(pos);
            if (state == null) {
                continue;
            }
            final double passiveCooling = state.settings().passiveCooling();
            if (passiveCooling <= 0.0D) {
                continue;
            }
            state.addHeat(-passiveCooling * TIME_STEP);
        }

        handleMeltdowns(positions);
    }

    private void propagateFlux(final List<BlockPos> positions) {
        for (BlockPos pos : positions) {
            final RBMKColumnState source = columns.get(pos);
            if (source == null) {
                continue;
            }
            final double outgoingFast = source.fastFlux();
            final double outgoingSlow = source.slowFlux();
            if (outgoingFast <= 0.0D && outgoingSlow <= 0.0D) {
                continue;
            }

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                final BlockPos neighborPos = pos.relative(direction);
                final RBMKColumnState neighbor = columns.get(neighborPos);
                if (neighbor == null) {
                    continue;
                }
                applyFluxTransfer(source, neighbor, outgoingFast, outgoingSlow);
            }
        }

        for (RBMKColumnState state : columns.values()) {
            if (!state.moderated()) {
                continue;
            }
            final double fast = state.fastFlux();
            final double slow = state.slowFlux();
            if (fast <= 0.0D) {
                continue;
            }
            state.resetFlux();
            state.addFastFlux(fast * 0.30D);
            state.addSlowFlux((fast * 0.70D) + slow);
        }
    }

    private static void applyFluxTransfer(final RBMKColumnState source,
                                          final RBMKColumnState neighbor,
                                          final double outgoingFast,
                                          final double outgoingSlow) {
        final RBMKColumnType type = neighbor.columnType();
        if (type == RBMKColumnType.ABSORBER) {
            neighbor.addSlowFlux((outgoingFast + outgoingSlow) * 0.05D);
            return;
        }
        if (type == RBMKColumnType.REFLECTOR) {
            neighbor.addSlowFlux((outgoingFast + outgoingSlow) * 0.25D);
            source.addSlowFlux((outgoingFast + outgoingSlow) * 0.20D);
            return;
        }
        if (type == RBMKColumnType.MODERATOR) {
            neighbor.addSlowFlux(outgoingFast * 0.80D + outgoingSlow * 0.50D);
            neighbor.setModerated(true);
            return;
        }
        if (type == RBMKColumnType.BLANK || type == RBMKColumnType.BOILER || type == RBMKColumnType.OUTGASSER
                || type == RBMKColumnType.STORAGE || type == RBMKColumnType.COOLER || type == RBMKColumnType.HEATEX
                || type == RBMKColumnType.CONTROL || type == RBMKColumnType.CONTROL_AUTO || type == RBMKColumnType.FUEL
                || type == RBMKColumnType.FUEL_SIM || type == RBMKColumnType.BREEDER) {
            neighbor.addFastFlux(outgoingFast * 0.45D);
            neighbor.addSlowFlux(outgoingSlow * 0.45D);
        }
    }

    private void handleMeltdowns(final List<BlockPos> positions) {
        for (BlockPos pos : positions) {
            final RBMKColumnState state = columns.get(pos);
            if (state == null || state.meltedDown()) {
                continue;
            }
            final double threshold = state.settings().meltdownHeat();
            if (threshold <= 0.0D) {
                continue;
            }
            if (state.heat() < threshold) {
                continue;
            }
            state.markMeltedDown();
            triggerMeltdown(pos, state);
        }
    }

    private void triggerMeltdown(final BlockPos corePos, final RBMKColumnState state) {
        final double strength = Math.max(0.0D, state.settings().meltdownExplosionStrength());
        level.explode(null,
                corePos.getX() + 0.5D,
                corePos.getY() + 0.5D,
                corePos.getZ() + 0.5D,
                (float) strength,
                Level.ExplosionInteraction.BLOCK);
        level.playSound(null, corePos, com.hbm.registries.ModSounds.BLOCK_RBMK_EXPLOSION.get(), SoundSource.BLOCKS, 6.0F, 1.0F);

        final int height = Math.max(1, state.columnHeight());
        for (int y = 0; y < height; y++) {
            final BlockPos pos = corePos.above(y);
            final BlockState blockState = level.getBlockState(pos);
            if (!blockState.isAir()) {
                level.destroyBlock(pos, false);
            }
        }

        level.setBlock(corePos, Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
        final BlockPos above = corePos.above();
        if (level.getBlockState(above).isAir()) {
            level.setBlock(above, Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            final BlockPos firePos = corePos.relative(direction);
            if (level.getBlockState(firePos).isAir() && level.getBlockState(firePos.below()).isSolidRender(level, firePos.below())) {
                level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        columns.remove(corePos);
    }
}
