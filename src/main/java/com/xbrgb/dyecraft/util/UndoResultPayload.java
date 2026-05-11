package com.xbrgb.dyecraft.util;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.xbrgb.dyecraft.DyecraftMod;

public record UndoResultPayload(boolean success, String messageKey) implements CustomPacketPayload {
    public static final Type<UndoResultPayload> TYPE = new Type<>(DyecraftMod.id("undo_result"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UndoResultPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, UndoResultPayload::success,
            ByteBufCodecs.STRING_UTF8, UndoResultPayload::messageKey,
            UndoResultPayload::new
    );
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}