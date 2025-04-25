package com.hbm.blockentity.machine;

import com.hbm.api.energy.ItemEnergyProxy;
import com.hbm.api.energy.fe.HBMEnergyStorage;
import com.hbm.api.energy.fe.SidedEnergyWrapper;
import com.hbm.block.machine.BlockAssembler;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BedLikeBlockEntity;
import com.hbm.capabilities.Capabilities;
import com.hbm.gui.menu.AssemblerMenu;
import com.hbm.recipe.AssemblerRecipe;
import com.hbm.recipe.ModRecipeType;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

public class AssemblerEntity extends BedLikeBlockEntity {
//    int progress = 0;               //进度
    int power = 100;                //功率
    int energyCapacity = 100_000;   //最大储能
    int countdown = 0;              //工作计时
    AssemblerRecipe recipeNow;      //当前正在使用的配方
    public static final int[] ASSEMBLE_SLOTS = new int[]{5,6,7,8,9,10,11,12,13,14,15,16};
//    private final Tuple<BasicEnergyContainer,LazyOptional<BasicEnergyContainer>> energyCap;
//    static final Map<Capability<?>, List<Tuple<Vec3i,Direction>>> posCaps= new HashMap<>();
//    private final Map<Capability<?>, List<Tuple<BlockPos,Direction>>> factCaps= new HashMap<>();
    //物品输入输出口
//    static final List<Tuple<Vec3i,Direction>> itemInout = List.of(new Tuple<>(new Vec3i(1,0,-1),Direction.EAST),new Tuple<>(new Vec3i(-2,0,0),Direction.WEST));
//    public final List<Tuple<BlockPos,Direction>> specInout = new ArrayList<>();

    public static final RecipeManager.CachedCheck<Container, AssemblerRecipe> quickCheck = RecipeManager.createCheck(ModRecipeType.ASSEMBLER_RECIPE.get());

    static final int[] INPUT_SLOTS = IntStream.range(5,17).toArray();
    static final int[] OUTPUT_SLOTS = new int[]{4};

    protected final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> getProgress();
                case 1 -> getEnergy().getEnergyStored();
                case 2 -> getEnergy().getMaxEnergyStored();
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
        capabilitiesCache.addCapabilityResolver(new SidedEnergyWrapper(new HBMEnergyStorage(100_000)));
//        capabilitiesCache.addCapabilityResolver(new SidedInvWrapper(new InvWrapper(this)));
        multiblockData.put(ForgeCapabilities.ENERGY, -1,0,1,Direction.SOUTH, 0,0,1,Direction.SOUTH, -1,0,-2,Direction.NORTH, 0,0,-2,Direction.NORTH)
                .put(ForgeCapabilities.ITEM_HANDLER, 1,0,-1, Direction.EAST, -2,0,0,Direction.WEST);
        multiblockData.transDirection(pPos,pBlockState.getValue(BlockAssembler.FACING));
//        energyCap = new Tuple<>(new BasicEnergyContainer(100_000),LazyOptional.empty());
//        posCaps.put(Capabilities.ENERGY,List.of(new Tuple<>(new Vec3i(-1,0,1),Direction.SOUTH),
//                new Tuple<>(new Vec3i(0,0,1),Direction.SOUTH),
//                new Tuple<>(new Vec3i(-1,0,-2),Direction.NORTH),
//                new Tuple<>(new Vec3i(0,0,-2),Direction.NORTH)));
//        HashMap<Vec3i,Direction> itemMap = new HashMap<>();
//        itemMap.put(new Vec3i(1,0,0),Direction.EAST);itemMap.put(new Vec3i(-2,0,-1),Direction.WEST);
//        itemCaps.put(ForgeCapabilities.ITEM_HANDLER,itemMap);
//        factCaps.put(Capabilities.ENERGY, new ArrayList<>());
//        Direction facing = pBlockState.getValue(BlockAssembler.FACING);
//        for (Tuple<Vec3i, Direction> tuple : posCaps.get(Capabilities.ENERGY)) {
//            BlockPos newPos = pPos.offset(BedLikeBlock.transOffsets(List.of(tuple.getA()), facing).get(0));
//            Direction newDir = DirectionUtils.horizRot(Direction.SOUTH, facing, tuple.getB());
//            factCaps.get(Capabilities.ENERGY).add(new Tuple<>(newPos,newDir));
//        }
//        specInout.add(new Tuple<>(pPos.offset(BedLikeBlock.transOffsets(List.of(itemInout.get(0).getA()), facing).get(0)),DirectionUtils.horizRot(Direction.SOUTH, facing, itemInout.get(0).getB())));
//        specInout.add(new Tuple<>(pPos.offset(BedLikeBlock.transOffsets(List.of(itemInout.get(1).getA()), facing).get(0)),DirectionUtils.horizRot(Direction.SOUTH, facing, itemInout.get(1).getB())));
    }

//    @Override
//    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
////        if (cap == Capabilities.ENERGY){
////            return energyCap.getB().cast();
////        }
////        return super.getCapability(cap, side);
//        return capabilitiesCache.getCapability(cap,side);
//    }
    public IEnergyStorage getEnergy(){
        return getCapability(ForgeCapabilities.ENERGY,null).orElse(null);
    }
    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
