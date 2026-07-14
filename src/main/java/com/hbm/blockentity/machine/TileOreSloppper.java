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
import com.hbm.particle.ParticleSystem;
import com.hbm.registries.*;
import com.hbm.utils.InventoryUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
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

    // 客户端动画变量
    @OnlyIn(Dist.CLIENT) public SlopperAnimation animation = SlopperAnimation.LOWERING;
    @OnlyIn(Dist.CLIENT) public float slider;
    @OnlyIn(Dist.CLIENT) public float prevSlider;
    @OnlyIn(Dist.CLIENT) public float bucket;
    @OnlyIn(Dist.CLIENT) public float prevBucket;
    @OnlyIn(Dist.CLIENT) public float blades;
    @OnlyIn(Dist.CLIENT) public float prevBlades;
    @OnlyIn(Dist.CLIENT) public float fan;
    @OnlyIn(Dist.CLIENT) public float prevFan;
    @OnlyIn(Dist.CLIENT) public int delay;


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
                return switch (slot){
                    case 0 -> stack.is(ModTags.Items.CHARGEABLE);
                    case 2 -> stack.is(ModItems.ORE_BEDROCK_RAW.get());
                    default -> false;
                } && super.isItemValid(slot, stack);
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
        // 对应的矿物转换成物品
        for (ItemBedrockOreCombine.CelestialBedrockOreType type : ItemBedrockOreCombine.CelestialBedrockOre.oreTypes) {
            ItemStack output = ItemBedrockOreCombine.make(ItemBedrockOreCombine.BedrockOreGrade.BASE, type, (int) ores[type.index]);
            ores[type.index] -= InventoryUtils.insertNoCheckSlots(output, new RangedWrapper(this.items, 3, 9));
        }
        sendUpdatePacket();
    }

    public FluidType getFluidOutput(FluidType input) {
        if (input == Fluids.WATER.getFluidType()) return ModFluids.SLOP.type().get();
        return null;
    }
    @Override
    protected void onUpdateClient() {
        this.prevSlider = this.slider;
        this.prevBucket = this.bucket;
        this.prevBlades = this.blades;
        this.prevFan = this.fan;

        if(this.processing) {

            this.blades += 15F;
            this.fan += 35F;

            if(blades >= 360) {
                blades -= 360;
                prevBlades -= 360;
            }

            if(fan >= 360) {
                fan -= 360;
                prevFan -= 360;
            }

            if(animation == SlopperAnimation.DUMPING && Minecraft.getInstance().player.distanceToSqr(this.worldPosition.getCenter().add(0, 3.5f, 0)) <= 2500) {
                CompoundTag data = new CompoundTag();
                data.putString("type", "vanillaExt");
                data.putString("mode", "blockdust");
                data.put("block", NbtUtils.writeBlockState(Blocks.IRON_BLOCK.defaultBlockState()));
                data.putDouble("mY", -0.2D);
                Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
                ParticleSystem.vanillaExt(data, this.worldPosition.getCenter().add(facing.getStepX() + level.random.nextGaussian() * 0.25, 3.75, facing.getStepZ() + level.random.nextGaussian() * 0.25));
            }

            if(delay > 0) {
                delay--;
                return;
            }

            switch(animation) {
                case LOWERING:
                    this.bucket += 1F/40F;
                    if(bucket >= 1F) {
                        bucket = 1F;
                        animation = SlopperAnimation.LIFTING;
                        delay = 20;
                    }
                    break;
                case LIFTING:
                    this.bucket -= 1F/40F;
                    if(bucket <= 0) {
                        bucket = 0F;
                        animation = SlopperAnimation.MOVE_SHREDDER;
                        delay = 10;
                    }
                    break;
                case MOVE_SHREDDER:
                    this.slider += 1/50F;
                    if(slider >= 1F) {
                        slider = 1F;
                        animation = SlopperAnimation.DUMPING;
                        delay = 60;
                    }
                    break;
                case DUMPING:
                    animation = SlopperAnimation.MOVE_BUCKET;
                    break;
                case MOVE_BUCKET:
                    this.slider -= 1/50F;
                    if(slider <= 0F) {
                        animation = SlopperAnimation.LOWERING;
                        delay = 10;
                    }
                    break;
            }
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuOreSlopper(pContainerId, pInventory, this, containerData);
    }

    @Override
    public Component getName() {
        return HBMLang.CONTAINER_ORE_SLOPPER.translate();
    }

    @Override
    public boolean canProvideInfo(ItemMachineUpgrade.UpgradeType type, int level) {
        return true;
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.putFloat(HBMKey.PROGRESS, progress);
        tag.putBoolean(HBMKey.RUNNING, processing);
        return tag;
    }

    @Override
    public void handleClientPacket(@NotNull CompoundTag tag) {
        super.handleClientPacket(tag);
        this.progress = tag.getFloat(HBMKey.PROGRESS);
        this.processing = tag.getBoolean(HBMKey.RUNNING);
    }

    @Override
    public void provideInfo(ItemMachineUpgrade.UpgradeType type, int level, List<Component> info) {
        info.add(IUpgradeInfoProvider.getStandardLabel(ModBlocks.MACHINE_ORE_SLOPPER.get()));
        if(type == ItemMachineUpgrade.UpgradeType.SPEED) {
            info.add(HBMLang.UPGRADE_DELAY.translate("-" + (level * 25) + "%").withStyle(ChatFormatting.GREEN));
            info.add(HBMLang.UPGRADE_CONSUMPTION.translate("+" + (level * 50) + "%").withStyle(ChatFormatting.RED));
        }
        if(type == ItemMachineUpgrade.UpgradeType.EFFECT) {
            info.add(HBMLang.UPGRADE_EFFICIENCY.translate("+" + (level * 10) + "%").withStyle(ChatFormatting.GREEN));
            info.add(HBMLang.UPGRADE_CONSUMPTION.translate("+" + (level * 100) + "%").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public Map<ItemMachineUpgrade.UpgradeType, Integer> getValidUpgrades() {
        HashMap<ItemMachineUpgrade.UpgradeType, Integer> upgrades = new HashMap<>();
        upgrades.put(ItemMachineUpgrade.UpgradeType.SPEED, 3);
        upgrades.put(ItemMachineUpgrade.UpgradeType.EFFECT, 3);
        return upgrades;
    }

    public enum SlopperAnimation {
        LOWERING, LIFTING, MOVE_SHREDDER, DUMPING, MOVE_BUCKET
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putFloat(HBMKey.PROGRESS, this.progress);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(HBMKey.PROGRESS, Tag.TAG_FLOAT))
            this.progress = nbt.getFloat(HBMKey.PROGRESS);
    }
}
