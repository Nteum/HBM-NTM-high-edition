package com.hbm.blockentity.interfaces;

import com.hbm.HBMLang;
import com.hbm.api.IIncrementalEnum;
import com.hbm.api.annotations.NothingNullByDefault;
import com.hbm.api.math.MathUtils;
import com.hbm.api.text.IHasTextComponent;
import com.hbm.api.text.ILangEntry;
import net.minecraft.network.chat.Component;

public interface IRedstoneControl {

    /**
     * Gets the RedstoneControl type from this block.
     *
     * @return this block's RedstoneControl type
     */
    RedstoneControl getControlType();

    /**
     * Sets this block's RedstoneControl type to a new value.
     *
     * @param type - RedstoneControl type to set
     */
    void setControlType(RedstoneControl type);

    /**
     * If the block is getting powered or not by redstone (indirectly).
     *
     * @return if the block is getting powered indirectly
     */
    boolean isPowered();

    /**
     * If the block was getting powered or not by redstone, last tick. Used for PULSE mode.
     */
    boolean wasPowered();

    /**
     * If the machine can be pulsed.
     */
    boolean canPulse();

    @NothingNullByDefault
    enum RedstoneControl implements IIncrementalEnum<RedstoneControl>, IHasTextComponent {
        DISABLED(HBMLang.REDSTONE_CONTROL_DISABLED),
        HIGH(HBMLang.REDSTONE_CONTROL_HIGH),
        LOW(HBMLang.REDSTONE_CONTROL_LOW),
        PULSE(HBMLang.REDSTONE_CONTROL_PULSE);

        private static final RedstoneControl[] MODES = values();
        private final ILangEntry langEntry;

        RedstoneControl(ILangEntry langEntry) {
            this.langEntry = langEntry;
        }

        @Override
        public Component getTextComponent() {
            return langEntry.translate();
        }

        @Override
        public RedstoneControl byIndex(int index) {
            return byIndexStatic(index);
        }

        public static RedstoneControl byIndexStatic(int index) {
            return MathUtils.getByIndexMod(MODES, index);
        }
    }
}