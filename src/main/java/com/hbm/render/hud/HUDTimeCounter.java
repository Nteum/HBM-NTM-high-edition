package com.hbm.render.hud;

import com.hbm.network.ClientMsgHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public abstract class HUDTimeCounter implements IGuiOverlay {
    protected final ResourceLocation overlayId;

    public HUDTimeCounter(ResourceLocation overlayId) {
        this.overlayId = overlayId;
    }
}
