package com.hbm.space.dim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.RecipeAtmosphere;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.space.dim.orbit.OrbitalStation;
import com.hbm.space.dim.orbit.Space;
import com.hbm.space.dim.trait.CBT_Atmosphere;
import com.hbm.space.dim.trait.CBT_War;
import com.hbm.space.dim.trait.CBT_Dyson;
import com.hbm.space.dim.trait.CBT_Atmosphere.FluidEntry;
import com.hbm.space.dim.trait.CBT_Water;
import com.hbm.space.dim.trait.CelestialBodyTrait;
import com.hbm.item.ItemVOVTdrive;
import com.hbm.space.util.AstronomyUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * CelestialBody stores planet data in a tree structure,
 * allowing for bodies orbiting bodies.
 *
 * Unit suffixes added when they differ from SI units, for clarity.
 *
 * Ported from 1.7.10 com.hbm.dim.CelestialBody to 1.20.1.
 */
public class CelestialBody {

    // === Fields ===

    public String name;

    /** The dimension associated with this body. Null for gas giants / non-landable bodies. */
    public ResourceKey<Level> dimension;

    /** Does this body have an associated dimension and a solid surface? */
    public boolean canLand = false;

    // Orbital elements
    public float massKg = 0;
    public float radiusKm = 0;
    /** Distance to the parent body */
    public float semiMajorAxisKm = 0;
    /** Pre-computed: sqrt(1 - e²) */
    public float semiMinorAxisFactor = 0;
    public float eccentricity = 0;
    public float inclination = 0;
    public float ascendingNode = 0;
    public float argumentPeriapsis = 0;

    /** Day length in seconds (default: 6 hours) */
    private int rotationalPeriod = 6 * 60 * 60;

    public float axialTilt = 0;

    /**
     * What level of technology can locate this body?
     * This defines the minimum level, automatically adjusted based on stardar location.
     */
    private int minProcessingLevel = 0;

    public ResourceLocation texture = null;
    public ResourceLocation biomeMask = null;
    public ResourceLocation cityMask = null;
    /** When too small to render the texture */
    public float[] color = new float[] {0.4F, 0.4F, 0.4F};

    public String tidallyLockedTo = null;

    public boolean hasRings = false;
    public float ringTilt = 0;
    public float[] ringColor = new float[] {0.5F, 0.5F, 0.5F};
    public float ringSize = 2;

    /** Has bedrock ice? */
    public boolean hasIce = false;

    /** The gas type for gas giants */
    public Fluid gas;

    /** Satellite bodies orbiting this body */
    public List<CelestialBody> satellites = new ArrayList<>();
    /** Parent body (null for the star) */
    public CelestialBody parent = null;

    /** Default traits assigned at construction */
    private final HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> traits =
        new HashMap<>();

    public ResourceLocation stoneTexture;
    public ResourceLocation surfaceTexture;
    public SolarSystem.Body type;

    /** Client-only shader reference */
    @OnlyIn(Dist.CLIENT)
    public Object shader; // com.hbm.render.shader.Shader — port the shader class to use this

    /**
     * If the shader renders the item within the quad (not filling it entirely),
     * scale it up from the true size.
     */
    public float shaderScale = 1;

    // === Static Lookup Maps ===

    private static final HashMap<ResourceKey<Level>, CelestialBody> dimToBodyMap = new HashMap<>();
    private static final HashMap<String, CelestialBody> nameToBodyMap = new HashMap<>();

    // === Constructors ===

    public CelestialBody(String name) {
        this.name = name;
        this.texture = ResourceLocation.tryBuild("hbm", "textures/misc/space/" + name + ".png");

        nameToBodyMap.put(name, this);
    }

    public CelestialBody(String name, ResourceKey<Level> dimension, SolarSystem.Body type) {
        this(name);
        this.dimension = dimension;
        this.canLand = true;
        this.type = type;

        if (dimension != null) {
            dimToBodyMap.put(dimension, this);
        }
    }

    // === Chainable Builders ===

