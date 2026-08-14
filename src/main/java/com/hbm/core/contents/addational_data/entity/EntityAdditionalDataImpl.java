package com.hbm.core.contents.addational_data.entity;

import com.hbm.core.contents.addational_data.BasicAdditionalDataImpl;

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
