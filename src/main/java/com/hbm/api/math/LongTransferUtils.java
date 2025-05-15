package com.hbm.api.math;

import com.hbm.api.enums.Action;
import com.hbm.api.annotations.NothingNullByDefault;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;

import java.util.function.IntSupplier;

//ref:mek
@NothingNullByDefault
public class LongTransferUtils {
    private LongTransferUtils(){}
    public static long insert(long stack, Action action, IntSupplier containerCount, Int2ObjectFunction<Long> inContainerGetter, InsertLong insert) {
        return 0L;
    }
    public static long extract(long amount, Action action, IntSupplier containerCount, ExtractLong extract) {
        return 0L;
    }
    @FunctionalInterface
    public interface InsertLong {
        long insert(int container, long amount, Action action);
    }

    @FunctionalInterface
    public interface ExtractLong {
        long extract(int container, long amount, Action action);
    }
}
