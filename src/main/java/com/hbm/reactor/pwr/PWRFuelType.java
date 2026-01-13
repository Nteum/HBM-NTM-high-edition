package com.hbm.reactor.pwr;

import net.minecraft.ChatFormatting;

import java.util.Locale;

public enum PWRFuelType {
    MEU(5.0D, new LogFunction(20 * 30).withDiv(2_500), 1_000_000_000D),
    HEU233(7.5D, new SqrtFunction(25D), 1_000_000_000D),
    HEU235(7.5D, new SqrtFunction(22.5D), 1_000_000_000D),
    MEN(7.5D, new LogFunction(22.5D * 30D).withDiv(2_500), 1_000_000_000D),
    HEN237(7.5D, new SqrtFunction(27.5D), 1_000_000_000D),
    MOX(7.5D, new LogFunction(20D * 30D).withDiv(2_500), 1_000_000_000D),
    MEP(7.5D, new LogFunction(22.5D * 30D).withDiv(2_500), 1_000_000_000D),
    HEP239(10.0D, new SqrtFunction(22.5D), 1_000_000_000D),
    HEP241(10.0D, new SqrtFunction(25D), 1_000_000_000D),
    MEA(7.5D, new LogFunction(25D * 30D).withDiv(2_500), 1_000_000_000D),
    HEA242(10.0D, new SqrtFunction(25D), 1_000_000_000D),
    HES326(12.5D, new SqrtFunction(27.5D), 1_000_000_000D),
    HES327(12.5D, new SqrtFunction(30D), 1_000_000_000D),
    BFB_AM_MIX(2.5D, new SqrtFunction(15D), 250_000_000D),
    BFB_PU241(2.5D, new SqrtFunction(15D), 250_000_000D);

    public final double heatEmission;
    public final FuelFunction function;
    public final double yield;

    PWRFuelType(double heatEmission, FuelFunction function, double yield) {
        this.heatEmission = heatEmission;
        this.function = function;
        this.yield = yield;
    }

    public double outputForFlux(double fluxPerRod) {
        return function.effonix(fluxPerRod);
    }

    public String getFunctionLabel() {
        return function.getLabel();
    }

    public String getDangerLabel() {
        return function.getDanger();
    }

    public static PWRFuelType fromIndex(int index) {
        PWRFuelType[] values = values();
        if (index < 0 || index >= values.length) {
            return MEU;
        }
        return values[index];
    }

    public interface FuelFunction {
        double effonix(double x);

        String getLabel();

        String getDanger();
    }

    private abstract static class BaseFunction implements FuelFunction {
        protected double div = 1D;
        protected double off = 0D;

        public BaseFunction withDiv(double div) {
            this.div = div;
            return this;
        }

        public BaseFunction withOff(double off) {
            this.off = off;
            return this;
        }

        protected double getX(double x) {
            return x / div + off;
        }

        protected String getXName() {
            return getXName(true);
        }

        protected String getXName(boolean brackets) {
            String x = "x";
            boolean modified = false;
            if (div != 1D) {
                x += " / " + String.format(Locale.ROOT, "%,.1f", div);
                modified = true;
            }
            if (off != 0D) {
                x += " + " + String.format(Locale.ROOT, "%,.1f", off);
                modified = true;
            }
            if (modified && brackets) {
                x = "(" + x + ")";
            }
            return x;
        }
    }

    private static final class LogFunction extends BaseFunction {
        private final double level;

        private LogFunction(double level) {
            this.level = level;
            withOff(1D);
        }

        @Override
        public double effonix(double x) {
            return Math.log10(getX(x)) * level;
        }

        @Override
        public String getLabel() {
            return "log10(" + getXName(false) + ") * " + String.format(Locale.ROOT, "%,.1f", level);
        }

        @Override
        public String getDanger() {
            return ChatFormatting.YELLOW + "MEDIUM / LOGARITHMIC";
        }
    }

    private static final class SqrtFunction extends BaseFunction {
        private final double level;

        private SqrtFunction(double level) {
            this.level = level;
        }

        @Override
        public double effonix(double x) {
            return Math.sqrt(getX(x)) * level;
        }

        @Override
        public String getLabel() {
            return "sqrt(" + getXName(false) + ") * " + String.format(Locale.ROOT, "%,.3f", level);
        }

        @Override
        public String getDanger() {
            return ChatFormatting.YELLOW + "MEDIUM / SQUARE ROOT";
        }
    }
}
