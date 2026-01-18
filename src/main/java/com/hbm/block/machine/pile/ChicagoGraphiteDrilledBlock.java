package com.hbm.block.machine.pile;

import com.hbm.blockentity.machine.pile.ChicagoBreederBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoDetectorBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoFuelBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoSourceBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoFuelBlockEntity.FuelVariant;
import com.hbm.blockentity.machine.pile.ChicagoSourceBlockEntity.SourceType;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Consumer;

public class ChicagoGraphiteDrilledBlock extends ChicagoInsertableBlock {

    public ChicagoGraphiteDrilledBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult shield = handleShieldUse(state, level, pos, player, hand);
        if (shield.consumesAction()) {
            return shield;
        }

        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (held.is(ModItems.PILE_ROD_URANIUM.get())) {
            return placeFuel(state, level, pos, player, held, FuelVariant.URANIUM);
        }
        if (held.is(ModItems.PILE_ROD_PU239.get())) {
            return placeFuel(state, level, pos, player, held, FuelVariant.PU239);
        }
        if (held.is(ModItems.PILE_ROD_SOURCE.get())) {
            return placeSource(state, level, pos, player, held, SourceType.SOURCE);
        }
        if (held.is(ModItems.PILE_ROD_PLUTONIUM.get())) {
            return placeSource(state, level, pos, player, held, SourceType.PLUTONIUM);
        }
        if (held.is(ModItems.PILE_ROD_BORON.get())) {
            return placeSimple(state, level, pos, player, held, ModBlocks.chicago_graphite_rod.get().defaultBlockState(), null);
        }
        if (held.is(ModItems.PILE_ROD_LITHIUM.get())) {
            return placeBreeder(state, level, pos, player, held);
        }
        if (held.is(ModItems.PILE_ROD_DETECTOR.get())) {
            return placeDetector(state, level, pos, player, held);
        }

        return InteractionResult.PASS;
    }

    private InteractionResult placeFuel(BlockState state, Level level, BlockPos pos, Player player, ItemStack held, FuelVariant variant) {
        BlockState target = copyAxisShield(state, ModBlocks.chicago_graphite_fuel.get().defaultBlockState());
        return placeSimple(state, level, pos, player, held, target, be -> {
            if (be instanceof ChicagoFuelBlockEntity fuel) {
                fuel.loadFromItem(variant, held);
            }
        });
    }

    private InteractionResult placeSource(BlockState state, Level level, BlockPos pos, Player player, ItemStack held, SourceType type) {
        BlockState target = copyAxisShield(state, ModBlocks.chicago_graphite_source.get().defaultBlockState());
        return placeSimple(state, level, pos, player, held, target, be -> {
            if (be instanceof ChicagoSourceBlockEntity source) {
                source.setType(type);
            }
        });
    }

    private InteractionResult placeBreeder(BlockState state, Level level, BlockPos pos, Player player, ItemStack held) {
        BlockState target = copyAxisShield(state, ModBlocks.chicago_graphite_breeder.get().defaultBlockState());
        return placeSimple(state, level, pos, player, held, target, be -> {
            if (be instanceof ChicagoBreederBlockEntity breeder) {
                breeder.setBaseItem(held);
            }
        });
    }

    private InteractionResult placeDetector(BlockState state, Level level, BlockPos pos, Player player, ItemStack held) {
        BlockState target = copyAxisShield(state, ModBlocks.chicago_graphite_detector.get().defaultBlockState());
        return placeSimple(state, level, pos, player, held, target, be -> {
            if (be instanceof ChicagoDetectorBlockEntity detector) {
                detector.insertRod(held);
            }
        });
    }

    private InteractionResult placeSimple(BlockState previousState, Level level, BlockPos pos, Player player, ItemStack held,
                                          BlockState targetState, Consumer<net.minecraft.world.level.block.entity.BlockEntity> initializer) {
        if (level.isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }
        level.setBlock(pos, targetState, 3);
        if (initializer != null) {
            net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                initializer.accept(be);
            }
        }
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
