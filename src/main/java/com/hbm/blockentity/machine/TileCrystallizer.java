package com.hbm.blockentity.machine;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.fluid_handler.ContainerWithFluid;
import com.hbm.Inventory.fluid_handler.FluidHelper;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.RecipeCrystallizer;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.api.fluid.FluidUtils;
import com.hbm.api.fluid.SingleFluidHandler;
import com.hbm.block.machine.MachineCrystallizer;
import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.DefaultMachineBE;
import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.gui.menu.MenuCrystallizer;
import com.hbm.item.machine.ItemMachineUpgrade;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModTags;
import com.hbm.utils.math.BobMth;
import com.hbm.core.contents.multiblock.MultiblockModule;
import com.hbm.utils.sound.AudioWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class TileCrystallizer extends DefaultMachineBE implements IUpgradeInfoProvider {
    public static final int maxPower = 1000000;
    public static final int maxFluid = 8000;
    public static final int demand = 1000;
    public short progress;
    public short duration = 600;

    private Map<ItemMachineUpgrade.UpgradeType, Integer> upgrades;

    public float angle;
    public float prevAngle;
    @OnlyIn(Dist.CLIENT) private AudioWrapper audio;

    private RecipeManager.CachedCheck<Container, RecipeCrystallizer> cachedCheck = RecipeManager.createCheck(ModRecipes.CRYSTALLIZER.type().get());
    RecipeCrystallizer recipeNow = null;

    public static int CONTAINER_DATA_SIZE = 3;
    private ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> progress;
                case 1 -> (int) energyContainer.getEnergy();
                case 2 -> duration;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {}

        @Override
        public int getCount() {
            return CONTAINER_DATA_SIZE;
        }
    };
    public TileCrystallizer(BlockPos pos, BlockState state) {
        super(HBMTiles.getTypeById(MachineCrystallizer.id), pos, state);
    }

    @Override
    protected void initCapabilities() {
        this.items = new ItemStackHandler(8){
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot){
                    case 1 -> stack.is(ModTags.Items.CHARGEABLE);
                    case 3 -> FluidHelper.isDrainable(stack);
                    case 5,6 -> stack.is(ModTags.Items.UPGRADE);
                    default -> true;
                } && super.isItemValid(slot, stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, new RangedWrapper(this.items, 0, 8){
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot){
                    case 0,1,3,5,6 -> true;
                    default -> false;
                } && super.isItemValid(slot, stack);
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot != 2 || slot != 4) return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        });
        this.energyContainer = new BasicEnergyContainer(maxPower){
            @Override
            public void onContentsChanged() {
                setChanged();
            }
        };
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(this.energyContainer));
        this.fluidHandler = new SingleFluidHandler(maxFluid){
            @Override
            public void onContentsChanged() {
                setChanged();
            }
        };
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluidHandler);
        this.multiblockModule = new MultiblockModule(((MachineCrystallizer) this.getBlockState().getBlock()).getMultiblockData());
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        // 能量沟通
        TransmitUtils.dischargeItem(this, this.items.getStackInSlot(1));
        // 从流体桶抽取流体，需要大改
        if (this.items.insertItem(4, Items.BUCKET.getDefaultInstance(), true).isEmpty())
            this.items.insertItem(4, FluidUtils.absorbFromItem(this.fluidHandler, items.getStackInSlot(3)), false);
        // 检查升级
        upgrades = IUpgradeInfoProvider.getUpgradeNow(upgrades, this, this.items, 5, 7);
        int speed = upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.SPEED, 0);
        int effect = upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.EFFECT, 0);
        int consumption = demand + speed * demand + effect * demand * 2;

        int oldProgress = this.progress;
        for(int i = 0; i < getCycleCount(); i++) {
            // 判断是否能处理
            recipeNow = cachedCheck.getRecipeFor(new ContainerWithFluid(this.items, this.fluidHandler), this.level).orElse(null);
            int base = recipeNow != null ? recipeNow.duration : 600;
            short duration = speed > 0 ? (short) Math.ceil((base * Math.max(1F - 0.25F * speed, 0.25F))) : (short) base;
            boolean canProcess = this.recipeNow != null
                    && this.energyContainer.extract(consumption, true) == consumption
                    && this.fluidHandler.drain(recipeNow.fluid.getVolume(), IFluidHandler.FluidAction.SIMULATE).getAmount() == recipeNow.fluid.getVolume()
                    && this.items.insertItem(2, recipeNow.output, true).isEmpty();
            if(canProcess) {
                progress++;
                this.energyContainer.extract(consumption, false);
                if(progress > duration) {
                    // 处理配方
                    this.items.insertItem(2, recipeNow.assemble(new RecipeWrapper(this.items), this.level), false);
                    float freeChance = effect > 0 ? Math.min(effect * recipeNow.productivity, 0.99F) : 0;   // 效率升级有概率免除材料消耗
                    if (this.level.random.nextFloat() > freeChance)
                        this.items.extractItem(0, recipeNow.input.value.count, false);
                    this.fluidHandler.drain(recipeNow.fluid.getVolume(), IFluidHandler.FluidAction.EXECUTE);
                    // 收尾工作
                    progress = 0;
                    recipeNow = null;
                    this.setChanged();
                }

            } else {
                progress = 0;
                recipeNow = null;
            }
        }
        if (oldProgress != progress)
            this.setChanged();
        this.sendUpdatePacket();
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        prevAngle = angle;

        if(progress > 0) {
            angle += 5F * this.getCycleCount();

            if(angle >= 360) {
                angle -= 360;
                prevAngle -= 360;
            }

            RandomSource rand = this.level.random;
            if(rand.nextInt(20) == 0 && Minecraft.getInstance().player.distanceToSqr(this.worldPosition.relative(Direction.UP, 6).getCenter()) < 50 * 50) {
                level.addParticle(ParticleTypes.CLOUD, this.worldPosition.getX() + rand.nextDouble(), this.worldPosition.getY() + 6.5, this.worldPosition.getZ() + rand.nextDouble(), 0, 0.1, 0);
            }

            if(Minecraft.getInstance().player.distanceToSqr(this.worldPosition.getCenter()) < 25 * 25) {
                if(audio == null) {
                    audio = createAudioLoop();
                    audio.startSound();
                } else if(!audio.isPlaying()) {
                    audio = rebootAudio(audio);
                }
                audio.keepAlive();
                audio.updateVolume(this.getVolume(1F));
                audio.updatePitch(0.75F);

            } else {
                if(audio != null) {
                    audio.stopSound();
                    audio = null;
                }
            }
        } else {
            if(audio != null) {
                audio.stopSound();
                audio = null;
            }
        }
    }

    public int getRequiredAcid(int base) {
        return base;
    }

    public float getCycleCount() {
        int speed = upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.OVERDRIVE, 0);
        return Math.min(1 + speed * 2, 7);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if(audio != null) { audio.stopSound(); audio = null; }
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.put(HBMKey.FLUIDS,  this.fluidHandler.serializeNBT());
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        if (tag.contains(HBMKey.FLUIDS, Tag.TAG_COMPOUND))
            this.fluidHandler.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuCrystallizer(pContainerId, pInventory, this, containerData);
    }

    @Override
    public Component getName() {
        return Component.translatable("block." + HBM.MODID + "." + MachineCrystallizer.id);
    }

    public static final Map<ItemMachineUpgrade.UpgradeType, Integer> VALID_UPGRADES = Map.of(ItemMachineUpgrade.UpgradeType.SPEED, 3, ItemMachineUpgrade.UpgradeType.EFFECT, 3, ItemMachineUpgrade.UpgradeType.OVERDRIVE, 3);

    @Override
    public boolean canProvideInfo(ItemMachineUpgrade.UpgradeType type, int level) {
        return VALID_UPGRADES.containsKey(type);
    }

    @Override
    public void provideInfo(ItemMachineUpgrade.UpgradeType type, int level, List<Component> info) {
        info.add(IUpgradeInfoProvider.getStandardLabel(ModBlocks.MACHINE_CRYSTALLIZER.get()));
        if(type == ItemMachineUpgrade.UpgradeType.SPEED) {
            info.add(HBMLang.UPGRADE_DELAY.translate("-" + (level * 25) + "%").withStyle(ChatFormatting.GREEN));
            info.add(HBMLang.UPGRADE_CONSUMPTION.translate("+" + (level * 100) + "%").withStyle(ChatFormatting.RED));
        }
        if(type == ItemMachineUpgrade.UpgradeType.EFFECT) {
            info.add(HBMLang.UPGRADE_EFFICIENCY.translate("x" + level).withStyle(ChatFormatting.GREEN));
            info.add(HBMLang.UPGRADE_CONSUMPTION.translate("+" + (level * 200) + "%").withStyle(ChatFormatting.RED));
        }
        if(type == ItemMachineUpgrade.UpgradeType.OVERDRIVE) {
            info.add(Component.literal("YES").withStyle(BobMth.getBlink() ? ChatFormatting.RED : ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public Map<ItemMachineUpgrade.UpgradeType, Integer> getValidUpgrades() {
        return VALID_UPGRADES;
    }
}
