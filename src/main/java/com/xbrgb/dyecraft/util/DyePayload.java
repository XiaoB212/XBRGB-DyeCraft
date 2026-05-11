package com.xbrgb.dyecraft.util;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.xbrgb.dyecraft.DyecraftMod;

public record DyePayload(int red, int green, int blue) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DyePayload> TYPE = new CustomPacketPayload.Type<>(DyecraftMod.id("dye_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DyePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, DyePayload::red,
            ByteBufCodecs.VAR_INT, DyePayload::green,
            ByteBufCodecs.VAR_INT, DyePayload::blue,
            DyePayload::new
    );
    @Override public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}