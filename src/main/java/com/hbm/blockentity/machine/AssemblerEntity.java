package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.HybridEnergyStorage;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.block.base.BlockContainerBase;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.blockentity.interfaces.IPower;
import com.hbm.registries.HBMCaps;
import com.hbm.gui.menu.AssemblerMenu;
import com.hbm.Inventory.recipe.AssemblerRecipe;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModTags;
import com.hbm.utils.InventoryUtils;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

public class AssemblerEntity extends DummyableBlockEntity implements IPower {
//    int progress = 0;               //进度
    int power = 100;                //功率
    int energyCapacity = 100_000;   //最大储能
    int countdown = 0;              //工作计时
    AssemblerRecipe recipeNow;      //当前正在使用的配方
    public ItemStack showItem;      //客户端显示的物品
    public static final int[] ASSEMBLE_SLOTS = new int[]{5,6,7,8,9,10,11,12,13,14,15,16};

    public static final RecipeManager.CachedCheck<Container, AssemblerRecipe> quickCheck = RecipeManager.createCheck(ModRecipes.ASSEMBLER.type().get());

    static final int[] INPUT_SLOTS = IntStream.range(5,17).toArray();
    static final int[] OUTPUT_SLOTS = new int[]{4};
    public final BasicEnergyContainer energyContainer = new BasicEnergyContainer(energyCapacity, energyCapacity, 0);
    private final HybridEnergyStorage forgeEnergy = new HybridEnergyStorage(this.energyContainer);
    private final ItemStackHandler items = new ItemStackHandler(17){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot){
                case 0 -> stack.is(ModTags.Items.BATTERY);
                case 1,2,3 -> stack.is(ModTags.Items.UPGRADE);
                case 4 -> false;
                default -> true;
            };
        }
    };

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
//        items = NonNullList.withSize(17,ItemStack.EMPTY);
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(energyContainer));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ENERGY, this.forgeEnergy);
        this.capabilitiesContent.forceAddCapability(ForgeCapabilities.ITEM_HANDLER, this.items);
        multiblockData = MultiblockData.mapping.get(ModBlocks.machine_assembler.get());
    }
    public IEnergyStorage getEnergy(){
        return this.forgeEnergy;
    }


    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        runDummyCaps(level, getBlockPos(), getBlockState(), this);
        TransmitUtils.dischargeItem(this,this.getItem(0));
        transportItem(this);  //暂时只能不加判断地传入物品
        boolean flagEmpty = this.craftSlotEmpty();
        if (this.running){
            if (this.recipeNow == null)stopMachine(this);
            else if (this.countdown==0){
                this.running = false;
                ItemStack resultItem = this.recipeNow.assemble(this, level.registryAccess());
                processOutput(this,resultItem);
            }else if (this.checkRecipe() && this.consumeEnergy(this.power, true)){
                this.countdown--;
                this.consumeEnergy(this.power, false);
            }else stopMachine(this);
        }else if (!flagEmpty){
            AssemblerRecipe recipe = AssemblerEntity.quickCheck.getRecipeFor(this, level).orElse(null);
            if (this.canProcess(recipe,this)) startMachine(this,recipe);
        }
        sendUpdatePacket();
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(HBMKey.RUNNING, this.running);
        if (this.recipeNow != null)
            tag.putString(HBMKey.RECIPE_NOW, this.recipeNow.getId().toString());
        return super.getReducedUpdateTag().merge(tag);
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        this.running = tag.getBoolean(HBMKey.RUNNING);
        if (tag.contains(HBMKey.RECIPE_NOW)){
            this.level.getRecipeManager().byKey(new ResourceLocation(tag.getString(HBMKey.RECIPE_NOW))).ifPresent(recipe -> {
                this.recipeNow = (AssemblerRecipe) recipe;
                this.showItem = recipeNow.getResultItem(this.level.registryAccess());
            });
        }
    }

    @Override
    public void handleClientPacket(@NotNull CompoundTag tag) {
        super.handleClientPacket(tag);
        if (tag.contains(HBMKey.RECIPE_NOW, Tag.TAG_STRING)){
            this.level.getRecipeManager().byKey(new ResourceLocation(tag.getString(HBMKey.RECIPE_NOW))).ifPresent(recipe -> {
                if (this.recipeNow == null || !this.running){    // 客户端选中配方不影响当前配方
                    this.recipeNow = (AssemblerRecipe) recipe;
                    this.setChanged();
                }
            });
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
        ItemStack resultStack = entity.items.getStackInSlot(4);
        if (resultStack.isEmpty())entity.items.setStackInSlot(4,itemStack);
        else {
            resultStack.setCount(resultStack.getCount() + itemStack.getCount());
            entity.items.setStackInSlot(4, resultStack);
        }
    }

    public static void transportItem(AssemblerEntity entity){
        if (entity.hasLevel()){
            List<Tuple<BlockPos, Direction>> tuples = entity.multiblockData.getCapLocation(ForgeCapabilities.ITEM_HANDLER, entity.worldPosition, entity.getBlockState().getValue(BlockContainerBase.FACING));
            if (entity.recipeNow != null)
                InventoryUtils.extractItem(entity.level,entity,tuples.get(0).getA(), Arrays.stream(INPUT_SLOTS).boxed().toList(),tuples.get(0).getB(),itemStack -> entity.recipeNow.checkItem(itemStack)); //拉取物品
            InventoryUtils.insertItem(entity,tuples.get(1).getA(),Arrays.stream(OUTPUT_SLOTS).boxed().toList(),tuples.get(1).getB());               //输出物品
        }
    }
    // 用于从临近方块吸收电力，现采用电网供电或电池主动供电，因此暂时空缺
    public static void runDummyCaps(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity){
//        AssemblerEntity entity = (AssemblerEntity) pBlockEntity;
//        List<Tuple<BlockPos, Direction>> tuples = entity.multiblockData.getCapLocation(HBMCaps.LONG_ENERGY, entity.worldPosition, entity.getBlockState().getValue(BlockContainerBase.FACING));
//        for (Tuple<BlockPos, Direction> tuple : tuples) {
//            BlockPos dummyPos = tuple.getA();
//            TransmitHelper.machineTransmit(level,dummyPos,level.getBlockState(dummyPos),level.getBlockEntity(dummyPos));
//        }
    }
    private boolean craftSlotEmpty(){
        for (int i = 5; i < 17; i++) {
            if (!this.items.getStackInSlot(i).isEmpty())return false;
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
        ItemStack resultStack = entity.items.getStackInSlot(4);
        ItemStack resultItem = recipe.getResultItem(level.registryAccess());
        int energyToUse = recipe.getProcessingTime() * entity.power;
        return  (resultStack.isEmpty() || resultStack.is(resultItem.getItem()) && resultStack.getCount()+resultItem.getCount()<resultStack.getMaxStackSize())
                && entity.consumeEnergy(energyToUse, true);
    }

    @Override
    public Component getDefaultName() {
        return HBMLang.CONTAINER_ASSEMBLER.translate();
//        return Component.translatable("hbm.machine.assembler");
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
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
    public void invalidateCaps() {
        super.invalidateCaps();
        this.capabilitiesContent.invalidateAll();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("countDown",countdown);
        pTag.put(HBMKey.ENERGY, this.energyContainer.serializeNBT());
        if (recipeNow!=null)
            pTag.putString("recipeNow",recipeNow.getId().toString());
        pTag.put("items", this.items.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        countdown = pTag.getInt("countDown");
        if (pTag.contains(HBMKey.ENERGY)) {
            this.energyContainer.deserializeNBT(pTag.getCompound(HBMKey.ENERGY));
        }
        if (pTag.contains("recipeNow")){
            ResourceLocation resourceLocation = new ResourceLocation(pTag.getString("recipeNow"));
            this.recipeNow = (AssemblerRecipe) this.level.getRecipeManager().byKey(resourceLocation).orElse(null);
        }
        if (pTag.contains("items", Tag.TAG_COMPOUND)) {
            this.items.deserializeNBT((CompoundTag) pTag.get("items"));
//            try {
//                this.items.deserializeNBT((CompoundTag) pTag.get("items"));
//            }catch (Exception e){
//                HBM.LOGGER.warn("Assembler's Items data lost.");
//            }
        }
    }

    private boolean consumeEnergy(long amount, boolean simulate) {
        if (amount <= 0) return true;
        long stored = this.energyContainer.getEnergy();
        if (stored < amount) {
            return false;
        }
        if (!simulate) {
            this.energyContainer.setEnergy(stored - amount);
            this.energyContainer.onContentsChanged();
        }
        return true;
    }

    @Override
    public long getPower() {
        //运作就输入power，否则只需要输入三分之一功率
        return running ? power : power / 3;
    }

    @Override
    public void distributeCapabilities() {
        this.multiblockData.assignCapabilities(this, this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    public AssemblerRecipe getRecipeNow(){
        return this.recipeNow;
    }
    public ItemStackHandler getItemHandler() { return items; }

    @Override
    public ItemStack getItem(int pSlot) {
        return this.items.getStackInSlot(pSlot);
    }
}
