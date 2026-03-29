package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.RBMKPeripheralMenu;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class RBMKPeripheralScreen extends AbstractRBMKScreen<RBMKPeripheralMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID, "textures/gui/reactors/gui_rbmk_console.png");
    private static final ResourceLocation GRID_TEXTURE = new ResourceLocation(HBM.MODID, "textures/gui/reactors/gui_rbmk_console_cells.png");
    private static final int AZ5_BUTTON_X = 26;
    private static final int AZ5_BUTTON_Y = 143;
    private static final int AZ5_BUTTON_WIDTH = 28;
    private static final int AZ5_BUTTON_HEIGHT = 15;
    private static final int GRID_ORIGIN_X = 87;
    private static final int GRID_ORIGIN_Y = 20;
    private static final int GRID_CELL_SIZE = 9;
    private static final int GRID_PITCH = 10;
    private static final int GRID_MARKER_OFFSET = 2;
    private static final int STATUS_LIGHT_SIZE = 18;
    private static final int[] STATUS_LIGHT_X = {6, 6, 6, 46, 46, 46};
    private static final int[] STATUS_LIGHT_Y = {8, 29, 50, 8, 29, 50};
    private static final int GRID_SPRITE_SIZE = 6;
    private static final int GRID_SPRITE_TEX_WIDTH = 18;
    private static final int GRID_SPRITE_TEX_HEIGHT = 6;
    private static final int GRID_SPRITE_COLUMN_U = 0;
    private static final int GRID_SPRITE_FUEL_U = 6;
    private static final int GRID_SPRITE_CONTROL_U = 12;

    private static final int LINK_FIELD_X = 90;
    private static final int LINK_FIELD_Y = 176;
    private static final int LINK_FIELD_WIDTH = 30;
    private static final int LINK_FIELD_HEIGHT = 12;
    private static final int LINK_FIELD_SPACING = 34;
    private static final int LINK_BUTTON_X = 90;
    private static final int LINK_BUTTON_Y = 192;
    private static final int LINK_BUTTON_WIDTH = 58;
    private static final int LINK_BUTTON_HEIGHT = 14;
    private static final int CLEAR_BUTTON_X = LINK_BUTTON_X + LINK_BUTTON_WIDTH + 4;

    private static final int GRID_COLOR_COLUMN = 0xFFB0B0B0;
    private static final int GRID_COLOR_FUEL = 0xFFF4C542;
    private static final int GRID_COLOR_CONTROL = 0xFF5DADE2;
    private static final int GRID_COLOR_LINK = 0xFF7ED957;
    private static final int LIGHT_OFF_COLOR = 0xFF2B2B2B;
    private static final int COORD_TEXT_COLOR = 0x00FF00;
    private final boolean consoleScreen;
    private AbstractButton az5Button;
    private EditBox linkXField;
    private EditBox linkYField;
    private EditBox linkZField;
    private AbstractButton linkButton;
    private AbstractButton clearButton;

    public RBMKPeripheralScreen(RBMKPeripheralMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 256;
        this.imageHeight = 256;
        this.consoleScreen = menu.getPeripheralType() == RBMKPeripheralType.CONSOLE;
    }

    @Override
    protected void init() {
        super.init();
        if (consoleScreen) {
            int buttonX = guiX(AZ5_BUTTON_X);
            int buttonY = guiY(AZ5_BUTTON_Y);
            az5Button = new InvisibleButton(buttonX, buttonY, AZ5_BUTTON_WIDTH, AZ5_BUTTON_HEIGHT, this::sendAz5);
            addRenderableWidget(az5Button);

            linkXField = createCoordField(guiX(LINK_FIELD_X), guiY(LINK_FIELD_Y));
            linkYField = createCoordField(guiX(LINK_FIELD_X + LINK_FIELD_SPACING), guiY(LINK_FIELD_Y));
            linkZField = createCoordField(guiX(LINK_FIELD_X + LINK_FIELD_SPACING * 2), guiY(LINK_FIELD_Y));
            addRenderableWidget(linkXField);
            addRenderableWidget(linkYField);
            addRenderableWidget(linkZField);

            linkButton = Button.builder(Component.translatable("gui.hbm.rbmk.console.link"), b -> sendManualLink())
                    .bounds(guiX(LINK_BUTTON_X), guiY(LINK_BUTTON_Y), LINK_BUTTON_WIDTH, LINK_BUTTON_HEIGHT)
                    .build();
            clearButton = Button.builder(Component.translatable("gui.hbm.rbmk.console.clear"), b -> clearManualLink())
                    .bounds(guiX(CLEAR_BUTTON_X), guiY(LINK_BUTTON_Y), LINK_BUTTON_WIDTH, LINK_BUTTON_HEIGHT)
                    .build();
            addRenderableWidget(linkButton);
            addRenderableWidget(clearButton);
        }
    }

    private void sendAz5() {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
        }
    }

    private EditBox createCoordField(int x, int y) {
        EditBox field = new EditBox(font, x, y, LINK_FIELD_WIDTH, LINK_FIELD_HEIGHT, Component.empty());
        field.setBordered(true);
        field.setMaxLength(10);
        field.setTextColor(COORD_TEXT_COLOR);
        field.setValue("0");
        field.setFilter(RBMKPeripheralScreen::isNumericInput);
        return field;
    }

    private static boolean isNumericInput(String value) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        int start = value.charAt(0) == '-' ? 1 : 0;
        if (start == 1 && value.length() == 1) {
            return true;
        }
        for (int i = start; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void sendManualLink() {
        if (minecraft == null || minecraft.gameMode == null) {
            return;
        }
        int x = parseCoordinate(linkXField);
        int y = parseCoordinate(linkYField);
        int z = parseCoordinate(linkZField);
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, RBMKPeripheralMenu.coordButtonId(RBMKPeripheralMenu.AXIS_X, x));
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, RBMKPeripheralMenu.coordButtonId(RBMKPeripheralMenu.AXIS_Y, y));
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, RBMKPeripheralMenu.coordButtonId(RBMKPeripheralMenu.AXIS_Z, z));
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, RBMKPeripheralMenu.LINK_APPLY_BUTTON);
    }

    private void clearManualLink() {
        if (minecraft == null || minecraft.gameMode == null) {
            return;
        }
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, RBMKPeripheralMenu.LINK_CLEAR_BUTTON);
    }

    private static int parseCoordinate(EditBox field) {
        String raw = field.getValue().trim();
        int parsed = 0;
        if (!raw.isEmpty() && !raw.equals("-")) {
            try {
                parsed = Integer.parseInt(raw);
            } catch (NumberFormatException ignored) {
                parsed = 0;
            }
        }
        int clamped = Mth.clamp(parsed, -30_000_000, 30_000_000);
        field.setValue(Integer.toString(clamped));
        return clamped;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (consoleScreen) {
            if (linkXField != null) {
                linkXField.tick();
            }
            if (linkYField != null) {
                linkYField.tick();
            }
            if (linkZField != null) {
                linkZField.tick();
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        if (consoleScreen) {
            if (linkXField != null) {
                linkXField.mouseClicked(mouseX, mouseY, button);
            }
            if (linkYField != null) {
                linkYField.mouseClicked(mouseX, mouseY, button);
            }
            if (linkZField != null) {
                linkZField.mouseClicked(mouseX, mouseY, button);
            }
        }
        return handled;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (consoleScreen) {
            if (linkXField != null && linkXField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (linkYField != null && linkYField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (linkZField != null && linkZField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (keyCode == 257 || keyCode == 335) { // ENTER / KP_ENTER
                sendManualLink();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (consoleScreen) {
            if (linkXField != null && linkXField.charTyped(codePoint, modifiers)) {
                return true;
            }
            if (linkYField != null && linkYField.charTyped(codePoint, modifiers)) {
                return true;
            }
            if (linkZField != null && linkZField.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String xValue = linkXField != null ? linkXField.getValue() : "0";
        String yValue = linkYField != null ? linkYField.getValue() : "0";
        String zValue = linkZField != null ? linkZField.getValue() : "0";
        super.resize(minecraft, width, height);
        if (linkXField != null) {
            linkXField.setValue(xValue);
        }
        if (linkYField != null) {
            linkYField.setValue(yValue);
        }
        if (linkZField != null) {
            linkZField.setValue(zValue);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        if (consoleScreen) {
            renderConsoleGrid(graphics);
            renderStatusLights(graphics);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        if (consoleScreen) {
            int labelY = guiY(LINK_FIELD_Y - 10);
            graphics.drawString(font, Component.translatable("gui.hbm.rbmk.console.coord.x"), guiX(LINK_FIELD_X), labelY, SECTION_COLOR, false);
            graphics.drawString(font, Component.translatable("gui.hbm.rbmk.console.coord.y"), guiX(LINK_FIELD_X + LINK_FIELD_SPACING), labelY, SECTION_COLOR, false);
            graphics.drawString(font, Component.translatable("gui.hbm.rbmk.console.coord.z"), guiX(LINK_FIELD_X + LINK_FIELD_SPACING * 2), labelY, SECTION_COLOR, false);
        }
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
        if (az5Button != null) {
            az5Button.active = hasData && consoleScreen;
            az5Button.visible = hasData && consoleScreen;
        }
        if (consoleScreen) {
            boolean active = true;
            if (linkButton != null) {
                linkButton.active = active;
                linkButton.visible = active;
            }
            if (clearButton != null) {
                clearButton.active = active;
                clearButton.visible = active;
            }
            if (linkXField != null) {
                linkXField.setEditable(active);
                linkXField.setVisible(active);
            }
            if (linkYField != null) {
                linkYField.setEditable(active);
                linkYField.setVisible(active);
            }
            if (linkZField != null) {
                linkZField.setEditable(active);
                linkZField.setVisible(active);
            }
        }
    }

    private void renderConsoleGrid(GuiGraphics graphics) {
        if (!menu.hasColumnData()) {
            return;
        }
        int originX = guiX(GRID_ORIGIN_X);
        int originY = guiY(GRID_ORIGIN_Y);
        for (int row = 0; row < RBMKPeripheralMenu.GRID_SIZE; row++) {
            for (int col = 0; col < RBMKPeripheralMenu.GRID_SIZE; col++) {
                int state = menu.getGridCell(col, row);
                if (state == RBMKPeripheralMenu.GRID_EMPTY) {
                    continue;
                }
                int spriteU = switch (state) {
                    case RBMKPeripheralMenu.GRID_FUEL -> GRID_SPRITE_FUEL_U;
                    case RBMKPeripheralMenu.GRID_CONTROL -> GRID_SPRITE_CONTROL_U;
                    default -> GRID_SPRITE_COLUMN_U;
                };
                int x = originX + col * GRID_PITCH + GRID_MARKER_OFFSET;
                int y = originY + row * GRID_PITCH + GRID_MARKER_OFFSET;
                graphics.blit(GRID_TEXTURE, x, y, spriteU, 0, GRID_SPRITE_SIZE, GRID_SPRITE_SIZE, GRID_SPRITE_TEX_WIDTH, GRID_SPRITE_TEX_HEIGHT);
            }
        }

        int centerState = menu.getGridCell(RBMKPeripheralMenu.GRID_CENTER, RBMKPeripheralMenu.GRID_CENTER);
        int borderColor = centerState != RBMKPeripheralMenu.GRID_EMPTY ? GRID_COLOR_LINK : LIGHT_OFF_COLOR;
        int cellX = originX + RBMKPeripheralMenu.GRID_CENTER * GRID_PITCH;
        int cellY = originY + RBMKPeripheralMenu.GRID_CENTER * GRID_PITCH;
        drawCellBorder(graphics, cellX, cellY, GRID_CELL_SIZE, borderColor);
    }

    private void renderStatusLights(GuiGraphics graphics) {
        boolean hasData = menu.hasColumnData();
        boolean anyColumn = false;
        boolean anyFuel = false;
        boolean anyControl = false;
        int centerState = RBMKPeripheralMenu.GRID_EMPTY;

        if (hasData) {
            centerState = menu.getGridCell(RBMKPeripheralMenu.GRID_CENTER, RBMKPeripheralMenu.GRID_CENTER);
            for (int row = 0; row < RBMKPeripheralMenu.GRID_SIZE; row++) {
                for (int col = 0; col < RBMKPeripheralMenu.GRID_SIZE; col++) {
                    int state = menu.getGridCell(col, row);
                    if (state != RBMKPeripheralMenu.GRID_EMPTY) {
                        anyColumn = true;
                    }
                    if (state == RBMKPeripheralMenu.GRID_FUEL) {
                        anyFuel = true;
                    } else if (state == RBMKPeripheralMenu.GRID_CONTROL) {
                        anyControl = true;
                    }
                }
            }
        }

        boolean localColumn = centerState != RBMKPeripheralMenu.GRID_EMPTY;
        boolean localFuel = centerState == RBMKPeripheralMenu.GRID_FUEL;
        boolean localControl = centerState == RBMKPeripheralMenu.GRID_CONTROL;

        drawStatusLight(graphics, 0, localColumn, GRID_COLOR_LINK);
        drawStatusLight(graphics, 1, localFuel, GRID_COLOR_FUEL);
        drawStatusLight(graphics, 2, localControl, GRID_COLOR_CONTROL);
        drawStatusLight(graphics, 3, anyColumn, GRID_COLOR_LINK);
        drawStatusLight(graphics, 4, anyFuel, GRID_COLOR_FUEL);
        drawStatusLight(graphics, 5, anyControl, GRID_COLOR_CONTROL);
    }

    private void drawStatusLight(GuiGraphics graphics, int index, boolean active, int activeColor) {
        if (index < 0 || index >= STATUS_LIGHT_X.length || index >= STATUS_LIGHT_Y.length) {
            return;
        }
        int x = guiX(STATUS_LIGHT_X[index]);
        int y = guiY(STATUS_LIGHT_Y[index]);
        int color = active ? activeColor : LIGHT_OFF_COLOR;
        graphics.fill(x, y, x + STATUS_LIGHT_SIZE, y + STATUS_LIGHT_SIZE, color);
    }

    private void drawCellBorder(GuiGraphics graphics, int x, int y, int size, int color) {
        graphics.fill(x, y, x + size, y + 1, color);
        graphics.fill(x, y + size - 1, x + size, y + size, color);
        graphics.fill(x, y, x + 1, y + size, color);
        graphics.fill(x + size - 1, y, x + size, y + size, color);
    }

    private static final class InvisibleButton extends AbstractButton {
        private final Runnable onPress;

        private InvisibleButton(int x, int y, int width, int height, Runnable onPress) {
            super(x, y, width, height, Component.empty());
            this.onPress = onPress;
        }

        @Override
        public void onPress() {
            onPress.run();
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            // Intentionally invisible; click region aligns with legacy texture button.
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            // No narration for invisible legacy button.
        }
    }
}
