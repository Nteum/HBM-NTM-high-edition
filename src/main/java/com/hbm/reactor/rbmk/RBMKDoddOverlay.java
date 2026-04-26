package com.hbm.reactor.rbmk;

import com.hbm.block.base.BlockDummyable;
import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.blockentity.base.TileProxyBase;
import com.hbm.blockentity.machine.rbmk.RBMKBaseEntity;
import com.hbm.blockentity.machine.rbmk.RBMKBoilerEntity;
import com.hbm.blockentity.machine.rbmk.RBMKControlRodEntity;
import com.hbm.blockentity.machine.rbmk.RBMKCoolerEntity;
import com.hbm.blockentity.machine.rbmk.RBMKFuelChannelEntity;
import com.hbm.blockentity.machine.rbmk.RBMKHeaterEntity;
import com.hbm.blockentity.machine.rbmk.RBMKOutgasserEntity;
import com.hbm.blockentity.machine.rbmk.RBMKPeripheralEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Client overlay helper that mimics legacy RBMK DODD output when aiming at
 * RBMK blocks.
 */
public final class RBMKDoddOverlay {

    private RBMKDoddOverlay() {
    }

    public static List<Component> describe(final Level level, final BlockPos hitPos) {
        final List<Component> lines = new ArrayList<>();
        final BlockState hitState = level.getBlockState(hitPos);
        lines.add(Component.literal("Dump of Ordered Data Diagnostic (DODD)").withStyle(ChatFormatting.GREEN));
        lines.add(hitState.getBlock().getName().copy().withStyle(ChatFormatting.YELLOW));

        final PeripheralDiagnostic peripheral = resolvePeripheral(level, hitPos, hitState);
        if (peripheral != null) {
            switch (peripheral.type()) {
                case CONSOLE, CRANE_CONSOLE, AUTOLOADER -> {
                    if (peripheral.linkedColumn() == null) {
                        lines.add(Component.literal("core: <not linked>").withStyle(ChatFormatting.RED));
                        return lines;
                    }
                    lines.add(Component.literal("core: " + peripheral.linkedColumn().toShortString()).withStyle(ChatFormatting.GRAY));
                    final RBMKColumnState linkedState = resolveState(level, peripheral.linkedColumn());
                    lines.add(Component.literal("linkedType: " + linkedState.columnType().name().toLowerCase(Locale.ROOT)));
                    if (peripheral.entity() != null && peripheral.type() == RBMKPeripheralType.AUTOLOADER) {
                        lines.add(Component.literal(String.format(Locale.ROOT,
                                "cycle: %d%% working: %s",
                                peripheral.entity().getAutoloaderCycle(),
                                peripheral.entity().isAutoloaderWorking() ? "1b" : "0b")));
                    }
                    return lines;
                }
                case DEBRIS -> {
                    lines.add(Component.literal("type: debris"));
                    return lines;
                }
                default -> {
                }
            }
        }

        final BlockPos corePos = resolveCorePos(level, hitPos, hitState);
        if (corePos == null) {
            lines.add(Component.literal("core: <not linked>").withStyle(ChatFormatting.RED));
            return lines;
        }
        lines.add(Component.literal("core: " + corePos.toShortString()).withStyle(ChatFormatting.GRAY));

        final RBMKBaseEntity base = level.getBlockEntity(corePos) instanceof RBMKBaseEntity be ? be : null;
        final BlockEntity aboveEntity = level.getBlockEntity(corePos.above());
        final RBMKFuelChannelEntity fuel = aboveEntity instanceof RBMKFuelChannelEntity fc ? fc : null;
        final RBMKControlRodEntity controlRod = aboveEntity instanceof RBMKControlRodEntity cr ? cr : null;
        final RBMKHeaterEntity heater = aboveEntity instanceof RBMKHeaterEntity he ? he : null;

        final RBMKColumnState state = resolveState(level, corePos);
        final double globalControl = level instanceof ServerLevel serverLevel
                ? RBMKManager.context(serverLevel).controlRodAverage()
                : 0.0D;

        lines.add(Component.literal("type: " + state.columnType().name().toLowerCase(Locale.ROOT)));
        lines.add(Component.literal(String.format(Locale.ROOT, "fluxFast: %.1fd", state.fastFlux())));
        lines.add(Component.literal(String.format(Locale.ROOT, "fluxSlow: %.1fd", state.slowFlux())));
        lines.add(Component.literal("hasRod: " + (state.hasRod() ? "1b" : "0b")));
        lines.add(Component.literal(String.format(Locale.ROOT, "heat: %.1fd / %.1fd", state.heat(), state.maxHeat())));
        lines.add(Component.literal("moderated: " + (state.moderated() ? "1b" : "0b")));
        lines.add(Component.literal("reasimSteam: " + state.steamAmount()));
        lines.add(Component.literal("reasimWater: " + state.waterAmount()));

        if (fuel != null) {
            lines.add(Component.literal(String.format(Locale.ROOT, "burn: %d/%d", fuel.burnTimeRemaining(), fuel.burnTimeTotal())));
            lines.add(Component.literal(String.format(Locale.ROOT, "enrichment: %.1f%%", state.enrichment() * 100.0D)));
            lines.add(Component.literal(String.format(Locale.ROOT, "xenon: %.1f%%", state.xenon())));
            lines.add(Component.literal(String.format(Locale.ROOT, "coreHeat: %.1fd / %.1fd", state.coreHeat(), state.coreMaxHeat())));
        } else if (controlRod != null) {
            lines.add(Component.literal(String.format(Locale.ROOT,
                    "control: %.0f%% -> %.0f%%", state.controlRodInsertion() * 100.0D, state.targetControlRodInsertion() * 100.0D)));
            lines.add(Component.literal("color: " + state.controlColor()));
        } else if (aboveEntity instanceof RBMKBoilerEntity boiler) {
            lines.add(Component.literal(String.format(Locale.ROOT,
                    "steamCompression: %d", boiler.compressionStage())));
        } else if (heater != null) {
            lines.add(Component.literal("heater: " + (heater.isActive() ? "on" : "off")));
        } else if (aboveEntity instanceof RBMKOutgasserEntity outgasser) {
            lines.add(Component.literal(String.format(Locale.ROOT, "progress: %.1f", outgasser.progress())));
        } else if (aboveEntity instanceof RBMKCoolerEntity) {
            lines.add(Component.literal("cooler: active"));
        }

        lines.add(Component.literal(String.format(Locale.ROOT,
                "controlLocal: %.0f%% controlGlobal: %.0f%%", state.controlRodInsertion() * 100.0D, globalControl * 100.0D)));
        return lines;
    }

