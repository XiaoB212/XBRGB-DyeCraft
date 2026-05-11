package com.xbrgb.dyecraft.util;

import com.xbrgb.dyecraft.DyecraftMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SetDefaultColorPayload(int color) implements CustomPacketPayload {
    public static final Type<SetDefaultColorPayload> TYPE = new Type<>(DyecraftMod.id("set_default_color"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetDefaultColorPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetDefaultColorPayload::color,
            SetDefaultColorPayload::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}