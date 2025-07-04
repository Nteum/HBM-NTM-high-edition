package com.hbm.blockentity.base2;

import com.hbm.api.fluid.mek.IExtendedFluidTank;
import com.hbm.api.fluid.mek.IMekanismFluidHandler;
import com.hbm.blockentity.interfaces.IRedstoneControl.RedstoneControl;
import com.hbm.capabilities.resolver.manager.ICapabilityHandlerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class HBMBlockEntity extends CapabilityBlockEntity implements IMekanismFluidHandler {
    /**
     * A timer used to send packets to clients.
     */
    public int ticker;
    private final List<ICapabilityHandlerManager<?>> capabilityHandlerManagers = new ArrayList<>();

//    protected final IBlockProvider blockProvider;

    private boolean canBeUpgraded;
    private boolean isDirectional;
    private boolean hasSound;
    private boolean hasGui;
    private boolean hasChunkloader;
    private boolean nameable;

    @Nullable
    private Component customName;

    //Methods for implementing ITileDirectional
    @Nullable
    private Direction cachedDirection;

    protected boolean redstone = false;
    private boolean redstoneLastTick = false;
    private RedstoneControl controlType = RedstoneControl.DISABLED;
    private int currentRedstoneLevel;

//    //Variables for handling ITileContainer
//    protected final ItemHandlerManager itemHandlerManager;
//
//    //Variables for handling IMekanismFluidHandler
//    private final FluidHandlerManager fluidHandlerManager;
//
//    //Variables for handling IMekanismStrictEnergyHandler
//    private final EnergyHandlerManager energyHandlerManager;

    public HBMBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    // 客户端更新
    protected void onUpdateClient(){}
    // 服务器更新
    protected void onUpdateServer(){}
    public static void clientTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        ((BaseMachineBlockEntity)pBlockEntity).onUpdateClient();
    }
    public static void serverTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        ((BaseMachineBlockEntity)pBlockEntity).onUpdateServer();
    }

    @Override
    public List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
        return null;
    }

    @Override
    public void onContentsChanged() {

    }
}
