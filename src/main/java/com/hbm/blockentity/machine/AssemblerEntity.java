package com.hbm.blockentity.machine;

import com.hbm.api.energy.ItemEnergyProxy;
import com.hbm.api.multiblock.BedLikeData;
import com.hbm.block.base.BedLikeBlock;
import com.hbm.block.machine.BlockAssembler;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.blockentity.base.BedLikeBlockEntity;
import com.hbm.blockentity.base.DummibleBlockEntity;
import com.hbm.capabilities.Capabilities;
import com.hbm.capabilities.energy.BasicEnergyContainer;
import com.hbm.gui.menu.AssemblerMenu;
import com.hbm.lib.DirectionUtils;
import com.hbm.recipe.AssemblerRecipe;
import com.hbm.recipe.BlastFurnaceRecipe;
import com.hbm.recipe.ModRecipeType;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import com.hbm.utils.InventoryUtils;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

public class AssemblerEntity extends BedLikeBlockEntity {
    int progress = 0;               //进度
    int power = 100;                //功率
    int energyCapacity = 100_000;   //最大储能
    private final Tuple<BasicEnergyContainer,LazyOptional<BasicEnergyContainer>> energyCap;
    static final Map<Capability<?>, List<Tuple<Vec3i,Direction>>> posCaps= new HashMap<>();
    private final Map<Capability<?>, List<Tuple<BlockPos,Direction>>> factCaps= new HashMap<>();
    static final List<Tuple<Vec3i,Direction>> itemInout = List.of(new Tuple<>(new Vec3i(1,0,-1),Direction.EAST),new Tuple<>(new Vec3i(-2,0,0),Direction.WEST));
    public final List<Tuple<BlockPos,Direction>> specInout = new ArrayList<>();

    public static final RecipeManager.CachedCheck<CraftingContainer, AssemblerRecipe> quickCheck = RecipeManager.createCheck(ModRecipeType.ASSEMBLER_RECIPE.get());

    static final int[] INPUT_SLOTS = IntStream.range(5,17).toArray();
    static final int[] OUTPUT_SLOTS = new int[]{4};

    protected final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> progress;
                case 1 -> (int) energyCap.getA().getEnergy();
                case 2 -> (int) energyCap.getA().getMaxEnergy();
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
        }

        @Override
        public int getCount() {
            return 3;
        }
    };
    public AssemblerEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.ASSEMBLER_ENTITY.get(), pPos, pBlockState);
        items = NonNullList.withSize(17,ItemStack.EMPTY);
        energyCap = new Tuple<>(new BasicEnergyContainer(100_000),LazyOptional.empty());
        posCaps.put(Capabilities.ENERGY,List.of(new Tuple<>(new Vec3i(-1,0,1),Direction.SOUTH),
                new Tuple<>(new Vec3i(0,0,1),Direction.SOUTH),
                new Tuple<>(new Vec3i(-1,0,-2),Direction.NORTH),
                new Tuple<>(new Vec3i(0,0,-2),Direction.NORTH)));
        HashMap<Vec3i,Direction> itemMap = new HashMap<>();
        itemMap.put(new Vec3i(1,0,0),Direction.EAST);itemMap.put(new Vec3i(-2,0,-1),Direction.WEST);
//        itemCaps.put(ForgeCapabilities.ITEM_HANDLER,itemMap);
        factCaps.put(Capabilities.ENERGY, new ArrayList<>());
        Direction facing = pBlockState.getValue(BlockAssembler.FACING);
        for (Tuple<Vec3i, Direction> tuple : posCaps.get(Capabilities.ENERGY)) {
            BlockPos newPos = pPos.offset(BedLikeBlock.transOffsets(List.of(tuple.getA()), facing).get(0));
            Direction newDir = DirectionUtils.horizRot(Direction.SOUTH, facing, tuple.getB());
            factCaps.get(Capabilities.ENERGY).add(new Tuple<>(newPos,newDir));
        }
        specInout.add(new Tuple<>(pPos.offset(BedLikeBlock.transOffsets(List.of(itemInout.get(0).getA()), facing).get(0)),DirectionUtils.horizRot(Direction.SOUTH, facing, itemInout.get(0).getB())));
        specInout.add(new Tuple<>(pPos.offset(BedLikeBlock.transOffsets(List.of(itemInout.get(1).getA()), facing).get(0)),DirectionUtils.horizRot(Direction.SOUTH, facing, itemInout.get(1).getB())));
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.ENERGY){
            return energyCap.getB().cast();
        }
        return super.getCapability(cap, side);
    }
    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (pBlockEntity instanceof AssemblerEntity entity)entity.running = entity.progress > 0;
        if (!level.isClientSide() && pState.is(ModBlocks.machine_assembler.get()) && pBlockEntity instanceof AssemblerEntity entity){
            if (entity.flagFormed){
                entity.flagFormed = false;
                entity.setDummyCaps();
            }
            absorbBatteryItem(entity);
            transportItem(entity);

        }
    }

    public static void absorbBatteryItem(AssemblerEntity entity){
        ItemStack itemStack = entity.items.get(0);
        if (!itemStack.isEmpty()){
            entity.getCapability(Capabilities.ENERGY).ifPresent(cap->cap.insert(ItemEnergyProxy.disCharge(itemStack)));
        }
    }
    public static void transportItem(AssemblerEntity entity){
        if (entity.hasLevel()){
            InventoryUtils.extractItem(entity.level,entity,entity.specInout.get(0).getA(), Arrays.stream(INPUT_SLOTS).boxed().toList(),entity.specInout.get(0).getB()); //拉取物品
            InventoryUtils.insertItem(entity,entity.specInout.get(1).getA(),Arrays.stream(OUTPUT_SLOTS).boxed().toList(),entity.specInout.get(1).getB());               //输出物品
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("hbm.machine.assembler");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new AssemblerMenu(pContainerId,pInventory,this,containerData);
    }
    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        energyCap.setB(LazyOptional.of(energyCap::getA));
        setDummyCaps();
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCap.getB().invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("progress",progress);
        pTag.put("energy",energyCap.getA().serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        progress = pTag.getInt("progress");
        energyCap.getA().deserializeNBT((CompoundTag) pTag.get("energy"));
    }
    public void setDummyCaps(){
        if (hasLevel() && !level.isClientSide()){
            for (Tuple<BlockPos, Direction> tuple : factCaps.get(Capabilities.ENERGY)) {
                if (level.getBlockEntity(tuple.getA()) instanceof DummibleBlockEntity entity){
                    entity.setCaps(Capabilities.ENERGY, energyCap.getA(),tuple.getB());
                }
            }
        }
    }
}