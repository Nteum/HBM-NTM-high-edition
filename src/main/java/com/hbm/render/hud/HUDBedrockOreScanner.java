package com.hbm.render.hud;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.config.ClientConfig;
import com.hbm.item.env.ItemBedrockOreCombine;
import com.hbm.item.env.ItemBedrockOreScanner;
import com.hbm.network.ClientMsgHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class HUDBedrockOreScanner extends  HUDTimeCounter{
    public HUDBedrockOreScanner() {
        super(ItemBedrockOreScanner.id);
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!HUDHelper.shouldDraw(this.overlayId)) return;
        ClientHUDDataCache clientHUDDataCache = ClientMsgHandler.getOrCreate().getTempData().get(this.overlayId);
        CompoundTag data = clientHUDDataCache.data();
        ResourceKey<Level> dimension = Minecraft.getInstance().level.dimension();
        ItemBedrockOreCombine.CelestialBedrockOre celestialBedrockOre = ItemBedrockOreCombine.CelestialBedrockOre.get(dimension);
        if (celestialBedrockOre == null) return;
        List<Component> infos = new ArrayList<>();
        if (data.contains(HBMKey.TIER, Tag.TAG_INT)) {
            MutableComponent component = HBMLang.TIER.translate(data.getInt(HBMKey.TIER));
            if (data.contains(HBMKey.FLUIDS, Tag.TAG_COMPOUND)) {
                Tag tag = data.get(HBMKey.FLUIDS);
                FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(data.getCompound(HBMKey.FLUIDS));
                component.append(" - ").append(HBMLang.GUI_TOOLTIP_FLUID.translate(fluidStack.getFluid(), fluidStack.getAmount()));
            }
            infos.add(component);
        }
        for (ItemBedrockOreCombine.CelestialBedrockOreType type : celestialBedrockOre.types) {
            int anInt = data.getInt(type.suffix);
            infos.add(Component.translatable("item." + HBM.MODID + ".bedrock.ore." + type.suffix).append(": " + (int)(anInt * 100 / 100D) + "(").append(ItemBedrockOreScanner.translateDensity(anInt).withStyle(ItemBedrockOreScanner.getColor(anInt))).append(")").withStyle(ChatFormatting.RESET));
        }
        HUDHelper.drawInfoBoard(infos, gui, guiGraphics, partialTick, screenWidth, screenHeight);
    }
}
