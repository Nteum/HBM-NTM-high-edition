package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.CrucibleFluidHandler;
import com.hbm.Inventory.material.BasicHeatHandler;
import com.hbm.Inventory.recipe.alloy.CrucibleRecipe;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.blockentity.tools.TileFoundryBase;
import com.hbm.datagen.recipe.ingredient.FluidStackIngredient;
import com.hbm.gui.menu.MenuCrucible;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.HBMMatters;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

;

public class CrucibleEntity extends DummyableBlockEntity {
    public int progress = 0;
    public static int MAX_HEAT = 100_000;
    public static int MAX_PROGRESS = 20_000;
    public static double diffusion = 0.25D;
//    public static final int CAPACITY = 20736;
    public static final int CAPACITY = 4096;
    private BasicHeatHandler heatHandler = BasicHeatHandler.of(MAX_HEAT);
    // 两个流体槽，前一个单纯存储物质，后一个可以发生金属混合。
    CrucibleFluidHandler storeStack = new CrucibleFluidHandler(CAPACITY);  // 存储熔融物质的stack
    CrucibleFluidHandler alloyStack = new CrucibleFluidHandler(CAPACITY);  // 存储合金流体的stack
    private CrucibleRecipe recipeNow;                                       // 当前指定的配方
    int isPour = 0;     // 是否正在向外浇筑金属

    private ItemStackHandler items = new ItemStackHandler(9){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return super.isItemValid(slot, stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };
    public ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> heatHandler.getHeat();
                case 1 -> progress;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {}

        @Override
        public int getCount() {
            return 2;
        }
    };

    public CrucibleEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.CRUCIBLE_ENTITY.get(), pPos, pBlockState);
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, items);
        this.capabilitiesContent.addCapability(HBMCaps.HEAT, heatHandler);
        multiblockData = MultiblockData.mapping.get(ModBlocks.machine_crucible.get());
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        this.heatHandler.receiveFromOther(this.level, this.worldPosition.relative(Direction.DOWN));
        this.heatHandler.decay();
        trySmelt();
        tryRecipe();
        tryPourFluid();

        sendUpdatePacket();
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
    }
    protected void trySmelt() {
        int delta = this.heatHandler.getHeat() - this.heatHandler.getMaxHeat() / 2;
        if(delta <= 0) return;
        delta = (int) (delta * 0.05);
        int slot = -1;
        for (int i = 0; i < this.items.getSlots(); i++) {
            ItemStack stackInSlot = this.items.getStackInSlot(i);
            if (!stackInSlot.isEmpty() && HBMMatters.canSmelt(stackInSlot)){
                slot = i;
            }
        }
        if (slot == -1) return;
        this.progress += delta;
        this.heatHandler.extractHeat(delta, false);
        if (this.progress > MAX_PROGRESS){
            FluidStack moltenMatter = HBMMatters.getMoltenMatter(this.items.getStackInSlot(slot));
            if (this.storeStack.getNeeded() < moltenMatter.getAmount()) return;
            this.storeStack.fill(moltenMatter, IFluidHandler.FluidAction.EXECUTE);
            this.items.extractItem(slot, 1, false);
            this.progress = 0;
        }
    }

    protected void tryRecipe(){
        // 1. 安全检查：只有在配方存在且时间到达时进入手动逻辑
        if (this.recipeNow != null && (this.level.getGameTime() + 1) % this.recipeNow.getFrequent() == 0) {
            List<FluidStackIngredient> inputs = this.recipeNow.getInput();
            boolean allAvailable = true;
            // 2. 第一遍循环：纯检查，不移动任何流体
            for (FluidStackIngredient ingredient : inputs) {
                int totalAmount = this.alloyStack.getAmountOf(ingredient) + this.storeStack.getAmountOf(ingredient);
                if (totalAmount < ingredient.getVolume()) {
                    allAvailable = false;
                    break;
                }
            }

            if (allAvailable) {
                // 3. 满足条件，开始消耗
                for (FluidStackIngredient ingredient : inputs) {
                    // 先从 alloyStack 扣，不够再从 storeStack 扣
                    int needed = ingredient.getVolume();
                    needed -= this.alloyStack.consume(ingredient, needed);
                    if (needed > 0) {
                        this.storeStack.consume(ingredient, needed);
                    }
                }

                // 4. 生成产物：直接放入 alloyStack 底部
                for (FluidStack output : recipeNow.getOutput()) {
                    // 建议使用 absorb 直接合并到最底层，如果没有同类则新增一层
                    this.alloyStack.absorb(0, output.copy());
                }
            }
        } else {
            // 自动融合逻辑
            CrucibleRecipe.autoMerge(this.alloyStack, this.level, this.recipeNow);
        }
        // 对流体槽槽进行必要的重整
        this.alloyStack.rebuild();
        this.storeStack.rebuild();
    }

    // 向周边铸造池灌注流体。
    public void tryPourFluid(){
        Direction direction = DirectionUtils.horizRot(Direction.SOUTH, this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING), Direction.EAST);

        boolean flagPour = tryPourFoundryOneSide(this.storeStack, direction);
        this.isPour = flagPour ? this.isPour | 1 : this.isPour & 0xfffffffe;
