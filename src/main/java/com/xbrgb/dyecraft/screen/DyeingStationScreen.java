package com.xbrgb.dyecraft.screen;

import com.xbrgb.dyecraft.DyecraftMod;
import com.xbrgb.dyecraft.util.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class DyeingStationScreen extends HandledScreen<DyeingStationScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(DyecraftMod.MOD_ID, "textures/gui/dyeing_station.png");
    private static final int BG_WIDTH = 215, BG_HEIGHT = 215, TEXTURE_W = 256, TEXTURE_H = 256;

    private TextFieldWidget hexField, rField, gField, bField, hField, sField, brField;
    private int currentColor = 0xFFFFFF;
    private boolean updating = false;
    private Text errorMessage = null;
    private int errorDisplayTicks = 0;
    private int errorColor = 0xFF5555;
    private boolean presetLoaded = false;

    public DyeingStationScreen(DyeingStationScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = BG_WIDTH;
        this.backgroundHeight = BG_HEIGHT;
    }

    public void setUndoMessage(Text msg, int color) {
        this.errorMessage = msg;
        this.errorDisplayTicks = 60;
        this.errorColor = color;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.titleY = 4;

        // 读取预设颜色（三独立分量）
        presetLoaded = false;
        int pr = handler.getPresetRed(), pg = handler.getPresetGreen(), pb = handler.getPresetBlue();
        if (pr == 0 && pg == 0 && pb == 0) {
            currentColor = 0xFFFFFF; // 默认白色
        } else {
            currentColor = (pr << 16) | (pg << 8) | pb;
            presetLoaded = true;
        }

        // HEX 输入框 (130,35) 60×15
        hexField = new TextFieldWidget(textRenderer, x + 130, y + 35, 60, 15, Text.empty());
        hexField.setMaxLength(7);
        hexField.setPlaceholder(Text.literal("#FFFFFF"));
        hexField.setChangedListener(s -> {
            if (updating) return;
            if (!s.startsWith("#")) { s = "#" + s; hexField.setText(s); }
            if (s.length() == 7) {
                updating = true;
                try {
                    int parsedColor = Integer.parseInt(s.substring(1), 16);
                    currentColor = parsedColor;
                    updateFieldsFromColor();
                } catch (NumberFormatException e) {} finally { updating = false; }
            }
        });
        addDrawableChild(hexField);

        // R 输入框 (110,52) 26×15
        rField = new TextFieldWidget(textRenderer, x + 110, y + 52, 26, 15, Text.empty());
        rField.setMaxLength(3); rField.setText("255");
        rField.setChangedListener(s -> { if (!updating) { updating = true; updateColorFromRGB(); updating = false; } });
        addDrawableChild(rField);

        // G 输入框 (145,52) 26×15
        gField = new TextFieldWidget(textRenderer, x + 145, y + 52, 26, 15, Text.empty());
        gField.setMaxLength(3); gField.setText("255");
        gField.setChangedListener(s -> { if (!updating) { updating = true; updateColorFromRGB(); updating = false; } });
        addDrawableChild(gField);

        // B 输入框 (180,52) 26×15
        bField = new TextFieldWidget(textRenderer, x + 180, y + 52, 26, 15, Text.empty());
        bField.setMaxLength(3); bField.setText("255");
        bField.setChangedListener(s -> { if (!updating) { updating = true; updateColorFromRGB(); updating = false; } });
        addDrawableChild(bField);

        // H 输入框 (110,70) 26×15
        hField = new TextFieldWidget(textRenderer, x + 110, y + 70, 26, 15, Text.empty());
        hField.setMaxLength(3); hField.setText("0");
        hField.setChangedListener(s -> { if (!updating) { updating = true; updateColorFromHSB(); updating = false; } });
        addDrawableChild(hField);

        // S 输入框 (145,70) 26×15
        sField = new TextFieldWidget(textRenderer, x + 145, y + 70, 26, 15, Text.empty());
        sField.setMaxLength(3); sField.setText("0");
        sField.setChangedListener(s -> { if (!updating) { updating = true; updateColorFromHSB(); updating = false; } });
        addDrawableChild(sField);

        // Br 输入框 (180,70) 26×15
        brField = new TextFieldWidget(textRenderer, x + 180, y + 70, 26, 15, Text.empty());
        brField.setMaxLength(3); brField.setText("100");
        brField.setChangedListener(s -> { if (!updating) { updating = true; updateColorFromHSB(); updating = false; } });
        addDrawableChild(brField);

        // ---------- 按钮 ----------
        // 染色按钮 (112,88) 28×20
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.dyecraft.dyeing_station.button.dye"), button -> {
            if (!validateInputs()) return;
            try {
                int r = Integer.parseInt(rField.getText()), g = Integer.parseInt(gField.getText()), b = Integer.parseInt(bField.getText());
                ClientPlayNetworking.send(new DyePayload(r, g, b));
                errorMessage = null;
            } catch (NumberFormatException ignored) {}
        }).dimensions(x + 112, y + 88, 28, 20).build());

        // 撤销按钮 (144,88) 28×20
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.dyecraft.dyeing_station.button.undo"), button -> {
            ClientPlayNetworking.send(new UndoPayload());
        }).dimensions(x + 144, y + 88, 28, 20).build());

        // 预设按钮 (176,88) 28×20
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.dyecraft.dyeing_station.button.preset"), button -> {
            try {
                int r = Integer.parseInt(rField.getText()), g = Integer.parseInt(gField.getText()), b = Integer.parseInt(bField.getText());
                ClientPlayNetworking.send(new SetPresetPayload(r, g, b));
                errorMessage = Text.translatable("gui.dyecraft.dyeing_station.preset.saved");
                errorDisplayTicks = 40; errorColor = 0x55FF55;
            } catch (NumberFormatException ignored) {}
        }).dimensions(x + 176, y + 88, 28, 20).build());

        updateFieldsFromColor();
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        if (!presetLoaded) {
            int pr = handler.getPresetRed(), pg = handler.getPresetGreen(), pb = handler.getPresetBlue();
            if (pr != 0 || pg != 0 || pb != 0) {
                currentColor = (pr << 16) | (pg << 8) | pb;
                updateFieldsFromColor();
                presetLoaded = true;
            }
        }
    }

    @Override
    public void close() {
        ClientPlayNetworking.send(new ClearSnapshotPayload());
        super.close();
    }

    private boolean validateInputs() {
        ItemStack redDye = handler.getSlot(6).getStack();
        ItemStack greenDye = handler.getSlot(7).getStack();
        ItemStack blueDye = handler.getSlot(8).getStack();
        if (redDye.isEmpty() || greenDye.isEmpty() || blueDye.isEmpty()) {
            errorMessage = Text.translatable("gui.dyecraft.dyeing_station.error.no_dye");
            errorDisplayTicks = 60; errorColor = 0xFF5555; return false;
        }
        int totalBlocks = 0;
        for (int i = 0; i < 3; i++) totalBlocks += handler.getSlot(i).getStack().getCount();
        if (totalBlocks == 0) {
            errorMessage = Text.translatable("gui.dyecraft.dyeing_station.error.no_block");
            errorDisplayTicks = 60; errorColor = 0xFF5555; return false;
        }
        int groupsNeeded = (totalBlocks + 15) / 16;
        if (redDye.getCount() < groupsNeeded || greenDye.getCount() < groupsNeeded || blueDye.getCount() < groupsNeeded) {
            errorMessage = Text.translatable("gui.dyecraft.dyeing_station.error.not_enough_dye");
            errorDisplayTicks = 60; errorColor = 0xFF5555; return false;
        }
        return true;
    }

    private void updateFieldsFromColor() {
        updating = true;
        int r = (currentColor >> 16) & 0xFF, g = (currentColor >> 8) & 0xFF, b = currentColor & 0xFF;
        rField.setText(""+r); gField.setText(""+g); bField.setText(""+b);
        float[] hsb = ColorUtils.RGBtoHSB(r, g, b);
        hField.setText(""+(int)hsb[0]); sField.setText(""+(int)hsb[1]); brField.setText(""+(int)hsb[2]);
        hexField.setText(String.format("#%06X", currentColor));
        updating = false;
    }

    private void updateColorFromRGB() {
        try {
            int r = Integer.parseInt(rField.getText()), g = Integer.parseInt(gField.getText()), b = Integer.parseInt(bField.getText());
            currentColor = (r << 16) | (g << 8) | b;
            updateFieldsFromColor();
        } catch (NumberFormatException ignored) {}
    }

    private void updateColorFromHSB() {
        try {
            float h = Float.parseFloat(hField.getText()), s = Float.parseFloat(sField.getText()), br = Float.parseFloat(brField.getText());
            currentColor = ColorUtils.HSBtoRGB(h, s, br);
            updateFieldsFromColor();
        } catch (NumberFormatException ignored) {}
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        if (client != null && client.getTextureManager() != null) {
            context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, TEXTURE_W, TEXTURE_H);
        } else {
            context.fill(x, y, x + backgroundWidth, y + backgroundHeight, 0xFFC6C6C6);
            context.fill(x + 1, y + 1, x + backgroundWidth - 1, y + backgroundHeight - 1, 0xFF8B8B8B);
        }
        context.fill(x + 130, y + 20, x + 190, y + 33, 0xFF000000 | currentColor);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, this.title, this.titleX, this.titleY, 0x404040, false);
        context.drawText(textRenderer, Text.translatable("container.inventory"), 28, 109, 0x404040, false);
        int lc = 0xFFFFFF;
        context.drawText(textRenderer, Text.literal("PRE"), 110, 23, lc, false);
        context.drawText(textRenderer, Text.literal("HEX"), 110, 39, lc, false);
        context.drawText(textRenderer, Text.literal("R"), 103, 56, lc, false);
        context.drawText(textRenderer, Text.literal("G"), 138, 56, lc, false);
        context.drawText(textRenderer, Text.literal("B"), 173, 56, lc, false);
        context.drawText(textRenderer, Text.literal("H"), 103, 74, lc, false);
        context.drawText(textRenderer, Text.literal("S"), 138, 74, lc, false);
        context.drawText(textRenderer, Text.literal("B"), 173, 74, lc, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
        if (errorMessage != null && errorDisplayTicks > 0) {
            float scale = 0.7f;
            int sw = (int)(textRenderer.getWidth(errorMessage) * scale), sh = (int)(9 * scale);
            int msgX = x + 190 - sw, msgY = y + 110;
            context.fill(msgX - 3, msgY - 2, msgX + sw + 3, msgY + sh + 2, 0xCC000000);
            context.getMatrices().push();
            context.getMatrices().translate(msgX, msgY, 0);
            context.getMatrices().scale(scale, scale, 1.0f);
            context.drawTextWithShadow(textRenderer, errorMessage, 0, 0, errorColor);
            context.getMatrices().pop();
            errorDisplayTicks--;
            if (errorDisplayTicks <= 0) errorMessage = null;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_E && (hexField.isFocused() || rField.isFocused() || gField.isFocused() || bField.isFocused()
                || hField.isFocused() || sField.isFocused() || brField.isFocused()))
            return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}