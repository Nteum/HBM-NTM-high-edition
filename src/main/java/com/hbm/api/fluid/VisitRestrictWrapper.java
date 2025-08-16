package com.hbm.api.fluid;

import com.hbm.api.Mode;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 放在机器的代理方块中，用于限制某个输出口固定对应某个tank
 * 这是为了保证机器流入到流体网络的流体类型的稳定。
 * */
public class VisitRestrictWrapper implements IExtendedFluidHandler{
    // 内部藏的流体handler
    IExtendedFluidHandler fluidHandler;
    // 可访问的tank
    Set<Integer> accessibleTanks = new HashSet<>();
    public VisitRestrictWrapper(IExtendedFluidHandler fluidHandler, boolean inputNoCheck, int ... tanks){
        this.fluidHandler = fluidHandler;
        if (inputNoCheck){
            for (int i = 0; i < fluidHandler.getTanks(); i++) {
                if (this.fluidHandler.allowInput(i))
                    this.accessibleTanks.add(i);
            }
            this.accessibleTanks.addAll(Arrays.stream(tanks).boxed().collect(Collectors.toSet()));
        } else
            this.accessibleTanks = Arrays.stream(tanks).boxed().collect(Collectors.toSet());
    }
    public VisitRestrictWrapper(IExtendedFluidHandler fluidHandler, int ... tanks){
        this(fluidHandler, true, tanks);
    }
    @Override
    public List<FluidTank> getFluidTanks() {
        return fluidHandler.getFluidTanks();
    }
    /**
     * 不在准许名单内的直接禁止
     * */
    @Override
    public Mode getMode(int tank) {
        return accessibleTanks.contains(tank) ? this.fluidHandler.getMode(tank) : Mode.NONE;
    }
}
