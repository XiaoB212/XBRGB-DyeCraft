package com.xbrgb.dyecraft.compat;

import com.xbrgb.dyecraft.DyecraftMod;
import com.xbrgb.dyecraft.block.DyeableBlock;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class DyecraftJadePlugin implements IWailaPlugin {
    public static final ResourceLocation DYEABLE_BLOCK = DyecraftMod.id("dyeable_block");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(DyeableBlockComponentProvider.INSTANCE, DyeableBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(DyeableBlockComponentProvider.INSTANCE, DyeableBlock.class);
    }
}