package com.hbm.item.tool;

import com.hbm.blockentity.machine.pile.ChicagoBreederBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoDetectorBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoFuelBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoPileBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoSourceBlockEntity;

import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Locale;

/**
 * Simple handheld drill that converts raw graphite blocks into drilled channels for the Chicago pile.
 * Also serves as the basic diagnostic tool for existing pile channels.
 */
public class HandDrillItem extends Item {

    private final boolean damageOnUse;

    public HandDrillItem(int durability) {
        super(withDurability(durability));
        this.damageOnUse = durability > 0;
    }

    private static Properties withDurability(int durability) {
        Properties properties = new Properties().stacksTo(1);
        return durability > 0 ? properties.durability(durability) : properties;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult inspect = probeChicago(context);
        if (inspect.consumesAction()) {
            return inspect;
        }
        InteractionResult result = drillGraphite(context);
        if (result.consumesAction() && !context.getLevel().isClientSide) {
            damageDrill(context);
        }
        return result;
    }

    private InteractionResult drillGraphite(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.chicago_graphite_block.get())) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            Direction.Axis axis = context.getClickedFace() != null ? context.getClickedFace().getAxis() : Direction.Axis.Y;
            BlockState target = ModBlocks.chicago_graphite_drilled.get().defaultBlockState()
                    .setValue(RotatedPillarBlock.AXIS, axis);
            level.setBlock(pos, target, Block.UPDATE_ALL);
            Block.popResource(level, pos, new ItemStack(ModItems.INGOT_GRAPHITE.get()));
            SoundType sound = state.getSoundType(level, pos, context.getPlayer());
            level.playSound(null, pos, sound.getBreakSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, pos);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private InteractionResult probeChicago(UseOnContext context) {
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (!(blockEntity instanceof ChicagoPileBlockEntity)) {
            return InteractionResult.PASS;
        }
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            Component message = describeChicago(blockEntity);
            if (message != null) {
                player.displayClientMessage(message, true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private Component describeChicago(BlockEntity entity) {
        if (entity instanceof ChicagoFuelBlockEntity fuel) {
            return describeFuel(fuel);
        }
        if (entity instanceof ChicagoSourceBlockEntity source) {
            return describeSource(source);
        }
        if (entity instanceof ChicagoBreederBlockEntity breeder) {
            return describeBreeder(breeder);
        }
        if (entity instanceof ChicagoDetectorBlockEntity detector) {
            return describeDetector(detector);
        }
        return Component.translatable("message.hbm.chicago.status.unknown");
    }

    private Component describeFuel(ChicagoFuelBlockEntity fuel) {
        ChicagoFuelBlockEntity.FuelVariant variant = fuel.getVariant();
        if (variant == null) {
            return Component.translatable("message.hbm.chicago.status.fuel_empty");
        }
        ItemStack rodStack = new ItemStack(variant.item.get());
        int life = fuel.getRodLife();
        int maxLife = variant.maxLife;
        double heat = fuel.getHeatLevel();
        double meltdown = fuel.getMeltdownHeat();
        double wearPercent = maxLife > 0 ? life * 100.0D / maxLife : 0.0D;
        double heatPercent = meltdown > 0 ? heat * 100.0D / meltdown : 0.0D;
        return Component.translatable("message.hbm.chicago.status.fuel",
                rodStack.getHoverName(),
                life,
                maxLife,
                formatPercent(wearPercent),
                formatNumber(heat),
                formatNumber(meltdown),
                formatPercent(heatPercent));
    }

    private Component describeSource(ChicagoSourceBlockEntity source) {
        ChicagoSourceBlockEntity.SourceType type = source.getSourceType();
        if (type == null) {
            return Component.translatable("message.hbm.chicago.status.source_empty");
        }
        ItemStack rodStack = new ItemStack(type.rod().get());
        return Component.translatable("message.hbm.chicago.status.source",
                rodStack.getHoverName(),
                type.fluxPerStream(),
                type.streams());
    }

    private Component describeBreeder(ChicagoBreederBlockEntity breeder) {
        if (!breeder.hasRod()) {
            return Component.translatable("message.hbm.chicago.status.breeder_empty");
        }
        ItemStack rodStack = breeder.getRodSnapshot();
        int progress = breeder.getProgress();
        int max = breeder.getMaxProgress();
        double percent = max > 0 ? progress * 100.0D / max : 0.0D;
        int lastFlux = breeder.getLastNeutrons();
        return Component.translatable("message.hbm.chicago.status.breeder",
                rodStack.getHoverName(),
                progress,
                max,
                formatPercent(percent),
                lastFlux);
    }

    private Component describeDetector(ChicagoDetectorBlockEntity detector) {
        if (!detector.hasRod()) {
            return Component.translatable("message.hbm.chicago.status.detector_empty", detector.getThresholdValue());
        }
        ItemStack rodStack = detector.getRodSnapshot();
        return Component.translatable("message.hbm.chicago.status.detector",
                rodStack.getHoverName(),
                detector.getThresholdValue());
    }

    private static String formatNumber(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static String formatPercent(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private void damageDrill(UseOnContext context) {
        if (!damageOnUse) {
            return;
        }
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        if (player.getAbilities().instabuild) {
            return;
        }
        ItemStack stack = context.getItemInHand();
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
    }
}
