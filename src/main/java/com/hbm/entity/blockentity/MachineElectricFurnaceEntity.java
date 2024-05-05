package com.hbm.entity.blockentity;

import com.hbm.block.machine.MachineElectricFurnace;
import com.hbm.item.machine.MachineUpgrade;
import com.hbm.client.gui.ElectricFurnaceScreenHandler;
import com.hbm.item.ModItems;
import com.hbm.tag.ModTagKeys;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MachineElectricFurnaceEntity extends BlockEntity implements NamedScreenHandlerFactory , SidedInventory {
    public int progress;            // 要传递
    public int power;               //要传递
    public static final int maxPower = 100000;
    public int maxProgress = 100;
    public int consumption = 50;    //熔炉的耗电量
    private int cooldown = 0;       // 熔炉的冷却时间
    int cookTime;                   //熔炉配方制作时间
    int cookTimeTotal;              //熔炉配方制作总时间
    private static final int[] slots_io = new int[] { 0, 1, 2 };    //slot 0是被烧的物品，slot 1是燃料，slot 2是产物
    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);    //4个物品槽：原料、电池、产出、升级
    private final RecipeManager.MatchGetter<Inventory, ? extends AbstractCookingRecipe> matchGetter;//确定配方的参数，参考原版熔炉代码
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){                     //用于在服务器和客户端之间传递整数
        @Override
        public int get(int index) {
            switch (index){
                case 0 -> {
                    return MachineElectricFurnaceEntity.this.progress;
                }
                case 1 -> {
                    return MachineElectricFurnaceEntity.this.power;
                }
                case 2 -> {
                    return MachineElectricFurnaceEntity.this.maxProgress;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index){
                case 0 -> MachineElectricFurnaceEntity.this.progress = value;
                case 1 -> MachineElectricFurnaceEntity.this.power = value;
                case 2 -> MachineElectricFurnaceEntity.this.maxProgress = value;
            }
        }

        @Override
        public int size() {
            return 3;
        }
    };

    //这里继承的构造函数其实是要改参数的，父类参数列表最前面的BlockEntityType<?> type要去掉，来适配Factory<T extends BlockEntity>这个接口
    public MachineElectricFurnaceEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.electric_furnace_entity, pos, state);
        this.matchGetter = RecipeManager.createCachedMatchGetter(RecipeType.SMELTING);  //电炉默认复用熔炉配方
    }
    // 继承自BlockEntity
    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("progress",progress);
        nbt.putInt("power",power);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        power = nbt.getInt("power");
        progress = nbt.getInt("progress");
    }
    //每tick会调用的函数，
    // 不知怎么回事无法把本类型投射到BlockEntityTicker中，只能改接口然后强制类型转换
    // 注意：必须在方块类中实现getTicker函数，这个函数才会被调用
    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity){
        MachineElectricFurnaceEntity entity = (MachineElectricFurnaceEntity) blockEntity;
        // 冷却时间更新
        if (entity.cooldown > 0)entity.cooldown--;

        //电量更新
        // 电池系统还不成熟，先考虑放入创造电池的情况
        if (entity.inventory.get(1).getItem().equals(ModItems.battery_creative))entity.power = maxPower;
        //这里还需要判断一下周围的电线连接

        //判断升级
        if (!entity.inventory.get(3).isEmpty()){
            MachineUpgrade upgrade = (MachineUpgrade) entity.inventory.get(3).getItem();
            int speedLevel = 0;
            int powerLevel = 0;
            if (upgrade.type == MachineUpgrade.UpgradeType.POWER)powerLevel = upgrade.level;
            else if (upgrade.type == MachineUpgrade.UpgradeType.SPEED)speedLevel = upgrade.level;
            // 速度升级和能效升级的影响
            entity.maxProgress -= speedLevel * 25;
            entity.consumption += speedLevel * 25;
            entity.maxProgress += powerLevel * 10;
            entity.consumption -= powerLevel * 25;
        }
        //没电则进入冷却循环
        if (!entity.hasPower())entity.cooldown = 20;
        //开始完成配方
        if (entity.hasPower()&& entity.canProcess(world.getRegistryManager())){
            entity.power -= entity.consumption;
            if (entity.progress == 0){  //如果没开始就初始化cooktime
                entity.cookTimeTotal = MachineElectricFurnaceEntity.getCookTime(world, entity);
                entity.cookTime = 0;
                entity.progress = 1;
                ((MachineElectricFurnace)world.getBlockState(pos).getBlock()).setLit(true);
            }else if (entity.cookTime < entity.cookTimeTotal){
                entity.cookTime++;
                entity.progress = Math.max((int) Math.floor((double) entity.cookTime /entity.cookTimeTotal* entity.maxProgress) ,entity.progress);
            }else if (entity.cookTime == entity.cookTimeTotal){ //到时间了就生成结果
                RecipeEntry recipe = (RecipeEntry) entity.matchGetter.getFirstMatch(entity, world).orElse(null);
                ItemStack itemStack = entity.inventory.get(0);
                ItemStack itemStack2 = recipe.value().getResult(world.getRegistryManager());
                ItemStack itemStack3 = entity.inventory.get(2);
                if (itemStack3.isEmpty()) {
                    entity.inventory.set(2, itemStack2.copy());
                } else if (itemStack3.isOf(itemStack2.getItem())) {
                    itemStack3.increment(1);
                }
                itemStack.decrement(1);
                entity.cookTime = entity.cookTimeTotal = entity.progress = 0;   //重置
                ((MachineElectricFurnace)world.getBlockState(pos).getBlock()).setLit(false);
                MachineElectricFurnaceEntity.markDirty(world, pos, state);      //表明状态改变
            }
        }else {     //如果物品被中途拿出来了，则状态重置
            entity.cookTime = entity.cookTimeTotal = entity.progress = 0;   //重置
        }
    }
    public boolean hasPower() {     //是否有足够的能量
        return power >= consumption;
    }
    public boolean canProcess(DynamicRegistryManager manager){    //是否可以开始完成配方
        //判断冷却时间是否到达，以及原料格是否有原料
        if (cooldown > 0 || inventory.get(0).isEmpty())return false;
        //判断根据熔炉配方是否能有产出
        RecipeEntry recipeEntry = (RecipeEntry) matchGetter.getFirstMatch(this, world).orElse(null);
        if (recipeEntry == null)return false;
        ItemStack result = recipeEntry.value().getResult(manager);
        if (result.isEmpty())return false;
        //判断预期产出与当前结果槽物品是否对应
        ItemStack itemStack = inventory.get(2);
        if (itemStack.isEmpty())return true;
        if (!ItemStack.areItemsEqual(itemStack,result))return false;
        //判断结果槽物品数量是否足以容纳配方产出
        return itemStack.getCount() < itemStack.getMaxCount();
    }
    private static int getCookTime(World world, MachineElectricFurnaceEntity furnace) {
        return furnace.matchGetter.getFirstMatch(furnace, world).map(recipe -> ((AbstractCookingRecipe)recipe.value()).getCookingTime()).orElse(200);
    }

    //也不知道是从哪里继承的，反正在blockentity里找不到，但就是可以继承
    @Override   //验证物品在槽位中是否有效
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 1){
            return stack.isIn(ModTagKeys.batteryItem);     //电池槽只能放电池
        }else if (slot == 2){
            return false;                                  //输出槽不能放物品
        }else if (slot == 3){                               //升级槽，只能放入能量或者速度升级
            return stack.getItem() instanceof MachineUpgrade
                    && (((MachineUpgrade)stack.getItem()).type == MachineUpgrade.UpgradeType.POWER
                    || ((MachineUpgrade)stack.getItem()).type == MachineUpgrade.UpgradeType.SPEED);
        }
        return SidedInventory.super.isValid(slot, stack);
    }

    //继承自NamedScreenHandlerFactory
    @Override
    public Text getDisplayName() {      // GUI顶部的字
        return Text.translatable(getCachedState().getBlock().getTranslationKey());  //这个到底是什么我还没搞明白
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {     //获取方块实体并存的screen handler
        return new ElectricFurnaceScreenHandler(syncId,playerInventory,this,propertyDelegate);
    }
    // 继承自SideIventory
    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return dir == Direction.UP && slot == 0 ||                          // 从上面输入原料
                dir != Direction.DOWN && dir != Direction.UP && slot == 1;  // 从侧面输入燃料
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot == 2;                          // 从下面输出产物
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : inventory) {
            if (itemStack.isEmpty())continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override   //从某个槽里取出一定数量物品
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(inventory,slot,amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory,slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = inventory.get(slot);
        boolean bl = !stack.isEmpty() && ItemStack.canCombine(itemStack, stack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        if (slot == 0 && !bl) {
            this.markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this,player);
    }

    @Override
    public void clear() {
        inventory.clear();
    }
}
