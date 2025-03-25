package com.hbm.contents.network;


import com.hbm.HBMLang;
import com.hbm.api.annotations.NothingNullByDefault;
import com.hbm.api.text.IHasTranslationKey;
import com.hbm.api.text.ILangEntry;

@NothingNullByDefault
public enum TransmissionType implements IHasTranslationKey {
    ENERGY("EnergyNetwork", "energy", HBMLang.TRANSMISSION_TYPE_ENERGY),
    FLUID("FluidNetwork", "fluids", HBMLang.TRANSMISSION_TYPE_FLUID),
    GAS("GasNetwork", "gases", HBMLang.TRANSMISSION_TYPE_GAS),
    INFUSION("InfusionNetwork", "infuse_types", HBMLang.TRANSMISSION_TYPE_INFUSION),
    PIGMENT("PigmentNetwork", "pigments", HBMLang.TRANSMISSION_TYPE_PIGMENT),
    SLURRY("SlurryNetwork", "slurries", HBMLang.TRANSMISSION_TYPE_SLURRY),
    ITEM("InventoryNetwork", "items", HBMLang.TRANSMISSION_TYPE_ITEM),
    HEAT("HeatNetwork", "heat", HBMLang.TRANSMISSION_TYPE_HEAT);

    private final String name;
    private final String transmission;
    private final ILangEntry langEntry;

    TransmissionType(String name, String transmission, ILangEntry langEntry) {
        this.name = name;
        this.transmission = transmission;
        this.langEntry = langEntry;
    }

    public String getName() {
        return name;
    }

    public String getTransmission() {
        return transmission;
    }

    public ILangEntry getLangEntry() {
        return langEntry;
    }

    @Override
    public String getTranslationKey() {
        return langEntry.getTranslationKey();
    }

    public boolean isChemical() {
        return this == GAS || this == INFUSION || this == PIGMENT || this == SLURRY;
    }

//    public boolean checkTransmissionType(Transmitter<?, ?, ?> transmitter) {
//        return transmitter.getSupportedTransmissionTypes().contains(this);
//    }
//
//    public boolean checkTransmissionType(TileEntityTransmitter transmitter) {
//        return checkTransmissionType(transmitter.getTransmitter());
//    }
}