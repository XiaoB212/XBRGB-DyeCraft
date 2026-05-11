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


package com.xbrgb.dyecraft.block;

import com.xbrgb.dyecraft.DyecraftMod;
import com.xbrgb.dyecraft.item.DyeableBlockItem;
import com.xbrgb.dyecraft.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {
    // 染料花：只注册方块，不生成对应的物品！
    public static final Block RED_DYE_FLOWER = registerBlockWithoutItem("red_dye_flower",
            new DyeFlowerBlock(AbstractBlock.Settings.copy(Blocks.WHEAT).nonOpaque(),
                    () -> ModItems.RED_DYE_SEEDS, () -> ModItems.RED_DYE_PLANT));

    public static final Block BLUE_DYE_FLOWER = registerBlockWithoutItem("blue_dye_flower",
            new DyeFlowerBlock(AbstractBlock.Settings.copy(Blocks.WHEAT).nonOpaque(),
                    () -> ModItems.BLUE_DYE_SEEDS, () -> ModItems.BLUE_DYE_PLANT));

    public static final Block GREEN_DYE_FLOWER = registerBlockWithoutItem("green_dye_flower",
            new DyeFlowerBlock(AbstractBlock.Settings.copy(Blocks.WHEAT).nonOpaque(),
                    () -> ModItems.GREEN_DYE_SEEDS, () -> ModItems.GREEN_DYE_PLANT));

    // 其他可染色方块照常注册（带物品）
    public static final Block DYEABLE_BLOCK = registerBlock("dyeable_block",
            new DyeableBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE)
                    .requiresTool()
                    .strength(2.0f, 6.0f)),
            block -> new DyeableBlockItem(block, new Item.Settings()));

    public static final Block DYEABLE_GLASS = registerBlock("dyeable_glass",
            new DyeableGlassBlock(AbstractBlock.Settings.copy(Blocks.GLASS)
                    .nonOpaque()
                    .strength(0.3f)
                    .requiresTool()),
            block -> new DyeableBlockItem(block, new Item.Settings()));

    public static final Block DYEABLE_CONCRETE = registerBlock("dyeable_concrete",
            new DyeableBlock(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)
                    .requiresTool()
                    .strength(1.8f, 9.0f)),
            block -> new DyeableBlockItem(block, new Item.Settings()));

    public static final Block DYEABLE_TERRACOTTA = registerBlock("dyeable_terracotta",
            new DyeableBlock(AbstractBlock.Settings.copy(Blocks.TERRACOTTA)
                    .requiresTool()
                    .strength(1.25f, 4.2f)),
            block -> new DyeableBlockItem(block, new Item.Settings()));

    public static final Block DYEABLE_WOOL = registerBlock("dyeable_wool",
            new DyeableBlock(AbstractBlock.Settings.copy(Blocks.WHITE_WOOL)
                    .strength(0.8f)),
            block -> new DyeableBlockItem(block, new Item.Settings()));

    public static final Block DYEING_STATION = registerBlock("dyeing_station",
            new DyeingStationBlock(AbstractBlock.Settings.copy(Blocks.OBSIDIAN)
                    .requiresTool()
                    .strength(50.0f, 1200.0f)
                    .nonOpaque()));

    // -------------------- 内部方法 --------------------

    // 原有：注册方块 + 普通BlockItem
    private static Block registerBlock(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(DyecraftMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, Identifier.of(DyecraftMod.MOD_ID, name), block);
    }

    // 原有：注册方块 + 自定义BlockItem
    private static Block registerBlock(String name, Block block, Function<Block, BlockItem> itemFactory) {
        Block registeredBlock = Registry.register(Registries.BLOCK, Identifier.of(DyecraftMod.MOD_ID, name), block);
        Registry.register(Registries.ITEM, Identifier.of(DyecraftMod.MOD_ID, name),
                itemFactory.apply(registeredBlock));
        return registeredBlock;
    }

    // 新加：只注册方块，不生成任何物品
    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(DyecraftMod.MOD_ID, name), block);
    }

    public static void register() {}
}