package com.hbm.Inventory.fluid;

import com.google.gson.JsonObject;
import com.hbm.HBM;
import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.trait.FluidTrait;
import com.hbm.Inventory.fluid.trait.FluidTraitSimple;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import com.hbm.Inventory.fluid.trait.FluidTraitSimple.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 流体类型，模仿HBM的流体类型
 * */
public class ExtendedFluidType extends FluidType {
    public String name;
    public ExtendedProperties hbmProperties;
    public ForgeFlowingFluid.Properties flowProperties;
    // 定义了源source的纹理图片，流动的纹理图片，以及流体覆盖层的图片（指的是颜色，例如水的蓝色纹理，岩浆的红色纹理，你可以到原版对应的位置看看是什么图片就知道了）
    public final ResourceLocation stillTexture;
    public final ResourceLocation flowingTexture;
    public final ResourceLocation overlayTexture;
    public final ResourceLocation guiTexture = GUI_CUSTOM_WATER;
    // 流体的着色颜色
    public final int tintColor;
    // 从流体中看外面的雾的颜色
    public final Vector3f fogColor;

    //原版水的静止、流动、覆盖的灰度贴图
    public static final ResourceLocation WATER_STILL_TEX = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOWING_TEX = new ResourceLocation("block/water_flow");
    public static final ResourceLocation WATER_OVERLAY = new ResourceLocation("block/water_still");

    // 流体GUI
    public static final ResourceLocation GUI_WATER = HBM.rl("textures/gui/fluids/water.png");
    public static final ResourceLocation GUI_LAVA = HBM.rl("textures/gui/fluids/lava.png");
    public static final ResourceLocation GUI_MILK = HBM.rl("textures/gui/fluids/milk.png");  // 如果不是原版也找不到颜色，就设为牛奶色，作为缺省颜色。
    public static final ResourceLocation GUI_CUSTOM_WATER = HBM.rl("textures/gui/fluids/custom_water.png");
    public static final ResourceLocation GUI_CUSTOM_OIL = HBM.rl("textures/gui/fluids/custom_oil.png");
    public static final ResourceLocation GUI_CUSTOM_TOXIN = HBM.rl("textures/gui/fluids/custom_toxin.png");
    public static final ResourceLocation GUI_CUSTOM_LAVA = HBM.rl("textures/gui/fluids/custom_lava.png");

