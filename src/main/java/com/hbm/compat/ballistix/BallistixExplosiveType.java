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

    OBSIDIAN("obsidian", 120, 10.0F, fullCube(), false),
    CONDENSIVE("condensive", 30, 3.5F, fullCube(), false),
    ATTRACTIVE("attractive", 30, 0.0F, fullCube(), false),
    REPULSIVE("repulsive", 30, 0.0F, fullCube(), false),
    INCENDIARY("incendiary", 80, 4.5F, fullCube(), false),
    SHRAPNEL("shrapnel", 40, 0.0F, fullCube(), false),
    CHEMICAL("chemical", 100, 0.0F, fullCube(), false),
    ANVIL("anvil", 100, 0.0F, fullCube(), false),
    INFESTIVE("infestive", 40, 0.0F, fullCube(), false),
    DEBILITATION("debilitation", 80, 0.0F, fullCube(), false),
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
