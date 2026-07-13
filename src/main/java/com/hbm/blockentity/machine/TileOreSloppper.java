package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.HBMUpgrade;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.IEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.block.machine.MachineOreSlopper;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DefaultMachineBE;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.gui.menu.MenuOreSlopper;
import com.hbm.item.env.ItemBedrockOre;
import com.hbm.item.env.ItemBedrockOreCombine;
import com.hbm.item.env.ItemBedrockOreRaw;
import com.hbm.item.machine.ItemMachineUpgrade;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toclient.S2CParticlePacket;
import com.hbm.registries.HBMDamage;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class TileOreSloppper extends DefaultMachineBE implements IUpgradeInfoProvider {
    public static final long maxPower = 100_000;

    public static final int waterUsedBase = 1_000;
    public int waterUsed = waterUsedBase;
    public static final long consumptionBase = 200;
    public long consumption = consumptionBase;

    public float progress;
    public boolean processing;

    public double[] ores = new double[ItemBedrockOreCombine.CelestialBedrockOre.oreTypes.size()];

//    public SlopperAnimation animation = SlopperAnimation.LOWERING;
//    public float slider;
//    public float prevSlider;
//    public float bucket;
//    public float prevBucket;
//    public float blades;
//    public float prevBlades;
//    public float fan;
//    public float prevFan;
//    public int delay;
//
//    BasicFluidHandler fluidHandler;
//    public double[] ores = new double[CelestialBedrockOre.getAllTypes().size()];
//    private SolarSystem.Body fromBody;
//
//    public UpgradeManagerNT upgradeManager = new UpgradeManagerNT();
//
//    public TileOreSloppper() {
//        super(11);
//        tanks = new FluidTank[2];
//        tanks[0] = new FluidTank(Fluids.WATER, 16_000);
//        tanks[1] = new FluidTank(Fluids.SLOP, 16_000);
//    }

    private ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return 0;
        }

        @Override
        public void set(int pIndex, int pValue) {}

        @Override
        public int getCount() {
            return 0;
        }
    };
    public TileOreSloppper(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.tileTypes.get("tile_" + MachineOreSlopper.name).get(), pos, state);
    }

    @Override
    protected void initCapabilities() {
        this.items = new ItemStackHandler(11){
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return super.isItemValid(slot, stack);
            }
        };
        this.fluidHandler = new BasicFluidHandler(2, 16_000);
        this.energyContainer = new BasicEnergyContainer(maxPower);
        super.initCapabilities();
    }

    private int speed = 0;
    private int efficiency = 0;
    @Override
    protected void preWork() {
        // 充能
        TransmitUtils.dischargeItem(this, this.items.getStackInSlot(0));
        // 更新升级功能
        Map<ItemMachineUpgrade.UpgradeType, Integer> upgrades = getValidUpgrades();
        this.speed = upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.SPEED, 0);
        this.efficiency = upgrades.getOrDefault(ItemMachineUpgrade.UpgradeType.EFFECT, 0);
        this.consumption = this.consumptionBase + (this.consumptionBase * speed) / 2 + (this.consumptionBase * efficiency);
    }

    @Override
    protected boolean canProcess() {
        FluidStack inputFluid = this.fluidHandler.getFluidInTank(0);
        FluidStack outputFluid = this.fluidHandler.getFluidInTank(1);
        if (this.energyContainer.extract(consumption, true) < consumption || inputFluid.getFluid().isSame(Fluids.WATER)
                || inputFluid.getAmount() < waterUsed || outputFluid.getAmount() + waterUsed < fluidHandler.getTankCapacity(1))
            return false;
        return this.items.getStackInSlot(2).is(ModItems.ORE_BEDROCK_RAW.get());
    }

    @Override
    protected void process() {
        this.energyContainer.extract(this.consumption, false);
        this.progress += 1F / (600 - speed * 150);
        this.processing = true;
        boolean markDirty = false;

        while(progress >= 1F && canProcess()) {
            progress -= 1F;
            ResourceKey<Level> oreBody = ItemBedrockOreRaw.getOreBody(this.items.getStackInSlot(2));
            for (ItemBedrockOreCombine.CelestialBedrockOreType type : ItemBedrockOreCombine.CelestialBedrockOre.get(oreBody).types)
                ores[type.index] += ItemBedrockOreRaw.getOreAmount(this.items.getStackInSlot(2), type) * (1d + efficiency * 0.1);
            this.items.extractItem(2, 1, false);
            this.fluidHandler.getFluidTanks().get(0).drain(waterUsed, IFluidHandler.FluidAction.EXECUTE);
            this.fluidHandler.getFluidTanks().get(1).fill(new FluidStack(ModFluids.SLOP.source().get(), waterUsed), IFluidHandler.FluidAction.EXECUTE);
            markDirty = true;
        }
        if(markDirty) this.setChanged();
        Vec3 center = this.worldPosition.getCenter();
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        List<Entity> entities = this.level.getEntities(null, new AABB(center.add(-0.5, 1, 0.5), center.add(1.5, 3, 1.5)).move(facing.getStepX(), 0, facing.getStepZ()));

        for(Entity e : entities) {
            e.hurt(HBMDamage.get(HBMDamage.TURBOFAN, this.level.registryAccess(), null, null), 1000f);
            if (!e.isAlive() && e instanceof LivingEntity){
                CompoundTag tag = new CompoundTag();
                tag.putString(HBMKey.TYPE, "giblets");
                tag.putInt("ent", e.getId());
                tag.putInt("cDiv", 5);
                ModMessages.sendToEntity(new S2CParticlePacket(tag, e.getX(), e.getY() + e.getEyeHeight() / 0.5, e.getZ()), e);
                // 音效
                this.level.playSound(null, e.getOnPos(), SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.RECORDS , 0.2f, 0.95f + level.random.nextFloat() * 0.2f);
            }
        }
    }

    @Override
    protected void noProcess() {
        this.progress = 0;
    }

    @Override
    protected void afterWork() {
        for(CelestialBedrockOreType type : CelestialBedrockOre.getAllTypes()) {
            ItemStack output = ItemBedrockOreNew.make(BedrockOreGrade.BASE, type);
            outer: while(ores[type.index] >= 1) {
                for(int i = 3; i <= 8; i++) if(slots[i] != null && slots[i].getItem() == output.getItem() && slots[i].getItemDamage() == output.getItemDamage() && slots[i].stackSize < output.getMaxStackSize()) {
                    slots[i].stackSize++; ores[type.index] -= 1F; continue outer;
                }
                for(int i = 3; i <= 8; i++) if(slots[i] == null) {
                    slots[i] = output; ores[type.index] -= 1F; continue outer;
                }
                break outer;
            }
        }

        this.networkPackNT(150);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();

        if(canSlop()) {
            this.power -= this.consumption;
            this.progress += 1F / (600 - speed * 150);
            this.processing = true;
            boolean markDirty = false;

            while(progress >= 1F && canSlop()) {
                progress -= 1F;

                fromBody = ItemBedrockOreBase.getOreBody(slots[2]);

                for(CelestialBedrockOreType type : CelestialBedrockOre.get(fromBody).types) {
                    ores[type.index] += (ItemBedrockOreBase.getOreAmount(slots[2], type) * (1D + efficiency * 0.1));
                }

                this.decrStackSize(2, 1);
                this.tanks[0].setFill(this.tanks[0].getFill() - waterUsed);
                this.tanks[1].setFill(this.tanks[1].getFill() + waterUsed);
                markDirty = true;
            }

            if(markDirty) this.markDirty();

            List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xCoord - 0.5, yCoord + 1, zCoord - 0.5, xCoord + 1.5, yCoord + 3, zCoord + 1.5).offset(dir.offsetX, 0, dir.offsetZ));

            for(Entity e : entities) {
                e.attackEntityFrom(ModDamageSource.turbofan, 1000F);

                if(!e.isEntityAlive() && e instanceof EntityLivingBase) {
                    NBTTagCompound vdat = new NBTTagCompound();
                    vdat.setString("type", "giblets");
                    vdat.setInteger("ent", e.getEntityId());
                    vdat.setInteger("cDiv", 5);
                    PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(vdat, e.posX, e.posY + e.height * 0.5, e.posZ), new TargetPoint(e.dimension, e.posX, e.posY + e.height * 0.5, e.posZ, 150));

                    worldObj.playSoundEffect(e.posX, e.posY, e.posZ, NTMSounds.VANILLA_GIB, 2.0F, 0.95F + worldObj.rand.nextFloat() * 0.2F);
                }
            }
        } else {
            this.progress = 0;
        }

        for(CelestialBedrockOreType type : CelestialBedrockOre.getAllTypes()) {
            ItemStack output = ItemBedrockOreNew.make(BedrockOreGrade.BASE, type);
            outer: while(ores[type.index] >= 1) {
                for(int i = 3; i <= 8; i++) if(slots[i] != null && slots[i].getItem() == output.getItem() && slots[i].getItemDamage() == output.getItemDamage() && slots[i].stackSize < output.getMaxStackSize()) {
                    slots[i].stackSize++; ores[type.index] -= 1F; continue outer;
                }
                for(int i = 3; i <= 8; i++) if(slots[i] == null) {
                    slots[i] = output; ores[type.index] -= 1F; continue outer;
                }
                break outer;
            }
        }

        this.networkPackNT(150);
    }

    public FluidType getFluidOutput(FluidType input) {
        if (input == Fluids.WATER.getFluidType()) return ModFluids.SLOP.type().get();
        return null;
    }
    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuOreSlopper(pContainerId, pInventory, this, containerData);
    }

    @Override
    public Component getName() {
        return HBMLang.CONTAINER_ORE_SLOPPER.translate();
    }

    public enum SlopperAnimation {
        LOWERING, LIFTING, MOVE_SHREDDER, DUMPING, MOVE_BUCKET
    }
}
