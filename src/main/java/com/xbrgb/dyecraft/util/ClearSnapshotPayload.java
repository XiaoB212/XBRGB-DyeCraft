package com.xbrgb.dyecraft.util;

import com.xbrgb.dyecraft.DyecraftMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClearSnapshotPayload() implements CustomPacketPayload {
    public static final Type<ClearSnapshotPayload> TYPE = new Type<>(DyecraftMod.id("clear_snapshot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClearSnapshotPayload> STREAM_CODEC =
            StreamCodec.unit(new ClearSnapshotPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}