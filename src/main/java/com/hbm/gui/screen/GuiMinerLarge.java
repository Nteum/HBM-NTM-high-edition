package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.blockentity.machine.TileMinerLarge;
import com.hbm.gui.menu.MenuMinerLarge;
import com.hbm.item.misc.ItemDrillbit;
import com.hbm.core.network.HBMNetwork;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import com.hbm.registries.ModSounds;
import com.hbm.utils.math.BitUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuiMinerLarge extends BaseMachineGui<MenuMinerLarge> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/machine/gui_mining_drill.png");

    public GuiMinerLarge(MenuMinerLarge pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        this.imageWidth = 242;
        this.imageHeight = 204;
        super.init();
        this.titleLabelX += 33;
        titleLabelY -= 16;
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xffffffff, false);
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        ItemDrillbit drill = menu.getDrill();
        int i = (int) (menu.getPower() * 52 / TileMinerLarge.MAX_POWER);
        pGuiGraphics.blit(TEXTURE, leftPos + 220, topPos + 70 - i, 229, 156 - i, 16, i);
        if (i > 0) pGuiGraphics.blit(TEXTURE, leftPos + 224, topPos + 4, 239, 156, 9, 12);
        if (menu.getSlot(4).hasItem() && menu.getSlot(4).getItem().getItem() instanceof ItemDrillbit && System.currentTimeMillis() % 1000 < 500)
            pGuiGraphics.blit(TEXTURE, leftPos + 171, topPos + 74, 209, 154, 18, 18);
        int state = menu.getState();
        if (BitUtil.getBool(state, TileMinerLarge.KEY_ENABLE_DRILL)){
            pGuiGraphics.blit(TEXTURE, leftPos + 6, topPos + 42, 209, 114, 20, 40);
            if (drill != null && menu.getPower() > 0) pGuiGraphics.blit(TEXTURE, leftPos + 11, topPos + 5, 209, 104, 10, 10);
            else if (System.currentTimeMillis() % 1000 < 500) pGuiGraphics.blit(TEXTURE, leftPos + 11, topPos + 5, 219, 104, 10, 10);
        }
        if (BitUtil.getBool(state, TileMinerLarge.KEY_ENABLE_CRUSHER)){
            pGuiGraphics.blit(TEXTURE, leftPos + 30, topPos + 42, 209, 114, 20, 40);
            pGuiGraphics.blit(TEXTURE, leftPos + 35, topPos + 5, 209, 104, 10, 10);
        }
        if (BitUtil.getBool(state, TileMinerLarge.KEY_ENABLE_WALLING)){
            pGuiGraphics.blit(TEXTURE, leftPos + 54, topPos + 42, 209, 114, 20, 40);
            pGuiGraphics.blit(TEXTURE, leftPos + 59, topPos + 5, 209, 104, 10, 10);
        }
        if (BitUtil.getBool(state, TileMinerLarge.KEY_ENABLE_VEINMINER)){
            pGuiGraphics.blit(TEXTURE, leftPos + 78, topPos + 42, 209, 114, 20, 40);
            pGuiGraphics.blit(TEXTURE, leftPos + 83, topPos + 5, 209, 104, 10, 10);
        }else {
            if (System.currentTimeMillis() % 1000 < 500) pGuiGraphics.blit(TEXTURE, leftPos + 83, topPos + 5, 219, 104, 10, 10);
        }
        if (BitUtil.getBool(state, TileMinerLarge.KEY_ENABLE_SILKTOUCH)){
            pGuiGraphics.blit(TEXTURE, leftPos + 102, topPos + 42, 209, 114, 20, 40);
            pGuiGraphics.blit(TEXTURE, leftPos + 35, topPos + 5, 209, 104, 10, 10);
        }else {
            if (System.currentTimeMillis() % 1000 < 500) pGuiGraphics.blit(TEXTURE, leftPos + 35, topPos + 5, 219, 104, 10, 10);
        }
        FluidStack fluidStack = menu.getFluidStack();
        RenderUtils.fluidTank(leftPos + 102, topPos + 42, 16, 52, (float) fluidStack.getAmount() / TileMinerLarge.MAX_TANK_CAPACITY, pGuiGraphics, fluidStack.getFluid());
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        showBgTexture(pGuiGraphics, TEXTURE);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        List<Component> tooltips = new ArrayList<>();
        if (isHovering(6, 42, 20, 40, pX, pY)){
            tooltips.add(HBMLang.GUI_MINER_LARGE_DRILL.translate());
        }else if (isHovering(30, 42, 20, 40, pX, pY)){
            tooltips.add(HBMLang.GUI_MINER_LARGE_CRUSHER.translate());
        }else if (isHovering(54, 42, 20, 40, pX, pY)){
            tooltips.add(HBMLang.GUI_MINER_LARGE_WALLING.translate());
        }else if (isHovering(78, 42, 20, 40, pX, pY)){
            tooltips.add(HBMLang.GUI_MINER_LARGE_VEIN_MINER.translate());
        }else if (isHovering(102, 42, 20, 40, pX, pY)){
            tooltips.add(HBMLang.GUI_MINER_LARGE_SILK_TOUCH.translate());
        }else if (isHovering(220, 18, 16, 52, pX, pY)){
            tooltips.add(HBMLang.GUI_TOOLTIP_ENERGY.translate(menu.getPower(), TileMinerLarge.MAX_POWER));
        }else if (isHovering(202, 18, 16, 52, pX, pY)){
            FluidStack fluidStack = menu.getFluidStack();
            tooltips.add(HBMLang.GUI_TOOLTIP_FLUID.translate(fluidStack.getFluid().getFluidType().getDescriptionId(), fluidStack.getAmount()));
        }
        if (!tooltips.isEmpty()) pGuiGraphics.renderComponentTooltip(font, tooltips, pX, pY);
    }

    @Override
    public boolean mouseClicked(double pX, double pY, int pButton) {
        int state = menu.getState();
        int oldState = state;
        if (isHovering(6, 42, 20, 40, pX, pY)){
            state = BitUtil.setBool(state, TileMinerLarge.KEY_ENABLE_DRILL, !BitUtil.getBool(state, TileMinerLarge.KEY_ENABLE_DRILL));
        }else if (isHovering(30, 42, 20, 40, pX, pY)){
            state = BitUtil.setBool(state, TileMinerLarge.KEY_ENABLE_CRUSHER, !BitUtil.getBool(state, TileMinerLarge.KEY_ENABLE_CRUSHER));
        }else if (isHovering(54, 42, 20, 40, pX, pY)){
            state = BitUtil.setBool(state, TileMinerLarge.KEY_ENABLE_WALLING, !BitUtil.getBool(state, TileMinerLarge.KEY_ENABLE_WALLING));
        }else if (isHovering(78, 42, 20, 40, pX, pY)){
            state = BitUtil.setBool(state, TileMinerLarge.KEY_ENABLE_VEINMINER, !BitUtil.getBool(state, TileMinerLarge.KEY_ENABLE_VEINMINER));
        }else if (isHovering(102, 42, 20, 40, pX, pY)){
            state = BitUtil.setBool(state, TileMinerLarge.KEY_ENABLE_SILKTOUCH, !BitUtil.getBool(state, TileMinerLarge.KEY_ENABLE_SILKTOUCH));
        }
        if (oldState != state){
            if (minecraft != null && minecraft.level != null) minecraft.level.playSound(null, menu.getPos(), ModSounds.BLOCK_LEVER_LARGE.get(), SoundSource.BLOCKS, 0.25f, 1.0f);
            CompoundTag tag = new CompoundTag();
            tag.putByte(HBMKey.STATE, (byte) state);
            HBMNetwork.sendToServer(new C2SSyncTileMessage(menu.getPos(), tag));
        }
        return super.mouseClicked(pX, pY, pButton);
    }
}
