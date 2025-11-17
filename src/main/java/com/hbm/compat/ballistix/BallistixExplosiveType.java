package com.hbm.compat.ballistix;

import java.util.Arrays;
import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Lightweight mirror of the Ballistix explosive catalog. Only the tier 1 set
 * (plus the landmine) is ported for now while the higher-tier effects are
 * reimplemented later.
 */
public enum BallistixExplosiveType {

    // Tier 0/1
    OBSIDIAN("obsidian", 120, 10.0F, fullCube(), false),
    CONDENSIVE("condensive", 30, 2.5F, fullCube(), false),
    ATTRACTIVE("attractive", 30, 6.0F, fullCube(), false),
    REPULSIVE("repulsive", 30, 6.0F, fullCube(), false),
    INCENDIARY("incendiary", 80, 7.0F, fullCube(), false),
    SHRAPNEL("shrapnel", 40, 4.0F, fullCube(), false),
    CHEMICAL("chemical", 100, 7.0F, fullCube(), false),
    ANVIL("anvil", 100, 6.0F, fullCube(), false),
    INFESTIVE("infestive", 40, 7.0F, fullCube(), false),
    DEBILITATION("debilitation", 80, 7.0F, fullCube(), false),
    // Tier 2
    FRAGMENTATION("fragmentation", 100, 8.0F, fullCube(), false),
    CONTAGIOUS("contagious", 100, 7.0F, fullCube(), false),
    BREACHING("breaching", 5, 5.0F, fullCube(), false),
    THERMOBARIC("thermobaric", 100, 30.0F, fullCube(), false),
    SONIC("sonic", 80, 20.0F, fullCube(), false),
    // Tier 3
    ANTIGRAVITY("antigravity", 100, 12.0F, fullCube(), false),
    EMP("emp", 80, 45.0F, fullCube(), false),
    NUCLEAR("nuclear", 200, 45.0F, fullCube(), false),
    ENDOTHERMIC("endothermic", 80, 30.0F, fullCube(), false),
    EXOTHERMIC("exothermic", 80, 40.0F, fullCube(), false),
    ENDER("ender", 100, 9.0F, fullCube(), false),
    HYPERSONIC("hypersonic", 150, 30.0F, fullCube(), false),
    REJUVINATION("rejuvination", 400, 12.0F, fullCube(), false),
    ANTIMATTER("antimatter", 400, 45.0F, fullCube(), false),
    LARGE_ANTIMATTER("largeantimatter", 600, 100.0F, fullCube(), false),
    DARKMATTER("darkmatter", 400, 50.0F, fullCube(), false),
    // Other
    LANDMINE("landmine", 5, 2.5F, landmineShape(), true);

    private static final VoxelShape FULL_CUBE = fullCube();
    private static final VoxelShape LANDMINE_SHAPE = landmineShape();

    private final String id;
    private final int defaultFuse;
    private final float baseRadius;
    private final VoxelShape shape;
    private final boolean pressureTriggered;

    BallistixExplosiveType(String id, int defaultFuse, float baseRadius, VoxelShape shape,
            boolean pressureTriggered) {
        this.id = id;
        this.defaultFuse = defaultFuse;
        this.baseRadius = baseRadius;
        this.shape = shape;
        this.pressureTriggered = pressureTriggered;
    }

    public String id() {
        return id;
    }

    public int defaultFuse() {
        return defaultFuse;
    }

    public float baseRadius() {
        return baseRadius;
    }

    public VoxelShape shape() {
        return shape;
    }

    public boolean isPressureTriggered() {
        return pressureTriggered;
    }

    public static List<BallistixExplosiveType> portedTypes() {
        return PORTED;
    }

    private static VoxelShape fullCube() {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    }

    private static VoxelShape landmineShape() {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
    }

    private static final List<BallistixExplosiveType> PORTED = Arrays.asList(values());
}
