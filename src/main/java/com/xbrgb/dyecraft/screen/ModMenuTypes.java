package com.xbrgb.dyecraft.screen;

import com.xbrgb.dyecraft.DyecraftMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, DyecraftMod.MOD_ID);

    public static final Supplier<MenuType<DyeingStationMenu>> DYEING_STATION = MENU_TYPES.register("dyeing_station",
            () -> new MenuType<>(DyeingStationMenu::new, net.minecraft.world.flag.FeatureFlags.VANILLA_SET));
}