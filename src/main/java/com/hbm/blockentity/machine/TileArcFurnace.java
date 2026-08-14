package com.hbm.blockentity.machine;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.CrucibleFluidHandler;
import com.hbm.Inventory.material.HBMMatForm;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.RecipeArcFurnace;
import com.hbm.core.contents.addational_data.Pollution;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.block.machine.MachineArcFurnace;
import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.DefaultMachineBE;
import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.gui.menu.MenuArcFurnace;
import com.hbm.item.machine.ItemMachineUpgrade;
import com.hbm.item.misc.ItemElectrode;
import com.hbm.particle.ParticleSystem;
import com.hbm.registries.*;
import com.hbm.core.contents.multiblock.MultiblockModule;
import com.hbm.utils.sound.AudioWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TileArcFurnace extends DefaultMachineBE implements IUpgradeInfoProvider {
    public static final long maxPower = 2_500_000;
    public static final int CAPACITY = HBMMatForm.BLOCK.quantity * 120;

    public float progress;
    public boolean liquidMode = false;
    public boolean hasMaterial;
    public int delay;

    public float lid;
    @OnlyIn(Dist.CLIENT) public float prevLid;
    @OnlyIn(Dist.CLIENT) public int approachNum;
    @OnlyIn(Dist.CLIENT) public float syncLid;

    protected CrucibleFluidHandler liquids;

    private AudioWrapper audioLid;
    private AudioWrapper audioProgress;

    public static final Map<ItemMachineUpgrade.UpgradeType, Integer> VALID_UPGRADES = Map.of(ItemMachineUpgrade.UpgradeType.SPEED, 3);
    private Map<ItemMachineUpgrade.UpgradeType, Integer> upgrades;
    private RecipeManager.CachedCheck<Container, RecipeArcFurnace> cachedCheck = RecipeManager.createCheck(ModRecipes.ARC_FURNACE.type().get());

    public byte[] electrodes = new byte[3];
    public static final byte ELECTRODE_NONE = 0;
    public static final byte ELECTRODE_FRESH = 1;
    public static final byte ELECTRODE_USED = 2;
    public static final byte ELECTRODE_DEPLETED = 3;

    public static final int CONTAINER_DATA_COUNT = 0;
    private ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return 0;
        }
        @Override
        public void set(int pIndex, int pValue) {}
        @Override
        public int getCount() {
            return CONTAINER_DATA_COUNT;
        }
    };
    public TileArcFurnace(BlockPos pos, BlockState state) {
        super(HBMTiles.getTypeById(MachineArcFurnace.id), pos, state);
    }

    @Override
    protected void initCapabilities() {
        this.items = new ItemStackHandler(30){
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot){
                    case 0,1,2 -> stack.getItem() instanceof ItemElectrode;
                    case 3 -> stack.is(ModTags.Items.CHARGEABLE);
                    case 4 -> stack.is(ModTags.Items.UPGRADE);
                    case 25,26,27,28,29 -> true;
                    default -> false;
                } && super.isItemValid(slot, stack);
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot < 5 || slot >= 25 || lid < 1) return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, new CombinedInvWrapper(new RangedWrapper(items, 0, 3),new RangedWrapper(items, 3,30)));
        this.energyContainer = new BasicEnergyContainer(maxPower){
            @Override
            public void onContentsChanged() {
                setChanged();
            }
        };
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(this.energyContainer));
        this.liquids = new CrucibleFluidHandler(CAPACITY){
            @Override
            public void onContentsChanged() {
                setChanged();
            }
        };
        this.multiblockModule = new MultiblockModule(((MachineArcFurnace) this.getBlockState().getBlock()).getMultiblockData());
    }

    public int getMaxInputSize() {
        int upgrade = upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.SPEED, 0);
        return upgrade == 0 ? 1 : upgrade == 1 ? 4 : upgrade == 2 ? 8 : 16;
    }
    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        // energy
        TransmitUtils.dischargeItem(this, this.items.getStackInSlot(3));
        // upgrades
        IUpgradeInfoProvider.getUpgradeNow(upgrades, this, this.items, 4, 5);
        int upgrade = upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.SPEED, 0);
        // 加载配方
        boolean markDirty = false;
        if(lid > 0) {   // 当盖子关闭的时候，将IO中的物品放在烘烤槽中。
            int IOidx = 25;
            int inputMaxSize = this.getMaxInputSize();
            for (int i = 25; i < 30; i++) {
                ItemStack ioStack = this.items.getStackInSlot(i);
                if (ioStack.isEmpty()) continue;
                RecipeArcFurnace recipe = cachedCheck.getRecipeFor(new RecipeWrapper(new RangedWrapper(this.items, i, i + 1)), this.level).orElse(null);
                if (recipe == null) continue;
                int recipeMax = this.liquidMode ? inputMaxSize : Math.min(inputMaxSize, ioStack.getMaxStackSize() / recipe.outputSolid.getCount());
                for (int j = 5; j < 25; j++) {
                    int expectInputSize = Math.min(ioStack.getCount(), recipeMax);
                    this.items.extractItem(i, ioStack.getCount() - expectInputSize + this.items.insertItem(j, ioStack.copyWithCount(expectInputSize), false).getCount(), false);
                }
            }
        }

        long power = this.energyContainer.getEnergy();
        if(power > 0) {
            boolean ingredients = this.hasIngredients();
            boolean electrodes = this.hasElectrodes();

            int consumption = (int) (1_000 * Math.pow(5, upgrade));

            if(ingredients && electrodes && delay <= 0 && this.liquids.size() > 0) {
                if(lid > 0) {
                    lid -= (float) (1F / (60F / (upgrade * 0.5 + 1)));
                    if(lid < 0) lid = 0;
                    this.progress = 0;
                } else {

                    if(power >= consumption) {
                        int duration = 400 / (upgrade * 2 + 1);
                        this.progress += 1F / duration;
                        this.energyContainer.extract(consumption, false);
                        if(this.progress >= 1F) {
                            this.progress = 0;
                            this.setChanged();
                            this.delay = (int) (120 / (upgrade * 0.5 + 1));
                            // 处理配方
                            for(int i = 5; i < 25; i++) {
                                RecipeArcFurnace recipe = cachedCheck.getRecipeFor(new RecipeWrapper(new RangedWrapper(this.items, i, i + 1)), this.level).orElse(null);
                                if(recipe == null) continue;

                                if(!liquidMode && recipe.outputSolid != null) {
                                    this.items.setStackInSlot(i, recipe.outputSolid.copyWithCount(recipe.outputSolid.getCount() * this.items.getStackInSlot(i).getCount()));
                                }

                                if(liquidMode && recipe.outputFluid != null) {
                                    while(!this.items.getStackInSlot(i).isEmpty()) {
                                        if(this.liquids.getTotalAmount() + Arrays.stream(recipe.outputFluid).map(FluidStack::getAmount).reduce(0, Integer::sum) <= CAPACITY) {
                                            this.items.extractItem(i, 1, false);
                                            for (FluidStack fluidStack : recipe.outputFluid) {
                                                this.liquids.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }

                            for(int i = 0; i < 3; i++) {
                                this.items.getStackInSlot(i).hurt(1,this.level.random, null);
                            }
                            // 污染
                            Pollution.increPollution(this.level, this.worldPosition, Pollution.Type.SOOT, 10);
                        }
                    }
                }
            } else {
                if(this.delay > 0) delay--;
                this.progress = 0;
                if(lid < 1) {
                    lid += (float) (1F / (60F / (upgrade * 0.5 + 1)));
                    if(lid > 1) lid = 1;
                }
            }

            hasMaterial = ingredients;
        }
    }

    public boolean hasIngredients() {
        for(int i = 5; i < 25; i++) {
            RecipeArcFurnace recipe = cachedCheck.getRecipeFor(new RecipeWrapper(new RangedWrapper(this.items, i, i + 1)), this.level).orElse(null);
            if(recipe == null) continue;
            if(liquidMode && recipe.outputFluid != null) return true;
            if(!liquidMode && recipe.outputSolid != null) return true;
        }
        return false;
    }

    public boolean hasElectrodes() {
        for(int i = 0; i < 3; i++) {
            if(this.items.getStackInSlot(i).getItem() instanceof ItemElectrode) return false;
        }
        return true;
    }


    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();

        this.prevLid = this.lid;

        if(this.approachNum > 0) {
            this.lid = this.lid + ((this.syncLid - this.lid) / (float) this.approachNum);
            --this.approachNum;
        } else {
            this.lid = this.syncLid;
        }
        Vec3 center = this.worldPosition.getCenter();

        if(this.lid != this.prevLid) {
            if(this.audioLid == null || !this.audioLid.isPlaying()) {
                this.audioLid = getLoopedSound(ModSounds.DOOR_WGH_START.get(), (float) center.x, (float) center.y, (float) center.z, this.getVolume(0.75F), 15F, 1.0F, 5);
                this.audioLid.startSound();
            }
            this.audioLid.keepAlive();
        } else {
            if(this.audioLid != null) {
                this.audioLid.stopSound();
                this.audioLid = null;
            }
        }

        if((lid == 1 || lid == 0) && lid != prevLid && !(this.prevLid == 0 && this.lid == 1)) {
            this.level.playLocalSound(this.worldPosition, ModSounds.DOOR_WGH_STOP.get(), SoundSource.BLOCKS, this.getVolume(1), 1F, true);
        }

        if(this.progress > 0) {
            if(this.audioProgress == null || !this.audioProgress.isPlaying()) {
                this.audioProgress = getLoopedSound(ModSounds.BLOCK_ELECTRIC_HUM.get(), (float) center.x, (float) center.y, (float) center.z, this.getVolume(1.5F), 15F, 0.75F, 5);
                this.audioProgress.startSound();
            }
            this.audioProgress.updatePitch(0.75F);
            this.audioProgress.keepAlive();
        } else {
            if(this.audioProgress != null) {
                this.audioProgress.stopSound();
                this.audioProgress = null;
            }
        }

        if(this.lid != this.prevLid && this.lid > this.prevLid && !(this.prevLid == 0 && this.lid == 1)
                && Minecraft.getInstance().player.distanceToSqr(this.worldPosition.relative(Direction.UP, 4).getCenter()) < 50 * 50) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "tower");
            data.putFloat("lift", 0.01F);
            data.putFloat("base", 0.5F);
            data.putFloat("max", 2F);
            data.putInt("life", 70 + level.random.nextInt(30));
            data.putDouble("posX", this.worldPosition.getX() + 0.5 + level.random.nextGaussian() * 0.5);
            data.putDouble("posZ", this.worldPosition.getZ() + 0.5 + level.random.nextGaussian() * 0.5);
            data.putDouble("posY", this.worldPosition.getY() + 4);
            data.putBoolean("noWind", true);
            data.putFloat("alphaMod", prevLid / lid);
            data.putInt("color", 0x000000);
            data.putFloat("strafe", 0.05F);
            for(int i = 0; i < 3; i++)
                ParticleSystem.handleParticleCombo(data);
        }

        if(this.lid != this.prevLid && this.lid < this.prevLid && this.lid > 0.5F && this.hasMaterial
                && Minecraft.getInstance().player.distanceToSqr(this.worldPosition.relative(Direction.UP, 4).getCenter()) < 50 * 50) {

            if(level.random.nextInt(5) == 0) {
                CompoundTag flame = new CompoundTag();
                flame.putString("type", "rbmkflame");
                flame.putDouble("posX", this.worldPosition.getX() + 0.5 + level.random.nextGaussian() * 0.5);
                flame.putDouble("posZ", this.worldPosition.getZ() + 0.5 + level.random.nextGaussian() * 0.5);
                flame.putDouble("posY", this.worldPosition.getY() + 2.75);
                flame.putInt("maxAge", 50);
                for(int i = 0; i < 2; i++)
                    ParticleSystem.handleParticleCombo(flame);
            }
        }
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.putFloat(HBMKey.PROGRESS, progress);
        tag.putFloat("lid", lid);
        tag.putBoolean("liquid_mode", liquidMode);
        tag.putBoolean("has_material", hasMaterial);
        tag.put(HBMKey.FLUIDS, this.liquids.serializeNBT());
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        this.progress = tag.getFloat(HBMKey.PROGRESS);
        this.lid = tag.getFloat("lid");
        this.liquidMode = tag.getBoolean("liquid_mode");
        this.hasMaterial = tag.getBoolean("has_material");
        this.liquids.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.progress = nbt.getFloat(HBMKey.PROGRESS);
        this.liquidMode = nbt.getBoolean("liquid_mode");
        this.liquids.deserializeNBT(nbt.getCompound(HBMKey.FLUIDS));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat(HBMKey.PROGRESS, progress);
        tag.putBoolean("liquid_mode", liquidMode);
        tag.put(HBMKey.FLUIDS, this.liquids.serializeNBT());
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuArcFurnace(pContainerId, pInventory, this, this.containerData);
    }

    @Override
    public Component getName() {
        return Component.translatable("block." + HBM.MODID + "." + MachineArcFurnace.id);
    }

    @Override
    public boolean canProvideInfo(ItemMachineUpgrade.UpgradeType type, int level) {
        return VALID_UPGRADES.containsKey(type);
    }

    @Override
    public void provideInfo(ItemMachineUpgrade.UpgradeType type, int level, List<Component> info) {
        info.add(IUpgradeInfoProvider.getStandardLabel(ModBlocks.MACHINE_ARC_FURNACE.get()));
        if(type == ItemMachineUpgrade.UpgradeType.SPEED) {
            info.add(HBMLang.UPGRADE_DELAY.translate("-" + (100 - 100 / (level * 2 + 1)) + "%"));
            info.add(HBMLang.UPGRADE_CONSUMPTION.translate("+" + ((int) Math.pow(5, level) * 100 - 100) + "%"));
        }
    }

    @Override
    public Map<ItemMachineUpgrade.UpgradeType, Integer> getValidUpgrades() {
        return VALID_UPGRADES;
    }
}
