package com.hbm.api.inventory;

import net.minecraft.core.Direction;

import java.util.List;

public class SideSlotAcc {


    public record Entry(Direction side, SideWay sideWay, List<Integer> slots){}
    public enum SideWay{
        IN,OUT,INOUT;
    }
}
