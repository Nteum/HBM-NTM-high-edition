package com.hbm.block.weapon;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.weapon.NukeBombEntity;
import com.hbm.blockentity.weapon.NukeBombFatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class NukeFat extends NukeBomb{
//    public static final VoxelShape FRONT = Block.box(-16,0,-14,0,30,16);
//    public static final VoxelShape MIDDLE = Block.box(0,0,-14,16,30,14);
//    public static final VoxelShape TAIL = Block.box(16,0,-14,32,30,14);
//    public static final VoxelShape SHAPE = Shapes.or(FRONT,MIDDLE,TAIL);
//    public static final VoxelShape SHAPE = Block.box(-14,0,-16,14,28,30);
    public NukeFat(Properties pProperties,int range) {
        super(pProperties,range);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NukeBombFatEntity(pPos,pState);
//        return null;
    }

//    @Override
//    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
//        return pBlockEntityType == ModBlockEntityType.NUKE_BOMB_FAT_ENTITY.get() ? NukeBombEntity::tick : null;
//    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        double d0 = (double)pPos.getX() + 0.5D;
        double d1 = (double)pPos.getY();
        double d2 = (double)pPos.getZ() + 0.5D;
        Direction direction = pState.getValue(FACING);
        Direction.Axis direction$axis = direction.getAxis();
        double d3 = 0.52D;
        double d4 = pRandom.nextDouble() * 0.6D - 0.3D;
        double d5 = direction$axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52D : d4;
        double d6 = pRandom.nextDouble() * 6.0D / 16.0D;
        double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52D : d4;
        pLevel.addParticle(ParticleTypes.DRAGON_BREATH,d0 + d5, d1 + d6, d2 + d7, 0.1D, 0.1D, 0.1D);
    }

//    @Override
//    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
//        return SHAPE;
//    }
    @Override
    public List<Vec3i> getOffsets() {
        return square(new int[]{1, 0, 0, 1, 1, 1});
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    public static class NukeItem extends BlockItem {

        public NukeItem(Block pBlock, Properties pProperties) {
            super(pBlock, pProperties);
        }

        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer) {
//            consumer.accept(new IClientItemExtensions() {
//                @Override
//                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
//                    return HBMxx.SPECIAL_ITEM_RENDER;
//                }
//            });
        }
    }
}
