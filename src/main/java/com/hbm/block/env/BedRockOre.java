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

import java.security.interfaces.ECKey;
import java.util.Arrays;
import java.util.List;

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
                BlockState state2 = pState.setValue(TYPE, BedRockOreType.COPPER);
                //2. 更新blockstate之后需要用level重置方块
                pLevel.setBlock(pPos,state2,2);
            } else {
                if (!pPlayer.addItem(value.main_product)) {
                    ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX(), pPos.getY() + 1, pPos.getZ(), new ItemStack(value.main_product.getItem()));
                    pLevel.addFreshEntity(itemEntity);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }


    public enum BedRockOreType implements StringRepresentable {
        IRON("iron",Items.RAW_IRON.getDefaultInstance(),FluidStack.EMPTY, 1),
        COPPER("copper",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        BORAX("borax",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        ASBESTOS("asbestos",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        NIOBIUM("niobium",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        TITANIUM("titanium",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        TUNGSTEN("tungsten",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        GOLD("gold",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        URANIUM("uranium",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        THORIUM("thorium",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        CHLOROCALCITE("chlorocalcite",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        FLUORITE("fluorite",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        HEMATITE("hematite",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        MALACHITE("malachite",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        NEODYMIUM("neodymium",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        COAL("coal",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        NITER("niter",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        REDSTONE("redstone",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        EMERALD("emerald",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        RARE("rare",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        GLOW_STONE("glow_stone",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        POWER_FIRE("power_fire",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
//        QUARTZ("quartz",Items.RAW_COPPER.getDefaultInstance(),FluidStack.EMPTY, 1),
        ;


        public String key;
        public ItemStack main_product;
        public List<ItemStack> by_product;
        public List<Integer> probilities;
        public FluidStack acid;
        public int tier;
        public int color;
        private static final int DEFAULT_COLOR = 0x8F9999;
        private BedRockOreType(String key, ItemStack main_product, Integer tier){
            this(key, main_product,null, null, tier, DEFAULT_COLOR);
        }
        private BedRockOreType(String key, ItemStack main_product, FluidStack acid, Integer tier){
            this(key, main_product,null, acid, tier, DEFAULT_COLOR);
        }
        private BedRockOreType(String key, ItemStack main_product, List<ItemStack> by_product,  FluidStack acid, int tier, int color){
            this.key = key;
            this.main_product = main_product;
            this.by_product = by_product;
            this.acid = acid;
            this.color = color;
            this.tier = tier;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.key;
        }
    }
}
