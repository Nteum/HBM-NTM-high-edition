package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.Inventory.material.BasicHeatHandler;
import com.hbm.addational_data.Pollution;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.blockentity.interfaces.IBurnFuel;
import com.hbm.blockentity.interfaces.IMachinePolluting;
import com.hbm.blockentity.interfaces.ITakeAir;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public abstract class TileFireboxBase extends DummyableBlockEntity implements IBurnFuel, IMachinePolluting {
    public int maxBurnTime;
    public int burnTime;            // 燃料还可以燃烧的时间
    public int burnHeat;            // 单位时间燃烧产生的热量
//    public int heatEnergy;          // 当前炉子储存的热量
    public boolean isBurn = false;  // 是否正在燃烧
    private int playersUsing = 0;

    public float doorAngle = 0;
    public float prevDoorAngle = 0;

    private BasicFluidHandler basicFluidHandler;
    private BasicHeatHandler heatHandler;

    private ItemStackHandler items = new ItemStackHandler(2){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot){
                case 0 -> true;
                default -> false;
            };
        }
    };
    public final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> maxBurnTime;
                case 1 -> burnTime;
                case 2 -> burnHeat;
                case 3 -> heatHandler.getHeat();
                case 4 -> isBurn ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {}

        @Override
        public int getCount() {
            return 5;
        }
    };
    public TileFireboxBase(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.TILE_FIREBOX.get(), pos, state);
        this.multiblockData = MultiblockData.mapping.get(ModBlocks.HEATER_FIREBOX.get());
        this.burnTime = 0;
//        this.heatEnergy = 0;
        this.basicFluidHandler = new BasicFluidHandler(3, 50);
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.basicFluidHandler);
        this.heatHandler = BasicHeatHandler.of(getMaxHeat());
        this.capabilitiesContent.addCapability(HBMCaps.HEAT, this.heatHandler);
    }

    @Override
    public FluidTank getPollutionTank(Pollution.Type type) {
        return switch (type){
            case SOOT -> this.basicFluidHandler.getFluidTanks().get(0);
            case HEAVYMETAL -> this.basicFluidHandler.getFluidTanks().get(1);
            case POISON -> this.basicFluidHandler.getFluidTanks().get(2);
            default -> null;
        };
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        // 生成污染粒子
        // 主循环
        boolean canBurn = false;
        if (burnTime <= 0){
            this.isBurn = false;
            for (int i = 0; i < 2; i++) {
                canBurn = ITakeAir.breatheAir(this.level, this.getBlockPos(), 0);
                ItemStack stackInSlot = this.items.getStackInSlot(i);
                int baseTime = getBurnTime(stackInSlot);
                if (baseTime > 0){
                    this.maxBurnTime = this.burnTime = (int) (baseTime * getTimeMult());
                    this.burnHeat = getBurnHeat(getBaseHeat(), stackInSlot);
                    this.items.extractItem(i, 1, false);
                    this.isBurn = true;
                    // 生成烟灰（如果下方存在烟灰接收器）
                    break;
                }
            }
        }else {
            if (this.heatHandler.getHeat() < this.getMaxHeat()){
                canBurn = ITakeAir.breatheAir(this.level, this.getBlockPos(), this.level.getGameTime() % (500 / getBaseHeat()) == 0 ? 1 : 0);
                if (canBurn){
                    this.burnTime --;
                    // 生成污染
                    if(level.getGameTime() % 20 == 0) this.pollute(this.level, this.worldPosition, Pollution.Type.SOOT, Pollution.SOOT_PER_SECOND * 3);
                }
            }else {
                canBurn = ITakeAir.breatheAir(this.level, this.getBlockPos(), 0);
            }
            if (canBurn){
                this.isBurn = true;
                // 燃烧效果
                if (level.random.nextInt(15) == 0){
                    level.playSound(null, this.worldPosition, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1,0.5f + level.random.nextFloat() * 0.5f);
                }
            }
        }

        if (isBurn){
            this.heatHandler.receiveHeat(this.burnHeat, false);
//            this.heatEnergy = Math.min(this.heatEnergy + this.burnHeat, getMaxHeat());
        }else {
            this.heatHandler.decay();
//            this.heatEnergy = Math.max(this.heatEnergy - Math.max(this.heatEnergy / 1000, 1), 0);
            if (canBurn) this.burnHeat = 0;
        }

        this.sendUpdatePacket();
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        this.prevDoorAngle = this.doorAngle;
        float swingSpeed = (doorAngle / 10F) + 3;

        if(this.playersUsing > 0) {
            this.doorAngle += swingSpeed;
        } else {
            this.doorAngle -= swingSpeed;
        }

        this.doorAngle = Mth.clamp(this.doorAngle, 0F, 135F);

        if (isBurn && level.getGameTime() % 5 == 0){
            Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            Vec3 smokePos = this.worldPosition.relative(facing, 2).getCenter();
            level.addParticle(ParticleTypes.FLAME, smokePos.x + level.random.nextDouble() * 0.5 - 0.25, smokePos.y - 0.25 + level.random.nextDouble() * 0.25, smokePos.z + level.random.nextDouble() * 0.5 - 0.25, 0, 0, 0);
        }
    }

    public abstract int getBaseHeat();
    public abstract double getTimeMult();
    public abstract int getMaxHeat();

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("maxBurnTime", maxBurnTime);
        tag.putInt("burnTime", burnTime);
        tag.putInt("burnHeat", burnHeat);
//        tag.putInt("heatEnergy", heatEnergy);
        tag.put(HBMKey.HEAT, this.heatHandler.serializeNBT());
        tag.put(HBMKey.ITEM, this.items.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.maxBurnTime = nbt.getInt("maxBurnTime");
        this.burnTime = nbt.getInt("burnTime");
        this.burnHeat = nbt.getInt("burnHeat");
//        this.heatEnergy = nbt.getInt("heatEnergy");
        this.heatHandler.deserializeNBT(nbt.getCompound(HBMKey.HEAT));
        if (nbt.contains(HBMKey.ITEM, Tag.TAG_COMPOUND)) this.items.deserializeNBT(nbt.getCompound(HBMKey.ITEM));
    }

    public ItemStackHandler getItemHandler(){
        return this.items;
    }
}
