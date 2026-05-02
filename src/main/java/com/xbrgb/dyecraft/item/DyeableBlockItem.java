/*
 * Copyright [2026] [XiaoB212 of copyright owner]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xbrgb.dyecraft.item;

import com.xbrgb.dyecraft.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class DyeableBlockItem extends BlockItem {
    public DyeableBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        setColorData(stack, 0xFFFFFFFF);
        stack.remove(DataComponentTypes.BLOCK_STATE);
        return stack;
    }

    public static void setColorData(ItemStack stack, int color) {
        int fullColor = 0xFF000000 | (color & 0xFFFFFF);
        stack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, comp -> comp.apply(nbt -> nbt.putInt("Color", fullColor)));
    }

    public static ItemStack createDefaultStack(Block block) {
        ItemStack stack = new ItemStack(block.asItem());
        setColorData(stack, 0xFFFFFFFF);
        stack.remove(DataComponentTypes.BLOCK_STATE);
        return stack;
    }

    @Override
    public Text getName(ItemStack stack) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        int color = 0xFFFFFFFF;
        if (nbt != null && nbt.contains("Color")) {
            color = nbt.getInt("Color");
        }
        int rgb = color & 0x00FFFFFF;
        String hex = String.format("#%06X", rgb);
        return Text.translatable(this.getTranslationKey()).append(" (" + hex + ")");
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (nbt != null && nbt.contains("Color")) {
            int color = nbt.getInt("Color");
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            float[] hsb = ColorUtils.RGBtoHSB(r, g, b);
            tooltip.add(Text.literal(String.format("RGB: %d, %d, %d", r, g, b)).formatted(Formatting.GRAY));
            tooltip.add(Text.literal(String.format("HEX: #%06X", color & 0xFFFFFF)).formatted(Formatting.GRAY));
            tooltip.add(Text.literal(String.format("HSB: %.0f°, %.0f%%, %.0f%%", hsb[0], hsb[1], hsb[2])).formatted(Formatting.GRAY));
        }
    }

    // 👇 这个新方法会在物品进入背包时自动修复不一致的组件
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            boolean needsFix = stack.contains(DataComponentTypes.BLOCK_STATE);
            // 检查并移除可能残留的 block_state 组件
            // 检查颜色数据是否为整数类型 (NBT 类型 3 代表 Int)
            var customData = stack.get(DataComponentTypes.CUSTOM_DATA);
            if (customData != null) {
                NbtCompound nbt = customData.copyNbt();
                if (nbt.contains("Color") && Objects.requireNonNull(nbt.get("Color")).getType() != 3) {
                    needsFix = true;
                }
            }
            // 如果需要，用标准方法重写数据
            if (needsFix) {
                int color = 0xFFFFFFFF;
                if (customData != null && customData.copyNbt().contains("Color")) {
                    color = customData.copyNbt().getInt("Color");
                }
                stack.remove(DataComponentTypes.BLOCK_STATE);
                setColorData(stack, color);
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}