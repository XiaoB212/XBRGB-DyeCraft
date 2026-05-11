package com.xbrgb.dyecraft.screen;

import com.xbrgb.dyecraft.DyecraftMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<DyeingStationScreenHandler> DYEING_STATION_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER,
                    Identifier.of(DyecraftMod.MOD_ID, "dyeing_station"),
                    new ScreenHandlerType<>(DyeingStationScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

    public static void register() {}
}