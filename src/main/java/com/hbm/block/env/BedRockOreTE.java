package com.hbm.block.env;

import com.hbm.HBMKey;
import com.hbm.blockentity.HBMTiles;
import com.hbm.world.feature.BedrockOreDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class BedRockOreTE extends BedRockOre implements EntityBlock {

    public BedRockOreTE(BedrockOreDefinition definition, Properties properties) {
        super(definition, properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileBedrockOre(pPos, pState);
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
            super(HBMTiles.TILE_BEDROCK_ORE.get(), pPos, pBlockState);
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
