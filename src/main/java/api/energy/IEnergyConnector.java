package api.energy;

import net.minecraft.core.Direction;

//用于判断储能方块特定方向上是否可以连通能量
public interface IEnergyConnector {
    /**
     * Whether the given side can be connected to
     * dir refers to the side of this block, not the connecting block doing the check
     * @param dir
     * @return
     */
    public default boolean canConnect(Direction dir) {
        return true;
    }
}
