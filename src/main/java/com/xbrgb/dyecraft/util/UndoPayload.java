/*
 * Copyright [2026] [XiaoB212 of copyright owner]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.xbrgb.dyecraft.util;

import com.xbrgb.dyecraft.DyecraftMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UndoPayload() implements CustomPayload {
    public static final Id<UndoPayload> ID = new Id<>(Identifier.of(DyecraftMod.MOD_ID, "undo_dye"));
    public static final PacketCodec<PacketByteBuf, UndoPayload> CODEC = PacketCodec.of(
            (value, buf) -> {},
            buf -> new UndoPayload()
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}