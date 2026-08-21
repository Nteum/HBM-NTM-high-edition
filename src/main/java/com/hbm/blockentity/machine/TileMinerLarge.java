package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.api.Mode;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.api.fluid.SingleFluidHandler;
import com.hbm.block.decoriate.BlockOre;
import com.hbm.block.env.BedRockOre;
import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.DummyableBE;
import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.config.ConfigWorld;
import com.hbm.gui.menu.MenuMinerLarge;
import com.hbm.item.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.item.misc.ItemDrillbit;
import com.hbm.item.misc.ItemDrillbit.EnumDrillType;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModTags;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.InventoryUtils;
import com.hbm.utils.WorldUtils;
import com.hbm.utils.math.BitUtil;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.utils.tool.HarvestUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TileMinerLarge extends DummyableBE implements IUpgradeInfoProvider {
    public static final long BASE_CONSUMPTION = 10_000L;
    public static final long MAX_POWER = 1_000_000;
    public static final int MAX_TANK_CAPACITY = 16_000;
    public static final Map<UpgradeType, Integer> VALID_UPGRADES = Map.of(UpgradeType.SPEED, 3, UpgradeType.EFFECT, 3, UpgradeType.POWER, 3);
    public static final int KEY_OPERATIONAL = 0;
    public static final int KEY_ENABLE_DRILL = 1;
    public static final int KEY_ENABLE_CRUSHER = 2;
    public static final int KEY_ENABLE_WALLING = 3;
    public static final int KEY_ENABLE_VEINMINER = 4;
    public static final int KEY_ENABLE_SILKTOUCH = 5;
    public static final int KEY_BEDROCK_DRILLING = 6;
    private final SingleFluidHandler fluidHandler;
    private BasicEnergyContainer energyContainer;
    private ItemStackHandler itemStackHandler = new ItemStackHandler(14){
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            boolean result = true;
            if (slot == 0) result = stack.is(ModTags.Items.BATTERY) ;
            else if (slot == 1) result = false;
            else if (slot >= 2 && slot < 4) result = stack.is(ModTags.Items.UPGRADE);
            return result && super.isItemValid(slot, stack);
        }
    };
    private Map<UpgradeType, Integer> upgrades;
    public long consumption = BASE_CONSUMPTION;
    private byte machineState = 0;
    public double speed = 1.0D;
    protected int ticksWorked = 0;
    protected int drillHeight;      // 钻头所在的纵坐标
    private int ring = 0;
    private BlockPos bedrockOrePos;
    // 客户端变量
    public float drillRotation = 0F;
    public float prevDrillRotation = 0F;
    public float drillExtension = 0F;
    public float prevDrillExtension = 0F;
    public float crusherRotation = 0F;
    public float prevCrusherRotation = 0F;
    public int chuteTimer = 0;

    protected ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> machineState;
                case 1 -> (int) energyContainer.getEnergy();    // 机器电能储量在int范围值内
                case 2 -> (int) consumption;
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
    public TileMinerLarge(BlockPos pos, BlockState state) {
        super(HBMTiles.TILE_MINER_LARGE.get(), pos, state);
        this.multiblockData = MultiblockData.mapping.get(ModBlocks.MINER_LARGE.get());
        this.fluidHandler = new SingleFluidHandler(MAX_TANK_CAPACITY, Mode.INPUT);
        this.energyContainer = new BasicEnergyContainer(MAX_POWER);
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluidHandler);
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(this.energyContainer));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this.itemStackHandler);
        this.drillHeight = pos.getY() - 4;
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuMinerLarge(pContainerId,pInventory,this,this.containerData);
    }

    @Override
    public Component getDefaultName() {
        return HBMLang.CONTAINER_MINER_LARGE.translate();
    }

    @Override
    protected void onUpdateServer() {
        boolean shouldSync = false;
        super.onUpdateServer();
        upgrades = IUpgradeInfoProvider.getUpgradeNow(upgrades, this, itemStackHandler, 2,3);
        int speedLevel = upgrades.getOrDefault(UpgradeType.SPEED, 0);
        int powerLevel = upgrades.getOrDefault(UpgradeType.POWER, 0);
        int radiusLevel = upgrades.getOrDefault(UpgradeType.EFFECT, 0);
        TransmitUtils.dischargeItem(this, itemStackHandler.getStackInSlot(0));
        EnumDrillType type = this.getInstalledDrill();

        consumption = BASE_CONSUMPTION * (1 + speedLevel) / (1 + powerLevel);

        if (this.level != null && this.level.getGameTime() % 20 == 0){
            // 试图向临近的箱子中输出物品
            IItemHandler outputItemHandler = getOutputItemHandler();
            if (outputItemHandler != null){
                InventoryUtils.insertNoCheckSlots(new RangedWrapper(this.itemStackHandler, 5, 14), outputItemHandler, 5);
            }
        }

        if(chuteTimer > 0) {
            chuteTimer--;
            shouldSync = true;
        }

        int maxDrillHeight = this.worldPosition.getY() - 4;     // 钻头默认位置
        int minHeight = this.level.dimensionType().minY();          // 世界最低位置
        int maxDepth = this.worldPosition.getY() - minHeight - 5;   // 最大挖掘深度
        int radius = 1 + radiusLevel * 2;                           // 挖掘半径

        this.running = BitUtil.getBool(this.machineState, KEY_ENABLE_DRILL) && type != null && this.energyContainer.getEnergy() >= this.getPowerConsumption();
        if (this.running){
            this.energyContainer.extract(this.getPowerConsumption(), false);
            this.speed = type.speed * (1 + speedLevel / 2D);
            boolean canDrillDown = true;   // 钻头是否能向下移动
            if (drillHeight == maxDrillHeight) radius = 1;
            int xCoord = this.worldPosition.getX();
            int zCoord = this.worldPosition.getZ();
            int y = this.drillHeight - 1;
            float combinedHardness = 0F;
            boolean isBedrockOreDrilling = BitUtil.getBool(this.machineState, KEY_BEDROCK_DRILLING) && bedrockOrePos != null && level.getBlockState(bedrockOrePos).getBlock() instanceof BedRockOre;
            BlockPos dirllPos = new BlockPos(xCoord, y, zCoord);
            boolean shouldCollectDrops = false;
            if (isBedrockOreDrilling){
                combinedHardness = 60 * 20;
                if (ConfigWorld.newBedrockOres.get()) combinedHardness *= 5;
                int ticksToWork = (int) Math.ceil(combinedHardness / this.speed);
                ticksWorked++;
                if (ticksWorked >= ticksToWork){
                    shouldCollectDrops = true;
                    List<ItemStack> itemStacks = drillBedrock();
                    List<ItemStack> remains = insertItemsAndOutput(itemStacks);
                    if (!remains.isEmpty()){
                        Vec3 center = dirllPos.getCenter();
                        for (ItemStack stack : remains) {
                            ItemEntity itemEntity = new ItemEntity(this.getLevel(), center.x, center.y, center.z, stack);
                            itemEntity.setDeltaMovement(level.random.nextFloat()*0.5f,0.1,level.random.nextFloat()*0.5f);
                            this.getLevel().addFreshEntity(itemEntity);
                        }
                    }
                }
            }else {
                if (ring > radius) ring = 0;    // 防止突然改变挖掘范围
                // 圈层挖掘
                outer: while (ring <= radius + 1){
                    boolean ringBlank = true;
                    for(int x = xCoord - ring; x <= xCoord + ring; x++) {
                        for(int z = zCoord - ring; z <= zCoord + ring; z++) {
                            /* Process blocks either if we are in the inner ring (1 = 3x3) or if the target block is on the outer edge */
                            if(ring == 1 || (x == xCoord - ring || x == xCoord + ring || z == zCoord - ring || z == zCoord + ring)) {
                                BlockPos pos = new BlockPos(x, y, z);
                                BlockState blockState = this.level.getBlockState(pos);
                                if(shouldIgnoreBlock(blockState)) continue;
                                else if (blockState.is(ModBlocks.DEPTH_STONE.get())){
                                    this.machineState = (byte) BitUtil.setBool(this.machineState, KEY_ENABLE_DRILL, false);
                                }else if (blockState.getBlock() instanceof BlockOre){
                                    bedrockOrePos = pos;
                                    this.machineState = (byte) BitUtil.setBool(this.machineState, KEY_BEDROCK_DRILLING, true);
                                    this.machineState = (byte) BitUtil.setBool(this.machineState, KEY_ENABLE_CRUSHER, false);
                                    break outer;
                                }
                                ringBlank = false;
                                combinedHardness += blockState.getDestroySpeed(this.level, pos);    // 代替旧版的getBlockHardness
                            }
                        }
                    }
                    if(!ringBlank) {    // 如果环状采矿有矿需要采，则进行采矿
                        ticksWorked++;
                        int ticksToWork = (int) Math.ceil(combinedHardness / this.speed);
                        if(ticksWorked >= ticksToWork) {
                            shouldCollectDrops = true;
                            // 破坏方块
                            for(int x = xCoord - ring; x <= xCoord + ring; x++) {
                                for(int z = zCoord - ring; z <= zCoord + ring; z++) {
                                    if(ring == 1 || (x == xCoord - ring || x == xCoord + ring || z == zCoord - ring || z == zCoord + ring)) {
                                        BlockPos pos = new BlockPos(x, y, z);
                                        BlockState blockState = level.getBlockState(pos);
                                        if(!this.shouldIgnoreBlock(blockState)) {
                                            if(blockState.is(Tags.Blocks.ORES)) {
                                                if(BitUtil.getBool(this.machineState, KEY_ENABLE_VEINMINER) && this.getInstalledDrill().vein) {
                                                    HarvestUtil.performMachineHarvest((ServerLevel) this.level, dirllPos, pos, HarvestUtil.HarvestContext.chain(128), this.getItemStackHandler(), 0, BitUtil.getBool(this.machineState, KEY_ENABLE_SILKTOUCH));
                                                }else
                                                    HarvestUtil.performMachineHarvest((ServerLevel) this.level, dirllPos, pos, HarvestUtil.HarvestContext.single(), this.getItemStackHandler(), 0, BitUtil.getBool(this.machineState, KEY_ENABLE_SILKTOUCH));
                                            }
                                            if (ring <= radius)
                                                HarvestUtil.performMachineHarvest((ServerLevel) this.level, dirllPos, pos, HarvestUtil.HarvestContext.single(), this.getItemStackHandler(), 0, BitUtil.getBool(this.machineState, KEY_ENABLE_SILKTOUCH));
                                        }
                                    }
                                }
                            }
                            ticksWorked = 0;
                        }
                        break;
                    }
                    ring ++;
                }
                // 边界条件
                if (ring >= radius + 1){
                    ring = 0;
                    // 如果还没挖到底就继续钻探
                    if (this.drillHeight > minHeight) this.drillHeight--;
                    else {
                        // 如果已经挖到底了而且没有基岩矿则停止钻探
                        if (bedrockOrePos == null) this.machineState = (byte) BitUtil.setBool(this.machineState, KEY_ENABLE_DRILL, false);// 钻到底了就可以停止钻探
                    }
                    // 填充边缘方块并清除流体方块
                    boolean isWalling = BitUtil.getBool(this.machineState, KEY_ENABLE_WALLING);
                    for(int x = xCoord - ring; x <= xCoord + ring; x++) {
                        for (int z = zCoord - ring; z <= zCoord + ring; z++) {
                            BlockPos concernPos = new BlockPos(x, y, z);
                            BlockState blockState = this.level.getBlockState(concernPos);
                            if(x == xCoord - ring || x == xCoord + ring || z == zCoord - ring || z == zCoord + ring) {
                                if (blockState.canBeReplaced() && (isWalling || !blockState.getFluidState().isEmpty())){
                                    // 让沙袋有一定概率发光
                                    if (this.level.random.nextInt(8) == 0) {
                                        this.level.setBlock(concernPos, ModBlocks.SAND_BAG.get().defaultBlockState().setValue(BlockStateProperties.LIT, true), 3);
                                    }else
                                        this.level.setBlock(concernPos, ModBlocks.SAND_BAG.get().defaultBlockState(), 3);
                                }
                            }else {
                                if (!blockState.getFluidState().isEmpty()) this.level.setBlock(concernPos, Blocks.AIR.defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }
            if (shouldCollectDrops){
                this.chuteTimer = 40;
                // 收集掉落物
                List<ItemEntity> entities = this.level.getEntities(EntityTypeTest.forClass(ItemEntity.class), new AABB(dirllPos).inflate(ring + 1, 0, ring + 1), entity -> true);
                IItemHandler itemHandler = getOutputItemHandler();
                for (ItemEntity itemEntity : entities) {
                    ItemStack itemStack = itemEntity.getItem();
                    ItemStack insertResult = ItemHandlerHelper.insertItemStacked(this.itemStackHandler, itemStack.copy(), false);
                    if (!insertResult.isEmpty() && itemHandler != null) insertResult = ItemHandlerHelper.insertItemStacked(itemHandler, insertResult.copy(), false);
                    if (insertResult.isEmpty()) itemEntity.discard();
                    else itemEntity.setItem(insertResult);
                }
            }
            shouldSync = true;
        }else {
            // 收回钻头。
            if (this.drillHeight < maxDrillHeight){
                this.drillHeight ++;
                shouldSync = true;
            }
        }
        if (shouldSync)
            sendUpdatePacket();
    }


    private IItemHandler getOutputItemHandler(){
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Vec3i vec3i = DirectionUtils.offsetRot(new Vec3i(0, -4, 4), Direction.SOUTH, facing);
        BlockEntity blockEntity = WorldUtils.getTileEntity(this.level, this.worldPosition.offset(vec3i));
        return blockEntity == null ? null : blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, facing.getOpposite()).orElse(null);
    }

    private List<ItemStack> drillBedrock(){
        EnumDrillType type = this.getInstalledDrill();
        List<ItemStack> stacks = new ArrayList<>();
        // 挖掘基岩矿
//        BedRockOre.TileBedrockOre ore = WorldUtils.getTileEntity(BedRockOre.TileBedrockOre.class, this.level, bedrockOrePos);
//        if (ore == null || ore.resource != null && ore.tier <= type.tier) return stacks;
//        FluidStack fluidInTank = this.fluidHandler.getFluidInTank(0);
//        if (ore.acidRequirement != null){
//            if (this.fluidHandler.drain(ore.acidRequirement, IFluidHandler.FluidAction.SIMULATE).getAmount() < ore.acidRequirement.getAmount()) return stacks;
//            this.fluidHandler.drain(ore.acidRequirement, IFluidHandler.FluidAction.EXECUTE);
//        }
//        ItemStack stack = ore.resource.copy();
//        stacks.add(stack);

//        if(stack.getItem() == ModItems.bedrock_ore_base) {
//            ItemBedrockOreBase.setOreAmount(worldObj, stack, pos.getX(), pos.getZ(), 1D + this.getInstalledDrill().fortune * 0.1D);
//        }
        return stacks;
    }

    private List<ItemStack> insertItemsAndOutput(List<ItemStack> itemsToCollect){
        if (itemsToCollect.isEmpty()) return itemsToCollect;
        IItemHandler itemHandler = getOutputItemHandler();
        if (itemHandler != null){
            InventoryUtils.insertNoCheckSlots(this.itemStackHandler, itemHandler);
        }
        List<ItemStack> remainItems = new ArrayList<>();
        for (ItemStack stack : itemsToCollect) {
            ItemStack insertResult = ItemHandlerHelper.insertItemStacked(this.itemStackHandler, stack, false);
            ItemStack stack1 = stack.copyWithCount(stack.getCount() - insertResult.getCount());
            if (!stack1.isEmpty()) remainItems.add(stack1);
        }
        return remainItems;
    }

    public boolean shouldIgnoreBlock(BlockState blockState) {
        return blockState.canBeReplaced() || blockState.is(Blocks.BEDROCK);
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        int targetDepth = this.worldPosition.getY() - this.drillHeight - 4;
        boolean enableCrusher = BitUtil.getBool(this.machineState, KEY_ENABLE_CRUSHER);
        //
        this.prevDrillExtension = this.drillExtension;

        if(this.drillExtension != targetDepth) {
            float diff = Math.abs(this.drillExtension - targetDepth);
            float speed = Math.max(0.15F, diff / 10F);

            if(diff <= speed) {
                this.drillExtension = targetDepth;
            } else {
                float sig = Math.signum(this.drillExtension - targetDepth);
                this.drillExtension -= sig * speed;
            }
        }

        this.prevDrillRotation = this.drillRotation;
        this.prevCrusherRotation = this.crusherRotation;

        if(this.running) {
            this.drillRotation += 15F;

            if(enableCrusher) {
                this.crusherRotation += 15F;
            }
        }

        if(this.drillRotation >= 360F) {
            this.drillRotation -= 360F;
            this.prevDrillRotation -= 360F;
        }

        if(this.crusherRotation >= 360F) {
            this.crusherRotation -= 360F;
            this.prevCrusherRotation -= 360F;
        }
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.put(HBMKey.FLUIDS, this.fluidHandler.serializeNBT());
        tag.putInt("chute", this.chuteTimer);
        tag.putInt("drill_height", this.drillHeight);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        this.fluidHandler.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        this.chuteTimer = tag.getInt("chute");
        this.drillHeight = tag.getInt("drill_height");
    }

    @Override
    public void handleClientPacket(@NotNull CompoundTag tag) {
        super.handleClientPacket(tag);
        if (tag.contains(HBMKey.STATE, Tag.TAG_BYTE)){
            this.machineState = tag.getByte(HBMKey.STATE);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putByte(HBMKey.STATE, machineState);
        pTag.put(HBMKey.ITEM, this.itemStackHandler.serializeNBT());
        pTag.put(HBMKey.FLUIDS, this.fluidHandler.serializeNBT());
        pTag.put(HBMKey.ENERGY, this.energyContainer.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.machineState = nbt.getByte(HBMKey.STATE);
        this.itemStackHandler.deserializeNBT(nbt.getCompound(HBMKey.ITEM));
        this.fluidHandler.deserializeNBT(nbt.getCompound(HBMKey.FLUIDS));
        this.energyContainer.deserializeNBT(nbt.getCompound(HBMKey.ENERGY));
    }
    @Override
    public boolean canProvideInfo(UpgradeType type, int level) {
        return VALID_UPGRADES.containsKey(type) && level <= VALID_UPGRADES.get(type);
    }

    @Override
    public void provideInfo(UpgradeType type, int level, List<Component> info) {
        info.add(IUpgradeInfoProvider.getStandardLabel(ModBlocks.MINER_LARGE.get()));
        if(type == UpgradeType.SPEED) {
            info.add(HBMLang.UPGRADE_DELAY.translate("-" + (100 - 200 / (level + 2)) + "%").withStyle(ChatFormatting.GREEN));
            info.add(HBMLang.UPGRADE_CONSUMPTION.translate("+" + (level * 100) + "%").withStyle(ChatFormatting.RED));
        }
        if(type == UpgradeType.POWER) {
            info.add(HBMLang.UPGRADE_CONSUMPTION.translate("-" + (100 - 100 / (level + 1)) + "%").withStyle(ChatFormatting.GREEN));
        }
    }

    @Override
    public Map<UpgradeType, Integer> getValidUpgrades() {
        return VALID_UPGRADES;
    }

    public ItemStackHandler getItemStackHandler(){
        return this.itemStackHandler;
    }

    public IFluidHandler getFluidHandler(){
        return this.fluidHandler;
    }

    public ItemDrillbit.EnumDrillType getInstalledDrill(){
        ItemStack stackInSlot = this.itemStackHandler.getStackInSlot(4);
        if (stackInSlot.getItem() instanceof ItemDrillbit itemDrillbit) return itemDrillbit.getType();
        return null;
    }

    public long getPowerConsumption(){
        return consumption;
    }

    public boolean isEnableCrusher(){
        return BitUtil.getBool(this.machineState, KEY_ENABLE_CRUSHER);
    }
}
