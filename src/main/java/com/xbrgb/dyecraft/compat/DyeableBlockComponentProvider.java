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

package com.xbrgb.dyecraft.compat;

import com.xbrgb.dyecraft.blockentity.DyeableBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum DyeableBlockComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public Identifier getUid() {
        return DyecraftJadePlugin.DYEABLE_BLOCK;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof DyeableBlockEntity dyeable) {
            int color = dyeable.getColor() & 0xFFFFFF; // 强制忽略 Alpha 通道
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            float[] hsb = java.awt.Color.RGBtoHSB(r, g, b, null);

            String hex = String.format("#%06X", color);
            String rgbStr = String.format("%d, %d, %d", r, g, b);
            String hsbStr = String.format("%.0f°, %.0f%%, %.0f%%",
                    hsb[0] * 360, hsb[1] * 100, hsb[2] * 100);



            // 第二行：RGB
            tooltip.add(Text.translatable("jade.dyecraft.rgb", rgbStr));

            // 第三行：HEX (单独显示，方便复制)
            tooltip.add(Text.translatable("jade.dyecraft.color", hex));

            // 第四行：HSB
            tooltip.add(Text.translatable("jade.dyecraft.hsb", hsbStr));
        }
    }

    @Override
    public void appendServerData(NbtCompound tag, BlockAccessor accessor) {
        // 无需发送额外数据
    }
}