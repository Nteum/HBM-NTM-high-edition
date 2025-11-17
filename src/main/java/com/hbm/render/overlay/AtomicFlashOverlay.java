package com.hbm.render.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;

/**
 * Client-side controller that draws the short-lived white flash after an
 * atomic detonation. Activated via {@link #trigger(float, int)} when the
 * server sends {@code S2CAtomicFlashPacket}.
 */
@OnlyIn(Dist.CLIENT)
public final class AtomicFlashOverlay {

    private static int ticksRemaining;
    private static int totalTicks;
    private static float maxAlpha;

    private AtomicFlashOverlay() {
    }

    public static void trigger(float strength, int durationTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }
        totalTicks = Math.max(1, durationTicks);
        ticksRemaining = totalTicks;
        maxAlpha = Mth.clamp(strength, 0.1F, 1.0F);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ticksRemaining <= 0) {
            return;
        }
        ticksRemaining--;
    }

    public static void onGuiRender(RenderGuiEvent.Post event) {
        if (ticksRemaining <= 0) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) {
            return;
        }
        float progress = 1.0F - (ticksRemaining / (float) totalTicks);
        float alpha = maxAlpha * (1.0F - progress);
        if (alpha <= 0.01F) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();
        int color = ((int) (alpha * 255.0F) << 24) | 0x00FFFFFF;
        RenderSystem.enableBlend();
        graphics.fill(0, 0, width, height, color);
        RenderSystem.disableBlend();
    }
}
