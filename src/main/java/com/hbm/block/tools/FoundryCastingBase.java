package com.hbm.block.tools;

import com.hbm.block.base.BlockBase;
import com.hbm.block.base.BlockDummyable;
import com.hbm.block.interfaces.ICrucibleAcceptor;
import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.blockentity.base.UpdateableBlockEntity;
import com.hbm.blockentity.tools.TileFoundryBase;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * 铸造盆的基类
 * */
public abstract class FoundryCastingBase extends BlockBase implements EntityBlock, ICrucibleAcceptor, ILookOverlay {
    static VoxelShape SHAPE;
    public FoundryCastingBase(Properties properties){
        super(properties.noOcclusion().isViewBlocking(BlockDummyable::never).dynamicShape());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            TileFoundryBase foundry = WorldUtils.getTileEntity(TileFoundryBase.class, pLevel, pPos);
            if (foundry != null){
                Containers.dropContents(pLevel, pPos, new RecipeWrapper(foundry.getItems()));
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? UpdateableBlockEntity::clientTicker : UpdateableBlockEntity::serverTicker;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && !pPlayer.hasPose(Pose.CROUCHING) && pHand == InteractionHand.MAIN_HAND && pHit.getType() == HitResult.Type.BLOCK){
            ItemStack itemInHand = pPlayer.getItemInHand(pHand);
            TileFoundryBase tileEntity = WorldUtils.getTileEntity(TileFoundryBase.class, pLevel, pPos);
            if (tileEntity != null){
                tileEntity.leftClick(pPlayer, pHand);
                // 我试过了，传回PASS和CONSUME都无法阻止副手的调用，只能直接过滤副手了。
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
}