    private static PeripheralDiagnostic resolvePeripheral(final Level level, final BlockPos hitPos, final BlockState hitState) {
        final BlockEntity hitEntity = level.getBlockEntity(hitPos);
        if (hitEntity instanceof RBMKPeripheralEntity peripheral) {
            return new PeripheralDiagnostic(peripheral.getPeripheralType(), hitPos, peripheral.getLinkedColumn(), peripheral);
        }
        if (hitEntity instanceof TileProxyBase tileProxy && tileProxy.cachedPos != null
                && level.getBlockEntity(tileProxy.cachedPos) instanceof RBMKPeripheralEntity peripheral) {
            return new PeripheralDiagnostic(peripheral.getPeripheralType(), tileProxy.cachedPos, peripheral.getLinkedColumn(), peripheral);
        }
        if (hitState.getBlock() instanceof BlockDummyable dummyable) {
            BlockPos core = dummyable.getCore(hitState, level, hitPos);
            if (level.getBlockEntity(core) instanceof RBMKPeripheralEntity peripheral) {
                return new PeripheralDiagnostic(peripheral.getPeripheralType(), core, peripheral.getLinkedColumn(), peripheral);
            }
        }
        return null;
    }

    private static boolean hasRod(final RBMKFuelChannelEntity fuel) {
        return fuel != null && !fuel.fuelStack().isEmpty();
    }

    private static RBMKColumnState resolveState(final Level level, final BlockPos corePos) {
        if (level instanceof ServerLevel serverLevel) {
            RBMKColumnState state = RBMKManager.context(serverLevel).column(corePos).orElse(null);
            if (state != null) {
                RBMKColumns.populateState(level, state);
                return state;
            }
        }

        final RBMKColumnState fallback = new RBMKColumnState(corePos, RBMKSettings.DEFAULT, RBMKLidType.NONE);
        if (level.getBlockEntity(corePos) instanceof RBMKBaseEntity base) {
            fallback.setLidType(base.getLidType());
        }
        RBMKColumns.populateState(level, fallback);
        return fallback;
    }

    private static BlockPos resolveCorePos(final Level level, final BlockPos hitPos, final BlockState hitState) {
        final BlockEntity hitEntity = level.getBlockEntity(hitPos);
        if (hitEntity instanceof RBMKBaseEntity) {
            return hitPos;
        }
        if (hitEntity instanceof RBMKPeripheralEntity peripheral && peripheral.getLinkedColumn() != null) {
            return peripheral.getLinkedColumn();
        }
        if (hitEntity instanceof TileProxyBase tileProxy) {
            if (tileProxy.cachedPos != null && level.getBlockEntity(tileProxy.cachedPos) instanceof RBMKBaseEntity) {
                return tileProxy.cachedPos;
            }
            if (tileProxy.cachedPos != null
                    && level.getBlockEntity(tileProxy.cachedPos) instanceof RBMKPeripheralEntity peripheral
                    && peripheral.getLinkedColumn() != null) {
                return peripheral.getLinkedColumn();
            }
        }

        if (hitState.getBlock() instanceof BlockRBMKBase baseBlock) {
            BlockPos core = baseBlock.getCore(hitState, level, hitPos);
            if (level.getBlockEntity(core) instanceof RBMKBaseEntity) {
                return core;
            }
        }
        if (hitState.getBlock() instanceof BlockDummyable dummyable) {
            BlockPos core = dummyable.getCore(hitState, level, hitPos);
            if (level.getBlockEntity(core) instanceof RBMKBaseEntity) {
                return core;
            }
            if (level.getBlockEntity(core.below()) instanceof RBMKBaseEntity) {
                return core.below();
            }
        }
        if (level.getBlockEntity(hitPos.below()) instanceof RBMKBaseEntity) {
            return hitPos.below();
        }
        if (RBMKColumns.classifyBlockPath(BuiltInRegistries.BLOCK.getKey(hitState.getBlock()).getPath()) != RBMKColumnType.BLANK) {
            return hitPos;
        }
        return null;
    }

    private record PeripheralDiagnostic(RBMKPeripheralType type, BlockPos pos,
                                        BlockPos linkedColumn, RBMKPeripheralEntity entity) {
    }
}
