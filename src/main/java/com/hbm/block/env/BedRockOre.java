package com.hbm.block.env;

import com.hbm.block.HBMBlockProperties;
import com.hbm.world.feature.BedrockOreDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.time.Year;

/** 基岩矿石 */
public class BedRockOre extends Block{
    public static final IntegerProperty VARIANT = HBMBlockProperties.BEDROCK_ORE_VARIANT;
    public final BedrockOreDefinition definition;

    public BedRockOre(BedrockOreDefinition definition, BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(VARIANT, 1));
        this.definition = definition;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(VARIANT);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        int variant = pContext.getLevel().random.nextInt(1, 10);
        return super.getStateForPlacement(pContext).setValue(VARIANT, variant);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    // 测试修改颜色
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        // 测试
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND && pState.is(this)){
            ItemStack itemStack = this.definition.getItemStack();
            Vec3 center = pPos.above().getCenter();
            pLevel.addFreshEntity(new ItemEntity(pLevel, center.x, center.y, center.z, itemStack));
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
}
