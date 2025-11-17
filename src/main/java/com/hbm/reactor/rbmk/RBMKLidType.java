package com.hbm.reactor.rbmk;

import net.minecraft.util.StringRepresentable;

public enum RBMKLidType implements StringRepresentable {
    NONE("none"),
    SOLID("solid"),
    GLASS("glass");

    private final String name;

    RBMKLidType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public boolean isPresent() {
        return this != NONE;
    }
}
