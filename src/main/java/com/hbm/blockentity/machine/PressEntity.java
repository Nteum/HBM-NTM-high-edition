package com.hbm.blockentity.machine;

import com.hbm.HBM;
import com.hbm.HBMLang;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.RecipePress;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.gui.menu.PressMenu;
import com.hbm.item.tool.ItemStamp;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModSounds;
import com.hbm.utils.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PressEntity extends BaseMachineBlockEntity {
    public static int SLOT_FUEL = 0;
    public static int SLOT_STAMP = 1;
    public static int SLOT_INPUT = 2;
    public static int SLOT_OUTPUT = 3;
    public static final int MAX_SPEED = 400;    // max speed ticks for acceleration
    public static final int progressAtMax = 25; // max progress speed when hot
    public final static int MAX_PRESS = 200;    // max tick count per operation assuming speed is 1

    public int speed = 0;                       // speed ticks up once (or four times if preheated) when operating
    public int burnTime = 0;                    // burn ticks of the loaded fuel, 200 ticks equal one operation
    public int press;                           // extension of the press, operation is completed if maxPress is reached
    public double renderPress;                  // client-side version of the press var, a double for smoother rendering
    public double lastPress;                    // for interp
    private int syncPress;                      // for interp
    private int turnProgress;                   // for interp 3: revenge of the sith
    boolean isRetracting = false;               // direction the press is currently going
    private int delay;                          // delay between direction changes to look a bit more appealing
    /*
     * 0: 燃料
     * 1: 模板
     * 2: 输入
     * 3：输出
     * */
    private final ItemStackHandler items = new ItemStackHandler(4){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot){
                case 0, 2, 3 -> true;
                case 1 -> stack.getItem() instanceof ItemStamp;
                default -> false;
            };
        }
    };
    public final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> speed;
                case 1 -> burnTime;
                case 3 -> press;
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
    public PressEntity( BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.PRESS_ENTITY.get(), pPos, pBlockState);
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, items);
    }

    @Override
    public Component getDefaultName() {
        return HBMLang.CONTAINER_PRESS.translate();
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new PressMenu(pContainerId, pInventory,this, containerData);
    }

    @Override
    protected void onUpdateServer() {
        boolean preheated = DirectionUtils.searchAround(level, getBlockPos(), ModBlocks.PRESS_PREHEATER.get());
        boolean canProcess = false;
        RecipeWrapper wrapper = new RecipeWrapper(this.items);
        Optional<RecipePress> recipe = Optional.empty();
        if (burnTime >= 200){
            recipe = level.getRecipeManager().getRecipeFor(ModRecipes.PRESS.type().get(), wrapper, level);
            canProcess = recipe.isPresent() && canInsertStackIntoOutput(items, recipe.get().getResultItem(level.registryAccess()));
        }

        if((canProcess || this.isRetracting) && this.burnTime >= 200) {
            this.speed += preheated ? 4 : 1;
            this.speed = Math.min(this.speed, MAX_SPEED);
        } else {
            this.speed = Math.max(speed - 1, 0);
        }

        if(delay <= 0) {
            int stampSpeed = speed * progressAtMax / MAX_SPEED;

            if(this.isRetracting) {
                this.press -= stampSpeed;

                if(this.press <= 0) {
                    this.isRetracting = false;
                    this.delay = 5;
                    this.press = 0;
                }
            } else if(canProcess) {
                this.press += stampSpeed;

                if(this.press >= MAX_PRESS) {
                    Vec3 center = this.getTilePos().getCenter();
                    this.level.playSound(null, center.x, center.y, center.z, ModSounds.BLOCK_PRESS_OPERATE.get(), SoundSource.BLOCKS, getVolume(1.5F), 1.0F);
                    this.items.insertItem(SLOT_OUTPUT, recipe.get().assemble(wrapper, level.registryAccess()), false);
                    this.items.extractItem(SLOT_INPUT, recipe.get().input.getItems()[0].getCount(), false);
                    ItemStack stampStack = this.items.getStackInSlot(SLOT_STAMP);
                    if (stampStack.hurt(1, level.random, null)){
                        stampStack.shrink(1);
                    }

                    this.isRetracting = true;
                    this.delay = 5;
                    if(this.burnTime >= 200) {
                        this.burnTime -= 200; // only subtract fuel if operation was actually successful
                    }

                    this.setChanged();
                }
            } else if(this.press > 0){
                this.isRetracting = true;
            }
        } else {
            delay--;
        }
        // 处理燃料
        ItemStack fuelStack = this.items.getStackInSlot(SLOT_FUEL);
        int fuelTime = 0;
        if (!fuelStack.isEmpty() && burnTime < 200 && (fuelTime = ForgeHooks.getBurnTime(fuelStack, ModRecipes.PRESS.type().get())) > 0){
            burnTime += fuelTime;
            if (fuelStack.getCount() == 1 && fuelStack.getItem() instanceof BucketItem){
                items.setStackInSlot(SLOT_FUEL, Items.BUCKET.getDefaultInstance());
            }else {
                fuelStack.shrink(1);
            }
            this.setChanged();
        }

        sendUpdatePacket();
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        // approach-based interpolation, GO!
        this.lastPress = this.renderPress;

        if(this.turnProgress > 0) {
            this.renderPress = this.renderPress + ((this.syncPress - this.renderPress) / (double) this.turnProgress);
            --this.turnProgress;
        } else {
            this.renderPress = this.syncPress;
        }
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.put("item", this.items.getStackInSlot(2).serializeNBT());
        tag.putInt("press", this.press);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        this.items.setStackInSlot(2, ItemStack.of((CompoundTag) tag.get("item")));
        this.syncPress = tag.getInt("press");
    }

    //存储数据
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("press", press);
        pTag.putInt("burnTime", burnTime);
        pTag.putInt("speed", speed);
        pTag.putBoolean("ret", isRetracting);
        pTag.put("items", this.items.serializeNBT());
    }
    //加载数据
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        press = pTag.getInt("press");
        burnTime = pTag.getInt("burnTime");
        speed = pTag.getInt("speed");
        isRetracting = pTag.getBoolean("ret");
        try {
            this.items.deserializeNBT((CompoundTag) pTag.get("items"));
        }catch (Exception e){
            HBM.LOGGER.warn("Press's Items data lost.");
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox();
    }

    // 检查输出槽是否能放下 result（考虑叠加、最大堆叠）
    private static boolean canInsertStackIntoOutput(ItemStackHandler items, ItemStack result) {
        ItemStack out = items.getStackInSlot(SLOT_OUTPUT);
        if (out.isEmpty()) return true;
        if (!ItemStack.isSameItemSameTags(out, result)) return false;
        int max = Math.min(out.getMaxStackSize(), items.getSlotLimit(SLOT_OUTPUT));
        return out.getCount() + result.getCount() <= max;
    }

    // 辅助：从 ItemStackHandler 得到输入堆栈，供 RecipeWrapper 使用（RecipeWrapper 可直接包装 items）
    public ItemStackHandler getItemHandler() { return items; }
}
