package com.hbm.item.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;

public class ItemFELCrystal extends Item {
    public EnumWavelengths wavelength = EnumWavelengths.NULL;
    public ItemFELCrystal(Properties pProperties, EnumWavelengths enumWavelengths) {
        super(pProperties);
        this.wavelength = enumWavelengths;
    }

    public enum EnumWavelengths{
        NULL("la creatura", "6 dollar", 0x010101, 0x010101, ChatFormatting.WHITE), //why do you exist?

        IR("wavelengths.name.ir", "wavelengths.waveRange.ir", 0xBB1010, 0xCC4040, ChatFormatting.RED),
        VISIBLE("wavelengths.name.visible", "wavelengths.waveRange.visible", 0, 0, ChatFormatting.GREEN),
        UV("wavelengths.name.uv", "wavelengths.waveRange.uv", 0x0A1FC4, 0x00EFFF, ChatFormatting.AQUA),
        GAMMA("wavelengths.name.gamma", "wavelengths.waveRange.gamma", 0x150560, 0xEF00FF, ChatFormatting.LIGHT_PURPLE),
        DRX("wavelengths.name.drx", "wavelengths.waveRange.drx", 0xFF0000, 0xFF0000, ChatFormatting.DARK_RED);

        public String name = "";
        public String wavelengthRange = "";
        public int renderedBeamColor;
        public int guiColor;
        public ChatFormatting textColor;

        private EnumWavelengths(String name, String wavelength, int color, int guiColor, ChatFormatting textColor) {
            this.name = name;
            this.wavelengthRange = wavelength;
            this.renderedBeamColor = color;
            this.guiColor = guiColor;
            this.textColor = textColor;
        }
    }
}
