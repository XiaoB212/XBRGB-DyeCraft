package com.xbrgb.dyecraft.blockentity;

import com.xbrgb.dyecraft.DyecraftMod;
import com.xbrgb.dyecraft.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DyecraftMod.MOD_ID);

    public static final Supplier<BlockEntityType<DyeableBlockEntity>> DYEABLE = BLOCK_ENTITIES.register("dyeable",
            () -> BlockEntityType.Builder.of(DyeableBlockEntity::new,
                    ModBlocks.DYEABLE_BLOCK.get(),
                    ModBlocks.DYEABLE_GLASS.get(),
                    ModBlocks.DYEABLE_CONCRETE.get(),
                    ModBlocks.DYEABLE_TERRACOTTA.get(),
                    ModBlocks.DYEABLE_WOOL.get()).build(null));

    public static final Supplier<BlockEntityType<DyeingStationBlockEntity>> DYEING_STATION = BLOCK_ENTITIES.register("dyeing_station",
            () -> BlockEntityType.Builder.of(DyeingStationBlockEntity::new,
                    ModBlocks.DYEING_STATION.get()).build(null));
}