//        this.isPour |= 1;
        flagPour = tryPourFoundryOneSide(this.alloyStack, direction.getOpposite());
        this.isPour = flagPour ? this.isPour | 2 : this.isPour & 0xfffffffd;
    }

    private boolean tryPourFoundryOneSide(CrucibleFluidHandler fluidContainer, Direction direction){
        boolean flagPour = false;
        BlockPos pos = this.getBlockPos().relative(direction, 2);
        if (!level.getBlockState(pos).canBeReplaced()) return flagPour;
        pos = pos.relative(Direction.DOWN);
        BlockEntity blockEntity = this.level.getBlockEntity(pos);
        if (blockEntity != null && blockEntity instanceof TileFoundryBase foundry){
            FluidStack fluidInTank = fluidContainer.getFluidInTank(0);
            FluidStack copied = fluidInTank.copy();
            if (!copied.isEmpty()) {
                if (copied.getAmount() > 1) copied.setAmount(1);
                FluidStack matConsume = foundry.pour(copied);
                fluidInTank.shrink(matConsume.getAmount());
                // 烫伤处在浇筑口的生物
                if (matConsume.getAmount() > 0){
                    flagPour = true;
                    List<LivingEntity> creatures = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos, pos.relative(Direction.UP)));
                    for (LivingEntity creature : creatures) {
                        creature.setSecondsOnFire(10);
                    }
                }
            }
        }
        return flagPour;
    }

    //GUI上显示的名字
    @Override
    public Component getDefaultName() {
        return HBMLang.CONTAINER_CRUCIBLE.translate();
    }
    //创建对应的菜单类
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuCrucible(pContainerId, pInventory, this, containerData);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(HBMKey.HEAT, Tag.TAG_COMPOUND)) heatHandler.deserializeNBT(nbt.getCompound(HBMKey.HEAT));
        this.progress = nbt.getInt(HBMKey.PROGRESS);
        if (nbt.contains("stack1", Tag.TAG_COMPOUND)) this.storeStack.deserializeNBT(nbt.getCompound("stack1"));
        if (nbt.contains("stack2", Tag.TAG_COMPOUND)) this.alloyStack.deserializeNBT(nbt.getCompound("stack2"));
        if (nbt.contains(HBMKey.ITEM, Tag.TAG_COMPOUND)) this.items.deserializeNBT(nbt.getCompound(HBMKey.ITEM));
        this.isPour = nbt.getInt("pour");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put(HBMKey.HEAT, heatHandler.serializeNBT());
        pTag.putInt(HBMKey.PROGRESS, this.progress);
        pTag.put("stack1", this.storeStack.serializeNBT());
        pTag.put("stack2", this.alloyStack.serializeNBT());
        pTag.put(HBMKey.ITEM, this.items.serializeNBT());
        pTag.putInt("pour", this.isPour);
    }

    public ItemStackHandler getItemHandler(){
        return this.items;
    }

    public CrucibleFluidHandler getStoreStack(){
        return this.storeStack;
    }

    public CrucibleFluidHandler getAlloyStack(){
        return this.alloyStack;
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.put("stack1", this.storeStack.serializeNBT());
        tag.put("stack2", this.alloyStack.serializeNBT());
        tag.putInt("pour", this.isPour);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag nbt) {
        super.handleUpdatePacket(nbt);
        if (nbt.contains("stack1", Tag.TAG_COMPOUND)) this.storeStack.deserializeNBT(nbt.getCompound("stack1"));
        if (nbt.contains("stack2", Tag.TAG_COMPOUND)) this.alloyStack.deserializeNBT(nbt.getCompound("stack2"));
        this.isPour = nbt.getInt("pour");
    }

    @Override
    public void handleClientPacket(@NotNull CompoundTag tag) {
        super.handleClientPacket(tag);
        if (tag.contains(HBMKey.BTN, Tag.TAG_INT)){
            int btnId = tag.getInt(HBMKey.BTN);
            switch (btnId){
                case 0 -> {     // 存储槽头部移动到合金槽头部
                    FluidStack fluidStack = this.storeStack.removeFluid(0);
                    this.alloyStack.absorb(0, fluidStack);
                }
                case 1 -> {     // 合金槽移动到存储槽
                    FluidStack fluidStack = this.alloyStack.removeFluid(0);
                    this.storeStack.absorb(0, fluidStack);
                }
            }
        }
        if (tag.contains(HBMKey.RECIPE_NOW, Tag.TAG_STRING)){
            this.recipeNow = CrucibleRecipe.getRecipe(new ResourceLocation(tag.getString(HBMKey.RECIPE_NOW)));
        }
        if (tag.contains(HBMKey.CLICK, Tag.TAG_INT)){
            int slotId = tag.getInt(HBMKey.CLICK);
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(tag.getCompound(HBMKey.FLUIDS));
            if (slotId == 0){
                this.storeStack.moveToBottom(fluidStack);
            }else if (slotId == 1){
                this.alloyStack.moveToBottom(fluidStack);
            }
        }
    }

    public boolean isStoreStackPouring(){
        return (this.isPour & 1) != 0;
    }

    public boolean isAlloyStackPouring(){
        return (this.isPour & 2) != 0;
    }
}
