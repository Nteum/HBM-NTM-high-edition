package com.hbm.blockentity.machine.tokamak;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.api.Mode;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.HybridEnergyStorage;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.api.inventory.ModeBuilder;
import com.hbm.api.math.MathUtils;
import com.hbm.block.machine.tokamak.TokamakCoilBlock;
import com.hbm.block.machine.tokamak.TokamakHeaterBlock;
import com.hbm.block.machine.tokamak.TokamakInjectorBlock;
import com.hbm.block.machine.tokamak.TokamakPortBlock;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.registries.HBMCaps;
import com.hbm.gui.menu.TokamakMenu;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 托卡马克控制器方块实体。
 * - 负责多方块结构校验（环形线圈+外壳）
 * - 简化的等离子体约束与能量学计算
 * - 管理 D/T 燃料与冷却剂
 * - 提供 GUI 数据与客户端渲染参数
 */
public class TokamakControllerBlockEntity extends BaseMachineBlockEntity implements MenuProvider {

    // 结构尺寸（可在 dev 环境调整）
    public static final double R0 = 4.0D;      // 主半径，环中心到环面中心的距离
    public static final double A_MINOR = 1.6D; // 小半径，等离子体截面半径

    // 物理常数（简化版，偏可玩性取向）
    private static final int COIL_SAMPLES = 24;
    private static final double COIL_RING_TOLERANCE = 0.45D;
    private static final int MIN_COILS_REQUIRED = 16;
    private static final double K_B = 0.75D;                   // 线圈 -> B 场比例系数
    private static final double K_FUSION = 2.0E-9D;             // 聚变功率系数，n*T^2*V（单位：HE/t）
    private static final double IGNITION_THRESHOLD = 1_500.0D;  // 约束评分阈值
    private static final double MIN_IGNITION_TEMPERATURE = 10_000.0D; // 最低点火温度（K）
    private static final double MELTDOWN_TEMPERATURE = 5.0E6D;  // 熔毁温度（K）
    private static final double BASE_HEATER_POWER = 1_200.0D;   // 单个加热器提供的功率（K/t）
    private static final double BASE_COOLING = 6_000.0D;        // 每 tick 最大冷却量（由冷却槽提供）
    private static final double COOLANT_PER_ITEM = BASE_COOLING * 40.0D; // 每个冷却物品提供的“冷却储备”
    private static final double PASSIVE_COOLING = 50.0D;        // 被动散热
    private static final double DENSITY_DECAY = 0.0020D;        // 等离子体自耗散
    private static final double DENSITY_GAIN = 0.0006D;         // 运行时密度增长
    private static final double MAX_DENSITY = 10.0D;
    private static final int COOLANT_MB_PER_TICK = 50;
    private static final double COOLING_PER_MB = 120.0D;        // 1 mB 冷却剂可带走的热量

    // 状态参数（对 GUI/渲染开放）
    private double temperatureK = 300;   // 等离子体温度
    private double density = 0;          // 粒子密度 (arb.)
    private double bField = 0;           // 合成磁场强度
    private double confinementScore = 0; // 约束评分
    private double instability = 0;      // 不稳定度，>1 触发失稳
    private boolean structureValid = false;
    private boolean burning = false;
    private boolean manualLock = false;  // 手动停机锁，GUI 按钮设置
    private int recheckTicker = 0;

    // 燃料与冷却缓存（体积单位，简化处理）
    private double fuelD = 0;
    private double fuelT = 0;
    private double coolantBuffer = 0;

    // 能量输出缓存
    private final BasicEnergyContainer energy = new BasicEnergyContainer(10_000_000);
    // 流体：0 冷却剂输入 1 废蒸汽输出
    private final BasicFluidHandler fluids = new BasicFluidHandler()
            .addTank(16_000, Mode.INPUT)
            .addTank(16_000, Mode.OUTPUT);
    private final LazyOptional<BasicFluidHandler> fluidOptional = LazyOptional.of(() -> fluids);