    public static final FluidType.Properties prop_air = FluidType.Properties.create().motionScale(1D).canPushEntity(false).canSwim(false).canDrown(false).fallDistanceModifier(1F).pathType(null).adjacentPathType(null).density(0).temperature(0).viscosity(0);
    public static final FluidType.Properties prop_lava = FluidType.Properties.create().canSwim(false).canDrown(false).pathType(BlockPathTypes.LAVA).adjacentPathType(null).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA).lightLevel(15).density(3000).viscosity(6000).temperature(1300);
    public static final FluidType.Properties prop_water =  FluidType.Properties.create().fallDistanceModifier(0F).canExtinguish(true).canConvertToSource(true).supportsBoating(true).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY).sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH).canHydrate(true);
    // 溶液
    public static final FluidType.Properties solution = prop_water.canConvertToSource(false);

    public ResourceLocation getStillTexture() {
        return stillTexture;
    }

    public ResourceLocation getFlowingTexture() {
        return flowingTexture;
    }

    public int getTintColor() {
        return tintColor;
    }

    public ResourceLocation getOverlayTexture() {
        return overlayTexture;
    }
    public ResourceLocation getGUITexture(){ return guiTexture;}

    public Vector3f getFogColor() {
        return fogColor;
    }
    public ExtendedFluidType(final String name, final int color, final Properties properties, final ExtendedProperties properties2){
        this(name, WATER_STILL_TEX, WATER_FLOWING_TEX, WATER_OVERLAY, color, new Vector3f(color % 256, (int)(color / 256) % 256, (int)(color / (256*256)) % 256),
                properties, properties2);
    }
    public ExtendedFluidType(final String name,final ResourceLocation stillTexture, final ResourceLocation flowingTexture, final ResourceLocation overlayTexture,
                             final int tintColor, final Vector3f fogColor, final Properties properties, final ExtendedProperties properties2) {
        super(properties.descriptionId("block.hbm." + name));
        this.name = name;
        this.hbmProperties = properties2;
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.tintColor = tintColor;
        this.fogColor = fogColor;
    }

    public static final FT_Liquid LIQUID = new FT_Liquid();
    public static final FT_Viscous VISCOUS = new FT_Viscous();
    public static final FT_Gaseous_ART EVAP = new FT_Gaseous_ART();
    public static final FT_Gaseous GASEOUS = new FT_Gaseous();
    public static final FT_Plasma PLASMA = new FT_Plasma();
    public static final FT_Amat ANTI = new FT_Amat();
    public static final FT_LeadContainer LEADCON = new FT_LeadContainer();
    public static final FT_NoContainer NOCON = new FT_NoContainer();
    public static final FT_NoID NOID = new FT_NoID();
    public static final FT_Delicious DELICIOUS = new FT_Delicious();
    public static final FT_Unsiphonable UNSIPHONABLE = new FT_Unsiphonable();

    public static class ExtendedProperties{
        public int poison = 0;          // 毒性
        public int flammability = 0;    // 可燃性
        public int reactivity = 0;      // 反应能力

        public HashMap<Class<? extends FluidTrait>, FluidTrait> traits = new HashMap();
        public static ExtendedProperties of(){
            return new ExtendedProperties();
        }
        public ExtendedProperties pfr(int p, int f, int r){
            this.poison = p;
            this.flammability = f;
            this.reactivity = r;
            return this;
        }

        public ExtendedProperties traits(FluidTrait... traits){
            for (FluidTrait trait : traits) {
                this.traits.put(trait.getClass(), trait);
            }
            return this;
        }
    }
    public <T extends FluidTrait> T getTrait(Class<? extends T> trait) { //generics, yeah!
        return (T) this.hbmProperties.traits.get(trait);
    }
    //需要继承这个方法，以便可以在客户端渲染流体
    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowingTexture;
            }

            @Override
            public @Nullable ResourceLocation getOverlayTexture() {
                return overlayTexture;
            }

            @Override
            public int getTintColor() {
                return tintColor;
            }
            //修改流体中看见雾的颜色
            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return fogColor;
            }
            //液体中的能见度，或者说雾的范围。
            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                RenderSystem.setShaderFogStart(1f);
                RenderSystem.setShaderFogEnd(6f); // distance when the fog starts
            }
        });
    }
    //============================Enums============================
    public static enum FuelGrade {
        LOW("Low"),			//heating and industrial oil				< star engine, iGen
        MEDIUM("Medium"),	//petroil									< diesel generator
        HIGH("High"),		//diesel, gasoline							< HP engine
        AERO("Aviation"),	//kerosene and other light aviation fuels	< turbofan
        GAS("Gaseous");		//fuel gasses like NG, PG and syngas		< gas turbine

        private String grade;

        private FuelGrade(String grade) {
            this.grade = grade;
        }

        public String getGrade() {
            return this.grade;
        }
    }

    public static FluidType getFTFromJson(JsonObject json, String key){
        return ForgeRegistries.FLUID_TYPES.get().getValue(new ResourceLocation(GsonHelper.convertToString(json, key)));
    }

    public static class PropertiesHolder extends ForgeFlowingFluid.Properties{
        public FluidType fluidType;
        public Fluid still;
        public Fluid flowing;
        public Item bucket;
        public LiquidBlock block;
        public PropertiesHolder(FluidType fluidType, Fluid still, Fluid flowing){
            super(()->fluidType, ()->still, ()->flowing);
        }
//        public PropertiesHolder(Supplier<? extends FluidType> fluidType, Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing) {
//            super(fluidType, still, flowing);
//        }
    }
}
