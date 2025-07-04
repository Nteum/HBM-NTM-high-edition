package com.hbm.capabilities.network;

import com.hbm.api.math.MathUtils;

public enum ConnType {
    NORMAL,FORBID,IN,OUT;
    private static final ConnType[] TYPES = values();
    public static ConnType byIndexStatic(int index) {
        return MathUtils.getByIndexMod(TYPES, index);
    }
}