    public CelestialBody withMassRadius(float kg, float km) {
        this.massKg = kg;
        this.radiusKm = km;
        return this;
    }

    public CelestialBody withOrbitalParameters(float semiMajorAxisKm, float eccentricity,
            float argumentPeriapsisDegrees, float inclinationDegrees, float ascendingNodeDegrees) {
        this.semiMajorAxisKm = semiMajorAxisKm;
        this.semiMinorAxisFactor = (float) Math.sqrt(1 - eccentricity * eccentricity);
        this.eccentricity = eccentricity;
        this.argumentPeriapsis = (float) Math.toRadians(argumentPeriapsisDegrees);
        this.inclination = (float) Math.toRadians(inclinationDegrees);
        this.ascendingNode = (float) Math.toRadians(ascendingNodeDegrees);
        return this;
    }

    public CelestialBody withRotationalPeriod(int seconds) {
        this.rotationalPeriod = seconds;
        return this;
    }

    public CelestialBody withAxialTilt(float degrees) {
        this.axialTilt = degrees;
        return this;
    }

    public CelestialBody withMinProcessingLevel(int level) {
        this.minProcessingLevel = level;
        return this;
    }

    public CelestialBody withTexture(ResourceLocation location) {
        this.texture = location;
        return this;
    }

    public CelestialBody withCityMask(ResourceLocation location) {
        this.cityMask = location;
        return this;
    }

    public CelestialBody withBiomeMask(ResourceLocation location) {
        this.biomeMask = location;
        return this;
    }

    public CelestialBody withBlockTextures(String stone, String surface) {
        this.stoneTexture = ResourceLocation.tryParse(stone);
        this.surfaceTexture = ResourceLocation.tryParse(surface);
        return this;
    }

    public CelestialBody withColor(float... color) {
        this.color = color;
        return this;
    }

    public CelestialBody withTidalLockingTo(String name) {
        tidallyLockedTo = name;
        return this;
    }

    public CelestialBody withRings(float tilt, float size, float... color) {
        this.hasRings = true;
        this.ringTilt = tilt;
        this.ringSize = size;
        this.ringColor = color;
        return this;
    }

    public CelestialBody withGas(Fluid gas) {
        this.gas = gas;
        return this;
    }

    public CelestialBody withSatellites(CelestialBody... bodies) {
        Collections.addAll(satellites, bodies);
        for (CelestialBody body : bodies) {
            body.parent = this;
        }
        return this;
    }

    public CelestialBody withTraits(CelestialBodyTrait... traits) {
        for (CelestialBodyTrait trait : traits)
            this.traits.put(trait.getClass(), trait);
        return this;
    }

    public CelestialBody withShader(ResourceLocation fragmentShader) {
        return withShader(fragmentShader, 1);
    }

    public CelestialBody withShader(ResourceLocation fragmentShader, float scale) {
        // Shader only available on client
        Level level = null;
        try {
            level = Minecraft.getInstance().level;
        } catch (Exception e) { /* Not on client */ }
        if (level == null) return this;

        // shader = new Shader(fragmentShader); // Requires ported Shader class
        shaderScale = scale;
        return this;
    }

    public CelestialBody withIce(boolean hasIce) {
        this.hasIce = hasIce;
        return this;
    }

    // === Terraforming — Trait Overrides ===

    /**
     * If trait overrides exist, delete existing traits from the world
     * and replace them with the saved ones.
     */

    public static void setTraits(Level world, CelestialBodyTrait... traits) {
        SolarSystemWorldSavedData traitsData = SolarSystemWorldSavedData.get(world);
        traitsData.setTraits(getBody(world).name, traits);
    }

    public static void setTraits(Level world,
            Map<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> traits) {
        setTraits(world, traits.values().toArray(new CelestialBodyTrait[0]));
    }

    public void setTraits(CelestialBodyTrait... traits) {
        SolarSystemWorldSavedData traitsData = SolarSystemWorldSavedData.get();
        traitsData.setTraits(name, traits);
    }

