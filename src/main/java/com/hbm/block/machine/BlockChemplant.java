package com.hbm.block.machine;

import com.hbm.block.base.BedLikeBlock;
import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.base2.TileProxyCombo;
import com.hbm.blockentity.machine.ChemplantEntity;
import com.hbm.gui.menu.ChemplantMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockChemplant extends BlockDummyable {
    public static final VoxelShape SHAPE = Block.box(-32.0,0.0D,-32.0D,32.0D,48.0D,32.0D);
    public BlockChemplant(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
//        return new ChemplantEntity(pPos,pState);
        return pState.getValue(IS_CORE) ? new ChemplantEntity(pPos,pState) : new TileProxyCombo(pPos, pState);
    }

//    @Override
//    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
//        if (!pLevel.isClientSide() && !pPlayer.getPose().equals(Pose.CROUCHING)){
//            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
//            if (blockEntity instanceof MenuProvider){   //如果有界面则打开界面
//                NetworkHooks.openScreen((ServerPlayer) pPlayer, (MenuProvider) blockEntity, buf -> buf.writeBlockPos(pPos));
//            }
//            return InteractionResult.CONSUME;
//        }else {
//            return InteractionResult.SUCCESS;
//        }
//    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(IS_CORE) ? SHAPE : Shapes.block();
    }
}
