package com.xbrgb.dyecraft;

import com.xbrgb.dyecraft.block.ModBlocks;
import com.xbrgb.dyecraft.blockentity.DyeableBlockEntity;
import com.xbrgb.dyecraft.screen.DyeingStationScreen;
import com.xbrgb.dyecraft.screen.ModMenuTypes;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class DyecraftModClient {
    public DyecraftModClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @EventBusSubscriber(modid = DyecraftMod.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.DYEABLE_GLASS.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.RED_DYE_FLOWER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLUE_DYE_FLOWER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.GREEN_DYE_FLOWER.get(), RenderType.cutout());
            });
        }

        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
            event.register((state, level, pos, tintIndex) -> {
                        if (level != null && pos != null) {
                            if (level.getBlockEntity(pos) instanceof DyeableBlockEntity be) {
                                return 0xFF000000 | be.getColor();
                            }
                        }
                        return 0xFFFFFFFF;
                    }, ModBlocks.DYEABLE_BLOCK.get(), ModBlocks.DYEABLE_GLASS.get(),
                    ModBlocks.DYEABLE_CONCRETE.get(), ModBlocks.DYEABLE_TERRACOTTA.get(),
                    ModBlocks.DYEABLE_WOOL.get());
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register((stack, tintIndex) -> {
                        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
                        if (customData != null) {
                            CompoundTag tag = customData.copyTag();
                            if (tag.contains("Color")) {
                                return 0xFF000000 | tag.getInt("Color");
                            }
                        }
                        return 0xFFFFFFFF;
                    }, ModBlocks.DYEABLE_BLOCK.get().asItem(), ModBlocks.DYEABLE_GLASS.get().asItem(),
                    ModBlocks.DYEABLE_CONCRETE.get().asItem(), ModBlocks.DYEABLE_TERRACOTTA.get().asItem(),
                    ModBlocks.DYEABLE_WOOL.get().asItem());
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.DYEING_STATION.get(), DyeingStationScreen::new);
        }
    }
}