package com.xbrgb.dyecraft.item;

import com.xbrgb.dyecraft.DyecraftMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DyecraftMod.MOD_ID);

    public static final DeferredItem<Item> RGB_DYE = ITEMS.register("rgb_dye",
            () -> new RGBDyeItem(new Item.Properties()));

    public static final DeferredItem<Item> RED_DYE_SEEDS = ITEMS.register("red_dye_seeds",
            () -> new ItemNameBlockItem(com.xbrgb.dyecraft.block.ModBlocks.RED_DYE_FLOWER.get(), new Item.Properties()));
    public static final DeferredItem<Item> BLUE_DYE_SEEDS = ITEMS.register("blue_dye_seeds",
            () -> new ItemNameBlockItem(com.xbrgb.dyecraft.block.ModBlocks.BLUE_DYE_FLOWER.get(), new Item.Properties()));
    public static final DeferredItem<Item> GREEN_DYE_SEEDS = ITEMS.register("green_dye_seeds",
            () -> new ItemNameBlockItem(com.xbrgb.dyecraft.block.ModBlocks.GREEN_DYE_FLOWER.get(), new Item.Properties()));

    public static final DeferredItem<Item> RED_DYE_PLANT = ITEMS.register("red_dye_plant",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BLUE_DYE_PLANT = ITEMS.register("blue_dye_plant",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GREEN_DYE_PLANT = ITEMS.register("green_dye_plant",
            () -> new Item(new Item.Properties()));
}