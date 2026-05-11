package com.xbrgb.dyecraft.util;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.xbrgb.dyecraft.DyecraftMod;

public record UndoPayload() implements CustomPacketPayload {
    public static final Type<UndoPayload> TYPE = new Type<>(DyecraftMod.id("undo_dye"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UndoPayload> STREAM_CODEC = StreamCodec.unit(new UndoPayload());
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}