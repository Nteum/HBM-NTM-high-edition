package com.hbm.capabilities;

import com.hbm.api.IContentsListener;
import com.hbm.api.annotations.NothingNullByDefault;
import com.hbm.blockentity.base.TransmitterBlockEntity.InteractPredicate;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
//存储一个储罐列表
@NothingNullByDefault
public abstract class DynamicHandler<TANK> implements IContentsListener {

    protected final Function<Direction, List<TANK>> containerSupplier;
    protected final InteractPredicate canExtract;
    protected final InteractPredicate canInsert;
    @Nullable
    private final IContentsListener listener;

    protected DynamicHandler(Function<Direction, List<TANK>> containerSupplier, InteractPredicate canExtract, InteractPredicate canInsert,
                             @Nullable IContentsListener listener) {
        this.containerSupplier = containerSupplier;
        this.canExtract = canExtract;
        this.canInsert = canInsert;
        this.listener = listener;
    }

    @Override
    public void onContentsChanged() {
        if (listener != null) {
            listener.onContentsChanged();
        }
    }

}