//        if (pBlockEntity instanceof AssemblerEntity entity)entity.running = entity.countdown > 0;
        if (!level.isClientSide() && pState.is(ModBlocks.machine_assembler.get()) && pBlockEntity instanceof AssemblerEntity entity){
            if (entity.flagFormed){
                entity.flagFormed = false;
                entity.setDummyCaps();
            }
            absorbBatteryItem(entity);
            transportItem(entity);  //暂时只能不加判断地传入物品

            boolean flagEmpty = entity.craftSlotEmpty();

            if (entity.running){
                if (entity.recipeNow == null)stopMachine(entity);
                else if (entity.countdown==0){
                    entity.running = false;
                    ItemStack itemStack = entity.recipeNow.assemble(entity, level.registryAccess());
//                    entity.items.set(4,itemStack);
                    processOutput(entity,itemStack);
                }else if (entity.checkRecipe() && entity.getEnergy().extractEnergy(entity.power,true)==entity.power){
                    entity.countdown--;
                    entity.getEnergy().extractEnergy(entity.power,false);
                }else stopMachine(entity);
            }else if (!flagEmpty){
                AssemblerRecipe recipe = AssemblerEntity.quickCheck.getRecipeFor(entity, level).orElse(null);
                if (entity.canProcess(recipe,entity)) startMachine(entity,recipe);
            }
        }
    }
    public static void startMachine(AssemblerEntity entity, AssemblerRecipe recipe){
        entity.countdown = recipe.getProcessingTime();
        entity.recipeNow = recipe;
        entity.running = true;
    }
    public static void stopMachine(AssemblerEntity entity){
        entity.running = false;
        entity.countdown = 0;
    }
    public static void processOutput(AssemblerEntity entity, ItemStack itemStack){
        ItemStack resultStack = entity.items.get(4);
        if (resultStack.isEmpty())entity.items.set(4,itemStack);
        else {
            resultStack.setCount(resultStack.getCount() + itemStack.getCount());
            entity.items.set(4, resultStack);
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
            List<Tuple<BlockPos, Direction>> tuples = entity.multiblockData.afterTrans.get(ForgeCapabilities.ITEM_HANDLER);
            InventoryUtils.extractItem(entity.level,entity,tuples.get(0).getA(), Arrays.stream(INPUT_SLOTS).boxed().toList(),tuples.get(0).getB(),itemStack -> entity.recipeNow.checkItem(itemStack)); //拉取物品
            InventoryUtils.insertItem(entity,tuples.get(1).getA(),Arrays.stream(OUTPUT_SLOTS).boxed().toList(),tuples.get(1).getB());               //输出物品
        }
    }
    private boolean craftSlotEmpty(){
        for (int i = 5; i < 17; i++) {
            if (!this.items.get(i).isEmpty())return false;
        }
        return true;
    }
    private int getProgress(){
        return running && recipeNow!=null ? (int) Math.ceil((1 - (double) countdown / recipeNow.getProcessingTime()) * 83) : 0;
    }
    public boolean checkRecipe(){
        return recipeNow != null && recipeNow.matches(this, level);
    }
    public boolean canProcess(@Nullable AssemblerRecipe recipe, AssemblerEntity entity){
        if (recipe == null)return false;
        ItemStack resultStack = entity.items.get(4);
        ItemStack resultItem = recipe.getResultItem(level.registryAccess());
        return  (resultStack.isEmpty() || resultStack.is(resultItem.getItem()) && resultStack.getCount()+resultItem.getCount()<resultStack.getMaxStackSize())
                && entity.getEnergy().extractEnergy(entity.power,true)==entity.power;
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
//        energyCap.setB(LazyOptional.of(energyCap::getA));
        setDummyCaps();
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
//        energyCap.getB().invalidate();
        capabilitiesCache.invalidateAll();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("countDown",countdown);
        if (recipeNow!=null)
            pTag.putString("recipeNow",recipeNow.getId().toString());
//        pTag.put("energy",energyCap.getA().serializeNBT());
//        pTag.put(CapabilitiesCache.STOREKEY, capabilitiesCache.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        countdown = pTag.getInt("countDown");
        if (pTag.contains("recipeNow")){
            ResourceLocation resourceLocation = new ResourceLocation(pTag.getString("recipeNow"));
            this.recipeNow = (AssemblerRecipe) this.level.getRecipeManager().byKey(resourceLocation).orElse(null);
        }
//        energyCap.getA().deserializeNBT((CompoundTag) pTag.get("energy"));
//        capabilitiesCache.deserializeNBT(pTag);
    }
    public void setDummyCaps(){
        if (hasLevel() && !level.isClientSide()){
//            for (Tuple<BlockPos, Direction> tuple : factCaps.get(Capabilities.ENERGY)) {
//                if (level.getBlockEntity(tuple.getA()) instanceof DummibleBlockEntity entity){
//                    entity.setCaps(Capabilities.ENERGY, energyCap.getA(),tuple.getB());
//                }
//            }
            this.capabilitiesCache.allocDummyBlockCaps(level,this.multiblockData);
        }
    }
}