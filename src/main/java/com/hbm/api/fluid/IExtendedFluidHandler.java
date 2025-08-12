package com.hbm.api.fluid;

import com.hbm.api.Mode;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 流体系统方案
 * <br>
 * 流体系统使用场景：
 * 1. 从含流体物品中输出流体到容器中，无方向，有编号，需要流体桶和含流体nbt物品分开讨论。isFluidValid, fill
 * 2. 从容器输出流体到含流体物品中，无方向，有编号，需要流体桶和含流体nbt物品分开讨论。drain
 * 3. 处理含流体配方，无方向，有编号，既有流体消耗也有产出。drain, fill
 * 4. 将流体输出到临近容器，有方向，无编号。需要检查每个方向以及容器是否接受流体。getCapacity, isFluidValid, fill, drain
 * 5. 从临近容器将流体拉入容器，少用，只用于可以输入输出的流体储罐，有方向，无编号。需要检验流体类型。
 * 6. 容器与管线系统的交互，不需要容器操作，而是需要管线系统主动决定拉取什么流体，有方向，无编号，需要检验流体类型。
 * <br>
 * 原则：
 * 1. 为避免重复计算，方块之间的流体交互尽量只考虑流体输出，而不考虑输入，一个方块流体输入的功能尽量通过 别的容器向它输出实现。
 * 2. 容器内部的流体交互不考虑方向，只考虑fluidTank的编号。
 * 3. 容器之间的流体交互不考虑fluidTank编号，只考虑方向，尽可能遵循ifluidhandler接口的功能，一种流体能否注入用fill函数判断。
 * 4. ifluidhandler以外新加入的接口尽可能不用在容器之间的交互上，保证forge流体的兼容性。
 * 5. 流体系统优先给自己mod的机器分配流体，再给其他mod机器分配流体，并且尽量保证不同机器有个最低输入流体量。
 * <br>
 * 接入限制：
 * 1. 每个tank有输入、输出、输入输出、禁止四种状态，这是tank自身的限制。
 * 2. 第二个限制是方向的限制，是否可以通过方块的某个面访问内部流体。这个由capabilitiesCache的方向实现，不在内部实现。
 * 3. 为了简化访问
 * */
public interface IExtendedFluidHandler extends IFluidHandler {

    List<FluidTank> getFluidTanks();
    Mode getMode(int tank);
    /** 仅考虑tank本身是否允许输入，未考虑流体本身是否适合输入，要和isFluidValid结合使用 */
    default boolean allowInput(int tank){
        Mode mode = getMode(tank);
        return tank >= 0 && tank < getTanks() && (mode == Mode.INPUT || mode == Mode.BOTH);
    }
    default boolean allowOutput(int tank){
        Mode mode = getMode(tank);
        return tank >= 0 && tank < getTanks() && (mode == Mode.OUTPUT || mode == Mode.BOTH);
    }
    @Override
    default int getTanks(){
        return getFluidTanks().size();
    }

    @Override
    default @NotNull FluidStack getFluidInTank(int tank){
        return tank >= 0 && tank < getTanks() ? getFluidTanks().get(tank).getFluid() : FluidStack.EMPTY;
    }

    @Override
    default int getTankCapacity(int tank){
        return tank >= 0 && tank < getTanks() ? getFluidTanks().get(tank).getCapacity() : 0;
    }
    /**
     * 这个函数这里检验的比较严格，除了流体种类外，还判断了一下tank是否还有足够的空间，因为这个函数基本就是在fill的时候用吧。
     * */
    @Override
    default boolean isFluidValid(int tank, @NotNull FluidStack stack){
        if (!allowInput(tank)) return false;
        FluidTank fluidTank = getFluidTanks().get(tank);
        return fluidTank.isFluidValid(stack) && (fluidTank.isEmpty() || fluidTank.getFluid().isFluidEqual(stack)) && fluidTank.getSpace() != 0;
    }
    /**
     * 不指定tank编号，逐个检查对应的tank是否和流体类型兼容
     * */
    @Override
    default int fill(FluidStack resource, FluidAction action){
        int amount = resource.getAmount();
        List<FluidTank> tanks = getFluidTanks();
        IntList typeMatchedTanks = new IntArrayList();
        IntList emptyTanks = new IntArrayList();
        for (int i = 0; i < tanks.size(); i++) {
            if (!isFluidValid(i, resource)) continue;
            if (tanks.get(i).isEmpty()) emptyTanks.add(i);
            else if (tanks.get(i).isFluidValid(resource)) typeMatchedTanks.add(i);
        }
        for (Integer tank : typeMatchedTanks) {
            if (!resource.isEmpty()) resource.shrink(tanks.get(tank).fill(resource,action));
        }
        for (Integer tank : emptyTanks) {
            if (!resource.isEmpty()) resource.shrink(tanks.get(tank).fill(resource,action));
        }
        return amount - resource.getAmount();
    }
    /**
     * 不指定tank编号，逐个检查对应的tank是否和流体类型兼容
     * */
    @Override
    default @NotNull FluidStack drain(FluidStack resource, FluidAction action){
        if (resource.isEmpty()) return FluidStack.EMPTY;
        int drainAmount = 0;
        List<FluidTank> tanks = getFluidTanks();
        for (int i = 0; i < tanks.size(); i++) {
            FluidTank tank = tanks.get(i);
            if (tank != null && allowOutput(i) && tank.getFluid().isFluidEqual(resource)){
                drainAmount += tank.drain(resource.getAmount() - drainAmount,action).getAmount();
            }
            if (drainAmount == resource.getAmount())break;
        }
        FluidStack resultStack = resource.copy();
        resultStack.setAmount(drainAmount);
        return resultStack;
    }

    @Override
    default @NotNull FluidStack drain(int maxDrain, FluidAction action){
        FluidStack resultStack = FluidStack.EMPTY;
        List<FluidTank> tanks = getFluidTanks();
        for (int i = 0; i < tanks.size(); i++) {
            FluidTank tank = tanks.get(i);
            if (tank != null && allowOutput(i) && !tank.isEmpty() && (resultStack.isEmpty() || tank.getFluid().isFluidEqual(resultStack))){
                FluidStack drainStack = tank.drain(maxDrain, action);
                if (resultStack.isEmpty())
                    resultStack = drainStack;
                else
                    resultStack.grow(drainStack.getAmount());
                maxDrain -= drainStack.getAmount();
                if (maxDrain == 0)break;
            }
        }
        return resultStack;
    }
}
