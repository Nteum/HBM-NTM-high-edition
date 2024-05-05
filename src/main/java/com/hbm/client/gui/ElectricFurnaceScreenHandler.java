package com.hbm.client.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

//ScreenHandler 在其他映射中也有叫 Menu 的，
// 是服务器的GUI类型，主要是用来管理一些GUI的数据，保证服务器和客户端GUI功能相一致
// ScreenHandler在客户端和服务器端都会创建，它们同步运行
// ScreenHandler需要单独注册
public class ElectricFurnaceScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    protected final World world;

    //供客户端调用的构造函数，一般也是用它注册的
    public ElectricFurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId,playerInventory,new SimpleInventory(4), new ArrayPropertyDelegate(3));
    }
    //服务器调用的构造函数，由相应的方块实体调用
    public ElectricFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.elecFurnace, syncId);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.getWorld();
        this.slots.clear(); //干点大逆不道的事
        //添加电炉专有的槽
        this.addSlot(new Slot(inventory,0,56,17));
        this.addSlot(new BatterySlot(this, inventory, 1, 56, 53));
        this.addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, 2, 116, 35));
        this.addSlot(new UpgradeSlot(inventory,3,148,36));
        int i;
        //添加玩家物品槽
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        //添加玩家快捷物品栏
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
        this.addProperties(propertyDelegate);   //添加玩家远程属性
//        DalisFunMod.LOGGER.info("--------------新建ElectricFurnaceScreenHandler " + inventory.size() + " "+ propertyDelegate.size());
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    public int getProgress(){
        return this.propertyDelegate.get(0);
    }

    public int getPower(){
        return this.propertyDelegate.get(1);
    }

    public int getMaxProgress(){
        return this.propertyDelegate.get(2);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
}
