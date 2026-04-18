package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.rbmk.RBMKPeripheralEntity;
import com.hbm.gui.menu.RBMKPeripheralMenu;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import com.hbm.reactor.rbmk.RBMKColumnType;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import com.hbm.reactor.rbmk.RBMKScreenType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
public class RBMKPeripheralScreen extends AbstractRBMKScreen<RBMKPeripheralMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID, "textures/gui/reactors/gui_rbmk_console.png");
    private static final int CONSOLE_WIDTH = 244;
    private static final int CONSOLE_HEIGHT = 172;
    private static final int GRID_ORIGIN_X = 86;
    private static final int GRID_ORIGIN_Y = 11;
    private static final int GRID_CELL_SIZE = 10;
    private static final int GRAPH_X0 = 7;
    private static final int GRAPH_X1 = 81;
    private static final int GRAPH_Y0 = 98;
    private static final int GRAPH_Y1 = 133;
    private static final int AZ5_BUTTON_X = 30;
    private static final int AZ5_BUTTON_Y = 138;
    private static final int AZ5_BUTTON_SIZE = 28;
    private static final int LEVEL_FIELD_X = 9;
    private static final int LEVEL_FIELD_Y = 84;
    private static final int LEVEL_FIELD_W = 35;
    private static final int LEVEL_FIELD_H = 9;
    private static final int LEVEL_APPLY_X = 48;
    private static final int LEVEL_APPLY_Y = 82;
    private static final int LEVEL_APPLY_SIZE = 12;
    private static final int COMPRESSOR_X = 70;
    private static final int COMPRESSOR_Y = 82;
    private static final int COMPRESSOR_SIZE = 12;
    private static final int COLOR_BUTTON_X = 6;
    private static final int COLOR_BUTTON_Y = 70;
    private static final int COLOR_BUTTON_SIZE = 10;
    private static final int COLOR_BUTTON_STEP = 11;
    private static final int SELECT_ALL_X = 61;
    private static final int SELECT_CLEAR_X = 72;
    private static final int SCREEN_ICON_X = 6;
    private static final int SCREEN_ASSIGN_X = 24;
    private static final int SCREEN_Y = 8;
    private static final int SCREEN_COL_STEP = 40;
    private static final int SCREEN_ROW_STEP = 21;
    private static final int SCREEN_ICON_SIZE = 18;
    private static final int LINE_COLOR = 0xFF00FF00;
    private static final int CENTER_BORDER_COLOR = 0xFF7ED957;

    private final boolean consoleScreen;
    private final boolean[] selection = new boolean[RBMKPeripheralMenu.GRID_SIZE * RBMKPeripheralMenu.GRID_SIZE];
    private EditBox levelField;
    private boolean az5LidClosed = true;
    private long lastAz5TriggerMillis;

    public RBMKPeripheralScreen(RBMKPeripheralMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.consoleScreen = menu.getPeripheralType() == RBMKPeripheralType.CONSOLE;
        if (consoleScreen) {
            this.imageWidth = CONSOLE_WIDTH;
            this.imageHeight = CONSOLE_HEIGHT;
        } else {
            this.imageWidth = 256;
            this.imageHeight = 256;
        }
    }

    @Override
    protected void init() {
        super.init();
        if (consoleScreen) {
            levelField = new EditBox(font, leftPos + LEVEL_FIELD_X, topPos + LEVEL_FIELD_Y, LEVEL_FIELD_W, LEVEL_FIELD_H, Component.empty());
            levelField.setBordered(false);
            levelField.setMaxLength(3);
            levelField.setTextColor(0x00FF00);
            levelField.setFilter(RBMKPeripheralScreen::isNumericField);
            levelField.setValue(Integer.toString(menu.getLocalControlPercent()));
            addRenderableWidget(levelField);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (consoleScreen && levelField != null) {
            levelField.tick();
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String levelValue = levelField != null ? levelField.getValue() : "0";
        super.resize(minecraft, width, height);
        if (consoleScreen && levelField != null) {
            levelField.setValue(levelValue);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (consoleScreen) {
            this.renderBackground(graphics);
            super.render(graphics, mouseX, mouseY, partialTick);
            renderConsoleTooltip(graphics, mouseX, mouseY);
            return;
        }
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        if (consoleScreen) {
            renderConsoleBackground(graphics);
            return;
        }
        renderConsoleGridFallback(graphics);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        if (consoleScreen) {
            return;
        }
        if (!consoleScreen) {
            super.renderLabels(graphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!consoleScreen) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (levelField != null && levelField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        int index = hoveredConsoleIndex(mouseX, mouseY);
        if (index >= 0 && getConsoleColumn(index) != null) {
            selection[index] = !selection[index];
            return true;
        }

        if (isWithin(mouseX, mouseY, SELECT_CLEAR_X, COLOR_BUTTON_Y, COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE)) {
            clearSelection();
            return true;
        }

        if (isWithin(mouseX, mouseY, SELECT_ALL_X, COLOR_BUTTON_Y, COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE)) {
            selectAllControlRods();
            return true;
        }

        for (int color = 0; color < 5; color++) {
            int buttonX = COLOR_BUTTON_X + color * COLOR_BUTTON_STEP;
            if (isWithin(mouseX, mouseY, buttonX, COLOR_BUTTON_Y, COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE)) {
                if (button == 1) {
                    sendSelectionCommand("assignColor", color);
                } else {
                    selectColorGroup(color);
                }
                return true;
            }
        }

        if (isWithin(mouseX, mouseY, LEVEL_APPLY_X, LEVEL_APPLY_Y, LEVEL_APPLY_SIZE, LEVEL_APPLY_SIZE)) {
            applySelectedRodLevel();
            return true;
        }

        if (isWithin(mouseX, mouseY, COMPRESSOR_X, COMPRESSOR_Y, COMPRESSOR_SIZE, COMPRESSOR_SIZE)) {
            sendSelectionCommand("compressor", 1);
            return true;
        }

        if (isWithin(mouseX, mouseY, AZ5_BUTTON_X, AZ5_BUTTON_Y, AZ5_BUTTON_SIZE, AZ5_BUTTON_SIZE)) {
            handleAz5Click();
            return true;
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 2; col++) {
                int slot = row * 2 + col;
                int iconX = SCREEN_ICON_X + col * SCREEN_COL_STEP;
                int iconY = SCREEN_Y + row * SCREEN_ROW_STEP;
                if (isWithin(mouseX, mouseY, iconX, iconY, SCREEN_ICON_SIZE, SCREEN_ICON_SIZE)) {
                    CompoundTag tag = new CompoundTag();
                    tag.putInt("toggle", slot);
                    sendConsoleTag(tag);
                    return true;
                }
                int assignX = SCREEN_ASSIGN_X + col * SCREEN_COL_STEP;
                if (isWithin(mouseX, mouseY, assignX, iconY, SCREEN_ICON_SIZE, SCREEN_ICON_SIZE)) {
                    CompoundTag tag = new CompoundTag();
                    tag.putInt("id", slot);
                    tag.putIntArray("cols", selectedIndices());
                    sendConsoleTag(tag);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (minecraft != null && (keyCode == GLFW.GLFW_KEY_ESCAPE || minecraft.options.keyInventory.matches(keyCode, scanCode))) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        if (!consoleScreen) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            applySelectedRodLevel();
            return true;
        }
        if (levelField != null && levelField.isFocused() && levelField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (consoleScreen && levelField != null && levelField.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    protected boolean hasColumnData() {
        return menu.hasColumnData();
    }

    @Override
    protected RBMKReadings primaryReadings() {
        return new RBMKReadings(menu.getHeat(), menu.getMeltdownThreshold(),
                menu.getEnergyStored(), menu.getEnergyCapacity(), menu.getWaterAmount(), menu.getSteamAmount());
    }

    @Override
    protected void renderControlPanel(GuiGraphics graphics, RBMKReadings readings) {
        int y = drawSectionHeader(graphics, CONTROL_X, CONTROL_Y, Component.translatable("gui.hbm.rbmk.section.control"));
        y = drawValueLine(graphics, CONTROL_X, y,
                Component.translatable("gui.hbm.rbmk.peripheral_kind", menu.getPeripheralType().displayName()), SECTION_COLOR);
        y = drawValueLine(graphics, CONTROL_X, y,
                Component.translatable("gui.hbm.rbmk.control_local", menu.getLocalControlPercent()), SECTION_COLOR);
        y = drawValueLine(graphics, CONTROL_X, y,
                Component.translatable("gui.hbm.rbmk.control_global", menu.getGlobalControlPercent()), SECTION_COLOR);
        Component consoleHint = consoleScreen
                ? Component.translatable("gui.hbm.rbmk.console.az5_hint")
                : Component.translatable("gui.hbm.rbmk.control.no_manual");
        drawValueLine(graphics, CONTROL_X, y, consoleHint, SECTION_COLOR);
    }

    @Override
    protected void renderAdvancedDetails(GuiGraphics graphics, RBMKReadings readings, int x, int startY) {
        int y = startY;
        y = drawValueLine(graphics, x, y,
                Component.translatable("gui.hbm.rbmk.columns_online", menu.getColumnCount()), SECTION_COLOR);
        if (menu.getColumnCount() <= 0) {
            y = drawValueLine(graphics, x, y, Component.translatable("gui.hbm.rbmk.console.idle"), SECTION_COLOR);
        }
        drawValueLine(graphics, x, y, recommendAction(readings, menu.getLocalControlPercent(), menu.getGlobalControlPercent()), SECTION_COLOR);
    }

    @Override
    protected void updateInteractionState(boolean hasData) {
        if (consoleScreen && levelField != null) {
            levelField.setEditable(hasData);
            levelField.setVisible(true);
        }
    }

    @Override
    protected boolean enableAdvancedToggle() {
        return !consoleScreen;
    }

    private void renderConsoleBackground(GuiGraphics graphics) {
        if (az5LidClosed) {
            graphics.blit(TEXTURE, leftPos + AZ5_BUTTON_X, topPos + AZ5_BUTTON_Y, 228, 172, AZ5_BUTTON_SIZE, AZ5_BUTTON_SIZE);
        }

        RBMKPeripheralEntity peripheral = menu.getPeripheral();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 2; col++) {
                int slot = row * 2 + col;
                RBMKPeripheralEntity.ConsoleScreen screen = peripheral != null ? peripheral.getScreen(slot) : null;
                RBMKScreenType type = screen != null ? screen.type() : RBMKScreenType.NONE;
                graphics.blit(TEXTURE, leftPos + SCREEN_ICON_X + col * SCREEN_COL_STEP,
                        topPos + SCREEN_Y + row * SCREEN_ROW_STEP,
                        type.spriteU(), 238, 18, 18);
            }
        }

        for (int i = 0; i < RBMKPeripheralMenu.GRID_SIZE * RBMKPeripheralMenu.GRID_SIZE; i++) {
            RBMKPeripheralEntity.ConsoleColumn column = getConsoleColumn(i);
            if (column == null) {
                continue;
            }
            int localX = i % RBMKPeripheralMenu.GRID_SIZE;
            int localY = i / RBMKPeripheralMenu.GRID_SIZE;
            int x = leftPos + GRID_ORIGIN_X + localX * GRID_CELL_SIZE;
            int y = topPos + GRID_ORIGIN_Y + localY * GRID_CELL_SIZE;
            graphics.blit(TEXTURE, x, y, column.type().consoleSpriteU(), 172, GRID_CELL_SIZE, GRID_CELL_SIZE);
            drawHeatOverlay(graphics, x, y, column);
            drawColumnOverlay(graphics, x, y, column);
            if (selection[i]) {
                graphics.blit(TEXTURE, x, y, 0, 192, GRID_CELL_SIZE, GRID_CELL_SIZE);
            }
        }

        int centerX = leftPos + GRID_ORIGIN_X + RBMKPeripheralMenu.GRID_CENTER * GRID_CELL_SIZE;
        int centerY = topPos + GRID_ORIGIN_Y + RBMKPeripheralMenu.GRID_CENTER * GRID_CELL_SIZE;
        drawCellBorder(graphics, centerX, centerY, GRID_CELL_SIZE, CENTER_BORDER_COLOR);
        drawFluxGraph(graphics);
    }

    private void renderConsoleGridFallback(GuiGraphics graphics) {
        if (!menu.hasColumnData()) {
            return;
        }
        for (int row = 0; row < RBMKPeripheralMenu.GRID_SIZE; row++) {
            for (int col = 0; col < RBMKPeripheralMenu.GRID_SIZE; col++) {
                int state = menu.getGridCell(col, row);
                if (state == RBMKPeripheralMenu.GRID_EMPTY) {
                    continue;
                }
                graphics.blit(TEXTURE, leftPos + GRID_ORIGIN_X + col * GRID_CELL_SIZE,
                        topPos + GRID_ORIGIN_Y + row * GRID_CELL_SIZE,
                        gridSpriteU(state), 172, GRID_CELL_SIZE, GRID_CELL_SIZE);
            }
        }
    }

    private void drawHeatOverlay(GuiGraphics graphics, int x, int y, RBMKPeripheralEntity.ConsoleColumn column) {
        CompoundTag data = column.data();
        double maxHeat = Math.max(1.0D, data.getDouble("maxHeat"));
        int heatHeight = Mth.clamp((int) Math.ceil((data.getDouble("heat") - 20.0D) * 10.0D / maxHeat), 0, 10);
        if (heatHeight > 0) {
            graphics.blit(TEXTURE, x, y + GRID_CELL_SIZE - heatHeight, 0, 192 - heatHeight, 10, heatHeight);
        }
    }

    private void drawColumnOverlay(GuiGraphics graphics, int x, int y, RBMKPeripheralEntity.ConsoleColumn column) {
        CompoundTag data = column.data();
        switch (column.type()) {
            case CONTROL -> {
                if (data.contains("color")) {
                    graphics.blit(TEXTURE, x, y, data.getInt("color") * 10, 202, 10, 10);
                }
                drawControlLevelOverlay(graphics, x, y, data.getDouble("level"));
            }
            case CONTROL_AUTO -> drawControlLevelOverlay(graphics, x, y, data.getDouble("level"));
            case FUEL, FUEL_SIM -> {
                drawFuelBar(graphics, x + 1, y, 11, data.getDouble("c_heat"), Math.max(1.0D, data.getDouble("c_maxHeat")));
                drawFuelBar(graphics, x + 4, y, 14, data.getDouble("enrichment") * 100.0D, 100.0D);
                drawFuelBar(graphics, x + 7, y, 17, data.getDouble("xenon"), 100.0D);
            }
            case BOILER -> {
                drawVerticalBar(graphics, x + 1, y, 41, data.getInt("water"), Math.max(1, data.getInt("maxWater")), 3);
                drawVerticalBar(graphics, x + 6, y, 46, data.getInt("steam"), Math.max(1, data.getInt("maxSteam")), 3);
                int compression = Mth.clamp(data.getInt("compression"), 0, 3);
                graphics.blit(TEXTURE, x + 4, y + 1 + compression * 2, 44, 183 + compression * 2, 2, 2);
            }
            case HEATEX -> {
                if (data.getBoolean("active")) {
                    graphics.blit(TEXTURE, x + 4, y + 4, 24, 183, 2, 4);
                }
            }
            default -> {
            }
        }
    }

    private void drawControlLevelOverlay(GuiGraphics graphics, int x, int y, double level) {
        int hidden = Mth.clamp(8 - (int) Math.ceil(level * 8.0D), 0, 8);
        if (hidden > 0) {
            graphics.blit(TEXTURE, x + 4, y + 1, 24, 183, 2, hidden);
        }
    }

    private void drawFuelBar(GuiGraphics graphics, int x, int y, int textureU, double value, double max) {
        int height = Mth.clamp((int) Math.ceil(value * 8.0D / Math.max(1.0D, max)), 0, 8);
        if (height > 0) {
            graphics.blit(TEXTURE, x, y + GRID_CELL_SIZE - height - 1, textureU, 191 - height, 2, height);
        }
    }

    private void drawVerticalBar(GuiGraphics graphics, int x, int y, int textureU, int value, int max, int width) {
        int height = Mth.clamp((int) Math.ceil(value * 8.0D / Math.max(1, max)), 0, 8);
        if (height > 0) {
            graphics.blit(TEXTURE, x, y + GRID_CELL_SIZE - height - 1, textureU, 191 - height, width, height);
        }
    }

    private void drawFluxGraph(GuiGraphics graphics) {
        RBMKPeripheralEntity peripheral = menu.getPeripheral();
        int[] fluxBuffer = peripheral != null ? peripheral.getFluxBuffer() : new int[0];
        if (fluxBuffer.length < 2) {
            return;
        }
        int highest = Integer.MIN_VALUE;
        int lowest = Integer.MAX_VALUE;
        for (int flux : fluxBuffer) {
            highest = Math.max(highest, flux);
            lowest = Math.min(lowest, flux);
        }
        int range = Math.max(1, highest - lowest);
        for (int i = 0; i < fluxBuffer.length - 1; i++) {
            int x0 = leftPos + GRAPH_X0 + Math.round(i * (GRAPH_X1 - GRAPH_X0) / (float) (fluxBuffer.length - 1));
            int x1 = leftPos + GRAPH_X0 + Math.round((i + 1) * (GRAPH_X1 - GRAPH_X0) / (float) (fluxBuffer.length - 1));
            int y0 = topPos + GRAPH_Y1 - Math.round((fluxBuffer[i] - lowest) * (GRAPH_Y1 - GRAPH_Y0) / (float) range);
            int y1 = topPos + GRAPH_Y1 - Math.round((fluxBuffer[i + 1] - lowest) * (GRAPH_Y1 - GRAPH_Y0) / (float) range);
            drawLine(graphics, x0, y0, x1, y1, LINE_COLOR);
        }

        graphics.pose().pushPose();
        graphics.pose().scale(0.5F, 0.5F, 1.0F);
        graphics.drawString(font, Integer.toString(highest), Math.round((leftPos + 8) / 0.5F), Math.round((topPos + 98) / 0.5F), 0x00FF00, false);
        graphics.drawString(font, Integer.toString(highest), Math.round((leftPos + 80 - font.width(Integer.toString(highest)) * 0.5F) / 0.5F), Math.round((topPos + 98) / 0.5F), 0x00FF00, false);
        graphics.drawString(font, Integer.toString(lowest), Math.round((leftPos + 8) / 0.5F), Math.round((topPos + 133 - font.lineHeight * 0.5F) / 0.5F), 0x00FF00, false);
        graphics.drawString(font, Integer.toString(lowest), Math.round((leftPos + 80 - font.width(Integer.toString(lowest)) * 0.5F) / 0.5F), Math.round((topPos + 133 - font.lineHeight * 0.5F) / 0.5F), 0x00FF00, false);
        graphics.pose().popPose();
    }

    private void renderConsoleTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        int index = hoveredConsoleIndex(mouseX, mouseY);
        if (index >= 0) {
            RBMKPeripheralEntity.ConsoleColumn column = getConsoleColumn(index);
            if (column != null) {
                graphics.renderComponentTooltip(this.font, column.getFancyStats(), mouseX, mouseY);
                return;
            }
        }

        if (isWithin(mouseX, mouseY, SELECT_ALL_X, COLOR_BUTTON_Y, COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE)) {
            graphics.renderComponentTooltip(this.font, List.of(Component.translatable("gui.hbm.rbmk.tooltip.select_all")), mouseX, mouseY);
            return;
        }
        if (isWithin(mouseX, mouseY, SELECT_CLEAR_X, COLOR_BUTTON_Y, COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE)) {
            graphics.renderComponentTooltip(this.font, List.of(Component.translatable("gui.hbm.rbmk.tooltip.clear_selection")), mouseX, mouseY);
            return;
        }
        for (int color = 0; color < 5; color++) {
            int buttonX = COLOR_BUTTON_X + color * COLOR_BUTTON_STEP;
            if (isWithin(mouseX, mouseY, buttonX, COLOR_BUTTON_Y, COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE)) {
                graphics.renderComponentTooltip(this.font, List.of(
                        Component.translatable("gui.hbm.rbmk.tooltip.select_group", color + 1),
                        Component.translatable("gui.hbm.rbmk.tooltip.assign_group", color + 1)), mouseX, mouseY);
                return;
            }
        }
        if (isWithin(mouseX, mouseY, COMPRESSOR_X, COMPRESSOR_Y, COMPRESSOR_SIZE, COMPRESSOR_SIZE)) {
            graphics.renderComponentTooltip(this.font, List.of(Component.translatable("gui.hbm.rbmk.tooltip.compressor")), mouseX, mouseY);
            return;
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 2; col++) {
                int slot = row * 2 + col;
                RBMKPeripheralEntity.ConsoleScreen screen = menu.getPeripheral() != null ? menu.getPeripheral().getScreen(slot) : null;
                int iconX = SCREEN_ICON_X + col * SCREEN_COL_STEP;
                int iconY = SCREEN_Y + row * SCREEN_ROW_STEP;
                if (isWithin(mouseX, mouseY, iconX, iconY, SCREEN_ICON_SIZE, SCREEN_ICON_SIZE)) {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(screenTypeName(screen != null ? screen.type() : RBMKScreenType.NONE));
                    if (screen != null && screen.display() != null) {
                        tooltip.add(Component.literal(screen.display()));
                    }
                    graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
                    return;
                }
                int assignX = SCREEN_ASSIGN_X + col * SCREEN_COL_STEP;
                if (isWithin(mouseX, mouseY, assignX, iconY, SCREEN_ICON_SIZE, SCREEN_ICON_SIZE)) {
                    graphics.renderComponentTooltip(this.font,
                            List.of(Component.translatable("gui.hbm.rbmk.tooltip.assign_screen", slot + 1)),
                            mouseX, mouseY);
                    return;
                }
            }
        }
    }

    private void handleAz5Click() {
        if (minecraft == null || minecraft.gameMode == null) {
            return;
        }
        if (az5LidClosed) {
            az5LidClosed = false;
            return;
        }
        long now = System.currentTimeMillis();
        if (lastAz5TriggerMillis + 3000L < now) {
            lastAz5TriggerMillis = now;
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
        }
    }

    private void applySelectedRodLevel() {
        if (levelField == null) {
            return;
        }
        String raw = levelField.getValue().trim();
        if (raw.isEmpty()) {
            return;
        }
        int percent;
        try {
            percent = Integer.parseInt(raw);
        } catch (NumberFormatException ignored) {
            return;
        }
        percent = Mth.clamp(percent, 0, 100);
        levelField.setValue(Integer.toString(percent));
        CompoundTag tag = new CompoundTag();
        tag.putDouble("level", percent);
        tag.putIntArray("cols", selectedIndices());
        sendConsoleTag(tag);
    }

    private void sendSelectionCommand(String key, int value) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(key, value);
        tag.putIntArray("cols", selectedIndices());
        sendConsoleTag(tag);
    }

    private void sendConsoleTag(CompoundTag tag) {
        if (menu.getPos() == null) {
            return;
        }
        ModMessages.sendToServer(new C2SSyncTileMessage(menu.getPos(), tag));
    }

    private void selectAllControlRods() {
        clearSelection();
        for (int i = 0; i < selection.length; i++) {
            RBMKPeripheralEntity.ConsoleColumn column = getConsoleColumn(i);
            if (column != null && (column.type() == RBMKColumnType.CONTROL || column.type() == RBMKColumnType.CONTROL_AUTO)) {
                selection[i] = true;
            }
        }
    }

    private void selectColorGroup(int color) {
        clearSelection();
        for (int i = 0; i < selection.length; i++) {
            RBMKPeripheralEntity.ConsoleColumn column = getConsoleColumn(i);
            if (column == null) {
                continue;
            }
            if ((column.type() == RBMKColumnType.CONTROL || column.type() == RBMKColumnType.CONTROL_AUTO)
                    && column.data().contains("color")
                    && column.data().getInt("color") == color) {
                selection[i] = true;
            }
        }
    }

    private void clearSelection() {
        for (int i = 0; i < selection.length; i++) {
            selection[i] = false;
        }
    }

    private int[] selectedIndices() {
        int count = 0;
        for (boolean selected : selection) {
            if (selected) {
                count++;
            }
        }
        int[] indices = new int[count];
        int cursor = 0;
        for (int i = 0; i < selection.length; i++) {
            if (selection[i]) {
                indices[cursor++] = i;
            }
        }
        return indices;
    }

    private int hoveredConsoleIndex(double mouseX, double mouseY) {
        if (!isWithin(mouseX, mouseY, GRID_ORIGIN_X, GRID_ORIGIN_Y,
                RBMKPeripheralMenu.GRID_SIZE * GRID_CELL_SIZE, RBMKPeripheralMenu.GRID_SIZE * GRID_CELL_SIZE)) {
            return -1;
        }
        int localX = (int) ((mouseX - (leftPos + GRID_ORIGIN_X)) / GRID_CELL_SIZE);
        int localY = (int) ((mouseY - (topPos + GRID_ORIGIN_Y)) / GRID_CELL_SIZE);
        if (localX < 0 || localX >= RBMKPeripheralMenu.GRID_SIZE || localY < 0 || localY >= RBMKPeripheralMenu.GRID_SIZE) {
            return -1;
        }
        return localY * RBMKPeripheralMenu.GRID_SIZE + localX;
    }

    private boolean isWithin(double mouseX, double mouseY, int localX, int localY, int width, int height) {
        int x = leftPos + localX;
        int y = topPos + localY;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private RBMKPeripheralEntity.ConsoleColumn getConsoleColumn(int index) {
        RBMKPeripheralEntity peripheral = menu.getPeripheral();
        return peripheral != null ? peripheral.getConsoleColumn(index) : null;
    }

    private static boolean isNumericField(String value) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        try {
            int parsed = Integer.parseInt(value);
            return parsed >= 0 && parsed <= 100;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static Component screenTypeName(RBMKScreenType type) {
        return type.displayName();
    }

    private int gridSpriteU(int state) {
        return switch (state) {
            case RBMKPeripheralMenu.GRID_FUEL -> 10;
            case RBMKPeripheralMenu.GRID_CONTROL -> 20;
            case RBMKPeripheralMenu.GRID_CONTROL_AUTO -> 30;
            case RBMKPeripheralMenu.GRID_BOILER -> 40;
            case RBMKPeripheralMenu.GRID_MODERATOR -> 50;
            case RBMKPeripheralMenu.GRID_ABSORBER -> 60;
            case RBMKPeripheralMenu.GRID_REFLECTOR -> 70;
            case RBMKPeripheralMenu.GRID_OUTGASSER -> 80;
            case RBMKPeripheralMenu.GRID_BREEDER -> 100;
            case RBMKPeripheralMenu.GRID_STORAGE -> 110;
            case RBMKPeripheralMenu.GRID_COOLER -> 120;
            case RBMKPeripheralMenu.GRID_HEATEX -> 130;
            default -> 0;
        };
    }

    private void drawCellBorder(GuiGraphics graphics, int x, int y, int size, int color) {
        graphics.fill(x, y, x + size, y + 1, color);
        graphics.fill(x, y + size - 1, x + size, y + size, color);
        graphics.fill(x, y, x + 1, y + size, color);
        graphics.fill(x + size - 1, y, x + size, y + size, color);
    }

    private void drawLine(GuiGraphics graphics, int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            graphics.fill(x0, y0, x0 + 2, y0 + 2, color);
            if (x0 == x1 && y0 == y1) {
                return;
            }
            int e2 = err * 2;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }
}
