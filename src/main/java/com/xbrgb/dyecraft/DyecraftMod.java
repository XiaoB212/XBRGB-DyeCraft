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
import com.xbrgb.dyecraft.blockentity.ModBlockEntities;
import com.xbrgb.dyecraft.item.DyeableBlockItem;
import com.xbrgb.dyecraft.item.ModItems;
import com.xbrgb.dyecraft.screen.ModScreenHandlers;
import com.xbrgb.dyecraft.util.ModPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DyecraftMod implements ModInitializer {
    public static final String MOD_ID = "dyecraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(
            RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "main"));

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing DyeCraft Mod...");

        ModItems.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModScreenHandlers.register();
        ModPackets.register();

        // 物品组
        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP,
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(ModItems.RGB_DYE))
                        .displayName(Text.translatable("itemGroup.dyecraft.main"))
                        .entries((context, entries) -> {
                            entries.add(new ItemStack(ModItems.RGB_DYE));
                            entries.add(new ItemStack(ModItems.RED_DYE_SEEDS));
                            entries.add(new ItemStack(ModItems.BLUE_DYE_SEEDS));
                            entries.add(new ItemStack(ModItems.GREEN_DYE_SEEDS));
                            entries.add(new ItemStack(ModItems.RED_DYE_PLANT));
                            entries.add(new ItemStack(ModItems.BLUE_DYE_PLANT));
                            entries.add(new ItemStack(ModItems.GREEN_DYE_PLANT));
                            // 可染色方块使用带 NBT 的默认堆叠
                            entries.add(DyeableBlockItem.createDefaultStack(ModBlocks.DYEABLE_BLOCK));
                            entries.add(DyeableBlockItem.createDefaultStack(ModBlocks.DYEABLE_GLASS));
                            entries.add(DyeableBlockItem.createDefaultStack(ModBlocks.DYEABLE_CONCRETE));
                            entries.add(DyeableBlockItem.createDefaultStack(ModBlocks.DYEABLE_TERRACOTTA));
                            entries.add(DyeableBlockItem.createDefaultStack(ModBlocks.DYEABLE_WOOL));
                            entries.add(new ItemStack(ModBlocks.DYEING_STATION));
                        })
                        .build());

        // 堆肥概率
        CompostingChanceRegistry.INSTANCE.add(ModItems.RED_DYE_SEEDS, 0.5f);
        CompostingChanceRegistry.INSTANCE.add(ModItems.BLUE_DYE_SEEDS, 0.5f);
        CompostingChanceRegistry.INSTANCE.add(ModItems.GREEN_DYE_SEEDS, 0.5f);
        CompostingChanceRegistry.INSTANCE.add(ModItems.RED_DYE_PLANT, 0.85f);
        CompostingChanceRegistry.INSTANCE.add(ModItems.BLUE_DYE_PLANT, 0.85f);
        CompostingChanceRegistry.INSTANCE.add(ModItems.GREEN_DYE_PLANT, 0.85f);

        LOGGER.info("DyeCraft Mod initialized!");
    }
}