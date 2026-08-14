package com.hbm.core.contents.addational_data.entity;

import com.hbm.core.contents.addational_data.IAdditionalData;

public interface IEntityAdditionalData extends IAdditionalData {
    /**
     * 生物死后重生获得的功能，目前只用于玩家。
     * */
    void copyAfterDeath(IEntityAdditionalData data);
}
