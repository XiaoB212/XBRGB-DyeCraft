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

/**
 * 染色数据包，从客户端发送到服务端，包含 RGB 颜色值
 */
public record DyePayload(int red, int green, int blue) implements CustomPayload {

    // 数据包唯一标识符
    public static final Id<DyePayload> ID = new Id<>(Identifier.of(DyecraftMod.MOD_ID, "dye_packet"));

    // 编解码器：将对象写入 PacketByteBuf 以及从 PacketByteBuf 读取
    public static final PacketCodec<PacketByteBuf, DyePayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeInt(value.red);
                buf.writeInt(value.green);
                buf.writeInt(value.blue);
            },
            buf -> new DyePayload(buf.readInt(), buf.readInt(), buf.readInt())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}