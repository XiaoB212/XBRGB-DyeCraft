package com.xbrgb.dyecraft.item;

import com.xbrgb.dyecraft.util.ColorUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DyeableBlockItem extends BlockItem {
    public DyeableBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        setColorData(stack, 0xFFFFFFFF);
        return stack;
    }

    public static void setColorData(ItemStack stack, int color) {
        int fullColor = 0xFF000000 | (color & 0xFFFFFF);
        CompoundTag tag = new CompoundTag();
        tag.putInt("Color", fullColor);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public Component getName(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        int color = 0xFFFFFFFF;
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("Color")) color = tag.getInt("Color");
        }
        String hex = String.format("#%06X", color & 0xFFFFFF);
        return Component.translatable(this.getDescriptionId()).append(" (" + hex + ")");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("Color")) {
                int color = tag.getInt("Color");
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;
                float[] hsb = ColorUtils.RGBtoHSB(r, g, b);
                tooltip.add(Component.literal(String.format("RGB: %d, %d, %d", r, g, b)).withStyle(net.minecraft.ChatFormatting.GRAY));
                tooltip.add(Component.literal(String.format("HEX: #%06X", color & 0xFFFFFF)).withStyle(net.minecraft.ChatFormatting.GRAY));
                tooltip.add(Component.literal(String.format("HSB: %.0f°, %.0f%%, %.0f%%", hsb[0], hsb[1], hsb[2])).withStyle(net.minecraft.ChatFormatting.GRAY));
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide) {
            boolean needsFix = false;
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                CompoundTag tag = customData.copyTag();
                if (tag.contains("Color") && !(tag.get("Color") instanceof Number)) {
                    needsFix = true;
                }
            }
            if (needsFix) {
                int color = 0xFFFFFFFF;
                if (customData != null) {
                    CompoundTag tag = customData.copyTag();
                    if (tag.contains("Color")) color = tag.getInt("Color");
                }
                setColorData(stack, color);
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }
}