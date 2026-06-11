package com.hbm.block.env;

import com.hbm.HBMKey;
import com.hbm.block.HBMBlockProperties;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.interfaces.ECKey;
import java.util.Arrays;
import java.util.List;

/** 基岩矿石 */
public class BedRockOre extends Block implements EntityBlock {
    public static final IntegerProperty VARIANT = HBMBlockProperties.BEDROCK_ORE_VARIANT;
    public BedRockOre(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(VARIANT, 1));
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new TileBedrockOre(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    // 测试修改颜色
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
//        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND){
//            TileBedrockOre tileEntity = WorldUtils.getTileEntity(TileBedrockOre.class, pLevel, pPos);
//            if (tileEntity != null){
//                tileEntity.setColor(0xFF00FF00);
//            }
//            return InteractionResult.CONSUME;
//        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    /**
     * 方块实体，主要用于记录信息
     * */
    public static class TileBedrockOre extends BlockEntity {

        public ItemStack resource;
        public FluidStack acidRequirement;
        public int tier;
        public int color;
        public int shape;

        public TileBedrockOre(BlockPos pPos, BlockState pBlockState) {
            super(ModBlockEntityType.TILE_BEDROCK_ORE.get(), pPos, pBlockState);
            this.tier = 1;
            this.color = 0xFFFFFFFF;
            this.shape = 3;
        }

        public TileBedrockOre setStyle(int color, int shape) {
            this.color = color;
            this.shape = shape;
            return this;
        }
        public int getColor(){
            return this.color;
        }
        public void setColor(int color) {
            this.color = color;

            setChanged();
            // 设置颜色需要更新到客户端
            if(level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }

        @Override
        protected void saveAdditional(CompoundTag pTag) {
            super.saveAdditional(pTag);
            if (resource != null) pTag.put(HBMKey.ITEM, this.resource.serializeNBT());
            if (acidRequirement != null) pTag.put(HBMKey.FLUIDS, acidRequirement.writeToNBT(new CompoundTag()));
            pTag.putInt(HBMKey.TIER, this.tier);
            pTag.putInt(HBMKey.COLOR, this.color);
            pTag.putInt(HBMKey.SHAPE, this.shape);
        }

        @Override
        public void load(CompoundTag pTag) {
            super.load(pTag);
            if (pTag.contains(HBMKey.ITEM, Tag.TAG_COMPOUND)) this.resource = ItemStack.of(pTag.getCompound(HBMKey.ITEM));
            if (pTag.contains(HBMKey.FLUIDS, Tag.TAG_COMPOUND)) this.acidRequirement = FluidStack.loadFluidStackFromNBT(pTag.getCompound(HBMKey.FLUIDS));
            this.tier = pTag.getInt(HBMKey.TIER);
            this.color = pTag.getInt(HBMKey.COLOR);
            this.shape = pTag.getInt(HBMKey.SHAPE);
        }
    }
}
