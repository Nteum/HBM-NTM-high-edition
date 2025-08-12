package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.UpgradeManagerNT;
import com.hbm.Inventory.recipe.ChemplantRecipe;
import com.hbm.Inventory.recipe.HBMRecipeMatcher;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.api.Mode;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.api.fluid.BasicFluidTank;
import com.hbm.api.fluid.FluidUtils;
import com.hbm.api.fluid.IExtendedFluidHandler;
import com.hbm.api.fluid.IExtendedFluidHandler.*;
import com.hbm.api.inventory.ModeBuilder;
import com.hbm.api.math.MathUtils;
import com.hbm.block.machine.BlockChemplant;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.DummyableBlockEntity;
import com.hbm.capabilities.Capabilities;
import com.hbm.gui.menu.ChemplantMenu;
import com.hbm.item.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.registries.ModSounds;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.InventoryUtils;
import com.hbm.utils.sound.AudioUtils;
import com.hbm.utils.sound.AudioWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChemplantEntity extends DummyableBlockEntity {
    private static final int maxFluid = 24_000;
    public int maxProgress = 100;
    public int progress = 0;
    int consumption = 100;
    int speed = 100;
    public UpgradeManagerNT upgradeManager = new UpgradeManagerNT();
    private BasicEnergyContainer energyContainer = new BasicEnergyContainer(100_100);
    private BasicFluidHandler fluidHandler;
    private final RecipeManager.CachedCheck<Container, ChemplantRecipe> recipeChecker = RecipeManager.createCheck(ModRecipes.CHEMPLANT.type().get());
    private ChemplantRecipe recipeNow = null;
    private static AudioWrapper audioWrapper;

    private boolean debugFlag = true;
    protected final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> getProgress();
                case 1 -> maxProgress;
                case 2 -> MathUtils.clampToInt(getCapability(Capabilities.LONG_ENERGY).orElse(null).getStored());
                case 3 -> MathUtils.clampToInt(getCapability(Capabilities.LONG_ENERGY).orElse(null).getCapacity());
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
    public ChemplantEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.CHEMPLANT_ENTITY.get(), pPos, pBlockState);
        this.items = NonNullList.withSize(20, ItemStack.EMPTY);
        this.slotModes = new ModeBuilder().addModes(4,Mode.BOTH,4,Mode.OUTPUT,2,Mode.INPUT,2,Mode.OUTPUT,6,Mode.INPUT,2,Mode.OUTPUT).get();
        this.fluidHandler = new BasicFluidHandler().addTanks(2, maxFluid, Mode.INPUT).addTanks(2, maxFluid, Mode.OUTPUT);
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluidHandler);
//        for (int i = 0; i < 4; i++) {
//            this.tanks.add(new BasicFluidTank(maxFluid));
//        }
//        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this);
        this.capabilitiesContent.addCapability(Capabilities.LONG_ENERGY, new ProxyEnergyHandler(this.energyContainer));
//        this.capabilitiesCache.addCapabilityResolver(new SidedFluidWrapper(new BaseFluidHandler(4,24_000)));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        // 处理能量和流体
        TransmitUtils.dischargeItem(this, this.getItem(0));
        // 做的很不成熟，还需要改
        InventoryUtils.handleItems(this, itemStack -> this.fluidHandler.drainItem(0,itemStack), 16, 18);
        InventoryUtils.handleItems(this, itemStack -> this.fluidHandler.drainItem(1,itemStack), 17, 19);
        InventoryUtils.handleItems(this, itemStack -> this.fluidHandler.fillItem(2,itemStack), 8, 10);
        InventoryUtils.handleItems(this, itemStack -> this.fluidHandler.fillItem(3,itemStack), 9, 11);
//        this.setItem(18, FluidUtils.absorbFromItem(this, 0, getItem(16)));
//        this.setItem(19, FluidUtils.absorbFromItem(this, 1, getItem(17)));
//        this.setItem(10, FluidUtils.absorbFromItem(this, 2, getItem(8)));
//        this.setItem(11, FluidUtils.absorbFromItem(this, 3, getItem(9)));
        // 检查升级
        upgradeManager.checkSlots(this, items, 3, 3);
        int speedLevel = upgradeManager.getLevel(UpgradeType.SPEED);
        int powerLevel = upgradeManager.getLevel(UpgradeType.POWER);
        int overLevel = upgradeManager.getLevel(UpgradeType.OVERDRIVE);
        this.speed = Math.max((100 - speedLevel * 25 + powerLevel * 5) / (overLevel + 1), 1);
        this.consumption = (100 + speedLevel * 300 - powerLevel * 20) * (overLevel + 1);
        // 处理
