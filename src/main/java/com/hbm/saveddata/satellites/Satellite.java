package com.hbm.saveddata.satellites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hbm.registries.ModItems;
import com.hbm.saveddata.SatelliteSavedData;
import com.hbm.space.dim.SolarSystem;
import com.hbm.space.util.AstronomyUtil;
import com.hbm.HBM;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Base class for all satellite types. Manages satellite registration,
 * item-stack configuration (NBT), network serialization, and orbital mechanics.
 *
 * Rendering methods are stubbed — render system is ported separately.
 *
 * Ported from 1.7.10 to 1.20.1.
 */
public abstract class Satellite {

    // === Registry ===

    public static final List<Class<? extends Satellite>> satellites = new ArrayList<>();
    public static final HashMap<Item, Class<? extends Satellite>> itemToClass = new HashMap<>();
    private static final HashMap<Class<? extends Satellite>, float[]> satelliteColors = new HashMap<>();

    // === Constants ===

    public static final float DEFAULT_INCLINATION   = 0F;
    public static final float MIN_INCLINATION       = -180.0F;
    public static final float MAX_INCLINATION       = 180.0F;
    public static final float DEFAULT_ALTITUDE_KM   = AstronomyUtil.DEFAULT_ALTITUDE_KM;
    public static final float MIN_ALTITUDE_KM       = 80.0F;
    public static final float MAX_ALTITUDE_KM       = 125.0F;
    public static final float MIN_BLINK_PERIOD      = 0.3F;
    public static final float MAX_BLINK_PERIOD      = 1.0F;
    public static final boolean DEFAULT_IS_BLINKING = false;
    public static final float DEFAULT_BLINK_PERIOD  = MIN_BLINK_PERIOD;
    public static final String DEFAULT_OWNER        = "None";
    public static final float DEFAULT_PHASE_OFFSET  = 0.0F;

    private static final ResourceLocation satelliteTexture =
        HBM.rl("textures/misc/space/satellite.png");

    // === Enums ===

    public enum InterfaceActions {
        HAS_MAP,      // lets the interface display loaded chunks
        CAN_CLICK,    // enables onClick events
        SHOW_COORDS,  // enables coordinates as a mouse tooltip
        HAS_RADAR,    // lets the interface display loaded entities
        HAS_ORES      // like HAS_MAP but only shows ores
    }

    public enum CoordActions {
        HAS_Y         // enables the Y-coord field which is disabled by default
    }

    public enum Interfaces {
        NONE,         // does not interact with any sat interface (i.e. asteroid miners)
        SAT_PANEL,    // allows interaction with the sat interface panel (graphical applications)
        SAT_COORD     // allows interaction with the sat coord remote (teleportation/coord actions)
    }

    // === Instance Fields ===

    public List<InterfaceActions> ifaceAcs = new ArrayList<>();
    public List<CoordActions>   coordAcs = new ArrayList<>();
    public Interfaces           satIface = Interfaces.NONE;

    public float inclination  = DEFAULT_INCLINATION;
    public float altitude     = DEFAULT_ALTITUDE_KM;
    public float phaseOffset  = DEFAULT_PHASE_OFFSET;

    public boolean isBlinking = DEFAULT_IS_BLINKING;
    public float blinkPeriod  = DEFAULT_BLINK_PERIOD;

    public String owner = DEFAULT_OWNER;

    public float colorR;
    public float colorG;
    public float colorB;

    public float health;

    // === Registration ===

    public static void register() {
        // Satellite head items — all currently available in ModItems
        registerSatellite(SatelliteMapper.class,     ModItems.SAT_HEAD_MAPPER.get(),    0.538F, 1.0F,   0.523F);
        registerSatellite(SatelliteScanner.class,    ModItems.SAT_HEAD_SCANNER.get(),   0.544F, 0.680F, 1.0F);
        registerSatellite(SatelliteRadar.class,      ModItems.SAT_HEAD_RADAR.get(),     0.134F, 1.0F,   0.134F);
        registerSatellite(SatelliteLaser.class,      ModItems.SAT_HEAD_LASER.get(),     0.221F, 0.663F, 1.0F);
        registerSatellite(SatelliteResonator.class,  ModItems.SAT_HEAD_RESONATOR.get(), 1.0F,   0.646F, 0.181F);
        // TODO(port): remaining satellite items not yet in ModItems —
        // sat_foeq, sat_miner, sat_lunar_miner, sat_dyson_relay, sat_gerald, sat_war
    }

