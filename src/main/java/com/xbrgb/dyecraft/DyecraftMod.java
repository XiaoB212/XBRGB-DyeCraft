package com.xbrgb.dyecraft;

import com.xbrgb.dyecraft.block.ModBlocks;
import com.xbrgb.dyecraft.blockentity.DyeingStationBlockEntity;
import com.xbrgb.dyecraft.blockentity.ModBlockEntities;
import com.xbrgb.dyecraft.item.ModItems;
import com.xbrgb.dyecraft.screen.ModMenuTypes;
import com.xbrgb.dyecraft.util.ModPackets;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

@Mod(DyecraftMod.MOD_ID)
public class DyecraftMod {
    public static final String MOD_ID = "dyecraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final Supplier<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.dyecraft.main"))
                    .icon(() -> new ItemStack(ModItems.RGB_DYE.get()))
                    .displayItems((params, output) -> {
                        output.accept(new ItemStack(ModItems.RGB_DYE.get()));
                        output.accept(new ItemStack(ModItems.RED_DYE_SEEDS.get()));
                        output.accept(new ItemStack(ModItems.BLUE_DYE_SEEDS.get()));
                        output.accept(new ItemStack(ModItems.GREEN_DYE_SEEDS.get()));
                        output.accept(new ItemStack(ModItems.RED_DYE_PLANT.get()));
                        output.accept(new ItemStack(ModItems.BLUE_DYE_PLANT.get()));
                        output.accept(new ItemStack(ModItems.GREEN_DYE_PLANT.get()));
                        output.accept(ModBlocks.DYEABLE_BLOCK.get().asItem().getDefaultInstance());
                        output.accept(ModBlocks.DYEABLE_GLASS.get().asItem().getDefaultInstance());
                        output.accept(ModBlocks.DYEABLE_CONCRETE.get().asItem().getDefaultInstance());
                        output.accept(ModBlocks.DYEABLE_TERRACOTTA.get().asItem().getDefaultInstance());
                        output.accept(ModBlocks.DYEABLE_WOOL.get().asItem().getDefaultInstance());
                        output.accept(new ItemStack(ModBlocks.DYEING_STATION.get().asItem()));
                    })
                    .build());

    public DyecraftMod(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        ModPackets.register(modEventBus);

        // 注册漏斗能力
        modEventBus.addListener(this::registerCapabilities);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.DYEING_STATION.get(),
                DyeingStationBlockEntity::getItemHandler
        );
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}