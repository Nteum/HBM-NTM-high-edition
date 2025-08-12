package com.hbm.gui.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

/** 规定的从menu获取方块实体的接口，这主要是为了弥补ContainerData的局限性。
 * 实现这个接口并通过相应方式注册，GUI就可以使用同步的方块实体的数据，而绕开ContainerData。
 * */
public interface ITileAccess {
    void setTile(BlockEntity blockEntity);
    /** 用于在ModMenuType中注册 */
    static <T extends AbstractContainerMenu>AbstractContainerMenu getInstance(int windowId, Inventory inv, FriendlyByteBuf data, Class<T> menuClass){
        T menu = null;
        try {
            menu = menuClass.getDeclaredConstructor(new Class[]{int.class, Inventory.class}).newInstance(windowId, inv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (menu instanceof ITileAccess){
            BlockPos pos = data.readBlockPos();
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
            ((ITileAccess) menu).setTile(be);
        }
        return menu;
    }
}
