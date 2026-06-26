package com.hbm.block.env;

import com.hbm.HBMLang;
import com.hbm.api.block.IDepthRockTool;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockDepth extends Block {
    public BlockDepth(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        // 1. 获取玩家当前手上拿着的物品（ItemStack）
        ItemStack heldItem = player.getMainHandItem(); // 高版本推荐明确获取主手物品

        // 2. 检查物品是否继承了你的自定义接口（假设你的接口也迁移到了高版本）
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof IDepthRockTool depthTool) {

            // 3. 执行你的特殊工具条件判定
            // 注意：因为高版本此处的参数是 BlockGetter（可能用于客户端虚拟世界渲染），
            // 如果你的接口里必须用到真实的 Level，可以进行安全强转
            if (level instanceof Level realLevel) {
                if (depthTool.canBreakRock(realLevel, player, heldItem, this, pos)) {
                    // 🎯 完美平替老版本的 (1D / 50D)
                    // 在 1.20.1 中，返回 0.02F 意味着如果不受特殊状态影响，刚好 50 个 Tick 挖完
                    return 0.02F;
                }
            }
        }

        // 4. 如果没有拿特殊工具，或者不满足条件，打回原版走正常的硬度与工具效率计算
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(HBMLang.BLOCK_DEPTH_DESC.translate());
    }
}
