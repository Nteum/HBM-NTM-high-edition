package com.hbm.item.bigexplosives;

import com.hbm.init.BigExplosivesModEntities;
import com.hbm.render.entity.FiveBombEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Deploys the five-hundred kilogram bomb entity so that it can fall and detonate.
 */
public class FiveHundredKilogramBombItem extends Item {

    private static final double DEFAULT_VERTICAL_OFFSET = 80.0D;

    public FiveHundredKilogramBombItem(final Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            final Vec3 spawnPos = resolveSpawnPosition(level, player, DEFAULT_VERTICAL_OFFSET);
            final FiveBombEntity bomb = BigExplosivesModEntities.FIVE_BOMB.get().create(level);
            if (bomb != null) {
                bomb.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, player.getYRot(), 0.0F);
                bomb.setDeltaMovement(0.0D, -0.5D, 0.0D);
                level.addFreshEntity(bomb);
                level.gameEvent(player, GameEvent.PROJECTILE_SHOOT, BlockPos.containing(spawnPos));
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
        }
        player.swing(hand, true);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private static Vec3 resolveSpawnPosition(final Level level, final Player player, final double verticalOffset) {
        final HitResult hitResult = player.pick(192.0D, 0.0F, false);
        final Vec3 base = switch (hitResult.getType()) {
            case BLOCK, ENTITY -> hitResult.getLocation();
            default -> player.position().add(player.getLookAngle().scale(40.0D));
        };
        final double maxY = level.getMaxBuildHeight() - 2.0D;
        final double minY = level.getMinBuildHeight() + 1.0D;
        final double y = Math.max(minY, Math.min(maxY, base.y + verticalOffset));
        return new Vec3(base.x, y, base.z);
    }
}