    public void setTraits(
            Map<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> traits) {
        setTraits(traits.values().toArray(new CelestialBodyTrait[0]));
    }

    /** Gets a clone of the body traits that are SAFE for modifying */
    public static HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait>
            getTraits(Level world) {
        SolarSystemWorldSavedData traitsData = SolarSystemWorldSavedData.get(world);
        HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> currentTraits =
            traitsData.getTraits(getBody(world).name);

        if (currentTraits == null) {
            currentTraits = new HashMap<>();
            CelestialBody body = getBody(world);
            for (CelestialBodyTrait trait : body.traits.values()) {
                currentTraits.put(trait.getClass(), trait);
            }
        }
        return currentTraits;
    }

    public HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> getTraits() {
        SolarSystemWorldSavedData traitsData = SolarSystemWorldSavedData.get();
        HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> currentTraits =
            traitsData.getTraits(name);

        if (currentTraits == null) {
            currentTraits = new HashMap<>();
            for (CelestialBodyTrait trait : traits.values()) {
                currentTraits.put(trait.getClass(), trait);
            }
        }
        return currentTraits;
    }

    public static void modifyTraits(Level world, CelestialBodyTrait... traits) {
        HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> currentTraits =
            getTraits(world);

        for (CelestialBodyTrait trait : traits) {
            currentTraits.put(trait.getClass(), trait);
        }

        setTraits(world, currentTraits);
    }

    public void modifyTraits(CelestialBodyTrait... traits) {
        HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> currentTraits =
            getTraits();

        for (CelestialBodyTrait trait : traits) {
            currentTraits.put(trait.getClass(), trait);
        }

        setTraits(currentTraits);
    }

    public static void clearTraits(Level world) {
        SolarSystemWorldSavedData traitsData = SolarSystemWorldSavedData.get(world);
        traitsData.clearTraits(getBody(world).name);
    }

    public void clearTraits() {
        SolarSystemWorldSavedData traitsData = SolarSystemWorldSavedData.get();
        traitsData.clearTraits(name);
    }

    /** Remove atmosphere trait — it has ceased to be */
    public static void degas(Level world) {
        HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> currentTraits =
            getTraits(world);
        currentTraits.remove(CBT_Atmosphere.class);
        setTraits(world, currentTraits);
    }

    /** Consume a specific amount of gas from the atmosphere. Returns true if successful. */
    public static boolean consumeGas(Level world, Fluid fluid, double amount) {
        HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> currentTraits =
            getTraits(world);

        CBT_Atmosphere atmosphere = (CBT_Atmosphere) currentTraits.get(CBT_Atmosphere.class);
        if (atmosphere == null) return false;

        boolean didConsume = false;
        int emptyIndex = -1;
        for (int i = 0; i < atmosphere.fluids.size(); i++) {
            FluidEntry entry = atmosphere.fluids.get(i);
            if (entry.fluid == fluid) {
                entry.pressure -= amount / AstronomyUtil.MB_PER_ATM;
                didConsume = true;
                emptyIndex = entry.pressure <= 0 ? i : -1;
                break;
            }
        }

        if (emptyIndex >= 0) {
            atmosphere.fluids.remove(emptyIndex);

            if (atmosphere.fluids.isEmpty()) {
                currentTraits.remove(CBT_Atmosphere.class);
            }
        }

        setTraits(world, currentTraits);
        return didConsume;
    }

