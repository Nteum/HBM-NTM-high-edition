package com.hbm.blockentity.machine;

import com.hbm.blockentity.interfaces.IHeatSource;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 锯木机（core 体系多方块机器）。
 * 移植自旧版 TileEntitySawmill：从下方热源吸热驱动锯条切割原木/木板。
 * - 3 槽：0 输入、1 输出、2 副产物（锯末）
 * - 简化：移除过速飞锯实体，仅保留切割逻辑
 */
public class SawmillEntity extends BEDummyable {
    public static final double DIFFUSION = 0.1D;
    public static final int PROCESSING_TIME = 600;
    public static final int MAX_HEAT = 300;

    public int heat;
    public int progress;
    public boolean hasBlade = true;

    public SawmillEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(3);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_SAWMILL.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        if (hasBlade){
            tryPullHeat();

            if (heat >= 100){
                ItemStack result = getOutput(items.getStackInSlot(0));
                if (result != null){
                    progress += heat / 10;
                    if (progress >= PROCESSING_TIME){
                        progress = 0;
                        items.setStackInSlot(0, ItemStack.EMPTY);
                        ItemStack out = items.getStackInSlot(1);
                        if (out.isEmpty()) items.setStackInSlot(1, result.copy());
                        else out.grow(result.getCount());

                        if (items.getStackInSlot(2).isEmpty() && this.getLevel().random.nextFloat() < 0.5F){
                            items.setStackInSlot(2, new ItemStack(ModItems.POWDER_SAWDUST.get()));
                        }
                        this.setChanged();
                    }
                } else {
                    progress = 0;
                }
            } else {
                progress = 0;
            }
        } else {
            progress = 0;
        }
        this.heat = 0;
        this.setChanged();
    }

    protected void tryPullHeat(){
        BlockEntity con = this.getLevel().getBlockEntity(this.worldPosition.below());
        if (con instanceof IHeatSource source){
            int heatSrc = (int)(source.getHeatStored() * DIFFUSION);
            if (heatSrc > 0){
                source.useUpHeat(heatSrc);
                this.heat += heatSrc;
                return;
            }
        }
        this.heat = Math.max(this.heat - Math.max(this.heat / 1000, 1), 0);
    }

    public ItemStack getOutput(ItemStack input){
        if (input == null || input.isEmpty()) return null;
        if (input.is(net.minecraft.tags.ItemTags.LOGS)){
            return new ItemStack(Items.OAK_PLANKS, 6);
        }
        if (input.is(Items.STICK)) return new ItemStack(ModItems.POWDER_SAWDUST.get());
        if (input.is(net.minecraft.tags.ItemTags.PLANKS)){
            return new ItemStack(Items.STICK, 6);
        }
        return null;
    }

    public void setHasBlade(boolean hasBlade){
        this.hasBlade = hasBlade;
        this.setChanged();
    }

    public MachineItemHandler getItemHandler(){ return items; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
