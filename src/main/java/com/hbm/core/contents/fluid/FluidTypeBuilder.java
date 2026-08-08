package com.hbm.core.contents.fluid;

import com.hbm.HBM;
import com.hbm.registries.RegistryHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * HbmFluidType 的链式构建器。
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 简单流体：水基冷却液
 * HbmFluidType coolant = HbmFluidType.builder()
 *     .name("coolant")
 *     .temperature(25)
 *     .color(0x3366FF)
 *     .stillTexture(rl("block/coolant_still"))
 *     .flowingTexture(rl("block/coolant_flow"))
 *     .addTag(FluidTraits.LIQUID)
 *     .addData(new TraitData.Coolable("hot_coolant", 1000, 900, 5000, List.of(
 *         new HeatingType.Efficiency(HeatingType.HEAT_EXCHANGER, 1.0)
 *     )))
 *     .build();
 *
 * // 复杂流体：原油
 * HbmFluidType crudeOil = HbmFluidType.builder()
 *     .name("crude_oil")
 *     .temperature(20).toxicity(1).reactivity(2)
 *     .color(0x1A1A1A).tint(0x2A2A2A)
 *     .stillTexture(rl("block/crude_oil_still"))
 *     .flowingTexture(rl("block/crude_oil_flow"))
 *     .overlayTexture(rl("block/crude_oil_overlay"))
 *     .addTag(FluidTraits.LIQUID)
 *     .addTag(FluidTraits.VISCOUS)
 *     .addTag(FluidTraits.FLAMMABLE)       // ← 标签 trait
 *     .addData(new TraitData.Flammable(300_000))
 *     .addData(new TraitData.Corrosion(25))
 *     .addData(new TraitData.Polluting(
 *         Map.of(PollutionType.SOOT, 0.5f),
 *         Map.of(PollutionType.SOOT, 2.0f)))
 *     .build();
 * }</pre>
 */
public class FluidTypeBuilder {

    // Forge FluidType 属性
    private String name;
    private String descriptionId;
    private float motionScale = 0.014f;
    private boolean canPushEntity = true;
    private boolean canSwim = false;
    private boolean canDrown = false;
    private float fallDistanceModifier;
    private boolean canExtinguish = false;
    private boolean canConvertToSource = false;
    private boolean supportsBoating;
    @Nullable
    private BlockPathTypes pathType, adjacentPathType;
    private boolean canHydrate = false;
    private int lightLevel = 0;
    private int density = 1000;
    private int temperature = HbmFluidType.ROOM_TEMPERATURE;
    private int viscosity = 1000;
    private Rarity rarity;

    // HBM 属性
    private int toxicity = 0;
    public int flammability = 0;
    private int reactivity = 0;
    private EnumSymbol symbol = EnumSymbol.NONE;
//    private int tickDelay = 5;
//    private double explosionResistance = 100;
    // Trait
    private final Set<FluidTraits> tags = EnumSet.noneOf(FluidTraits.class);
    private final Map<Class<?>, IFluidTrait> dataTraits = new IdentityHashMap<>();
    // 客户端属性
    private int hbmColor = 0xFFFFFF;
    public boolean renderWithTint = false;
    protected Map<SoundAction, SoundEvent> sounds;

    // 外观
    private ResourceLocation stillTexture;;
    private ResourceLocation flowingTexture;
    private int tint = -1;      // tint和color似乎有些微妙的差别
    private Optional<ResourceLocation> overlayTexture = Optional.empty();
    private ResourceLocation guiTexture;