    /** Emit gas into the atmosphere. */
    public static void emitGas(Level world, Fluid fluid, double amount) {
        HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> currentTraits =
            getTraits(world);

        CBT_Atmosphere atmosphere = (CBT_Atmosphere) currentTraits.get(CBT_Atmosphere.class);
        if (atmosphere == null) {
            atmosphere = new CBT_Atmosphere();
            currentTraits.put(CBT_Atmosphere.class, atmosphere);
        }

        boolean hasFluid = false;
        for (FluidEntry entry : atmosphere.fluids) {
            if (entry.fluid == fluid) {
                entry.pressure += amount / AstronomyUtil.MB_PER_ATM;
                hasFluid = true;
                break;
            }
        }

        if (!hasFluid) {
            // Sort existing fluids and remove the lowest fraction if at capacity
            if (atmosphere.fluids.size() >= 8) {
                atmosphere.sortDescending();
                atmosphere.fluids.remove(atmosphere.fluids.size() - 1);
            }

            atmosphere.fluids.add(new FluidEntry(fluid, amount / AstronomyUtil.MB_PER_ATM));
        }

        setTraits(world, currentTraits);
    }

    /** React atmosphere according to a recipe. Returns true if reaction occurred. */
    public static boolean reactAtmosphere(Level world, RecipeAtmosphere recipe) {
        HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> currentTraits = getTraits(world);
        CBT_Atmosphere atmosphere = (CBT_Atmosphere) currentTraits.get(CBT_Atmosphere.class);
        if (atmosphere == null) return false;

        return recipe.matches(world, atmosphere, 64);
    }

    /** Run atmospheric chemistry updates. */
    public static void updateChemistry(Level world) {
        boolean hasUpdated = false;
        HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> currentTraits = getTraits(world);
        CBT_Atmosphere atmosphere = (CBT_Atmosphere) currentTraits.get(CBT_Atmosphere.class);

        CBT_Water water = (CBT_Water) currentTraits.get(CBT_Water.class);
        if (water == null) {
            if (atmosphere != null) {
                double pressure = 0;
                for (FluidEntry entry : atmosphere.fluids) {
                    if (entry.fluid == HBMFluids.STEAM.source().get()
                        || entry.fluid == HBMFluids.HOTSTEAM.source().get()
                        || entry.fluid == HBMFluids.SUPERHOTSTEAM.source().get()
                        || entry.fluid == HBMFluids.ULTRAHOTSTEAM.source().get()
                        || entry.fluid == HBMFluids.SPENTSTEAM.source().get()) {
                        pressure += entry.pressure;
                    }
                }

                if (pressure > 0.2D) {
                    currentTraits.put(CBT_Water.class, new CBT_Water());
                    hasUpdated = true;
                }
            }
        }

        if (atmosphere != null) {
            for (RecipeAtmosphere recipe : world.getRecipeManager().getAllRecipesFor(ModRecipes.ATMOSPHERE.type().get())) {
                if (reactAtmosphere(world, recipe)) {
                    hasUpdated = true;
                }
            }
        }

        if (hasUpdated)
            setTraits(world, currentTraits);
    }

    /**
     * Called once per tick to attenuate swarm counts based on a swarm half-life.
     * Only operates on the central star.
     */
    public static void updateSwarms() {
        HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> currentTraits =
            SolarSystem.kerbol.getTraits();

        CBT_Dyson dyson = (CBT_Dyson) currentTraits.get(CBT_Dyson.class);
        if (dyson == null) return;

        dyson.attenuate();
    }

    // === War / Damage ===

    public static void damage(int dmg, Level world) {
        HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> currentTraits =
            getTraits(world);

        CBT_War war = (CBT_War) currentTraits.get(CBT_War.class);
        if (war == null) {
            war = new CBT_War();
            currentTraits.put(CBT_War.class, war);
        }

        if (war.shield > 0) {
            war.shield -= dmg;
        } else {
            war.health -= dmg;
        }

        setTraits(world, currentTraits);
    }

    // === Static Getters ===

    public static Collection<CelestialBody> getAllBodies() {
        return nameToBodyMap.values();
    }

    public static Collection<CelestialBody> getLandableBodies() {
        return dimToBodyMap.values();
    }

    public static CelestialBody getBody(String name) {
        if (SolarSystem.kerbol == null) {
            // 防御性初始化 — 防止在 commonSetup 之前被调用
            SolarSystem.init();
        }
        CelestialBody body = nameToBodyMap.get(name);
        return body != null ? body : dimToBodyMap.get(Level.OVERWORLD);
    }

