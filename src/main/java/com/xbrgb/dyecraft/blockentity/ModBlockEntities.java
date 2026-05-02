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

package com.xbrgb.dyecraft.blockentity;

import com.xbrgb.dyecraft.DyecraftMod;
import com.xbrgb.dyecraft.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<DyeableBlockEntity> DYEABLE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(DyecraftMod.MOD_ID, "dyeable_block"),
                    BlockEntityType.Builder.create(DyeableBlockEntity::new,
                            ModBlocks.DYEABLE_BLOCK,
                            ModBlocks.DYEABLE_GLASS,
                            ModBlocks.DYEABLE_CONCRETE,
                            ModBlocks.DYEABLE_TERRACOTTA,
                            ModBlocks.DYEABLE_WOOL).build());

    public static final BlockEntityType<DyeingStationBlockEntity> DYEING_STATION_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(DyecraftMod.MOD_ID, "dyeing_station"),
                    BlockEntityType.Builder.create(DyeingStationBlockEntity::new,
                            ModBlocks.DYEING_STATION).build());

    public static void register() {
    }
}