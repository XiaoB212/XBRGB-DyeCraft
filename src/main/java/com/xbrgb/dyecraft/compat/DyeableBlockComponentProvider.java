package com.xbrgb.dyecraft.compat;

import com.xbrgb.dyecraft.blockentity.DyeableBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum DyeableBlockComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public ResourceLocation getUid() {
        return DyecraftJadePlugin.DYEABLE_BLOCK;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof DyeableBlockEntity dyeable) {
            int color = dyeable.getColor() & 0xFFFFFF;
            int r = (color >> 16) & 0xFF, g = (color >> 8) & 0xFF, b = color & 0xFF;
            float[] hsb = java.awt.Color.RGBtoHSB(r, g, b, null);
            String hex = String.format("#%06X", color);
            String rgbStr = String.format("RGB: %d, %d, %d", r, g, b);
            String hsbStr = String.format("HSB: %.0f°, %.0f%%, %.0f%%", hsb[0] * 360, hsb[1] * 100, hsb[2] * 100);
            tooltip.add(Component.literal(hex));
            tooltip.add(Component.literal(rgbStr));
            tooltip.add(Component.literal(hsbStr));
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {}
}