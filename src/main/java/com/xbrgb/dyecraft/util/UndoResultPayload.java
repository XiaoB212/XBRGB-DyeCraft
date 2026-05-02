package com.xbrgb.dyecraft.util;

import com.xbrgb.dyecraft.DyecraftMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UndoResultPayload(boolean success, String messageKey) implements CustomPayload {
    public static final Id<UndoResultPayload> ID = new Id<>(Identifier.of(DyecraftMod.MOD_ID, "undo_result"));
    public static final PacketCodec<PacketByteBuf, UndoResultPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeBoolean(value.success);
                buf.writeString(value.messageKey);
            },
            buf -> new UndoResultPayload(buf.readBoolean(), buf.readString())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}