package com.xbrgb.dyecraft.util;

import com.xbrgb.dyecraft.DyecraftMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SetPresetPayload(int red, int green, int blue) implements CustomPayload {
    public static final Id<SetPresetPayload> ID = new Id<>(Identifier.of(DyecraftMod.MOD_ID, "set_preset"));
    public static final PacketCodec<PacketByteBuf, SetPresetPayload> CODEC = PacketCodec.of(
            (value, buf) -> { buf.writeInt(value.red); buf.writeInt(value.green); buf.writeInt(value.blue); },
            buf -> new SetPresetPayload(buf.readInt(), buf.readInt(), buf.readInt())
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}