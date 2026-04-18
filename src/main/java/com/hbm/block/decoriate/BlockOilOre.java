package com.hbm.block.decoriate;

import com.hbm.Inventory.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class BlockOilOre extends BlockOre {
    private final Supplier<BlockState> depletedState;

    public BlockOilOre(Properties properties, Supplier<BlockState> depletedState) {
        super(properties);
        this.depletedState = depletedState;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (!held.is(Items.BUCKET)) {
            return super.use(state, level, pos, player, hand, hit);
        }
        if (!level.isClientSide) {
            ItemStack filled = new ItemStack(ModFluids.OIL.bucket().get());
            if (!player.getAbilities().instabuild) {
                if (held.getCount() == 1) {
                    player.setItemInHand(hand, filled);
                } else {
                    held.shrink(1);
                    if (!player.getInventory().add(filled)) {
                        player.drop(filled, false);
                    }
                }
            } else if (held.getCount() == 1) {
                player.setItemInHand(hand, filled);
            } else if (!player.getInventory().add(filled)) {
                player.drop(filled, false);
            }
            level.setBlock(pos, depletedState.get(), 3);
            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
