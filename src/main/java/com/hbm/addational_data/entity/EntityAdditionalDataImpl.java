package com.hbm.addational_data.entity;

import com.hbm.addational_data.BasicAdditionalDataImpl;

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
