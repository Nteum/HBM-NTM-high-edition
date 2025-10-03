package com.hbm.addational_data.entity;

import com.hbm.addational_data.DataEntry;
import com.hbm.addational_data.IAdditionalData;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.Optional;

public interface IEntityAdditionalData extends IAdditionalData {
    /**
     * 生物死后重生获得的功能，目前只用于玩家。
     * */
    void copyAfterDeath(IEntityAdditionalData data);
}
