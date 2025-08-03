package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.UpgradeManagerNT;
import com.hbm.Inventory.recipe.AssemblerRecipe;
import com.hbm.Inventory.recipe.ChemplantRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.fluid.BasicFluidTank;
import com.hbm.api.fluid.IExtendedFluidTank;
import com.hbm.block.machine.BlockChemplant;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.DummyableBlockEntity;
import com.hbm.capabilities.Capabilities;
import com.hbm.gui.menu.ChemplantMenu;
import com.hbm.item.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChemplantEntity extends DummyableBlockEntity {
    private static final int maxFluid = 24_000;
    public int maxProgress = 100;
    int progress = 0;
    int consumption = 100;
    int speed = 100;
    public UpgradeManagerNT upgradeManager = new UpgradeManagerNT();
    private List<IExtendedFluidTank> tanks = new ArrayList<>();
    private BasicEnergyContainer energyContainer = new BasicEnergyContainer(100_100);
    private final RecipeManager.CachedCheck<Container, ChemplantRecipe> recipeChecker = RecipeManager.createCheck(ModRecipes.CHEMPLANT.type().get());
    private ChemplantRecipe recipeNow = null;
    protected final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> getProgress();
                case 1 -> getCapability(ForgeCapabilities.ENERGY).orElse(null).getEnergyStored();
                case 2 -> getCapability(ForgeCapabilities.ENERGY).orElse(null).getMaxEnergyStored();
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
    public ChemplantEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.CHEMPLANT_ENTITY.get(), pPos, pBlockState);
        this.items = NonNullList.withSize(21, ItemStack.EMPTY);
        for (int i = 0; i < 4; i++) {
            this.tanks.add(new BasicFluidTank(maxFluid));
        }
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this);
        this.capabilitiesContent.addCapability(Capabilities.LONG_ENERGY, new ProxyEnergyHandler(this.energyContainer));
//        this.capabilitiesCache.addCapabilityResolver(new SidedFluidWrapper(new BaseFluidHandler(4,24_000)));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        // 检查升级
        upgradeManager.checkSlots(this, items, 3, 3);
        int speedLevel = upgradeManager.getLevel(UpgradeType.SPEED);
        int powerLevel = upgradeManager.getLevel(UpgradeType.POWER);
        int overLevel = upgradeManager.getLevel(UpgradeType.OVERDRIVE);
        this.speed = Math.max((100 - speedLevel * 25 + powerLevel * 5) / (overLevel + 1), 1);
        this.consumption = (100 + speedLevel * 300 - powerLevel * 20) * (overLevel + 1);
        // 处理
        if(!canProcess()) {
            this.progress = 0;
        } else {
            this.energyContainer.extract(this.consumption, false);
            this.maxProgress = Math.max(this.recipeNow.getDuration() * this.speed / 100, 1);
            this.progress ++;
            if (this.progress >= this.maxProgress){
                // 处理配方
                this.recipeNow.assemble(this);
                this.progress = 0;
                this.markForSave();
            }
        }
        sendUpdatePacket();
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        Direction facing = this.getBlockState().getValue(BlockChemplant.FACING);
        Direction rot = DirectionUtils.horizRot(Direction.SOUTH, facing, Direction.EAST);
        double x = getBlockPos().getX() + 0.5 + facing.getStepX() * 1.125 + rot.getStepX() * 0.125;
        double y = getBlockPos().getY() + 3;
        double z = getBlockPos().getZ() + 0.5 + facing.getStepZ() * 1.125 + rot.getStepZ() * 0.125;
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.1, 0.0);
    }

    private boolean canProcess() {
        if (this.energyContainer.getEnergy() < this.consumption) return false;
        // 检查配方，同时判断输入槽物品
        ChemplantRecipe recipe = recipeChecker.getRecipeFor(this, level).orElse(null);
        if(recipe == null) return false;
        // 检查输出槽流体

        // 检查输出槽物品

//        if(!hasRequiredFluids(recipe)) return false;
//        if(!hasSpaceForFluids(recipe)) return false;
//        if(!hasRequiredItems(recipe)) return false;
//        if(!hasSpaceForItems(recipe)) return false;
        if (!this.recipeNow.equals(recipe)){
            this.recipeNow = recipe;
            this.progress = 0;
        }
        return true;
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        return super.getReducedUpdateTag();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(HBMKey.RECIPE_NOW)){
            ResourceLocation resourceLocation = new ResourceLocation(nbt.getString(HBMKey.RECIPE_NOW));
            this.recipeNow = (ChemplantRecipe) this.level.getRecipeManager().byKey(resourceLocation).orElse(null);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (recipeNow!=null)
            pTag.putString(HBMKey.RECIPE_NOW,recipeNow.getId().toString());
    }

    @Override
    public List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
        return this.tanks;
    }

    @Override
    public BasicEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    protected int getProgress(){return progress;}

    //===================wroldly container
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
    //===============
    @Override
    public Component getDefaultName() {
        return Component.translatable(HBMLang.CHEMPLANT.getTranslationKey());
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new ChemplantMenu(pContainerId, pInventory, this, containerData);
    }
}