    // ===== Forge 属性 =====
    public FluidTypeBuilder descriptionId(String id) { this.descriptionId = id; return this; }
    public FluidTypeBuilder density(int v) { this.density = v; return this; }
    public FluidTypeBuilder viscosity(int v) { this.viscosity = v; return this; }
    public FluidTypeBuilder lightLevel(int v) { this.lightLevel = v; return this; }
    public FluidTypeBuilder canConvertToSource() { this.canConvertToSource = true; return this; }
    public FluidTypeBuilder canExtinguish() { this.canExtinguish = true; return this; }
    public FluidTypeBuilder canSwim() { this.canSwim = true; return this; }
    public FluidTypeBuilder noPush() { this.canPushEntity = false; return this; }
    public FluidTypeBuilder gaseous() {
        this.density = -1000;
        this.viscosity = 200;
        this.motionScale = 0.002f;
        return this;
    }
    // ===== HBM 属性（快捷名） =====
    public FluidTypeBuilder name(String name) { this.name = name; return this; }
    public FluidTypeBuilder toxicity(int v) { this.toxicity = v; return this; }
    public FluidTypeBuilder reactivity(int v) { this.reactivity = v; return this; }
    public FluidTypeBuilder temperature(int v) { this.temperature = v; return this; }
    public FluidTypeBuilder color(int hex) { this.hbmColor = hex; return this; }
    public FluidTypeBuilder symbol(EnumSymbol symbol){ this.symbol = symbol; return this; }
    // ===== 外观 =====
    public FluidTypeBuilder stillTexture(ResourceLocation rl) { this.stillTexture = rl; return this; }
    public FluidTypeBuilder flowingTexture(ResourceLocation rl) { this.flowingTexture = rl; return this; }
    public FluidTypeBuilder tint(int hex) { this.tint = hex; return this; }
    public FluidTypeBuilder overlayTexture(ResourceLocation rl) { this.overlayTexture = Optional.of(rl); return this; }

    // ===== Trait =====
    public FluidTypeBuilder addTag(FluidTraits trait) { this.tags.add(trait); return this; }
    public FluidTypeBuilder addData(IFluidTrait data) { this.dataTraits.put(data.getClass(), data); return this; }

    @SafeVarargs
    public final FluidTypeBuilder addTags(FluidTraits... traits) {
        Collections.addAll(this.tags, traits);
        return this;
    }

    public FluidTypeBuilder addData(IFluidTrait ... data){
        for (IFluidTrait fluidTrait : data) {
            addData(fluidTrait);
        }
        return this;
    }

    private static final ResourceLocation WATER_STILL_TEX = ResourceLocation.parse("block/water_still");
    private static final ResourceLocation WATER_FLOWING_TEX = ResourceLocation.parse("block/water_flow");

    // ===== 构建 =====
    public HbmFluidType build() {
        if (descriptionId == null || descriptionId.isEmpty()) descriptionId = "fluid." + HBM.MODID + "." + name;
        if (guiTexture == null) guiTexture = RegistryHelper.rl("textures/gui/fluids/" + name + ".png");
        if (stillTexture == null) stillTexture = WATER_STILL_TEX;
        if (flowingTexture == null) flowingTexture = WATER_FLOWING_TEX;

        FluidType.Properties props = FluidType.Properties.create()
                .descriptionId(descriptionId)
                .density(density)
                .viscosity(viscosity)
                .lightLevel(lightLevel)
                .canConvertToSource(canConvertToSource)
                .canHydrate(canHydrate)
                .canDrown(canDrown)
                .canPushEntity(canPushEntity)
                .canSwim(canSwim)
                .canExtinguish(canExtinguish)
                .fallDistanceModifier(fallDistanceModifier)
                .rarity(rarity)
                .supportsBoating(supportsBoating)
                .pathType(pathType)
                .adjacentPathType(adjacentPathType)
                .motionScale(motionScale)
                ;

        if (sounds != null) sounds.forEach(props::sound);

        var appearance = new FluidAppearance(stillTexture, flowingTexture, tint, overlayTexture, guiTexture);

        return new HbmFluidType(name, props, toxicity, flammability, reactivity, symbol,
                tags, dataTraits, appearance, renderWithTint, hbmColor);
    }
}
