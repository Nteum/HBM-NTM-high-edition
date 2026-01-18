package com.hbm.item.env;

import com.hbm.HBMKey;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ItemEggGlyphidToBirth extends ItemEggGlyphid{
    public ItemEggGlyphidToBirth(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide){
            CompoundTag tag = pStack.getOrCreateTag();
            if (!tag.contains(HBMKey.COUNTDOWN)) tag.putInt(HBMKey.COUNTDOWN, 100);
            int countdown = tag.getInt(HBMKey.COUNTDOWN);
            countdown --;
            if (countdown <= 0){
                ServerPlayer player = (ServerPlayer) pEntity;
                player.getInventory().removeItem(pStack);
                if (player.isOnFire()) return;
                // 生成一只僵尸
                RandomSource random = pLevel.getRandom();
                // 1. 计算玩家周围的随机位置
                double x = player.getX() + (random.nextDouble() - 0.5) * 10; // ±5 格范围
                double y = player.getY() + 1;
                double z = player.getZ() + (random.nextDouble() - 0.5) * 10;
                BlockPos pos = new BlockPos((int)x, (int)y, (int)z);

                // 2. 检查位置是否安全（非液体/非固体方块）
                if (!isSpawnPositionSafe(pLevel, pos)) return;

                // 3. 创建怪物实体
                Zombie zombie = EntityType.ZOMBIE.create(pLevel);

                zombie.moveTo(x, y, z, random.nextFloat() * 360, 0);

                // 4. 添加到世界
                pLevel.addFreshEntity(zombie);
            }
            tag.putInt(HBMKey.COUNTDOWN, countdown);
        }
    }

    // 检查生成位置是否安全
    private static boolean isSpawnPositionSafe(Level world, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = world.getBlockState(below);

        return !belowState.liquid();
    }
}
