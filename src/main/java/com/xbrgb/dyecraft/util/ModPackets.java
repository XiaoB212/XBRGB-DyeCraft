package com.xbrgb.dyecraft.util;

import com.xbrgb.dyecraft.blockentity.DyeingStationBlockEntity;
import com.xbrgb.dyecraft.screen.DyeingStationMenu;
import com.xbrgb.dyecraft.screen.DyeingStationScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPackets {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModPackets::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // 染色请求
        registrar.playToServer(DyePayload.TYPE, DyePayload.STREAM_CODEC, (payload, context) -> {
            var player = context.player();
            if (player.containerMenu instanceof DyeingStationMenu menu) {
                DyeingStationBlockEntity station = menu.getBlockEntity();
                if (station != null) {
                    station.applyDye(payload.red(), payload.green(), payload.blue());
                }
            }
        });

        // 撤销请求
        registrar.playToServer(UndoPayload.TYPE, UndoPayload.STREAM_CODEC, (payload, context) -> {
            var player = context.player();
            if (player.containerMenu instanceof DyeingStationMenu menu) {
                DyeingStationBlockEntity station = menu.getBlockEntity();
                if (station != null) {
                    DyeingStationBlockEntity.UndoResult result = station.undoLastDye();
                    boolean success = result == DyeingStationBlockEntity.UndoResult.SUCCESS;
                    String key = switch (result) {
                        case SUCCESS -> "gui.dyecraft.dyeing_station.undo.success";
                        case NOTHING_TO_UNDO -> "gui.dyecraft.dyeing_station.undo.too_many";
                        case INVENTORY_MODIFIED -> "gui.dyecraft.dyeing_station.undo.changed";
                    };
                    context.reply(new UndoResultPayload(success, key));
                }
            }
        });

        // 预设颜色请求
        registrar.playToServer(SetDefaultColorPayload.TYPE, SetDefaultColorPayload.STREAM_CODEC, (payload, context) -> {
            var player = context.player();
            if (player.containerMenu instanceof DyeingStationMenu menu) {
                DyeingStationBlockEntity station = menu.getBlockEntity();
                if (station != null) {
                    station.setDefaultColor(payload.color());
                }
            }
        });

        // 清除快照请求（关闭界面时发送）
        registrar.playToServer(ClearSnapshotPayload.TYPE, ClearSnapshotPayload.STREAM_CODEC, (payload, context) -> {
            var player = context.player();
            if (player.containerMenu instanceof DyeingStationMenu menu) {
                DyeingStationBlockEntity station = menu.getBlockEntity();
                if (station != null) {
                    station.clearSnapshotStack();
                }
            }
        });

        // 撤销结果通知（客户端接收）
        registrar.playToClient(UndoResultPayload.TYPE, UndoResultPayload.STREAM_CODEC, (payload, context) -> {
            Minecraft.getInstance().execute(() -> {
                if (Minecraft.getInstance().screen instanceof DyeingStationScreen screen) {
                    screen.setUndoMessage(
                            net.minecraft.network.chat.Component.translatable(payload.messageKey()),
                            payload.success() ? 0x55FF55 : 0xFF5555
                    );
                }
            });
        });
    }
}