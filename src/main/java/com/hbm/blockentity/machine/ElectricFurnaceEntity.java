package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.UpgradeManagerNT;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.HybridEnergyStorage;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.api.math.MathUtils;
import com.hbm.block.machine.BlockElectricFurnace;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.registries.HBMCaps;
import com.hbm.gui.menu.ElectricFurnaceMenu;
import com.hbm.item.machine.ItemMachineUpgrade;
import com.hbm.item.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModTags;
import com.hbm.utils.InventoryUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ElectricFurnaceEntity extends BaseMachineBlockEntity implements MenuProvider, IUpgradeInfoProvider {
    public int progress;
    public static final long maxPower = 100000;
    public int maxProgress = 100;
    public int consumption = 50;
    private int cooldown = 0;
    public UpgradeManagerNT upgradeManager = new UpgradeManagerNT();
    private final BasicEnergyContainer energyContainer = new BasicEnergyContainer(maxPower);
    private final HybridEnergyStorage forgeEnergy = new HybridEnergyStorage(this.energyContainer);
    private final RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> quickCheck;
    protected final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> MathUtils.clampToInt(energyContainer.getEnergy());
                case 3 -> MathUtils.clampToInt(energyContainer.getCapacity());
                default -> 0;
            };
        }
        @Override
        public void set(int pIndex, int pValue) {

        }
        @Override
        public int getCount() {
            return 4;
        }
    };

    public ElectricFurnaceEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.ELECTRIC_FURNACE_ENTITY.get(), pPos, pBlockState);
        this.quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);
        // 0:raw material slot, 1:Battery slot ,原本电池是0号槽，但由于熔炉配方只检查0号槽，所以把0号槽改成输入材料槽了。
        items = NonNullList.withSize(4, ItemStack.EMPTY);
        capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this);
        capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(this.energyContainer));
        capabilitiesContent.addCapability(ForgeCapabilities.ENERGY, this.forgeEnergy);
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        if (pIndex == 1 && pStack.is(ModTags.Items.BATTERY)) return true;
        if (pIndex == 0)return this.quickCheck.getRecipeFor(this,level).isEmpty();
        return false;
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return super.canPlaceItemThroughFace(pIndex, pItemStack, pDirection) && (pIndex == 1 || pIndex == 2);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null)return;
        boolean markDirty = false;

        if(!this.getLevel().isClientSide()) {
            // 检查升级
            upgradeManager.checkSlots(this, items, 3, 3);

            int speedLevel = upgradeManager.getLevel(UpgradeType.SPEED);
            int powerLevel = upgradeManager.getLevel(UpgradeType.POWER);

            this.consumption = 50 + speedLevel * 50 - powerLevel * 15;
            this.maxProgress = 100 - speedLevel * 25 + powerLevel * 10;

            TransmitUtils.dischargeItem(this, items.get(1));
            // 给升级组件添加tooltip
            addUpgradeTooltips(items.get(3));

            if(!hasPower()) {
                cooldown = 20;
            }else if (cooldown > 0)
                cooldown --;

//            if(this.level.getGameTime() % 40 == 0) this.updateConnections();
            boolean isLit = false;
            boolean changeState = false;

            if (energyContainer.getEnergy() >= this.consumption && cooldown == 0 && canProcess()){
                progress ++;

                this.energyContainer.extract(this.consumption, false);

                // 需要处理污染，暂时空着
//                if(worldObj.getTotalWorldTime() % 20 == 0) PollutionHandler.incrementPollution(worldObj, xCoord, yCoord, zCoord, Pollution.Type.SOOT, PollutionHandler.SOOT_PER_SECOND);

                if(this.progress >= maxProgress) {
                    this.progress = 0;
                    this.processItem();
                    markDirty = true;
                    changeState = true;
                }else if (this.progress == 1){
                    changeState = true;
                }
            }else {
                progress = 0;
            }

            this.networkPackNT(50);

            BlockState state = this.level.getBlockState(this.worldPosition);
            if (changeState){
                state = state.setValue(BlockElectricFurnace.LIT, isLit);
                this.level.setBlock(this.worldPosition, state, 3);
            }

            if(markDirty) {
                this.setChanged(this.level, this.worldPosition, state);
            }
        }
    }
    // 确保配方可以被处理
    private boolean canProcess(){
        assert level != null;
        // 获取配方
        Optional<? extends AbstractCookingRecipe> recipeFor = this.quickCheck.getRecipeFor(this, level);
        if (recipeFor.isEmpty())return false;
        AbstractCookingRecipe recipe = recipeFor.get();
        // 验证是否可以处理配方
        ItemStack resultItem = recipe.getResultItem(this.level.registryAccess());
        if (resultItem.isEmpty())return false;
        ItemStack slotItem = this.items.get(2);
        return InventoryUtils.canAddItemEntirely(slotItem, resultItem);
    }

    private void processItem() {
        assert level != null;
        // 获取配方
        Optional<? extends AbstractCookingRecipe> recipeFor = this.quickCheck.getRecipeFor(this, level);
        if (recipeFor.isEmpty())return;
        AbstractCookingRecipe recipe = recipeFor.get();
        // 验证是否可以处理配方
        ItemStack resultItem = recipe.getResultItem(this.level.registryAccess());
        if (resultItem.isEmpty())return;
        ItemStack slotItem = this.items.get(2);
        if (!InventoryUtils.canAddItemEntirely(slotItem, resultItem))return;
        // 修改物品内容
        ItemStack inItem = recipe.getIngredients().get(0).getItems()[0];
        this.items.set(0, InventoryUtils.shrink(inItem.getCount(), this.items.get(0)));
        this.items.set(2, InventoryUtils.growNoCheck(1, slotItem, resultItem));
    }

    private boolean hasPower(){
        return energyContainer.getEnergy() >= this.consumption;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.energyContainer.deserializeNBT(pTag.getCompound(HBMKey.ENERGY));
        this.progress = pTag.getInt(HBMKey.PROGRESS);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put(HBMKey.ENERGY, this.energyContainer.serializeNBT());
        pTag.putInt(HBMKey.PROGRESS, progress);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable(HBMLang.ELECTRIC_FURNACE.key());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new ElectricFurnaceMenu(pContainerId, pInventory, this, this.containerData);
    }
    // 给升级组件添加tooltip
    // 通过Component.Searlizer来转换成json然后记录在itemstack的tag里
    // 有点麻烦，处理json串暂时不知道怎么做。
    public void addUpgradeTooltips(ItemStack stack){
        List<Component> tooltips = new ArrayList<>();
        if (!(stack.getItem() instanceof ItemMachineUpgrade))return;
        ItemMachineUpgrade machineUpgrade = (ItemMachineUpgrade) stack.getItem();
        if (!canProvideInfo(machineUpgrade.type, machineUpgrade.tier, true))return;

        provideInfo(machineUpgrade.type,machineUpgrade.tier, tooltips, true);
//        Component.Serializer.toJson()
//        stack.addTagElement("elementData", );
    }

    @Override
    public boolean canProvideInfo(UpgradeType type, int level, boolean extendedInfo) {
        return type == UpgradeType.SPEED || type == UpgradeType.POWER;
    }

    @Override
    public void provideInfo(UpgradeType type, int level, List<Component> tooltips, boolean extendedInfo) {
        tooltips.add(IUpgradeInfoProvider.getStandardLabel(ModBlocks.FURNACE_ELECTRIC.get()));
        if(type == UpgradeType.SPEED) {
            tooltips.add(Component.translatable(this.KEY_DELAY, "-" + (level * 25) + "%").withStyle(ChatFormatting.GREEN));
            tooltips.add(Component.translatable(this.KEY_CONSUMPTION, "-" + (level * 100) + "%").withStyle(ChatFormatting.RED));
        }else if(type == UpgradeType.POWER) {
            tooltips.add(Component.translatable(this.KEY_DELAY, "-" + (level * 10) + "%").withStyle(ChatFormatting.GREEN));
            tooltips.add(Component.translatable(this.KEY_CONSUMPTION, "-" + (level * 30) + "%").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public HashMap<UpgradeType, Integer> getValidUpgrades() {
        HashMap<UpgradeType, Integer> upgrades = new HashMap<>();
        upgrades.put(UpgradeType.SPEED, 3);
        upgrades.put(UpgradeType.POWER, 3);
        return upgrades;
    }
}
