package com.hbm.render.overlay;

import com.hbm.HBM;
import com.hbm.config.ClientConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.fml.ModList;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Lightweight debug overlay that shows "[player]:{shortSHA}" in the lower-left corner.
 * The SHA token is derived from the mod version and player UUID so QA can correlate reports.
 */
@OnlyIn(Dist.CLIENT)
public final class DebugTagOverlay {

    private static final int TEXT_COLOR = 0xC0FFFFFF;
    private static final int BACKGROUND_COLOR = 0x4C000000;
    private static final int PADDING = 6;
    private static final int SHORT_SHA_LENGTH = 8;
    private static final Map<UUID, String> SIGNATURE_CACHE = new HashMap<>();
    private static String cachedVersion;

    private DebugTagOverlay() {
    }

    public static void onGuiRender(RenderGuiEvent.Post event) {
        if (!ClientConfig.DEBUG_TAG_OVERLAY.get()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.options.renderDebug) {
            return;
        }
        String versionLine = "HBM-" + getVersion() + " Debug Info";
        GuiGraphics graphics = event.getGuiGraphics();
        Font font = mc.font;
        String tag = "[" + mc.player.getGameProfile().getName() + "]:{" + getSignature(mc.player.getUUID()) + "}";
        int width = Math.max(font.width(versionLine), font.width(tag));
        int x = PADDING;
        int y = event.getWindow().getGuiScaledHeight() - (font.lineHeight * 2) - (PADDING + 2);
        RenderSystem.enableBlend();
        graphics.fill(x - 3, y - 4, x + width + 3, y + font.lineHeight * 2 + 4, BACKGROUND_COLOR);
        graphics.drawString(font, versionLine, x, y, TEXT_COLOR, false);
        graphics.drawString(font, tag, x, y + font.lineHeight + 2, TEXT_COLOR, false);
        RenderSystem.disableBlend();
    }

    private static String getSignature(UUID uuid) {
        return SIGNATURE_CACHE.computeIfAbsent(uuid, DebugTagOverlay::computeSignature);
    }

    private static String computeSignature(UUID uuid) {
        String source = getVersion() + ":" + uuid;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(source.getBytes(StandardCharsets.UTF_8));
            String hex = HexFormat.of().withUpperCase().formatHex(hash);
            return hex.substring(0, Math.min(SHORT_SHA_LENGTH, hex.length()));
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(source.hashCode()).toUpperCase(Locale.ROOT);
        }
    }

    private static String getVersion() {
        if (cachedVersion == null) {
            cachedVersion = ModList.get().getModContainerById(HBM.MODID)
                    .map(container -> container.getModInfo().getVersion().toString())
                    .orElse("dev-build");
        }
        return cachedVersion;
    }
}