    public static CelestialBody getBody(ResourceKey<Level> dim) {
        if (SolarSystem.kerbol == null) {
            // 防御性初始化 — 防止在 commonSetup 之前被调用
            SolarSystem.init();
        }
        CelestialBody body = dimToBodyMap.get(dim);
        return body != null ? body : dimToBodyMap.get(Level.OVERWORLD);
    }

    /** Returns the body for the given level's dimension */
    public static CelestialBody getBody(Level world) {
        return getBody(world.dimension());
    }

    public static CelestialBody getBodyOrNull(String name) {
        return nameToBodyMap.get(name);
    }

    public static CelestialBody getBodyOrNull(ResourceKey<Level> dim) {
        return dimToBodyMap.get(dim);
    }

    public static ItemVOVTdrive.Target getTarget(Level world, int x, int z) {
        if (inOrbit(world)) {
            OrbitalStation station = !world.isClientSide()
                ? OrbitalStation.getStationFromPosition(x, z)
                : OrbitalStation.clientStation;
            return new ItemVOVTdrive.Target(station.orbiting, true, station.hasStation);
        }

        return new ItemVOVTdrive.Target(getBody(world), false, true);
    }

    public static CelestialBody getStar(Level world) {
        return getBody(world).getStar();
    }

    public static CelestialBody getPlanet(Level world) {
        return getBody(world).getPlanet();
    }

    /** Get gravity modifier for an entity on this body */
    public static float getGravity(LivingEntity entity) {
        if (entity instanceof WaterAnimal)
            return AstronomyUtil.STANDARD_GRAVITY;

        if (inOrbit(entity.level())) {
            if (!entity.isNoGravity()) {
                OrbitalStation station = entity.level().isClientSide()
                    ? OrbitalStation.clientStation
                    : OrbitalStation.getStationFromPosition(
                        (int) entity.getX(), (int) entity.getZ());

                float gravity = AstronomyUtil.STANDARD_GRAVITY * station.gravityMultiplier;
                if (gravity < 0.2f) return 0;
                return gravity;
            }
            return 0;
        }

        CelestialBody body = CelestialBody.getBody(entity.level());
        return body.getSurfaceGravity() * AstronomyUtil.PLAYER_GRAVITY_MODIFIER;
    }

    public static boolean inOrbit(Level world) {
        return world.dimension().equals(Space.LEVEL_KEY);
    }

    public static SolarSystem.Body getEnum(Level world) {
        return getBody(world).getEnum();
    }

    public static int getMeta(Level world) {
        return getBody(world).getEnum().ordinal();
    }

    public static double getRotationalPeriod(Level world) {
        return getBody(world).getRotationalPeriod();
    }

    public static float getSemiMajorAxis(Level world) {
        return getBody(world).semiMajorAxisKm;
    }

    public static boolean hasTrait(Level world, Class<? extends CelestialBodyTrait> trait) {
        return getBody(world).hasTrait(trait);
    }

    @SuppressWarnings("unchecked")
    public static <T extends CelestialBodyTrait> T getTrait(Level world, Class<? extends T> trait) {
        return getBody(world).getTrait(trait);
    }

    public static boolean hasDefaultTrait(Level world,
            Class<? extends CelestialBodyTrait> trait) {
        return getBody(world).hasDefaultTrait(trait);
    }

    @SuppressWarnings("unchecked")
    public static <T extends CelestialBodyTrait> T getDefaultTrait(Level world,
            Class<? extends T> trait) {
        return getBody(world).getDefaultTrait(trait);
    }

    // === Instance Methods ===

    public String getUnlocalizedName() {
        return name;
    }

    public SolarSystem.Body getEnum() {
        return type;
    }

    /** Walk up to the root (star) of the body tree */
    public CelestialBody getStar() {
        CelestialBody body = this;
        while (body.parent != null)
            body = body.parent;
        return body;
    }

