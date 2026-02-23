package com.hbm.item.misc;

import com.hbm.HBMKey;
import com.hbm.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

public class ItemWiring extends Item {
    public ItemWiring(Properties pProperties) {
        super(pProperties);
    }
}
