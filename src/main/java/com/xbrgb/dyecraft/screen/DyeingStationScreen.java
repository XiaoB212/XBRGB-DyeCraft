package com.xbrgb.dyecraft.screen;

import com.xbrgb.dyecraft.DyecraftMod;
import com.xbrgb.dyecraft.util.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class DyeingStationScreen extends AbstractContainerScreen<DyeingStationMenu> {
    private static final ResourceLocation TEXTURE = DyecraftMod.id("textures/gui/dyeing_station.png");
    private static final int BG_WIDTH = 215, BG_HEIGHT = 215;

    private EditBox hexField, rField, gField, bField, hField, sField, brField;
    private int currentColor = 0xFFFFFF;
    private boolean updating = false;

    private Component errorMessage = null;
    private int errorDisplayTicks = 0;
    private int errorColor = 0xFF5555;
    private boolean defaultColorLoaded = false;

    public DyeingStationScreen(DyeingStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = BG_WIDTH;
        this.imageHeight = BG_HEIGHT;
    }

    public void setUndoMessage(Component msg, int color) {
        this.errorMessage = msg;
        this.errorDisplayTicks = 60;
        this.errorColor = color;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 4;

        hexField = new EditBox(font, leftPos + 130, topPos + 35, 60, 15, Component.empty());
        hexField.setMaxLength(7);
        hexField.setResponder(s -> {
            if (updating) return;
            if (!s.startsWith("#")) {
                s = "#" + s;
                hexField.setValue(s);
            }
            if (s.length() == 7) {
                try {
                    currentColor = Integer.parseInt(s.substring(1), 16);
                    updateFieldsFromColor();
                } catch (NumberFormatException ignored) {}
            }
        });
        addRenderableWidget(hexField);

        rField = addField(110, 52, 26, "255");
        gField = addField(145, 52, 26, "255");
        bField = addField(180, 52, 26, "255");
        hField = addField(110, 70, 26, "0");
        sField = addField(145, 70, 26, "0");
        brField = addField(180, 70, 26, "100");

        rField.setResponder(s -> { if (!updating) { updateColorFromRGB(); } });
        gField.setResponder(s -> { if (!updating) { updateColorFromRGB(); } });
        bField.setResponder(s -> { if (!updating) { updateColorFromRGB(); } });
        hField.setResponder(s -> { if (!updating) { updateColorFromHSB(); } });
        sField.setResponder(s -> { if (!updating) { updateColorFromHSB(); } });
        brField.setResponder(s -> { if (!updating) { updateColorFromHSB(); } });

        // 染色按钮 (112,88) 尺寸 28×20
        addRenderableWidget(Button.builder(Component.translatable("gui.dyecraft.dyeing_station.button.dye"), btn -> {
            if (!validateInputs()) return;
            try {
                int r = Integer.parseInt(rField.getValue()), g = Integer.parseInt(gField.getValue()), b = Integer.parseInt(bField.getValue());
                PacketDistributor.sendToServer(new DyePayload(r, g, b));
                errorMessage = null;
            } catch (NumberFormatException ignored) {}
        }).pos(leftPos + 112, topPos + 88).size(28, 20).build());

        // 撤销按钮 (144,88) 尺寸 28×20
        addRenderableWidget(Button.builder(Component.translatable("gui.dyecraft.dyeing_station.button.undo"), btn -> {
            PacketDistributor.sendToServer(new UndoPayload());
        }).pos(leftPos + 144, topPos + 88).size(28, 20).build());

        // 预设按钮 (176,88) 尺寸 28×20
        addRenderableWidget(Button.builder(Component.translatable("gui.dyecraft.dyeing_station.button.preset"), btn -> {
            try {
                int r = Integer.parseInt(rField.getValue()), g = Integer.parseInt(gField.getValue()), b = Integer.parseInt(bField.getValue());
                int color = 0xFF000000 | (r << 16) | (g << 8) | b;
                PacketDistributor.sendToServer(new SetDefaultColorPayload(color));
                errorMessage = Component.translatable("gui.dyecraft.dyeing_station.preset.saved");
                errorDisplayTicks = 40;
                errorColor = 0x55FF55;
            } catch (NumberFormatException ignored) {}
        }).pos(leftPos + 176, topPos + 88).size(28, 20).build());

        updateFieldsFromColor();
    }

    private EditBox addField(int x, int y, int w, String defVal) {
        EditBox box = new EditBox(font, leftPos + x, topPos + y, w, 15, Component.empty());
        box.setMaxLength(3);
        box.setValue(defVal);
        addRenderableWidget(box);
        return box;
    }

    private boolean validateInputs() {
        ItemStack redDye = menu.getSlot(6).getItem();
        ItemStack greenDye = menu.getSlot(7).getItem();
        ItemStack blueDye = menu.getSlot(8).getItem();
        if (redDye.isEmpty() || greenDye.isEmpty() || blueDye.isEmpty()) {
            errorMessage = Component.translatable("gui.dyecraft.dyeing_station.error.no_dye");
            errorDisplayTicks = 60; errorColor = 0xFF5555; return false;
        }
        int totalBlocks = 0;
        for (int i = 0; i < 3; i++) totalBlocks += menu.getSlot(i).getItem().getCount();
        if (totalBlocks == 0) {
            errorMessage = Component.translatable("gui.dyecraft.dyeing_station.error.no_block");
            errorDisplayTicks = 60; errorColor = 0xFF5555; return false;
        }
        int groupsNeeded = (totalBlocks + 15) / 16;
        if (redDye.getCount() < groupsNeeded || greenDye.getCount() < groupsNeeded || blueDye.getCount() < groupsNeeded) {
            errorMessage = Component.translatable("gui.dyecraft.dyeing_station.error.not_enough_dye");
            errorDisplayTicks = 60; errorColor = 0xFF5555; return false;
        }
        return true;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (!defaultColorLoaded) {
            int def = menu.getDefaultColor();
            if (def != 0 && def != 0xFFFFFFFF) {
                currentColor = def & 0xFFFFFF;
                updateFieldsFromColor();
                defaultColorLoaded = true;
            }
        }
    }

    private void updateFieldsFromColor() {
        updating = true;
        int r = (currentColor >> 16) & 0xFF, g = (currentColor >> 8) & 0xFF, b = currentColor & 0xFF;
        rField.setValue(""+r); gField.setValue(""+g); bField.setValue(""+b);
        float[] hsb = ColorUtils.RGBtoHSB(r, g, b);
        hField.setValue(""+(int)hsb[0]); sField.setValue(""+(int)hsb[1]); brField.setValue(""+(int)hsb[2]);
        hexField.setValue(String.format("#%06X", currentColor));
        updating = false;
    }

    private void updateColorFromRGB() {
        try {
            int r = Integer.parseInt(rField.getValue()), g = Integer.parseInt(gField.getValue()), b = Integer.parseInt(bField.getValue());
            currentColor = (r << 16) | (g << 8) | b;
            updateFieldsFromColor();
        } catch (NumberFormatException ignored) {}
    }

    private void updateColorFromHSB() {
        try {
            float h = Float.parseFloat(hField.getValue()), s = Float.parseFloat(sField.getValue()), br = Float.parseFloat(brField.getValue());
            currentColor = ColorUtils.HSBtoRGB(h, s, br);
            updateFieldsFromColor();
        } catch (NumberFormatException ignored) {}
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1,1,1,1);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        graphics.fill(leftPos + 130, topPos + 20, leftPos + 190, topPos + 33, 0xFF000000 | currentColor);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 28, 109, 0x404040, false);
        graphics.drawString(font, "PRE", 110, 23, 0xFFFFFF);
        graphics.drawString(font, "HEX", 110, 39, 0xFFFFFF);
        graphics.drawString(font, "R", 103, 56, 0xFFFFFF);
        graphics.drawString(font, "G", 138, 56, 0xFFFFFF);
        graphics.drawString(font, "B", 173, 56, 0xFFFFFF);
        graphics.drawString(font, "H", 103, 74, 0xFFFFFF);
        graphics.drawString(font, "S", 138, 74, 0xFFFFFF);
        graphics.drawString(font, "B", 173, 74, 0xFFFFFF);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        this.renderTooltip(graphics, mouseX, mouseY);
        if (errorMessage != null && errorDisplayTicks > 0) {
            float scale = 0.7f;
            int sw = (int)(font.width(errorMessage) * scale), sh = (int)(9 * scale);
            int msgX = leftPos + 190 - sw, msgY = topPos + 110;
            graphics.fill(msgX - 3, msgY - 2, msgX + sw + 3, msgY + sh + 2, 0xCC000000);
            graphics.pose().pushPose();
            graphics.pose().translate(msgX, msgY, 0);
            graphics.pose().scale(scale, scale, 1.0f);
            graphics.drawString(font, errorMessage, 0, 0, errorColor);
            graphics.pose().popPose();
            errorDisplayTicks--;
            if (errorDisplayTicks <= 0) errorMessage = null;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_E &&
            (hexField.isFocused() || rField.isFocused() || gField.isFocused() || bField.isFocused()
             || hField.isFocused() || sField.isFocused() || brField.isFocused())) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        // 关闭界面时清除撤销快照
        PacketDistributor.sendToServer(new ClearSnapshotPayload());
        super.onClose();
    }
}