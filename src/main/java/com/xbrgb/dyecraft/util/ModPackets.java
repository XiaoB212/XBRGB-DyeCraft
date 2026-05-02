package com.xbrgb.dyecraft.util;

import com.xbrgb.dyecraft.blockentity.DyeingStationBlockEntity;
import com.xbrgb.dyecraft.screen.DyeingStationScreen;
import com.xbrgb.dyecraft.screen.DyeingStationScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ModPackets {
    public static void register() {
        // 染色请求
        PayloadTypeRegistry.playC2S().register(DyePayload.ID, DyePayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(DyePayload.ID, (payload, context) -> {
            context.player().server.execute(() -> {
                if (context.player().currentScreenHandler instanceof DyeingStationScreenHandler handler) {
                    if (handler.getInventory() instanceof DyeingStationBlockEntity station) {
                        station.applyDye(payload.red(), payload.green(), payload.blue());
                    }
                }
            });
        });

        // 撤销请求
        PayloadTypeRegistry.playC2S().register(UndoPayload.ID, UndoPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(UndoPayload.ID, (payload, context) -> {
            context.player().server.execute(() -> {
                if (context.player().currentScreenHandler instanceof DyeingStationScreenHandler handler) {
                    if (handler.getInventory() instanceof DyeingStationBlockEntity station) {
                        DyeingStationBlockEntity.UndoResult result = station.undoLastDye();
                        boolean success;
                        String key;
                        switch (result) {
                            case SUCCESS -> { success = true; key = "gui.dyecraft.dyeing_station.undo.success"; }
                            case NOTHING_TO_UNDO -> { success = false; key = "gui.dyecraft.dyeing_station.undo.too_many"; }
                            case INVENTORY_CHANGED -> { success = false; key = "gui.dyecraft.dyeing_station.undo.changed"; }
                            default -> { success = false; key = "gui.dyecraft.dyeing_station.undo.too_many"; }
                        }
                        ServerPlayNetworking.send(context.player(), new UndoResultPayload(success, key));
                    }
                }
            });
        });

        // 清除快照
        PayloadTypeRegistry.playC2S().register(ClearSnapshotPayload.ID, ClearSnapshotPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ClearSnapshotPayload.ID, (payload, context) -> {
            context.player().server.execute(() -> {
                if (context.player().currentScreenHandler instanceof DyeingStationScreenHandler handler) {
                    if (handler.getInventory() instanceof DyeingStationBlockEntity station) {
                        station.clearSnapshotStack();
                    }
                }
            });
        });

        // 撤销结果接收（客户端）
        PayloadTypeRegistry.playS2C().register(UndoResultPayload.ID, UndoResultPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(UndoResultPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                if (client.currentScreen instanceof DyeingStationScreen screen) {
                    Text msg = Text.translatable(payload.messageKey());
                    int color = payload.success() ? 0x55FF55 : 0xFF5555;
                    screen.setUndoMessage(msg, color);
                }
            });
        });
    }
}