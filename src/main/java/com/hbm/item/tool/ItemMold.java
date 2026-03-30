package com.hbm.item.tool;

import com.hbm.Inventory.material.HBMMatForm;
import net.minecraft.world.item.Item;

public class ItemMold extends Item {
    private HBMMatForm form;
    private int size = 1;
    public ItemMold(Properties pProperties, HBMMatForm form) {
        this(pProperties.stacksTo(1), form, 1);
    }
    public ItemMold(Properties pProperties, HBMMatForm form, int size) {
        super(pProperties);
        this.form = form;
        this.size = size;
    }
    public int getQuantity(){
        return form.quantity * size;
    }

    public HBMMatForm getForm(){
        return this.form;
    }

    public int getSize(){
        return this.size;
    }
}
