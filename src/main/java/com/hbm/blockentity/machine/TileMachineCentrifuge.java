package com.hbm.blockentity.machine;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.RecipeCentrifuge;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.block.machine.MachineCentrifuge;
import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.DefaultMachineBE;
import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.gui.menu.MenuCentrifuge;
import com.hbm.item.machine.ItemMachineUpgrade;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModSounds;
import com.hbm.registries.ModTags;
import com.hbm.utils.math.BobMth;
import com.hbm.core.contents.multiblock.MultiblockModule;
import com.hbm.core.client.sounds.AudioWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class TileMachineCentrifuge extends DefaultMachineBE implements IUpgradeInfoProvider{
    public int progress;
    private Map<ItemMachineUpgrade.UpgradeType, Integer> upgrades;

    //configurable values
    public static int maxPower = 100000;
    public static int processingSpeed = 200;
    public static int baseConsumption = 200;
    public static final int CONTAINER_DATA_COUNT = 2;
    private ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> (int) energyContainer.getEnergy();
                case 1 -> progress;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {}

        @Override
        public int getCount() {
            return CONTAINER_DATA_COUNT;
        }
    };
    public TileMachineCentrifuge(BlockPos pos, BlockState state) {
        super(HBMTiles.getTypeById(MachineCentrifuge.id), pos, state);
    }

    @Override
    protected void initCapabilities() {
        // 过滤自身的物品
        this.items = new ItemStackHandler(8){
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot){
                    case 1 -> stack.is(ModTags.Items.CHARGEABLE);
                    case 6,7 -> stack.is(ModTags.Items.UPGRADE);
                    default -> true;
                } && super.isItemValid(slot, stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setChanged();
            }
        };
        // 过滤和外界交互的物品限制
        RangedWrapper rangedWrapper = new RangedWrapper(this.items, 0, 8) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot){
                    case 1,2 -> true;
                    default -> false;
                } && super.isItemValid(slot, stack);
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot < 2 || slot > 5) return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        };
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, rangedWrapper);
        this.energyContainer = new BasicEnergyContainer(maxPower){
            @Override
            public void onContentsChanged() {
                super.onContentsChanged();
                setChanged();
            }
        };
        this.multiblockModule = new MultiblockModule(((MachineCentrifuge) this.getBlockState().getBlock()).getMultiblockData());
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(this.energyContainer));
    }

    @Override
    protected boolean canProcess() {
        recipeNow = cachedCheck.getRecipeFor(new RecipeWrapper(this.items), this.level).orElse(null);
        if (recipeNow == null) return false;
        ItemStack[] results = recipeNow.assemble(new RecipeWrapper(this.items), this.level);
        for (int i = 0; i < 4; i++) {
            if (!this.items.insertItem(2 + i, results[i], true).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasPower() {
        return this.energyContainer.getEnergy() > 0;
    }

    public boolean isProcessing() {
        return this.progress > 0;
    }

    private RecipeManager.CachedCheck<Container, RecipeCentrifuge> cachedCheck = RecipeManager.createCheck(ModRecipes.CENTRIFUGE.type().get());
    private RecipeCentrifuge recipeNow = null;
    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        // 能源交换
        TransmitUtils.dischargeItem(this, this.items.getStackInSlot(1));
        // 计算升级
        int consumption = baseConsumption;
        int speed = 1;

        upgrades = IUpgradeInfoProvider.getUpgradeNow(upgrades, this, this.items, 2,4);

        speed += upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.SPEED, 0);
        consumption += upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.SPEED, 0) * baseConsumption;

        speed *= (1 + upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.OVERDRIVE, 0) * 5);
        consumption += upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.OVERDRIVE, 0) * baseConsumption * 50;

        consumption /= (1 + upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.POWER, 0));
        // 判断运行 与 处理配方
        int progressBefore = progress;
        boolean canProcess = this.energyContainer.extract(consumption, true) == consumption && canProcess();
        if (canProcess){
            progress += speed;
            this.energyContainer.extract(consumption, false);
            if (progress >= processingSpeed){
                // 处理配方
                ItemStack[] results = recipeNow.assemble(new RecipeWrapper(this.items), this.level);
                for (int i = 0; i < 4; i++) {
                    this.items.insertItem(2 + i, results[i], false);
                }
                this.items.extractItem(0, 1, false);
                // 还原状态
                progress = 0;
                this.recipeNow = null;
            }
        }else {
            progress = 0;
            this.recipeNow = null;
        }

        if (progress != progressBefore){
            this.setChanged();
            this.sendUpdatePacket();
        }
    }

    @OnlyIn(Dist.CLIENT) private int audioDuration = 0;
    @OnlyIn(Dist.CLIENT) private AudioWrapper audio = null;
    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        if(progress > 0) {
            audioDuration += 2;
        } else {
            audioDuration -= 3;
        }

        audioDuration = Mth.clamp(audioDuration, 0, 60);

        if(audioDuration > 10 && Minecraft.getInstance().player.distanceToSqr(this.worldPosition.getCenter()) < 25 * 25) {
            if(audio == null) {
                audio = createAudioLoop();
                audio.startSound();
            } else if(!audio.isPlaying()) {
                audio = rebootAudio(audio);
            }
            audio.updateVolume(1);
            audio.updatePitch((audioDuration - 10) / 100F + 0.5F);
            audio.keepAlive();

        } else {
            if(audio != null) {
                audio.stopSound();
                audio = null;
            }
        }
    }

    @Override
    public AudioWrapper createAudioLoop() {
        AudioWrapper audioWrapper = new AudioWrapper(ModSounds.BLOCK_CENTRIFUGE_OPERATE.get(), 1.0F, 10F, 1.0F, true);
        audioWrapper.setKeepAlive(20);
        return audioWrapper;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.energyContainer.deserializeNBT(nbt.getCompound(HBMKey.ENERGY));
        this.progress = nbt.getInt(HBMKey.PROGRESS);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put(HBMKey.ENERGY, this.energyContainer.serializeNBT());
        pTag.putInt(HBMKey.PROGRESS, this.progress);
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.putInt(HBMKey.PROGRESS, this.progress);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        if (tag.contains(HBMKey.PROGRESS, Tag.TAG_INT))
            this.progress = tag.getInt(HBMKey.PROGRESS);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if(audio != null) { audio.stopSound(); audio = null; }
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuCentrifuge(pContainerId, pInventory, this, containerData);
    }

    @Override
    public Component getName() {
        return Component.translatable("block." + HBM.MODID + "." + MachineCentrifuge.id);
    }

    // --------- 升级相关信息 -------------------

    public static final Map<ItemMachineUpgrade.UpgradeType, Integer> VALID_UPGRADES = Map.of(ItemMachineUpgrade.UpgradeType.SPEED, 3, ItemMachineUpgrade.UpgradeType.POWER, 3, ItemMachineUpgrade.UpgradeType.OVERDRIVE, 3);
    @Override
    public boolean canProvideInfo(ItemMachineUpgrade.UpgradeType type, int level) {
        return VALID_UPGRADES.containsKey(type);
    }

    @Override
    public void provideInfo(ItemMachineUpgrade.UpgradeType type, int level, List<Component> info) {
        info.add(IUpgradeInfoProvider.getStandardLabel(ModBlocks.MACHINE_CENTRIFUGE.get()));
        if(type == ItemMachineUpgrade.UpgradeType.SPEED) {
            info.add(HBMLang.UPGRADE_DELAY.translate("-" + (100 - 100 / (level + 1)) + "%").withStyle(ChatFormatting.GREEN));
            info.add(HBMLang.UPGRADE_CONSUMPTION.translate("+" + (level * 100) + "%").withStyle(ChatFormatting.RED));
        }
        if(type == ItemMachineUpgrade.UpgradeType.POWER) {
            info.add(HBMLang.UPGRADE_CONSUMPTION.translate( "-" + (100 - 100 / (level + 1)) + "%").withStyle(ChatFormatting.GREEN));
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
