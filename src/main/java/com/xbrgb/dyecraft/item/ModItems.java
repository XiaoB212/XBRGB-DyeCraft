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

import com.xbrgb.dyecraft.DyecraftMod;
import com.xbrgb.dyecraft.block.ModBlocks;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item RGB_DYE = register("rgb_dye", new RGBDyeItem(new Item.Settings()));

    public static final Item RED_DYE_SEEDS = register("red_dye_seeds",
            new AliasedBlockItem(ModBlocks.RED_DYE_FLOWER, new Item.Settings()));
    public static final Item BLUE_DYE_SEEDS = register("blue_dye_seeds",
            new AliasedBlockItem(ModBlocks.BLUE_DYE_FLOWER, new Item.Settings()));
    public static final Item GREEN_DYE_SEEDS = register("green_dye_seeds",
            new AliasedBlockItem(ModBlocks.GREEN_DYE_FLOWER, new Item.Settings()));

    public static final Item RED_DYE_PLANT = register("red_dye_plant", new Item(new Item.Settings()));
    public static final Item BLUE_DYE_PLANT = register("blue_dye_plant", new Item(new Item.Settings()));
    public static final Item GREEN_DYE_PLANT = register("green_dye_plant", new Item(new Item.Settings()));

    private static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(DyecraftMod.MOD_ID, id), item);
    }

    public static void register() {}
}