    public TokamakControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.TOKAMAK_CONTROLLER.get(), pos, state);
        // 0:D槽 1:T槽 2:冷却剂 3:预留控制槽 4:副产物输出 5:电池充电槽
        this.items = NonNullList.withSize(6, ItemStack.EMPTY);
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this);
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(energy));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ENERGY, new HybridEnergyStorage(energy));
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, fluids);
        this.energy.setListener(this);
        this.slotModes = new ModeBuilder().addModes(
                3, Mode.INPUT,
                1, Mode.NONE,
                1, Mode.OUTPUT,
                1, Mode.BOTH
        ).get();
        // 冷却剂输入仅限 COOLANT/水；废蒸汽只允许自身类型
        this.fluids.getFluidTanks().set(0, new FluidTank(16_000) {
            @Override
            public boolean isFluidValid(final FluidStack stack) {
                return stack.getFluid().isSame(ModFluids.COOLANT.source().get()) || stack.getFluid().isSame(ModFluids.IRRADIATED_WATER.source().get()) || stack.getFluid().isSame(net.minecraft.world.level.material.Fluids.WATER);
            }
        });
        this.fluids.getFluidTanks().set(1, new FluidTank(16_000) {
            @Override
            public boolean isFluidValid(final FluidStack stack) {
                return stack.getFluid().isSame(ModFluids.SPENT_STEAM.source().get());
            }
        });
    }

    // GUI 同步数据：B、T、n、评分、能量、状态位
    private final ContainerData containerData = new SimpleContainerData(6) {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> MathUtils.clampToInt(bField * 100);       // B*100
                case 1 -> MathUtils.clampToInt(temperatureK / 100); // 温度压缩
                case 2 -> MathUtils.clampToInt(density * 10_000);   // 密度放大
                case 3 -> MathUtils.clampToInt(confinementScore * 100);
                case 4 -> MathUtils.clampToInt(energy.getEnergy());
                case 5 -> burning ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // 客户端 -> 服务端交互由按键处理，不在数据槽写入
        }
    };

    @Override
    protected void onUpdateServer() {
        Level level = getLevel();
        if (level == null) return;

        if (++recheckTicker % 20 == 0) {
            structureValid = validateStructure();
        }

        // 补充燃料与冷却剂（先做，保证本 tick 生效）
        refuelFromItems();
        refillCoolant();

        // 线圈磁场与外部条件统计
        double totalCoilCurrent = computeCoilCurrent(level);
        this.bField = K_B * totalCoilCurrent / (Math.max(R0, 0.1D) * COIL_SAMPLES);
        double heaterPower = computeHeaterPower(level);
        double volume = torusVolume();

        // 加热：外部加热 + 上一 tick 的聚变余热
        double heating = heaterPower;
        // 冷却：消耗冷却物品或被动散热
        double cooling = computeCooling();

        // 温度与密度演化
        temperatureK = Math.max(0, temperatureK + heating - cooling);
        density = Math.max(0, density * (1 - DENSITY_DECAY));

        // 更新约束评分，决定点火/停机
        confinementScore = bField * Math.sqrt(Math.max(temperatureK, 0));
        boolean hasFuel = fuelD > 0.0D && fuelT > 0.0D;
        if (manualLock || !structureValid || !hasFuel || temperatureK < MIN_IGNITION_TEMPERATURE) {
            burning = false;
        }
        boolean canIgnite = structureValid
                && hasFuel
                && temperatureK >= MIN_IGNITION_TEMPERATURE
                && confinementScore >= IGNITION_THRESHOLD
                && !manualLock;
        if (!burning && canIgnite) {
            burning = true;
        }

        double fusionPower = 0;
        if (burning) {
            fusionPower = K_FUSION * density * temperatureK * temperatureK * volume;
            // 消耗燃料，保持 D/T 比例
            double fuelUse = 0.0005D + fusionPower * 1.0E-7D;
            fuelD = Math.max(0, fuelD - fuelUse);
            fuelT = Math.max(0, fuelT - fuelUse);
            density = Math.min(MAX_DENSITY, density + DENSITY_GAIN);
            temperatureK += fusionPower / 5000.0D; // 聚变带来的自加热
            // 产出能量
            long toStore = (long) Math.min(fusionPower, energy.getNeeded());
            if (toStore > 0) {
                energy.receive(toStore, false);
            } else {
                instability += 0.05D;
            }
        }

        // 稳定性评估：冷却不足/磁场不足/输出受阻会导致不稳定
        instability += computeInstability(fusionPower);
        instability = Math.max(0, instability - 0.005D); // 自愈部分

        boolean overheated = temperatureK > MELTDOWN_TEMPERATURE;
        boolean unstable = burning && instability > 1.2D;
        if (overheated || unstable) {
            triggerMeltdown();
        }

        TransmitUtils.outputOnly(this);
        TransmitUtils.chargeItem(this, this.items.get(5));
        this.networkPackNT(64);
        setChanged();
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        // 客户端只需要平滑动画，具体渲染由 BER 读取字段
    }

    /**
     * 校验是否存在一个以控制器为圆心的环状线圈。
     * 扫描一个厚度带（|r-R0|<=tolerance）以容忍方块栅格误差，避免必须照抄严格坐标。
     */
    public boolean validateStructure() {
        Level level = getLevel();
        if (level == null) {
            return false;
        }

        int scanRadius = Mth.ceil(R0 + 1.0D);
        int ringRadius = (int) Math.round(R0);
        int coils = 0;
        for (int dx = -scanRadius; dx <= scanRadius; dx++) {
            for (int dz = -scanRadius; dz <= scanRadius; dz++) {
                if (!isCoilRingPosition(dx, dz, ringRadius)) {
                    continue;
                }
                BlockPos pos = worldPosition.offset(dx, 0, dz);
                if (level.getBlockState(pos).getBlock() instanceof TokamakCoilBlock) {
                    coils++;
                }
            }
        }

        return coils >= MIN_COILS_REQUIRED;
    }

    private double computeCoilCurrent(Level level) {
        double sum = 0.0D;
        int scanRadius = Mth.ceil(R0 + 1.0D);
        int ringRadius = (int) Math.round(R0);
        for (int dx = -scanRadius; dx <= scanRadius; dx++) {
            for (int dz = -scanRadius; dz <= scanRadius; dz++) {
                if (!isCoilRingPosition(dx, dz, ringRadius)) {
                    continue;
                }
                BlockPos pos = worldPosition.offset(dx, 0, dz);
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof TokamakCoilBlock) {
                    int strength = state.getValue(TokamakCoilBlock.STRENGTH);
                    sum += 50.0D * strength;
                }
            }
        }
        return sum;
    }

    private static boolean isCoilRingPosition(final int dx, final int dz, final int ringRadius) {
        if (dx == 0 && dz == 0) {
            return false;
        }
        int chebyshev = Math.max(Math.abs(dx), Math.abs(dz));
        if (chebyshev == ringRadius) {
            return true;
        }
        double radius = Math.sqrt((double) dx * dx + (double) dz * dz);
        return Math.abs(radius - R0) <= COIL_RING_TOLERANCE;
    }

    private double computeHeaterPower(Level level) {
        double heaters = 0;
        for (BlockPos p : BlockPos.withinManhattan(worldPosition, 3, 3, 3)) {
            BlockState state = level.getBlockState(p);
            if (state.getBlock() instanceof TokamakHeaterBlock) {
                heaters += state.getValue(TokamakHeaterBlock.ACTIVE) ? 1D : 0.25D; // 未激活提供少量预热
            }
        }
        return heaters * BASE_HEATER_POWER;
    }

    private double computeCooling() {
        double cooling = PASSIVE_COOLING;
        if (coolantBuffer > 0.0D) {
            double used = Math.min(coolantBuffer, BASE_COOLING);
            cooling += used;
            coolantBuffer -= used;
        }
        return cooling;
    }

    private double drainCoolantFromTank() {
        FluidTank coolant = fluids.getFluidTanks().get(0);
        FluidTank steam = fluids.getFluidTanks().get(1);
        if (coolant.isEmpty()) {
            return 0.0D;
        }
        int steamSpace = steam.getCapacity() - steam.getFluidAmount();
        if (steamSpace <= 0) {
            return 0.0D;
        }
        int mb = Math.min(COOLANT_MB_PER_TICK, Math.min(coolant.getFluidAmount(), steamSpace));
        if (mb <= 0) {
            return 0.0D;
        }
        coolant.drain(mb, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        steam.fill(new FluidStack(ModFluids.SPENT_STEAM.source().get(), mb), net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        return mb * COOLING_PER_MB;
    }

    private void refuelFromItems() {
        Item deuteriumCell = ModItems.CELL_DEUTERIUM.get();
        Item tritiumCell = ModItems.CELL_TRITIUM.get();
        Item emptyCell = ModItems.CELL_EMPTY.get();
        ItemStack dStack = this.items.get(0);
        ItemStack tStack = this.items.get(1);
        if (fuelD < 1.0D && dStack.is(deuteriumCell)) {
            fuelD += 1.0D;
            dStack.shrink(1);
            pushByproduct(new ItemStack(emptyCell));
        }
        if (fuelT < 1.0D && tStack.is(tritiumCell)) {
            fuelT += 1.0D;
            tStack.shrink(1);
            pushByproduct(new ItemStack(emptyCell));
        }
        this.items.set(0, dStack);
        this.items.set(1, tStack);
    }

    private void refillCoolant() {
        ItemStack coolant = this.items.get(2);
        if (coolantBuffer < BASE_COOLING && !coolant.isEmpty()) {
            coolantBuffer += COOLANT_PER_ITEM;
            coolant.shrink(1);
            this.items.set(2, coolant);
        }
    }

    private double computeInstability(double fusionPower) {
        if (!burning) {
            return 0.0D;
        }
        double instab = 0.0D;
        if (!structureValid) {
            instab += 0.25D;
        }
        if (temperatureK >= MIN_IGNITION_TEMPERATURE) {
            if (coolantBuffer <= 0.0D) {
                instab += 0.03D;
            }
            if (bField < 4.0D) {
                instab += 0.05D;
            }
            if (fusionPower > 0.0D && energy.getNeeded() <= 0) {
                instab += 0.05D;
            }
        }
        return instab;
    }

    private double torusVolume() {
        // V = 2*pi^2 * R0 * a^2
        return 2 * Math.PI * Math.PI * R0 * A_MINOR * A_MINOR;
    }

    private void triggerMeltdown() {
        if (level == null || level.isClientSide) {
            return;
        }
        burning = false;
        instability = 0;
        // 销毁一定范围的方块并产生爆炸 + 熔融堆芯（岩浆）
        level.explode(null, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D, 6.0F, Level.ExplosionInteraction.BLOCK);
        for (BlockPos p : BlockPos.withinManhattan(worldPosition, 2, 2, 2)) {
            BlockState state = level.getBlockState(p);
            if (!state.isAir()) {
                level.destroyBlock(p, false);
            }
        }
        level.setBlock(worldPosition, Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            final BlockPos firePos = worldPosition.relative(direction);
            if (level.getBlockState(firePos).isAir() && level.getBlockState(firePos.below()).isSolidRender(level, firePos.below())) {
                level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        temperatureK = 1500;
        density = 0;
        fuelD = fuelT = 0;
    }

    private void pushByproduct(final ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        final int outputSlot = 4;
        final ItemStack existing = this.items.get(outputSlot);
        if (existing.isEmpty()) {
            this.items.set(outputSlot, stack);
            return;
        }
        if (!ItemStack.isSameItemSameTags(existing, stack)) {
            dropStack(stack);
            return;
        }
        int max = Math.min(existing.getMaxStackSize(), this.getMaxStackSize());
        int space = max - existing.getCount();
        if (space <= 0) {
            dropStack(stack);
            return;
        }
        int toAdd = Math.min(space, stack.getCount());
        existing.grow(toAdd);
        stack.shrink(toAdd);
        this.items.set(outputSlot, existing);
        if (!stack.isEmpty()) {
            dropStack(stack);
        }
    }

    private void dropStack(final ItemStack stack) {
        if (level == null || stack.isEmpty()) {
            return;
        }
        Containers.dropItemStack(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 1.0D, worldPosition.getZ() + 0.5D, stack);
    }

    //==================== 数据序列化 ====================//
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.temperatureK = tag.getDouble("temp");
        this.density = tag.getDouble("density");
        this.bField = tag.getDouble("bField");
        this.confinementScore = tag.getDouble("score");
        this.instability = tag.getDouble("instability");
        this.fuelD = tag.getDouble("fuelD");
        this.fuelT = tag.getDouble("fuelT");
        this.coolantBuffer = tag.getDouble("coolant");
        this.burning = tag.getBoolean("burning");
        this.manualLock = tag.getBoolean("manualLock");
        this.energy.deserializeNBT(tag.getCompound(HBMKey.ENERGY));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("temp", temperatureK);
        tag.putDouble("density", density);
        tag.putDouble("bField", bField);
        tag.putDouble("score", confinementScore);
        tag.putDouble("instability", instability);
        tag.putDouble("fuelD", fuelD);
        tag.putDouble("fuelT", fuelT);
        tag.putDouble("coolant", coolantBuffer);
        tag.putBoolean("burning", burning);
        tag.putBoolean("manualLock", manualLock);
        tag.put(HBMKey.ENERGY, this.energy.serializeNBT());
        tag.put(HBMKey.FLUIDS, this.fluids.serializeNBT());
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable(HBMLang.TOKAMAK.key());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new TokamakMenu(containerId, inventory, this, containerData);
    }

    //==================== 物品槽规则 ====================//
    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        Item deuteriumCell = ModItems.CELL_DEUTERIUM.get();
        Item tritiumCell = ModItems.CELL_TRITIUM.get();
        if (index == 0) return stack.is(deuteriumCell);
        if (index == 1) return stack.is(tritiumCell);
        if (index == 2) {
            return !stack.is(ModTags.Items.BATTERY)
                    && !stack.getCapability(HBMCaps.LONG_ENERGY).isPresent()
                    && !stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
        }
        if (index == 5) {
            return stack.is(ModTags.Items.BATTERY)
                    || stack.getCapability(HBMCaps.LONG_ENERGY).isPresent()
                    || stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
        }
        return false;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction side) {
        return canPlaceItem(index, stack);
    }

    //==================== 对客户端渲染的暴露接口 ====================//
    public float getPlasmaRadius() {
        return (float) Mth.clamp((temperatureK / MELTDOWN_TEMPERATURE) * 1.2F, 0.2F, 1.25F);
    }

    public float getBrightness() {
        return (float) Mth.clamp(confinementScore / (IGNITION_THRESHOLD * 2), 0, 1);
    }

    public float getSwirlSpeed() {
        return burning ? 0.8F : 0.2F;
    }

    public float getInstabilityFactor() {
        return (float) Mth.clamp(instability, 0, 1.5F);
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    /**
     * GUI 交互：手动启停。
     * @param start true 表示启动聚变；false 表示停机并加锁
     */
    public void setManualRunning(boolean start) {
        if (start) {
            this.manualLock = false;
        } else {
            this.manualLock = true;
            this.burning = false;
        }
        this.setChanged();
        this.sendUpdatePacket();
    }

    //==================== Capabilities 侧向约束 ====================//
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable Direction side) {
        if (side != null && level != null) {
            Block block = level.getBlockState(worldPosition.relative(side)).getBlock();
            boolean isPort = block instanceof TokamakPortBlock;
            boolean isInjector = block instanceof TokamakInjectorBlock;
            if (cap == ForgeCapabilities.FLUID_HANDLER || cap == HBMCaps.LONG_ENERGY || cap == ForgeCapabilities.ENERGY) {
                if (!isPort) {
                    return LazyOptional.empty();
                }
            } else if (cap == ForgeCapabilities.ITEM_HANDLER) {
                if (!isInjector) {
                    return LazyOptional.empty();
                }
            }
        }
        return super.getCapability(cap, side);
    }
}