    /**
     * Register a satellite type.
     * @param sat  Satellite class
     * @param item Satellite item (placed in a rocket to launch)
     * @param r/g/b Default color
     */
    public static void registerSatellite(Class<? extends Satellite> sat, Item item,
                                         float r, float g, float b) {
        if (!itemToClass.containsKey(item) && !itemToClass.containsValue(sat)) {
            satellites.add(sat);
            itemToClass.put(item, sat);
            satelliteColors.put(sat, new float[] {r, g, b});
        }
    }

    public static boolean isSatelliteItem(Item item) {
        return itemToClass.containsKey(item);
    }

    // === ItemStack NBT Helpers ===

    public static void ensureItemData(ItemStack stack) {
        getItemData(stack);
    }

    private static CompoundTag getItemData(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt == null) {
            nbt = new CompoundTag();
            float[] color = getRegisteredColor(stack.getItem());
            nbt.putFloat("satInclination",  DEFAULT_INCLINATION);
            nbt.putFloat("satAltitude",     DEFAULT_ALTITUDE_KM);
            nbt.putFloat("satPhaseOffset",  DEFAULT_PHASE_OFFSET);
            nbt.putBoolean("satIsBlinking", DEFAULT_IS_BLINKING);
            nbt.putFloat("satBlink",        DEFAULT_BLINK_PERIOD);
            nbt.putString("satOwner",       DEFAULT_OWNER);
            nbt.putFloat("satColorR",       color[0]);
            nbt.putFloat("satColorG",       color[1]);
            nbt.putFloat("satColorB",       color[2]);
            stack.setTag(nbt);
        } else {
            if (!nbt.contains("satInclination"))
                nbt.putFloat("satInclination", DEFAULT_INCLINATION);
            if (!nbt.contains("satPhaseOffset"))
                nbt.putFloat("satPhaseOffset", DEFAULT_PHASE_OFFSET);
            else
                nbt.putFloat("satPhaseOffset", normalizePhaseOffset(nbt.getFloat("satPhaseOffset")));
            if (!nbt.contains("satIsBlinking"))
                nbt.putBoolean("satIsBlinking", DEFAULT_IS_BLINKING);
            if (!nbt.contains("satBlink"))
                nbt.putFloat("satBlink", DEFAULT_BLINK_PERIOD);
            else
                nbt.putFloat("satBlink", clampBlinkPeriod(nbt.getFloat("satBlink")));
        }
        return nbt;
    }

    // --- Getters from ItemStack ---

    public static float getInclination(ItemStack stack)   { return getItemData(stack).getFloat("satInclination"); }
    public static float getAltitude(ItemStack stack)      { return getItemData(stack).getFloat("satAltitude"); }
    public static float getPhaseOffset(ItemStack stack)   { return getItemData(stack).getFloat("satPhaseOffset"); }
    public static String getOwner(ItemStack stack)        { return getItemData(stack).getString("satOwner"); }
    public static float getColorR(ItemStack stack)        { return getItemData(stack).getFloat("satColorR"); }
    public static float getColorG(ItemStack stack)        { return getItemData(stack).getFloat("satColorG"); }
    public static float getColorB(ItemStack stack)        { return getItemData(stack).getFloat("satColorB"); }
    public static float getBlinkPeriod(ItemStack stack)   { return getItemData(stack).getFloat("satBlink"); }
    public static boolean isBlinking(ItemStack stack)     { return getItemData(stack).getBoolean("satIsBlinking"); }

    // --- Setters on ItemStack ---

    public static void setInclination(ItemStack stack, float v)  { getItemData(stack).putFloat("satInclination", v); }
    public static void setAltitude(ItemStack stack, float v)     { getItemData(stack).putFloat("satAltitude", v); }
    public static void setPhaseOffset(ItemStack stack, float v)  { getItemData(stack).putFloat("satPhaseOffset", normalizePhaseOffset(v)); }
    public static void setOwner(ItemStack stack, String v)       { getItemData(stack).putString("satOwner", v); }
    public static void setColor(ItemStack stack, float r, float g, float b) {
        CompoundTag nbt = getItemData(stack);
        nbt.putFloat("satColorR", r);
        nbt.putFloat("satColorG", g);
        nbt.putFloat("satColorB", b);
    }
    public static void setBlinking(ItemStack stack, boolean v)   { getItemData(stack).putBoolean("satIsBlinking", v); }
    public static void setBlinkPeriod(ItemStack stack, float v)  { getItemData(stack).putFloat("satBlink", clampBlinkPeriod(v)); }

    public static float clampBlinkPeriod(float v) {
        return Math.max(MIN_BLINK_PERIOD, Math.min(MAX_BLINK_PERIOD, v));
    }

    public static float normalizePhaseOffset(float v) {
        float wrapped = v % 360.0F;
        if (wrapped < 0.0F) wrapped += 360.0F;
        return wrapped;
    }

    public static void copyItemData(ItemStack from, ItemStack to) {
        if (to == null) return;
        setInclination(to, getInclination(from));
        setAltitude(to, getAltitude(from));
        setPhaseOffset(to, getPhaseOffset(from));
        setOwner(to, getOwner(from));
        setColor(to, getColorR(from), getColorG(from), getColorB(from));
        setBlinking(to, isBlinking(from));
        setBlinkPeriod(to, getBlinkPeriod(from));
    }

    // === Target Dimension Resolution ===

    /**
     * Returns the dimension this satellite type targets.
     * Specific satellite types are bound to specific bodies.
     */
    public static ResourceKey<Level> getTargetDimensionId(
            Class<? extends Satellite> satelliteClass, ResourceKey<Level> fallback) {
        if (satelliteClass == null) return fallback;
        if (SatelliteFoeq.class.isAssignableFrom(satelliteClass))
            return com.hbm.space.dim.SolarSystem.Body.DUNA.getDimensionId();
        if (SatelliteLunarMiner.class.isAssignableFrom(satelliteClass))
            return com.hbm.space.dim.SolarSystem.Body.MUN.getDimensionId();
        if (SatelliteMiner.class.isAssignableFrom(satelliteClass))
            return com.hbm.space.dim.SolarSystem.Body.DRES.getDimensionId();
        return fallback;
    }

    public static ResourceKey<Level> getTargetDimensionId(
            ItemStack stack, ResourceKey<Level> fallback) {
        if (stack == null || stack.getItem() == null) return fallback;
        return getTargetDimensionId(itemToClass.get(stack.getItem()), fallback);
    }

    // === Orbit ===

    /**
     * Called when a rocket carrying this satellite reaches space.
     * Creates the satellite instance and stores it in SatelliteSavedData.
     * If the satellite type is bound to a specific body, the satellite is
     * stored in that body's dimension data instead.
     */
    public static void orbit(Level world, int id, int freq,
                             double x, double y, double z, ItemStack stack) {
        if (world.isClientSide()) return;

        Satellite sat = create(id);
        if (sat == null) return;

        ResourceKey<Level> target = getTargetDimensionId(sat.getClass(), world.dimension());
        if (!world.dimension().equals(target)) {
            var server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                net.minecraft.server.level.ServerLevel targetWorld = server.getLevel(target);
                if (targetWorld != null) world = targetWorld;
            }
        }

        sat.inclination  = getInclination(stack);
        sat.altitude     = getAltitude(stack);
        sat.phaseOffset  = getPhaseOffset(stack);
        sat.isBlinking   = isBlinking(stack);
        sat.blinkPeriod  = getBlinkPeriod(stack);
        sat.owner        = getOwner(stack);
        sat.colorR       = getColorR(stack);
        sat.colorG       = getColorG(stack);
        sat.colorB       = getColorB(stack);

        SatelliteSavedData data = SatelliteSavedData.getData(world, (int) x, (int) z);
        data.sats.put(freq, sat);
        sat.onOrbit(world, x, y, z);
        data.setDirty();
    }

    /** Instantiate a satellite by its registry ID */
    public static Satellite create(int id) {
        Satellite sat = null;
        try {
            Class<? extends Satellite> c = satellites.get(id);
            sat = c.getDeclaredConstructor().newInstance();
            float[] color = getRegisteredColor(c);
            sat.colorR = color[0];
            sat.colorG = color[1];
            sat.colorB = color[2];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sat;
    }

    public static int getIDFromItem(Item item) {
        Class<? extends Satellite> sat = itemToClass.get(item);
        return satellites.indexOf(sat);
    }

    public int getID() {
        return satellites.indexOf(this.getClass());
    }

    // === NBT Serialization ===

    public void writeToNBT(CompoundTag nbt) {
        nbt.putFloat("satInclination",  inclination);
        nbt.putFloat("satAltitude",     altitude);
        nbt.putFloat("satPhaseOffset",  normalizePhaseOffset(phaseOffset));
        nbt.putBoolean("satIsBlinking", isBlinking);
        nbt.putFloat("satBlink",        blinkPeriod);
        nbt.putString("satOwner",       owner);
        nbt.putFloat("satColorR",       colorR);
        nbt.putFloat("satColorG",       colorG);
        nbt.putFloat("satColorB",       colorB);
    }

    public void readFromNBT(CompoundTag nbt) {
        inclination  = nbt.getFloat("satInclination");
        altitude     = nbt.contains("satAltitude")    ? nbt.getFloat("satAltitude")    : DEFAULT_ALTITUDE_KM;
        phaseOffset  = nbt.contains("satPhaseOffset") ? normalizePhaseOffset(nbt.getFloat("satPhaseOffset")) : DEFAULT_PHASE_OFFSET;
        isBlinking   = nbt.contains("satIsBlinking")  ? nbt.getBoolean("satIsBlinking") : DEFAULT_IS_BLINKING;
        blinkPeriod  = nbt.contains("satBlink")       ? clampBlinkPeriod(nbt.getFloat("satBlink")) : DEFAULT_BLINK_PERIOD;
        owner        = nbt.contains("satOwner")       ? nbt.getString("satOwner")       : DEFAULT_OWNER;
        float[] reg  = getRegisteredColor(getClass());
        colorR       = nbt.contains("satColorR")      ? nbt.getFloat("satColorR")       : reg[0];
        colorG       = nbt.contains("satColorG")      ? nbt.getFloat("satColorG")       : reg[1];
        colorB       = nbt.contains("satColorB")      ? nbt.getFloat("satColorB")       : reg[2];
    }

    // === Network Serialization (FriendlyByteBuf) ===

    public void serialize(FriendlyByteBuf buf) {
        buf.writeFloat(inclination);
        buf.writeFloat(altitude);
        buf.writeFloat(normalizePhaseOffset(phaseOffset));
        buf.writeUtf(owner);
        buf.writeFloat(colorR);
        buf.writeFloat(colorG);
        buf.writeFloat(colorB);
        buf.writeBoolean(isBlinking);
        buf.writeFloat(blinkPeriod);
    }

    public void deserialize(FriendlyByteBuf buf) {
        inclination  = buf.readFloat();
        altitude     = buf.readFloat();
        phaseOffset  = normalizePhaseOffset(buf.readFloat());
        owner        = buf.readUtf();
        colorR       = buf.readFloat();
        colorG       = buf.readFloat();
        colorB       = buf.readFloat();
        isBlinking   = buf.readBoolean();
        blinkPeriod  = clampBlinkPeriod(buf.readFloat());
    }

    // === Callbacks (override in subclasses) ===

    /** Called when the satellite reaches space */
    public void onOrbit(Level world, double x, double y, double z) { }

    /** Called by the sat interface when clicking on the screen */
    public void onClick(Level world, int x, int z) { }

    /** Called by the coord sat interface */
    public void onCoordAction(Level world, net.minecraft.world.entity.player.Player player,
                              int x, int y, int z) { }

    // === Rendering (client-only) ===

    /**
     * Render this satellite in the sky at its orbital position.
     * The caller owns the PoseStack; solar-angle rotation is applied externally
     * (shared across all satellites), this method applies the per-satellite
     * inclination and orbit-angle rotations.
     */
    public void render(float partialTicks, net.minecraft.client.multiplayer.ClientLevel world,
            net.minecraft.client.Minecraft mc,
            com.mojang.blaze3d.vertex.PoseStack poseStack, float solarAngle, long id) {

        double ticks = (double) System.currentTimeMillis() / 50.0;
        float orbitAngle = applyPhaseOffsetToOrbitAngle(
            phaseOffset, altitude, (ticks / 600.0) * -360.0, 360.0F);
        float renderAltitude = Math.max(1.0F, altitude);

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, satelliteTexture);
        RenderSystem.setShaderColor(colorR, colorG, colorB,
            getBlinkAlpha(isBlinking, blinkPeriod));

        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(inclination));
        poseStack.mulPose(Axis.XP.rotationDegrees(orbitAngle));

        var m = poseStack.last().pose();
        float s = 0.5F;
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        b.vertex(m, -s, renderAltitude, -s).uv(0, 0).endVertex();
        b.vertex(m,  s, renderAltitude, -s).uv(0, 1).endVertex();
        b.vertex(m,  s, renderAltitude,  s).uv(1, 1).endVertex();
        b.vertex(m, -s, renderAltitude,  s).uv(1, 0).endVertex();
        t.end();

        poseStack.popPose();
        RenderSystem.disableBlend();
    }

    /**
     * Draw a satellite quad with explicit parameters.
     * Used by the held-item preview. The caller owns the PoseStack.
     */
    public static void renderDefault(float partialTicks,
            net.minecraft.client.multiplayer.ClientLevel world,
            net.minecraft.client.Minecraft mc,
            com.mojang.blaze3d.vertex.PoseStack poseStack, float solarAngle, long seed,
            float r, float g, float b, float inclination, float altitude,
            float phaseOffset, boolean isBlinking, float blinkPeriod) {

        double ticks = (double) System.currentTimeMillis() / 50.0;
        float orbitAngle = applyPhaseOffsetToOrbitAngle(
            phaseOffset, altitude, (ticks / 600.0) * -360.0, 360.0F);
        float renderAltitude = Math.max(1.0F, altitude);

        Tesselator t = Tesselator.getInstance();
        BufferBuilder buf = t.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, satelliteTexture);
        RenderSystem.setShaderColor(r, g, b, getBlinkAlpha(isBlinking, blinkPeriod));

        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(inclination));
        poseStack.mulPose(Axis.XP.rotationDegrees(orbitAngle));

        var m = poseStack.last().pose();
        float s = 0.5F;
        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buf.vertex(m, -s, renderAltitude, -s).uv(0, 0).endVertex();
        buf.vertex(m,  s, renderAltitude, -s).uv(0, 1).endVertex();
        buf.vertex(m,  s, renderAltitude,  s).uv(1, 1).endVertex();
        buf.vertex(m, -s, renderAltitude,  s).uv(1, 0).endVertex();
        t.end();

        poseStack.popPose();
        RenderSystem.disableBlend();
    }

    /**
     * Draw the orbital ring for a satellite preview.
     * The caller owns the PoseStack; solar-angle rotation must already be applied.
     */
    public static void renderOrbitLine(float solarAngle, float r, float g, float b,
            float inclination, float altitude, boolean isBlinking, float blinkPeriod,
            com.mojang.blaze3d.vertex.PoseStack poseStack) {

        float renderAltitude = Math.max(1.0F, altitude);
        float alpha = 0.35F * getBlinkAlpha(isBlinking, blinkPeriod);

        Tesselator t = Tesselator.getInstance();
        BufferBuilder buf = t.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(1.0F);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(r, g, b, alpha);

        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(inclination));

        var m = poseStack.last().pose();
        buf.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i <= 72; i++) {
            double angle = Math.PI * 2.0 * i / 72.0;
            buf.vertex(m, 0.0f, (float) (renderAltitude * Math.cos(angle)),
                    (float) (renderAltitude * Math.sin(angle)))
                .color(r, g, b, alpha).endVertex();
        }
        t.end();

        poseStack.popPose();
        RenderSystem.disableBlend();
    }

    /** Convert phase offset to an orbit angle accounting for altitude speed. */
    public static float applyPhaseOffsetToOrbitAngle(float phaseOffset, float altitude,
            double baseAngle, float fullRotation) {
        double orbitSpeed = getAltitudeOrbitSpeed(altitude);
        double phase = normalizePhaseOffset(phaseOffset) / 360.0 * fullRotation;
        double angle = baseAngle * orbitSpeed + phase;
        double wrapped = angle % fullRotation;
        if (wrapped < 0.0) wrapped += fullRotation;
        return (float) wrapped;
    }

    /** Orbital speed in km/s at the given altitude (scaled for rendering). */
    public static float getOrbitSpeedKmPerSecond(float altitude) {
        double radiusKm = Math.max(1.0, altitude);
        double turnsPerSecond = getAltitudeOrbitSpeed(altitude) / 30.0;
        return (float) (2.0 * Math.PI * radiusKm * turnsPerSecond);
    }

    private static double getAltitudeOrbitSpeed(float altitude) {
        return Math.pow((double) DEFAULT_ALTITUDE_KM / Math.max(1.0, altitude), 1.5);
    }

    /** Blink alpha: ramps down over the blink period if blinking. */
    private static float getBlinkAlpha(boolean isBlinking, float blinkPeriod) {
        if (!isBlinking) return 1.0F;
        long cycleMillis = (long) (clampBlinkPeriod(blinkPeriod) * 1000.0F);
        if (cycleMillis <= 0L) return 1.0F;
        return 1.0F - (float) (System.currentTimeMillis() % cycleMillis) / cycleMillis;
    }

    public float getHealth() { return health; }

    // === Internal Helpers ===

    private static float[] getRegisteredColor(Item item) {
        Class<? extends Satellite> satelliteClass = itemToClass.get(item);
        if (satelliteClass == null)
            throw new IllegalStateException("No satellite class registered for item: " + item);
        return getRegisteredColor(satelliteClass);
    }

    private static float[] getRegisteredColor(Class<? extends Satellite> satelliteClass) {
        float[] color = satelliteColors.get(satelliteClass);
        if (color == null)
            throw new IllegalStateException("No color registered for satellite class: " + satelliteClass);
        return color;
    }
}
