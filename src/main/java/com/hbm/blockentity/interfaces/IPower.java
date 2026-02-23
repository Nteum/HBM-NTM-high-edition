package com.hbm.blockentity.interfaces;
/**
 * 用于表示功率有限的机器，实时返回每个tick需要的功率，这样可以避免每次分配功率都用机器的所有剩余容量计算。
 * 功率可能和配方有关，部分配方耗能就是高
 * */
public interface IPower {
    long getPower();
}
