package com.hbm.utils.multiblock;

public class MultiblockModule extends MultiblockData{
    public boolean isFormed = false;
    // 不需要序列化，每次重载都需要重新分配
    public boolean distributed = false;
}
