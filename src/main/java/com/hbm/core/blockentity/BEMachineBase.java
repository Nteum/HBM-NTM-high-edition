package com.hbm.core.blockentity;

import com.hbm.blockentity.HBMTiles;
import com.hbm.gui.HBMMenus;
import com.hbm.registries.RegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static com.hbm.HBM.MODID;

/**
 * 有menu界面的机器的基类，如果不需要menu，可以直接不实现createMenu
 */
public abstract class BEMachineBase extends BECapabilities implements Nameable, MenuProvider {
    public BEMachineBase(BlockPos pos, BlockState state) {
        super(HBMTiles.getTypeById(HBMTiles.getId(BEMachineBase.getId(state))), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    /**
     * 命名默认就是方块自身的名字
     */
    protected static String getId(BlockState blockState){
        return RegistryHelper.blockRL(blockState.getBlock()).getPath();
    }
    @Override
    public Component getName() {
        return Component.translatable("block." + MODID + "." + getId(this.getBlockState()));
    }

    public ContainerData getContainerData(){
        return new ContainerData() {
            @Override
            public int get(int p_39284_) {
                return 0;
            }

            @Override
            public void set(int p_39285_, int p_39286_) {}

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    public MenuType<?> getMenuType(){
        return HBMMenus.getById(HBMMenus.getId(this.getId(this.getBlockState())));
    }

    //========================直接从BaseContainerBlockEntity复制的=====================
    protected LockCode lockKey = LockCode.NO_LOCK;
    public boolean canOpen(Player pPlayer) {
        return canUnlock(pPlayer, this.lockKey, this.getDisplayName());
    }

    public static boolean canUnlock(Player pPlayer, LockCode pCode, Component pDisplayName) {
        if (!pPlayer.isSpectator() && !pCode.unlocksWith(pPlayer.getMainHandItem())) {
            pPlayer.displayClientMessage(Component.translatable("container.isLocked", pDisplayName), true);
            pPlayer.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
            return false;
        } else {
            return true;
        }
    }

    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return this.canOpen(pPlayer) ? this.createMenu(pContainerId, pPlayerInventory) : null;
    }

    protected abstract AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory);
}
