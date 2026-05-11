package com.xbrgb.dyecraft.block;

import com.xbrgb.dyecraft.DyecraftMod;
import com.xbrgb.dyecraft.item.DyeableBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DyecraftMod.MOD_ID);

    // 染料花等无物品块的注册保持不变...

    public static final DeferredBlock<Block> RED_DYE_FLOWER = BLOCKS.register("red_dye_flower",
            () -> new DyeFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noOcclusion(),
                    () -> com.xbrgb.dyecraft.item.ModItems.RED_DYE_SEEDS.get(),
                    () -> com.xbrgb.dyecraft.item.ModItems.RED_DYE_PLANT.get()));
    public static final DeferredBlock<Block> BLUE_DYE_FLOWER = BLOCKS.register("blue_dye_flower",
            () -> new DyeFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noOcclusion(),
                    () -> com.xbrgb.dyecraft.item.ModItems.BLUE_DYE_SEEDS.get(),
                    () -> com.xbrgb.dyecraft.item.ModItems.BLUE_DYE_PLANT.get()));
    public static final DeferredBlock<Block> GREEN_DYE_FLOWER = BLOCKS.register("green_dye_flower",
            () -> new DyeFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noOcclusion(),
                    () -> com.xbrgb.dyecraft.item.ModItems.GREEN_DYE_SEEDS.get(),
                    () -> com.xbrgb.dyecraft.item.ModItems.GREEN_DYE_PLANT.get()));

    // 可染色方块（使用 DyeableBlockItem）
    public static final DeferredBlock<Block> DYEABLE_BLOCK = register("dyeable_block",
            () -> new DyeableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_STONE)
                    .requiresCorrectToolForDrops().strength(2.0f, 6.0f)));
    public static final DeferredBlock<Block> DYEABLE_GLASS = register("dyeable_glass",
            () -> new DyeableGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .noOcclusion().strength(0.3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DYEABLE_CONCRETE = register("dyeable_concrete",
            () -> new DyeableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE)
                    .requiresCorrectToolForDrops().strength(1.8f, 9.0f)));
    public static final DeferredBlock<Block> DYEABLE_TERRACOTTA = register("dyeable_terracotta",
            () -> new DyeableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA)
                    .requiresCorrectToolForDrops().strength(1.25f, 4.2f)));
    public static final DeferredBlock<Block> DYEABLE_WOOL = register("dyeable_wool",
            () -> new DyeableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
                    .strength(0.8f)));

    // 染色台（使用普通 BlockItem）
    public static final DeferredBlock<Block> DYEING_STATION = BLOCKS.register("dyeing_station",
            () -> new DyeingStationBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                    .requiresCorrectToolForDrops().strength(50.0f, 1200.0f).noOcclusion()));

    // 在静态块中单独注册染色台的普通物品
    static {
        com.xbrgb.dyecraft.item.ModItems.ITEMS.register("dyeing_station",
                () -> new BlockItem(DYEING_STATION.get(), new Item.Properties()));
    }

    // 可染色方块的通用注册方法（返回 DeferredBlock 并使用 DyeableBlockItem）
    private static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> blockSupplier) {
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        com.xbrgb.dyecraft.item.ModItems.ITEMS.register(name,
                () -> new DyeableBlockItem(block.get(), new Item.Properties()));
        return block;
    }
}