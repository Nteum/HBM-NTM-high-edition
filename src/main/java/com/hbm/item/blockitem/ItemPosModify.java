package com.hbm.item.blockitem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ItemPosModify extends BlockItem {
    private final Vec3i placeOffset;
    public ItemPosModify(Block pBlock, Vec3i placeOffset, Properties pProperties) {
        super(pBlock, pProperties);
        this.placeOffset = placeOffset;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        // 1. 构造原本的放置上下文（以便拿到原本游戏计算出的点击格子）
        BlockPlaceContext originalPlaceContext = new BlockPlaceContext(context);
        BlockPos originalPos = originalPlaceContext.getClickedPos();

        // 2. 将目标位置强行向上平移 1 个格子
        BlockPos targetPos = originalPos.offset(placeOffset);

        // 3. 安全检查：确保上方那个格子是空气或可替换的方块（如草丛）
        BlockState targetState = level.getBlockState(targetPos);
        if (!targetState.canBeReplaced(originalPlaceContext)) {
            return InteractionResult.FAIL; // 如果上方有阻挡，放置失败
        }

        // 4. 【核心偷梁换柱】：利用 at 方法，传入原本的 context 和我们新算出来的 targetPos
        // 这会生成一个坐标已经被修改到上方的全新 BlockPlaceContext
        BlockPlaceContext shiftedPlaceContext = BlockPlaceContext.at(originalPlaceContext, targetPos, context.getClickedFace());

        // 5. 绕过普通的 useOn，直接调用 BlockItem 内部真正的物理放置管线
        if (!shiftedPlaceContext.canPlace()) {
            return InteractionResult.FAIL;
        }

        // 获取方块的默认放置状态（能顺便自动计算机器的面朝向）
        BlockState placementState = this.getPlacementState(shiftedPlaceContext);
        if (placementState == null) {
            return InteractionResult.FAIL;
        }

        // 真正把方块拍进世界里（内部包含 setBlock, 扣物品, 播放音效）
        if (!this.place(shiftedPlaceContext).consumesAction()) {
            return InteractionResult.FAIL;
        }

        // 播放放置音效并让手臂挥动（返回客户端成功状态）
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
