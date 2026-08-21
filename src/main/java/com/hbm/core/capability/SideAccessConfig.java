package com.hbm.core.capability;

import com.hbm.api.Mode;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.Direction;

import java.util.EnumMap;
import java.util.Map;

/**
 * 面向（Direction）能力访问权限配置。
 *
 * 每个面可以独立定义：
 * - 物品槽：该面上哪些槽可以入、哪些可以出（Mode 语义：INPUT/BOTH/OUTPUT/NONE）
 * - 流体槽：该面上哪些槽可以注入、哪些可以抽出
 * - 能量：该面是只进、只出、双向还是禁止
 *
 * 默认全部为双向开放（BOTH），未配置的面保持默认。
 * 配合 BECapabilities#getItemHandler/getFluidHandler/getEnergyHandler 使用，
 * 由 SidedItemWrapper/SidedFluidWrapper/SidedEnergyWrapper 消费。
 */
public class SideAccessConfig {
    // 每个方向的物品槽位模式：slotIndex -> Mode
    private final Map<Direction, Map<Integer, Mode>> itemModes = new EnumMap<>(Direction.class);
    // 每个方向的流体槽位模式：tankIndex -> Mode
    private final Map<Direction, Map<Integer, Mode>> fluidModes = new EnumMap<>(Direction.class);
    // 每个方向的能量模式：0 禁止、1 输入、2 输出、3 双向
    private final Map<Direction, Integer> energyModes = new EnumMap<>(Direction.class);

    /** 设置某面某物品槽的出入模式 */
    public SideAccessConfig setItemMode(Direction side, Mode mode, int ... slots){
        for (int slot : slots) {
            itemModes.computeIfAbsent(side, s -> new java.util.HashMap<>()).put(slot, mode);
        }
        return this;
    }
    private static final Direction[] SIDE = new Direction[]{Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH};
    // 设置机器侧面接口，
    public SideAccessConfig setSideItemMode(Mode mode, int ... slots){
        for (Direction side : SIDE) {
            for (int slot : slots) {
                itemModes.computeIfAbsent(side, s -> new java.util.HashMap<>()).put(slot, mode);
            }
        }
        return this;
    }
    /** 设置某面某流体槽的出入模式 */
    public SideAccessConfig setFluidMode(Direction side, int tank, Mode mode){
        fluidModes.computeIfAbsent(side, s -> new java.util.HashMap<>()).put(tank, mode);
        return this;
    }
    /** 设置某面的能量模式 */
    public SideAccessConfig setEnergyMode(Direction side, int mode){
        energyModes.put(side, mode);
        return this;
    }

    /** 获取某面允许输入的物品槽 */
    public IntSet getInputSlots(Direction side, int slotCount){
        IntSet result = new IntOpenHashSet();
        Map<Integer, Mode> modes = itemModes.get(side);
        if (modes == null){
            for (int i = 0; i < slotCount; i++) result.add(i);
            return result;
        }
        for (int i = 0; i < slotCount; i++){
            Mode mode = modes.getOrDefault(i, Mode.NONE);   // 默认为none，不指定无法操作
            if (mode == Mode.INPUT || mode == Mode.BOTH) result.add(i);
        }
        return result;
    }
    /** 获取某面允许输出的物品槽 */
    public IntSet getOutputSlots(Direction side, int slotCount){
        IntSet result = new IntOpenHashSet();
        Map<Integer, Mode> modes = itemModes.get(side);
        if (modes == null){
            for (int i = 0; i < slotCount; i++) result.add(i);
            return result;
        }
        for (int i = 0; i < slotCount; i++){
            Mode mode = modes.getOrDefault(i, Mode.BOTH);
            if (mode == Mode.OUTPUT || mode == Mode.BOTH) result.add(i);
        }
        return result;
    }
    /** 获取某面允许注入的流体槽 */
    public IntSet getInputTanks(Direction side, int tankCount){
        IntSet result = new IntOpenHashSet();
        Map<Integer, Mode> modes = fluidModes.get(side);
        if (modes == null){
            for (int i = 0; i < tankCount; i++) result.add(i);
            return result;
        }
        for (int i = 0; i < tankCount; i++){
            Mode mode = modes.getOrDefault(i, Mode.BOTH);
            if (mode == Mode.INPUT || mode == Mode.BOTH) result.add(i);
        }
        return result;
    }
    /** 获取某面允许抽出的流体槽 */
    public IntSet getOutputTanks(Direction side, int tankCount){
        IntSet result = new IntOpenHashSet();
        Map<Integer, Mode> modes = fluidModes.get(side);
        if (modes == null){
            for (int i = 0; i < tankCount; i++) result.add(i);
            return result;
        }
        for (int i = 0; i < tankCount; i++){
            Mode mode = modes.getOrDefault(i, Mode.BOTH);
            if (mode == Mode.OUTPUT || mode == Mode.BOTH) result.add(i);
        }
        return result;
    }
    /** 获取某面的能量模式，默认双向 */
    public int getEnergyMode(Direction side){
        return energyModes.getOrDefault(side, 3);
    }
    public boolean hasItemConfig(Direction side){
        return itemModes.containsKey(side);
    }
    public boolean hasFluidConfig(Direction side){
        return fluidModes.containsKey(side);
    }
    public boolean hasEnergyConfig(Direction side){
        return energyModes.containsKey(side);
    }
}
