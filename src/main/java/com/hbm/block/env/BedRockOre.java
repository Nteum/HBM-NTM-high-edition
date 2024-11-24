package com.hbm.block.env;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

/** 基岩矿石 */
public class BedRockOre extends Block {
    public static final EnumProperty<BedRockOreType> TYPE = EnumProperty.create("bedrock_ore_type", BedRockOreType.class);

    public BedRockOre(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(TYPE,BedRockOreType.IRON));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(TYPE);
    }
    //设置方块格式
    public void setType(BedRockOreType type){
        this.defaultBlockState().setValue(TYPE,type);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        pLevel.setBlockAndUpdate(pPos,pState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        //创造模式下右键获取基岩矿石对应的物品。
        if (!pLevel.isClientSide() && pPlayer.isCreative()){
            BedRockOreType value = pState.getValue(TYPE);
            if (pPlayer.hasPose(Pose.CROUCHING)) {
                //1. blockstate.setvalue返回的才是新值
                BlockState state2 = pState.setValue(TYPE, BedRockOreType.DIA);
                //2. 更新blockstate之后需要用level重置方块
                pLevel.setBlock(pPos,state2,2);
            } else {
                if (!pPlayer.addItem(value.resource)) {
                    ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX(), pPos.getY() + 1, pPos.getZ(), new ItemStack(value.resource.getItem()));
                    pLevel.addFreshEntity(itemEntity);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }


    public enum BedRockOreType implements StringRepresentable {
        IRON("iron",Items.RAW_IRON.getDefaultInstance(),FluidStack.EMPTY, 1,1,1),
        COPPER("copper",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1,1,1),
        DIA("diamond",Items.DIAMOND.getDefaultInstance(),FluidStack.EMPTY, 1,1,1);

        public String key;
        public ItemStack resource;
        public FluidStack acidRequirement;
        public int tier;
        public int color;
        public int shape;
        private BedRockOreType(String key, ItemStack resource, FluidStack acidRequirement, int tier, int color, int shape){
            this.key = key;
            this.resource = resource;
            this.acidRequirement = acidRequirement;
            this.color = color;
            this.tier = tier;
            this.shape = shape;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.key;
        }
    }
}
