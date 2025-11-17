package com.hbm.reactor.rbmk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Collections;
import java.util.HashMap;
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

    private final ServerLevel level;
    private final Map<BlockPos, RBMKColumnState> columns = new HashMap<>();

    RBMKLevelContext(final ServerLevel level) {
        this.level = level;
    }

    public ServerLevel level() {
        return level;
    }

    public Map<BlockPos, RBMKColumnState> snapshot() {
        return Collections.unmodifiableMap(columns);
    }

    public RBMKColumnState registerColumn(final BlockPos corePos, final RBMKSettings settings) {
        RBMKColumnState state = new RBMKColumnState(corePos.immutable(), settings.columnHeight(), RBMKLidType.NONE);
        columns.put(corePos.immutable(), state);
        return state;
    }

    public void removeColumn(final BlockPos corePos) {
        columns.remove(corePos);
    }

    public Optional<RBMKColumnState> column(final BlockPos corePos) {
        return Optional.ofNullable(columns.get(corePos));
    }

    public boolean setLidState(final BlockPos corePos, final RBMKLidType lidType) {
        final RBMKColumnState state = columns.get(corePos);
        if (state == null) {
            return false;
        }
        state.setLidType(lidType);
        return true;
    }
}
