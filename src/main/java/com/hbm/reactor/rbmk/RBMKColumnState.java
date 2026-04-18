package com.hbm.reactor.rbmk;

import net.minecraft.core.BlockPos;

import java.util.Objects;

/**
 * Snapshot describing a single RBMK structural column and its current reactor
 * telemetry. The fields mirror the old console/DODD concepts so GUI code does
 * not need to query live block entities directly.
 */
public final class RBMKColumnState {

    private final BlockPos corePosition;
    private final RBMKSettings settings;
    private RBMKColumnType columnType = RBMKColumnType.BLANK;
    private RBMKLidType lidType;
    private double controlRodInsertion;
    private double targetControlRodInsertion;
    private double heat;
    private double maxHeat;
    private double fastFlux;
    private double slowFlux;
    private double enrichment = 1.0D;
    private double xenon;
    private double coreHeat;
    private double coreMaxHeat = 1_500.0D;
    private int waterAmount;
    private int maxWater;
    private int steamAmount;
    private int maxSteam;
    private int steamCompression;
    private int controlColor = -1;
    private boolean moderated;
    private boolean hasRod;
    private boolean meltedDown;

    RBMKColumnState(final BlockPos corePosition, final RBMKSettings settings, final RBMKLidType lidType) {
        this.corePosition = Objects.requireNonNull(corePosition, "corePosition");
        this.settings = Objects.requireNonNull(settings, "settings");
        this.lidType = lidType;
    }

    public BlockPos corePosition() {
        return corePosition;
    }

    public int columnHeight() {
        return settings.columnHeight();
    }

    public RBMKSettings settings() {
        return settings;
    }

    public RBMKColumnType columnType() {
        return columnType;
    }

    public void setColumnType(final RBMKColumnType columnType) {
        this.columnType = Objects.requireNonNullElse(columnType, RBMKColumnType.BLANK);
    }

    public RBMKLidType lidType() {
        return lidType;
    }

    public void setLidType(final RBMKLidType lidType) {
        this.lidType = lidType;
    }

    public double controlRodInsertion() {
        return controlRodInsertion;
    }

    public void setControlRodInsertion(final double controlRodInsertion) {
        this.controlRodInsertion = clamp01(controlRodInsertion);
    }

    public double targetControlRodInsertion() {
        return targetControlRodInsertion;
    }

    public void setTargetControlRodInsertion(final double targetControlRodInsertion) {
        this.targetControlRodInsertion = clamp01(targetControlRodInsertion);
    }

    public double heat() {
        return heat;
    }

    public void addHeat(final double delta) {
        heat = Math.max(0.0D, heat + delta);
    }

    public void setHeat(final double heat) {
        this.heat = Math.max(0.0D, heat);
    }

    public double maxHeat() {
        return maxHeat > 0.0D ? maxHeat : settings.meltdownHeat();
    }

    public void setMaxHeat(final double maxHeat) {
        this.maxHeat = Math.max(0.0D, maxHeat);
    }

    public double fastFlux() {
        return fastFlux;
    }

    public void addFastFlux(final double amount) {
        fastFlux += Math.max(0.0D, amount);
    }

    public double slowFlux() {
        return slowFlux;
    }

    public void addSlowFlux(final double amount) {
        slowFlux += Math.max(0.0D, amount);
    }

    public void resetFlux() {
        fastFlux = 0.0D;
        slowFlux = 0.0D;
        moderated = false;
    }

    public double enrichment() {
        return enrichment;
    }

    public void setEnrichment(final double enrichment) {
        this.enrichment = clamp01(enrichment);
    }

    public double xenon() {
        return xenon;
    }

    public void setXenon(final double xenon) {
        this.xenon = Math.max(0.0D, xenon);
    }

    public double coreHeat() {
        return coreHeat;
    }

    public void setCoreHeat(final double coreHeat) {
        this.coreHeat = Math.max(0.0D, coreHeat);
    }

    public double coreMaxHeat() {
        return coreMaxHeat;
    }

    public void setCoreMaxHeat(final double coreMaxHeat) {
        this.coreMaxHeat = Math.max(1.0D, coreMaxHeat);
    }

    public int waterAmount() {
        return waterAmount;
    }

    public int maxWater() {
        return maxWater;
    }

    public int steamAmount() {
        return steamAmount;
    }

    public int maxSteam() {
        return maxSteam;
    }

    public void setFluidBuffer(final int waterAmount, final int maxWater, final int steamAmount, final int maxSteam) {
        this.waterAmount = Math.max(0, waterAmount);
        this.maxWater = Math.max(0, maxWater);
        this.steamAmount = Math.max(0, steamAmount);
        this.maxSteam = Math.max(0, maxSteam);
    }

    public int steamCompression() {
        return steamCompression;
    }

    public void setSteamCompression(final int steamCompression) {
        this.steamCompression = Math.max(0, steamCompression);
    }

    public int controlColor() {
        return controlColor;
    }

    public void setControlColor(final int controlColor) {
        this.controlColor = controlColor;
    }

    public boolean moderated() {
        return moderated;
    }

    public void setModerated(final boolean moderated) {
        this.moderated = moderated;
    }

    public boolean hasRod() {
        return hasRod;
    }

    public void setHasRod(final boolean hasRod) {
        this.hasRod = hasRod;
    }

    public boolean meltedDown() {
        return meltedDown;
    }

    public void markMeltedDown() {
        this.meltedDown = true;
    }

    private static double clamp01(final double value) {
        if (value < 0.0D) {
            return 0.0D;
        }
        if (value > 1.0D) {
            return 1.0D;
        }
        return value;
    }

    @Override
    public String toString() {
        return "RBMKColumnState{" +
                "corePosition=" + corePosition +
                ", settings=" + settings +
                ", columnType=" + columnType +
                ", lidType=" + lidType +
                ", controlRodInsertion=" + controlRodInsertion +
                ", heat=" + heat +
                ", fastFlux=" + fastFlux +
                ", slowFlux=" + slowFlux +
                ", meltedDown=" + meltedDown +
                '}';
    }
}
