package com.hbm.addational_data.entity;

import com.hbm.HBM;
import com.hbm.addational_data.BasicAdditionalDataImpl;
import com.hbm.addational_data.DataEntry;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toclient.S2CEntitySyncPacket;
import com.hbm.utils.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class EntityAdditionalDataImpl extends BasicAdditionalDataImpl implements IEntityAdditionalData{
    @Override
    public void copyAfterDeath(IEntityAdditionalData old) {
        old.getEntries().forEach((key, val) -> {
            if (key.keepAfterReborn) {
                setData(key, val);
            }
        });
    }
}