//        if (progress <= maxProgress) progress++;
//        else progress = - 100;

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
        // 显示机器客户端特效的范围。
        int effectRange = 100;
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null || this.level == null)return;
        double dist = localPlayer.distanceToSqr(this.worldPosition.getCenter());
        if (dist < effectRange){
            if (progress > 0){
                if (this.level.getGameTime() % 3 == 0){
                    // 显示烟雾
                    Direction facing = this.getBlockState().getValue(BlockChemplant.FACING);
                    Direction rot = DirectionUtils.horizRot(Direction.SOUTH, facing, Direction.EAST);
                    double x = getBlockPos().getX() + 0.5 + facing.getStepX() * 1.125 + rot.getStepX() * 0.125;
                    double y = getBlockPos().getY() + 3;
                    double z = getBlockPos().getZ() + 0.5 + facing.getStepZ() * 1.125 + rot.getStepZ() * 0.125;
                    level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.1, 0.0);
                }
            }
        }
        audioWrapper = (audioWrapper == null) ? createAudioLoop() : audioWrapper;
        if (dist < audioWrapper.range){
            float volume = this.getVolume(1F);
            // 机器运行的响动
            if (progress > 0 && volume > 0){
                audioWrapper.volume = volume;
                audioWrapper.playLoopSound(localPlayer.position(), this.level.getRandom().nextLong());
            }else {
                // 停止声音
                audioWrapper.stopLoopSound();
            }
        }
    }

    protected AudioWrapper createAudioLoop(){
        return new AudioWrapper(ModSounds.BLOCK_CHEMPLANT_OPERATE.get(), SoundSource.BLOCKS);
    }

    private boolean canProcess() {
        if (this.energyContainer.getEnergy() < this.consumption) return false;
        // 检查配方，同时判断输入槽物品
        ChemplantRecipe recipe = recipeChecker.getRecipeFor(this, level).orElse(null);
        if(recipe == null) return false;
        // 检查输出物品槽与输出流体槽
        if (!HBMRecipeMatcher.checkOutputSlots(this.items.subList(4,8), recipe.resultItems)
                || !HBMRecipeMatcher.checkOutputTanks(this.fluidHandler.getFluidTanks().subList(2,4), recipe.resultFLuids))
            return false;

        if (this.recipeNow == null || !this.recipeNow.equals(recipe)){
            this.recipeNow = recipe;
            this.progress = 0;
        }
        return true;
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = new CompoundTag();
//        for (int i = 0; i < this.tanks.size(); i++) {
//            CompoundTag tempTag = new CompoundTag();
//            this.tanks.get(i).writeToNBT(tempTag);
//            tag.put(HBMKey.FLUIDS + "_" + i, tempTag);
//        }
        tag.put(HBMKey.FLUIDS, this.fluidHandler.serializeNBT());
        tag.putInt(HBMKey.PROGRESS, progress);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
//        for (int i = 0; i < this.tanks.size(); i++) {
//            this.tanks.get(i).readFromNBT(tag.getCompound(HBMKey.FLUIDS + "_" + i));
//        }
        this.fluidHandler.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        this.progress = tag.getInt(HBMKey.PROGRESS);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(HBMKey.RECIPE_NOW)){
            ResourceLocation resourceLocation = new ResourceLocation(nbt.getString(HBMKey.RECIPE_NOW));
            this.recipeNow = (ChemplantRecipe) this.level.getRecipeManager().byKey(resourceLocation).orElse(null);
        }
        this.energyContainer.deserializeNBT(nbt.getCompound(HBMKey.ENERGY));
//        for (int i = 0; i < this.tanks.size(); i++) {
//            this.tanks.get(i).readFromNBT(nbt.getCompound(HBMKey.FLUIDS + "_" + i));
//        }
        this.fluidHandler.deserializeNBT(nbt.getCompound(HBMKey.FLUIDS));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (recipeNow!=null)
            pTag.putString(HBMKey.RECIPE_NOW,recipeNow.getId().toString());
        pTag.put(HBMKey.ENERGY, this.energyContainer.serializeNBT());
//        for (int i = 0; i < this.tanks.size(); i++) {
//            CompoundTag tempTag = new CompoundTag();
//            this.tanks.get(i).writeToNBT(tempTag);
//            pTag.put(HBMKey.FLUIDS + "_" + i, tempTag);
//        }
        pTag.put(HBMKey.FLUIDS, this.fluidHandler.serializeNBT());
    }

//    @Override
//    public List<? extends IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
//        return this.tanks;
//    }

//    @Override
    public List getFluidTanks(@Nullable Direction side) {
        return this.fluidHandler.getFluidTanks();
    }

    @Override
    public BasicEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    protected int getProgress(){return Math.max(0, progress);}

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
