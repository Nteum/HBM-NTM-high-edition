package com.hbm.gui.screen;

import com.hbm.gui.menu.BaseMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Shared scaffolding for RBMK GUIs. Handles sectioned layout (status,
 * controls, advanced), detail toggling, and safety coloring so subclasses
 * can focus on their specific controls.
 */
public abstract class AbstractRBMKScreen<M extends BaseMachineMenu> extends AbstractContainerScreen<M> {

    protected static final int STATE_X = 12;
    protected static final int STATE_Y = 28;
    protected static final int CONTROL_X = 168;
    protected static final int CONTROL_Y = 28;
    protected static final int ADVANCED_X = 12;
    protected static final int ADVANCED_Y = 132;
    protected static final int LINE_HEIGHT = 11;
    protected static final int SECTION_GAP = 6;

    protected static final int HEAT_COLOR = 0xFFAA33;
    protected static final int ENERGY_COLOR = 0x66FF66;
    protected static final int COOLANT_COLOR = 0x66CCFF;
    protected static final int STEAM_COLOR = 0xAAAADD;
    protected static final int SECTION_COLOR = 0xF0F0F0;

    protected boolean detailsVisible = false;
    private Button detailsToggle;

    protected AbstractRBMKScreen(M menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        if (enableAdvancedToggle()) {
            addDetailsToggle();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean hasData = hasColumnData();
        if (detailsToggle != null) {
            detailsToggle.active = hasData;
            detailsToggle.visible = hasData;
        }
        updateInteractionState(hasData);
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        if (!hasColumnData()) {
            graphics.drawString(this.font, Component.translatable("gui.hbm.rbmk.no_column"), guiX(STATE_X), guiY(STATE_Y + LINE_HEIGHT), 0xFF5555, false);
            return;
        }
        RBMKReadings readings = primaryReadings();
        int y = drawSectionHeader(graphics, STATE_X, STATE_Y, Component.translatable("gui.hbm.rbmk.section.status"));
        drawStateSection(graphics, readings, STATE_X, y);
        renderControlPanel(graphics, readings);
        renderAdvancedPanel(graphics, readings);
    }

    private void addDetailsToggle() {
        int buttonX = guiX(ADVANCED_X);
        int buttonY = guiY(ADVANCED_Y - 18);
        detailsToggle = Button.builder(getDetailsLabel(), b -> toggleDetails())
                .bounds(buttonX, buttonY, 102, 18)
                .build();
        addRenderableWidget(detailsToggle);
    }

    private void toggleDetails() {
        detailsVisible = !detailsVisible;
        if (detailsToggle != null) {
            detailsToggle.setMessage(getDetailsLabel());
        }
    }

    private Component getDetailsLabel() {
        return Component.translatable(detailsVisible ? "gui.hbm.rbmk.details.hide" : "gui.hbm.rbmk.details.show");
    }

    private void renderAdvancedPanel(GuiGraphics graphics, RBMKReadings readings) {
        int y = drawSectionHeader(graphics, ADVANCED_X, ADVANCED_Y, Component.translatable("gui.hbm.rbmk.section.advanced"));
        if (!detailsVisible) {
            return;
        }
        drawAdvancedContent(graphics, readings, ADVANCED_X + 4, y);
    }

    protected void drawStateSection(GuiGraphics graphics, RBMKReadings readings, int x, int y) {
        ReactorStatus status = resolveStatus(readings);
        y = drawValueLine(graphics, x, y, Component.translatable("gui.hbm.rbmk.status", Component.translatable(status.translationKey())), status.color());
        y = drawValueLine(graphics, x, y,
                Component.translatable("gui.hbm.rbmk.heat", formatOneDecimal(readings.heat()), formatOneDecimal(readings.meltdownThreshold())),
                HEAT_COLOR);
        y = drawValueLine(graphics, x, y,
                Component.translatable("gui.hbm.rbmk.energy", readings.energyStored(), readings.energyCapacity()),
                ENERGY_COLOR);
        y = drawValueLine(graphics, x, y,
                Component.translatable("gui.hbm.rbmk.coolant", readings.coolantMb()),
                COOLANT_COLOR);
        drawValueLine(graphics, x, y,
                Component.translatable("gui.hbm.rbmk.steam", readings.steamMb()),
                STEAM_COLOR);
    }

    protected int drawSectionHeader(GuiGraphics graphics, int x, int y, Component text) {
        graphics.drawString(this.font, text, guiX(x), guiY(y), SECTION_COLOR, false);
        return y + LINE_HEIGHT;
    }

    protected int drawValueLine(GuiGraphics graphics, int x, int y, Component text, int color) {
        graphics.drawString(this.font, text, guiX(x), guiY(y), color, false);
        return y + LINE_HEIGHT;
    }

    protected static String formatOneDecimal(float value) {
        return String.format(java.util.Locale.ROOT, "%.1f", value);
    }

    protected ReactorStatus resolveStatus(RBMKReadings readings) {
        if (readings.meltdownThreshold() <= 0) {
            return ReactorStatus.OFFLINE;
        }
        float ratio = readings.heat() / readings.meltdownThreshold();
        if (ratio >= 0.9F) {
            return ReactorStatus.CRITICAL;
        }
        if (ratio >= 0.7F) {
            return ReactorStatus.WARNING;
        }
        if (readings.energyStored() > 0 || readings.steamMb() > 0) {
            return ReactorStatus.RUNNING;
        }
        return ReactorStatus.OFFLINE;
    }

    private void drawAdvancedContent(GuiGraphics graphics, RBMKReadings readings, int x, int startY) {
        renderAdvancedDetails(graphics, readings, x, startY);
    }

    protected int guiX(int localX) {
        return this.leftPos + localX;
    }

    protected int guiY(int localY) {
        return this.topPos + localY;
    }

    protected abstract boolean hasColumnData();

    protected abstract RBMKReadings primaryReadings();

    protected abstract void renderControlPanel(GuiGraphics graphics, RBMKReadings readings);

    protected abstract void renderAdvancedDetails(GuiGraphics graphics, RBMKReadings readings, int x, int startY);

    protected abstract void updateInteractionState(boolean hasData);

    protected boolean enableAdvancedToggle() {
        return true;
    }

    protected Component recommendAction(RBMKReadings readings, int localControlPercent, int globalControlPercent) {
        if (readings.coolantMb() < 2_000) {
            return Component.translatable("gui.hbm.rbmk.action.coolant");
        }
        if (readings.energyCapacity() > 0 && readings.energyStored() >= readings.energyCapacity()) {
            return Component.translatable("gui.hbm.rbmk.action.dump_power");
        }
        ReactorStatus status = resolveStatus(readings);
        if (status == ReactorStatus.CRITICAL || status == ReactorStatus.WARNING) {
            return Component.translatable("gui.hbm.rbmk.action.insert_rods");
        }
        if (localControlPercent >= 95 && readings.heat() < readings.meltdownThreshold() * 0.3F) {
            return Component.translatable("gui.hbm.rbmk.action.raise_rods");
        }
        return Component.translatable("gui.hbm.rbmk.action.normal");
    }

    protected record RBMKReadings(float heat, float meltdownThreshold, int energyStored,
                                  int energyCapacity, int coolantMb, int steamMb) {}

    protected enum ReactorStatus {
        OFFLINE("gui.hbm.rbmk.status.offline", 0x979797),
        RUNNING("gui.hbm.rbmk.status.running", 0x7ED957),
        WARNING("gui.hbm.rbmk.status.warning", 0xFFD166),
        CRITICAL("gui.hbm.rbmk.status.critical", 0xFF5555);

        private final String translationKey;
        private final int color;

        ReactorStatus(String translationKey, int color) {
            this.translationKey = translationKey;
            this.color = color;
        }

        public String translationKey() {
            return translationKey;
        }

        public int color() {
            return color;
        }
    }
}
