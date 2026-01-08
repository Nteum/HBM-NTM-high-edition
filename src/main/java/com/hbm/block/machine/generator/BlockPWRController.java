package com.hbm.block.machine.generator;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.blockentity.machine.PWRBlockEntity;
import com.hbm.blockentity.machine.PWRControllerBlockEntity;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class BlockPWRController extends BlockMachineBase {
    private static final int MAX_SIZE = 4096;

    public BlockPWRController(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PWRControllerBlockEntity(pPos, pState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (pPlayer.getPose().equals(Pose.CROUCHING)) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!(blockEntity instanceof PWRControllerBlockEntity controller)) {
            return InteractionResult.CONSUME;
        }
        if (!controller.isAssembled()) {
            assemble(pLevel, pPos, controller, pPlayer);
            return InteractionResult.CONSUME;
        }
        NetworkHooks.openScreen((ServerPlayer) pPlayer, controller, buf -> buf.writeBlockPos(pPos));
        return InteractionResult.CONSUME;
    }

    private void assemble(Level level, BlockPos pos, PWRControllerBlockEntity controller, Player player) {
        Map<BlockPos, Block> assembly = new HashMap<>();
        Map<BlockPos, Block> rods = new HashMap<>();
        Map<BlockPos, Block> sources = new HashMap<>();
        boolean errored = false;

        assembly.put(pos, this);
        Direction dir = level.getBlockState(pos).getValue(FACING).getOpposite();

        if (!floodFill(level, pos.relative(dir), player, assembly, rods, sources)) {
            errored = true;
        }

        if (rods.isEmpty()) {
            sendError(player, "Fuel rods required");
            errored = true;
        }
        if (sources.isEmpty()) {
            sendError(player, "Neutron sources required");
            errored = true;
        }

        if (!errored) {
            for (Map.Entry<BlockPos, Block> entry : assembly.entrySet()) {
                BlockPos partPos = entry.getKey();
                Block block = entry.getValue();

                if (block == ModBlocks.pwr_controller.get()) {
                    continue;
                }

                boolean port = block == ModBlocks.pwr_port.get();
                BlockState newState = ModBlocks.pwr_block.get().defaultBlockState()
                        .setValue(BlockPWR.PORT, port);
                level.setBlock(partPos, newState, Block.UPDATE_ALL);
                BlockEntity blockEntity = level.getBlockEntity(partPos);
                if (blockEntity instanceof PWRBlockEntity pwr) {
                    pwr.setStoredBlock(block);
                    pwr.setCorePos(pos);
                }
            }
            controller.setup(assembly, rods);
        }

        controller.setAssembled(!errored);
        controller.sendUpdatePacket();
    }

    private boolean floodFill(Level level, BlockPos start, Player player, Map<BlockPos, Block> assembly, Map<BlockPos, Block> rods, Map<BlockPos, Block> sources) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.pollLast();
            if (assembly.containsKey(current)) {
                continue;
            }
            if (assembly.size() >= MAX_SIZE) {
                sendError(player, "Max size exceeded");
                return false;
            }

            Block block = level.getBlockState(current).getBlock();
            if (isValidCasing(block)) {
                assembly.put(current, block);
                continue;
            }
            if (isValidCore(block)) {
                assembly.put(current, block);
                if (block == ModBlocks.pwr_fuel_block.get()) {
                    rods.put(current, block);
                }
                if (block == ModBlocks.pwr_neutron_source.get()) {
                    sources.put(current, block);
                }
                for (Direction dir : Direction.values()) {
                    queue.add(current.relative(dir));
                }
                continue;
            }
            sendError(player, "Non-reactor block");
            return false;
        }
        return true;
    }

    private boolean isValidCore(Block block) {
        return block == ModBlocks.pwr_fuel_block.get()
                || block == ModBlocks.pwr_control.get()
                || block == ModBlocks.pwr_channel.get()
                || block == ModBlocks.pwr_heatex.get()
                || block == ModBlocks.pwr_heatsink.get()
                || block == ModBlocks.pwr_neutron_source.get();
    }

    private boolean isValidCasing(Block block) {
        return block == ModBlocks.pwr_casing.get()
                || block == ModBlocks.pwr_reflector.get()
                || block == ModBlocks.pwr_port.get();
    }

    private void sendError(Player player, String message) {
        player.displayClientMessage(Component.literal(message), true);
    }
}
