package com.hbm.blockentity.machine;

import com.hbm.HBMLang;
import com.hbm.Inventory.UpgradeManagerNT;
import com.hbm.api.Mode;
import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.core.blockentity.BECapabilities;
import com.hbm.core.blockentity.BEMachineBase;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.capability.energy.EnergyTransferUtils;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.addational_data.Pollution;
import com.hbm.core.contents.fluid.TraitData;
import com.hbm.gui.menu.MenuFurnaceElectric;
import com.hbm.item.machine.ItemMachineUpgrade;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModTabs;
import com.hbm.registries.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileEntityMachineElectricFurnace extends BEMachineBase implements IUpgradeInfoProvider {
    private static final int[] slots_io = new int[] { 0, 1, 2 };
    public static final long maxPower = 100000;

    public int progress;
    public long power;
    public int maxProgress = 100;
    public int consumption = 50;
    private int cooldown = 0;

    SmeltingRecipe recipeNow = null;
    private RecipeManager.CachedCheck<Container, ? extends SmeltingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);
    public static final Map<ItemMachineUpgrade.UpgradeType, Integer> VALID_UPGRADES = Map.of(ItemMachineUpgrade.UpgradeType.SPEED, 3, ItemMachineUpgrade.UpgradeType.POWER, 3);
    private Map<ItemMachineUpgrade.UpgradeType, Integer> upgrades = new HashMap<>();

    @Override
    protected ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex){
                    case 0 -> (int) energyContainer.getEnergy();
                    case 1 -> maxProgress;
                    case 2 -> progress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {}

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    public TileEntityMachineElectricFurnace(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(4);
        this.items.setSlotFilter(0, stack -> stack.is(ModTags.Items.BATTERY));
        this.items.setSlotFilter(3, stack -> stack.is(ModTags.Items.UPGRADE));
        sideAccess.setItemMode(Direction.UP, Mode.INPUT, 1).setItemMode(Direction.DOWN, Mode.OUTPUT, 2);
        addCapability(ForgeCapabilities.ITEM_HANDLER, this::getItemHandler);
        this.energyContainer = new BasicEnergyHandler(maxPower);
        addCapability(HBMCaps.LONG_ENERGY, this::getEnergyHandler);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        boolean markDirty = false;

        if(cooldown > 0) cooldown --;
        EnergyTransferUtils.dischargeItem(this.energyContainer, this.items.getStackInSlot(0));

        this.consumption = 50;
        this.maxProgress = 100;

        IUpgradeInfoProvider.getUpgradeNow(upgrades, this, this.items, 3, 4);
        int speedLevel = upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.SPEED, 0);
        int powerLevel = upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.POWER, 0);

        maxProgress -= speedLevel * 25;
        consumption += speedLevel * 50;
        maxProgress += powerLevel * 10;
        consumption -= powerLevel * 15;

        boolean hasPower = this.energyContainer.getEnergy() >= consumption;
        if (!hasPower) cooldown = 20;
        boolean canProcess = (!this.items.getStackInSlot(1).isEmpty() && cooldown == 0)
                && (recipeNow = quickCheck.getRecipeFor(new RecipeWrapper(new RangedWrapper(this.items, 1, 2)), this.level).orElse(null)) != null
                && this.items.insertItem(2, recipeNow.getResultItem(this.level.registryAccess()), true).isEmpty();
        boolean isRun = this.progress > 0;
        if (hasPower && canProcess){
            progress++;
            this.energyContainer.extract(consumption, false);

            if(this.level.getGameTime() % 20 == 0)
                Pollution.increPollution(this.level, this.worldPosition, Pollution.Type.SOOT, Pollution.SOOT_PER_SECOND);

            if(this.progress >= maxProgress) {
                this.progress = 0;
                this.items.extractItem(1, 1, false);
                this.items.insertItem(2, recipeNow.getResultItem(level.registryAccess()).copy(), false);
                markDirty = true;
            }
        } else {
            progress = 0;
        }

        if(isRun ^ this.progress > 0) {
            markDirty = true;
            this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(BlockStateProperties.LIT, this.progress > 0));
        }

        this.sendUpdatePacket();

        if(markDirty) this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("progress", progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.progress = tag.getInt("progress");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuFurnaceElectric(pContainerId, pInventory, this, getContainerData());
    }

    @Override
    public boolean canProvideInfo(ItemMachineUpgrade.UpgradeType type, int level) {
        return VALID_UPGRADES.containsKey(type);
    }

    @Override
    public void provideInfo(ItemMachineUpgrade.UpgradeType type, int level, List<Component> info) {
        info.add(IUpgradeInfoProvider.getStandardLabel(ModBlocks.MACHINE_ELECTRIC_FURNACE.get()));
        if(type == ItemMachineUpgrade.UpgradeType.SPEED) {
            info.add(HBMLang.UPGRADE_DELAY.translate( "-" + (level * 25) + "%").withStyle(ChatFormatting.GREEN));
            info.add(HBMLang.UPGRADE_CONSUMPTION.translate( "+" + (level * 100) + "%").withStyle(ChatFormatting.RED));
        }
        if(type == ItemMachineUpgrade.UpgradeType.POWER) {
            info.add(HBMLang.UPGRADE_CONSUMPTION.translate(  "-" + (level * 30) + "%").withStyle(ChatFormatting.GREEN));
            info.add(HBMLang.UPGRADE_DELAY.translate( "+" + (level * 10) + "%").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public Map<ItemMachineUpgrade.UpgradeType, Integer> getValidUpgrades() {
        return VALID_UPGRADES;
    }
}