    /** Walk up to the planet (direct child of the star) */
    public CelestialBody getPlanet() {
        if (this.parent == null) return this;
        CelestialBody body = this;
        while (body.parent.parent != null)
            body = body.parent;
        return body;
    }

    /**
     * Returns the day length in ticks, adjusted for the 20-minute Minecraft day.
     */
    public double getRotationalPeriod() {
        return (double) rotationalPeriod
            * (AstronomyUtil.DAY_FACTOR / (double) AstronomyUtil.TIME_MULTIPLIER) * 20;
    }

    /**
     * Returns the year length in days, derived from semi-major axis.
     */
    public double getOrbitalPeriod() {
        double semiMajorAxis = semiMajorAxisKm * 1_000;
        double orbitalPeriod = 2 * Math.PI * Math.sqrt(
            (semiMajorAxis * semiMajorAxis * semiMajorAxis)
                / (AstronomyUtil.GRAVITATIONAL_CONSTANT * parent.massKg)
        );
        return orbitalPeriod / (double) AstronomyUtil.SECONDS_IN_KSP_DAY;
    }

    /** Get the gravitational force at the surface, derived from mass and radius */
    public float getSurfaceGravity() {
        float radius = radiusKm * 1000;
        return AstronomyUtil.GRAVITATIONAL_CONSTANT * massKg / (radius * radius);
    }

    /** Get the power multiplier for sun-based machines */
    public float getSunPower() {
        float distanceAU = getPlanet().semiMajorAxisKm / AstronomyUtil.KM_IN_AU;
        return 1 / (distanceAU * distanceAU);
    }

    /**
     * Processing level is based on where you are processing from.
     * E.g., if you're on Duna, Ike will be tier 0.
     */
    public int getProcessingLevel(CelestialBody from) {
        int level = 3;

        if (this == from) {
            level = 0; // Self: tier 0
        } else if (this == from.parent || this.parent == from) {
            level = 0; // Going to/from a moon: tier 0
        } else {
            level = 1; // Otherwise: tier 1
        }

        return Math.max(level, minProcessingLevel);
    }

    // === Trait Accessors ===

    public boolean hasTrait(Class<? extends CelestialBodyTrait> trait) {
        return getTraitsUnsafe().containsKey(trait);
    }

    @SuppressWarnings("unchecked")
    public <T extends CelestialBodyTrait> T getTrait(Class<? extends T> trait) {
        return (T) getTraitsUnsafe().get(trait);
    }

    public boolean hasDefaultTrait(Class<? extends CelestialBodyTrait> trait) {
        return traits.containsKey(trait);
    }

    @SuppressWarnings("unchecked")
    public <T extends CelestialBodyTrait> T getDefaultTrait(Class<? extends T> trait) {
        return (T) traits.get(trait);
    }

    /**
     * Don't modify traits returned from this!
     * Returns the active traits (from world saved data if available, else defaults).
     */
    private HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> getTraitsUnsafe() {
        HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> traits;

        // Check if we're on client or server
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            // Client side
            traits = SolarSystemWorldSavedData.getClientTraits(name);
        } else {
            // Server side
            traits = SolarSystemWorldSavedData.get().getTraits(name);
        }

        if (traits != null)
            return traits;

        return this.traits;
    }

    // === Heightmap ===

    /**
     * Loads heightmap data for a given chunk.
     * Returns the heightmap array for the specified chunk, or null if unavailable.
     */
    public int[] getHeightmap(int chunkX, int chunkZ) {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || dimension == null) return null;

        ServerLevel world = server.getLevel(dimension);
        if (world == null) return null;

        // Load OR generate the desired chunk
        LevelChunk chunk = world.getChunk(chunkX, chunkZ);

        // Build heightmap array from the chunk's WORLD_SURFACE heightmap
        int[] heightMap = new int[256]; // 16x16
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                heightMap[z * 16 + x] = chunk.getHeight(
                    Heightmap.Types.WORLD_SURFACE, x, z
                );
            }
        }
        return heightMap;
    }
}
