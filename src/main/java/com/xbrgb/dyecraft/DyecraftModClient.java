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


package com.xbrgb.dyecraft;

import com.xbrgb.dyecraft.block.ModBlocks;
import com.xbrgb.dyecraft.blockentity.DyeableBlockEntity;
import com.xbrgb.dyecraft.screen.DyeingStationScreen;
import com.xbrgb.dyecraft.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;

public class DyecraftModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 方块颜色提供器
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
                    if (world != null && pos != null) {
                        if (world.getBlockEntity(pos) instanceof DyeableBlockEntity be) {
                            // 强制添加完全不透明的 Alpha 通道
                            int rgb = be.getColor();
                            return 0xFF000000 | rgb;
                        }
                    }
                    return 0xFFFFFFFF; // 不透明白色
                }, ModBlocks.DYEABLE_BLOCK, ModBlocks.DYEABLE_GLASS, ModBlocks.DYEABLE_CONCRETE,
                ModBlocks.DYEABLE_TERRACOTTA, ModBlocks.DYEABLE_WOOL);

        // 物品颜色提供器
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                    NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
                    if (nbt != null && nbt.contains("Color")) {
                        int rgb = nbt.getInt("Color");
                        // 强制添加完全不透明的 Alpha 通道
                        return 0xFF000000 | rgb;
                    }
                    return 0xFFFFFFFF; // 不透明白色
                }, ModBlocks.DYEABLE_BLOCK.asItem(), ModBlocks.DYEABLE_GLASS.asItem(),
                ModBlocks.DYEABLE_CONCRETE.asItem(), ModBlocks.DYEABLE_TERRACOTTA.asItem(),
                ModBlocks.DYEABLE_WOOL.asItem());

        // 渲染层
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RED_DYE_FLOWER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BLUE_DYE_FLOWER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GREEN_DYE_FLOWER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DYEABLE_GLASS, RenderLayer.getTranslucent());

        HandledScreens.register(ModScreenHandlers.DYEING_STATION_SCREEN_HANDLER, DyeingStationScreen::new);
    }
}