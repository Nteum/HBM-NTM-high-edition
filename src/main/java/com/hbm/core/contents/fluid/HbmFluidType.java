package com.hbm.core.contents.fluid;

import com.hbm.HBMLang;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Consumer;

/**
 * HBM 流体类型 —— 继承 Forge FluidType，兼容 IFluidHandler 生态，
 * 同时支持 HBM 的 Trait 系统和复杂流体特性。
 *
 * <h3>架构</h3>
 * <pre>
 * ┌─────────────────────────────────────┐
 * │  Forge FluidType（基类）              │ ← 兼容所有管道/罐子/模组
 * │  ├── 注册: DeferredRegister          │
 * │  └── 渲染: IClientFluidTypeExtensions│
 * ├─────────────────────────────────────┤
 * │  HbmFluidType（扩展）                 │ ← HBM 特性系统
 * │  ├── 基础属性: 毒性/反应性/温度        │
 * │  ├── 标签 Trait: EnumSet<FluidTraits> │
 * │  ├── 数据 Trait: Map<Class, TraitData> │
 * │  └── 外观: FluidAppearance           │
 * └─────────────────────────────────────┘
 * </pre>
 */
public class HbmFluidType extends FluidType {
    public static final int ROOM_TEMPERATURE = 20;

    private final String name;     // 注册名
    // ===== 基础属性（双端安全） =====
    private final int toxicity;      // 0-5
    private final int flammability;    // 0-5
    private final int reactivity;   // 摄氏度
    private final EnumSymbol symbol;    // 性质
    // ===== Trait 存储 =====
    private final Set<FluidTraits> tags;
    private final Map<Class<?>, IFluidTrait> dataTraits;
    // ===== 外观（存 ResourceLocation + int，不存渲染对象） =====
    private final FluidAppearance appearance;
    // ===== 兼容旧 HBM 属性 =====
    private final boolean renderWithTint;
    private final int hbmColor; // 旧的 HEX 颜色，用于 pipe 着色和 GUI
    // ==== 用于注册方块 =====
    public ForgeFlowingFluid.Properties flowProperties;
    /**
     * 完整构造器。推荐使用 {@link FluidTypeBuilder} 链式构建。
     */
    HbmFluidType(String name, Properties properties, int toxicity, int flammability, int reactivity, EnumSymbol symbol,
                 Set<FluidTraits> tags, Map<Class<?>, IFluidTrait> dataTraits,
                 FluidAppearance appearance, boolean renderWithTint, int hbmColor) {
        super(properties);
        this.toxicity = toxicity;
        this.reactivity = reactivity;
        this.flammability = flammability;
        this.tags = Collections.unmodifiableSet(EnumSet.copyOf(tags));
        this.dataTraits = Collections.unmodifiableMap(new IdentityHashMap<>(dataTraits));
        this.appearance = appearance;
        this.renderWithTint = renderWithTint;
        this.hbmColor = hbmColor;
        this.name = name;
        this.symbol = symbol;
    }

    // ================================================================
    // Trait 查询
    // ================================================================

    /** 是否有指定标签 Trait */
    public boolean hasTrait(FluidTraits trait) {
        return tags.contains(trait);
    }

    /** 获取带数据的 Trait，无则返回 null */
    @SuppressWarnings("unchecked")
    public <T extends IFluidTrait> T getTrait(Class<T> key) {
        return (T) dataTraits.get(key);
    }

    /** 是否有指定类型的数据 Trait */
    public boolean hasTrait(Class<? extends IFluidTrait> key) {
        return dataTraits.containsKey(key);
    }

    /** 所有特质，含标签和数据 */
    public Collection<IFluidTrait> allTraits() {
        List<IFluidTrait> all = new ArrayList<>(tags);
        all.addAll(dataTraits.values());
        return Collections.unmodifiableList(all);
    }

//    public HbmFluidType withData(){
//
//    }

    // ================================================================
    // 基础属性查询
    // ================================================================
    public String getName() { return name; }
    public int getToxicity()        { return toxicity; }
    public int getReactivity()      { return reactivity; }
    public int getFlammability()     { return flammability; }
    public int getHbmColor()       { return hbmColor; }
    public int getTint() { return appearance.tint(); }
    public ResourceLocation getTexture() {return appearance.guiTexture(); }
    public boolean isHot() { return getTemperature() >= 100; }
    /** 是否腐蚀性 */
    public boolean isCorrosive() {
        var c = getTrait(TraitData.Corrosion.class);
        return c != null && c.rating() > 0;
    }
    public boolean isAntiMatter() { return hasTrait(FluidTraits.ANTIMATTER);}
    public boolean hasNoContainer() { return hasTrait(FluidTraits.NO_CONTAINER); }
    public boolean hasNoID() { return false; }
    public boolean needsLeadContainer() { return false; }
    public boolean isDispersable() { return !(hasTrait(FluidTraits.ANTIMATTER) || hasTrait(FluidTraits.NO_CONTAINER) || hasTrait(FluidTraits.VISCOUS)); }
    /** 是否常温气态 */
    public boolean isGaseous() {
        return hasTrait(FluidTraits.GASEOUS) || hasTrait(FluidTraits.GASEOUS_AT_ROOM_TEMP);
    }

    /** 是否强腐蚀（>50） */
    public boolean isStronglyCorrosive() {
        var c = getTrait(TraitData.Corrosion.class);
        return c != null && c.isStrong();
    }

    // ================================================================
    // 外观
    // ================================================================

    public FluidAppearance getAppearance() {
        return appearance;
    }

    // ================================================================
    // 客户端渲染注入
    // ================================================================

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return appearance.still();
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return appearance.flowing();
            }

            @Override
            public ResourceLocation getOverlayTexture() {
                return appearance.overlay().orElse(null);
            }

            @Override
            public int getTintColor() {
                return appearance.tint();
            }
            //修改流体中看见雾的颜色
            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return Vec3.fromRGB24(appearance.tint()).toVector3f();
            }
            //液体中的能见度，或者说雾的范围。
            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                RenderSystem.setShaderFogStart(1f);
                RenderSystem.setShaderFogEnd(6f); // distance when the fog starts
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void addInfo(List<Component> info) {
        int temperature = this.getTemperature();
        if(temperature != ROOM_TEMPERATURE) {
            info.add(Component.literal(temperature + "°C").withStyle(temperature < 0 ? ChatFormatting.BLUE : ChatFormatting.RED));
        }

        boolean shiftHeld = isLeftShiftPressed();

        int listSize = info.size();

        for (IFluidTrait trait : this.dataTraits.values()) {
            if (trait == null) continue;
            trait.addTooltip(info);
            if (shiftHeld) trait.addTooltipHidden(info);
        }

        if (listSize != info.size() && !shiftHeld) info.add(HBMLang.GUI_SHOW_HIDDEN_INFO
                .translate(Component.literal("LSHIFT").withStyle(ChatFormatting.YELLOW))
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isLeftShiftPressed() {
        Window window = Minecraft.getInstance().getWindow();
        if (window == null) return false;
        // GLFW.glfwGetKey 返回 1 (按下) 或 0 (释放)
        return GLFW.glfwGetKey(window.getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;
    }

    // ================================================================
    // Builder
    // ================================================================

    public static FluidTypeBuilder builder() {
        return new FluidTypeBuilder();
    }
}
