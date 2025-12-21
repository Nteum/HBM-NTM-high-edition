package com.hbm.blockentity.generator;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.ExtendedFluidType;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.Inventory.fluid.trait.FT_Flammable;
import com.hbm.addational_data.Pollution;
import com.hbm.api.energy.*;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.api.fluid.FluidUtils;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.BaseMachineBlockEntity;
import com.hbm.blockentity.base2.DummyableBlockEntity;
import com.hbm.blockentity.interfaces.IControlReceiver;
import com.hbm.blockentity.machine.ChemplantEntity;
import com.hbm.capabilities.HBMCaps;
import com.hbm.datagen.recipe.provider.PressRecipeProvider;
import com.hbm.gui.menu.ChemplantMenu;
import com.hbm.gui.menu.MenuWoodBurner;
import com.hbm.registries.ModTags;
import com.hbm.utils.BurnSystem;
import com.hbm.utils.BurnSystem.*;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.InventoryUtils;
import com.hbm.utils.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class TileWoodBurner extends DummyableBlockEntity implements IControlReceiver {
    public static int SLOT_NUM = 5;
    public static int ASH_THRESH = 2000;
    public static double[] burnData = BurnSystem.getMod(ModBlockEntityType.WOOD_BURNER.get());

    public int burnTime;
    public int maxBurnTime;
    public boolean liquidBurn = false;
    // 和running不同，它是指启动按钮是否按下
    public boolean isOn = false;
    protected int powerGen = 0;
    protected int ashLevel = 0;
    protected AABB box;

    protected IEnergyContainer energyContainer;
    protected ItemStackHandler itemStackHandler;
    protected BasicFluidHandler fluidHandler;
    protected ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> (int) energyContainer.getEnergy();
                case 1 -> (int) energyContainer.getCapacity();
                case 2 -> burnTime;
                case 3 -> maxBurnTime;
                case 4 -> isOn ? 0 : 1;
                case 5 -> liquidBurn ? 0 : 1;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
        }

        @Override
        public int getCount() {
            return 6;
        }
    };
    public TileWoodBurner(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.WOOD_BURNER.get(), pPos, pBlockState);
        energyContainer = new BasicEnergyContainer(100_000);
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(energyContainer));
        itemStackHandler = new ItemStackHandler(6){
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot){
                    case 0, 2 -> true;
                    case 1, 3 -> false;
                    case 4 -> stack.is(ModTags.Items.BATTERY);
                    default -> false;
                };
            }
        };
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, itemStackHandler);
        fluidHandler = new BasicFluidHandler(1, 16_000){
            @Override
            public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
                return stack.getFluid().isSame(ModFluids.WOOD_OIL.source().get());
            }
        };
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, fluidHandler);
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuWoodBurner(pContainerId, pInventory, this, containerData);
    }

    @Override
    public Component getDefaultName() {
        return HBMLang.CONTAINER_WOOD_BURNER.translate();
    }

    protected int getBurnTime(ItemStack stack){
        int baseTime = stack.getBurnTime(RecipeType.SMELTING);
        if (stack.is(ItemTags.LOGS)) return baseTime * 4;
        // 1.7.10版本这里写的是wood，但由于mc目前没有对所有木制品统一的tag，所以暂时仅限于木板
        if (stack.is(ModTags.Items.WOOD)) return baseTime * 2;
        return baseTime;
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();

        powerGen = 0;
        // 和GUI中流体储罐和电池的交互
        InventoryUtils.handleItems(this, stack -> FluidUtils.absorbFromItem(this.fluidHandler, stack), 2, 3);
        TransmitUtils.chargeItem((IEnergyHandler) getCapability(HBMCaps.LONG_ENERGY), this.itemStackHandler.getStackInSlot(4));
        // 吸收流体？ 暂时不让发电机主动输出电力
        // 发电内容
        boolean filled = this.energyContainer.getEnergy() == this.energyContainer.getCapacity();
        float pollution = Pollution.SOOT_PER_SECOND;

        if (!filled && isOn){
            this.burnTime --;

            if(!liquidBurn) {
                if(this.burnTime <= 0) {
                    EnumAshType ashType = BurnSystem.getAshFromFuel(this.itemStackHandler.getStackInSlot(0));
                    ItemStack stackInSlot = this.itemStackHandler.getStackInSlot(1);
                    if (stackInSlot.isEmpty() || stackInSlot.is(ashType.item) && stackInSlot.getCount() < stackInSlot.getMaxStackSize()){
                        int burn = BurnSystem.getBurnTime(this.itemStackHandler.getStackInSlot(0), ModBlockEntityType.WOOD_BURNER.get());
                        this.ashLevel += burn;
                        if (this.ashLevel >= ASH_THRESH) {
                            this.itemStackHandler.insertItem(1, ashType.item.getDefaultInstance(), false);
                        }
                        this.maxBurnTime = this.burnTime = burn;
                        if (stackInSlot.hasCraftingRemainingItem()) {
                            this.itemStackHandler.setStackInSlot(0, stackInSlot.getCraftingRemainingItem());
                        }else {
                            this.itemStackHandler.extractItem(0, 1, false);
                        }
                        powerGen += 100;
                        setChanged();
                    }
                }else powerGen += 100;
            } else {
                FluidStack fluidInTank = this.fluidHandler.getFluidInTank(0);
                FluidType fluidType = fluidInTank.getFluid().getFluidType();
                if (fluidType instanceof ExtendedFluidType extendedFluidType){
                    FT_Flammable trait = extendedFluidType.getTrait(FT_Flammable.class);
                    if (trait != null){
                        int toBurn = Math.min(fluidInTank.getAmount(), 2);
                        if (toBurn > 0){
                            powerGen += (int) (trait.getHeatEnergy() * toBurn / 2_000L);
                            this.fluidHandler.drain(toBurn, IFluidHandler.FluidAction.EXECUTE);
                            pollution *= toBurn / 2f;
                        }
                    }
                }
            }
        }

        this.energyContainer.receive(this.powerGen, false);

        running = !filled && isOn && burnTime > 0;
        if (running){
            if (this.level.random.nextInt(20) == 0) Pollution.increPollution(this.level, getBlockPos(), Pollution.Type.SOOT, pollution);
        }

        sendUpdatePacket();
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        if(powerGen > 0) {
            Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction rot = DirectionUtils.leftRot(Direction.UP, facing);
            Vec3 pos = getBlockPos().getCenter();
            level.addParticle(ParticleTypes.SMOKE, pos.x - facing.getStepX() + rot.getStepX(), pos.y + 4, pos.z - facing.getStepZ() + rot.getStepZ(), 0, 0.05, 0);
        }
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.putInt("powergen", powerGen);
        tag.put(HBMKey.FLUIDS, this.fluidHandler.serializeNBT());
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        if (tag.contains("powergen", Tag.TAG_INT)) this.powerGen = tag.getInt("powergen");
        NBTHelper.setCompoundIfPreset(tag, HBMKey.FLUIDS, fluidHandler::deserializeNBT);
    }

    @Override
    public void handleClientPacket(@NotNull CompoundTag tag) {
        super.handleClientPacket(tag);
        if(tag.contains("toggle")) {
            this.isOn = !this.isOn;
            setChanged();
        }
        if(tag.contains("switch")) {
            this.liquidBurn = !this.liquidBurn;
            setChanged();
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        burnTime = nbt.getInt("burnTime");
        maxBurnTime = nbt.getInt("maxBurnTime");
        isOn = nbt.getBoolean("isOn");
        liquidBurn = nbt.getBoolean("liquidBurn");
        if (nbt.contains(HBMKey.ITEM)) itemStackHandler.deserializeNBT(nbt);
        if (nbt.contains(HBMKey.FLUIDS)) fluidHandler.deserializeNBT(nbt);
        if (nbt.contains(HBMKey.ENERGY)) energyContainer.deserializeNBT(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("burnTime", burnTime);
        nbt.putInt("maxBurnTime", maxBurnTime);
        nbt.putBoolean("isOn", isOn);
        nbt.putBoolean("liquidBurn", liquidBurn);
        if (itemStackHandler != null) nbt.put(HBMKey.ITEM, itemStackHandler.serializeNBT());
        if (fluidHandler != null) nbt.put(HBMKey.FLUIDS, fluidHandler.serializeNBT());
        if (energyContainer != null) nbt.put(HBMKey.ENERGY, energyContainer.serializeNBT());
    }

    @Override
    public boolean hasPermission(Player player) {
        return this.stillValid(player);
    }

    @Override
    public void receiveControl(CompoundTag data) {
    }

    @Override
    public AABB getRenderBoundingBox() {
        if (box == null){
            box = new AABB(getBlockPos().offset(-1, 0, -1), getBlockPos().offset(2, 6, 2));
        }
        return box;
    }

    public IItemHandler getItemHandler(){
        return this.itemStackHandler;
    }
    public FluidStack getFluid(){
        return this.fluidHandler != null ? this.fluidHandler.getFluidInTank(0) : FluidStack.EMPTY;
    }
}
