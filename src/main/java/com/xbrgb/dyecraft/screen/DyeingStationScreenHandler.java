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

package com.xbrgb.dyecraft.screen;

import com.xbrgb.dyecraft.block.ModBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class DyeingStationScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public DyeingStationScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(12), new ArrayPropertyDelegate(3));
    }

    public DyeingStationScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate delegate) {
        super(ModScreenHandlers.DYEING_STATION_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.propertyDelegate = delegate;
        addProperties(delegate);
        inventory.onOpen(playerInventory.player);

        // 九个可染色方块槽位（Y 从 23 开始）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(inventory, col + row * 3, 30 + col * 18, 23 + row * 18) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return stack.getItem() == ModBlocks.DYEABLE_BLOCK.asItem() ||
                                stack.getItem() == ModBlocks.DYEABLE_GLASS.asItem() ||
                                stack.getItem() == ModBlocks.DYEABLE_CONCRETE.asItem() ||
                                stack.getItem() == ModBlocks.DYEABLE_TERRACOTTA.asItem() ||
                                stack.getItem() == ModBlocks.DYEABLE_WOOL.asItem();
                    }
                });
            }
        }

        // 染料槽（Y 调整到 88）
        this.addSlot(new Slot(inventory, 9, 30, 88) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.RED_DYE;
            }
        });
        this.addSlot(new Slot(inventory, 10, 50, 88) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.GREEN_DYE;
            }
        });
        this.addSlot(new Slot(inventory, 11, 70, 88) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.BLUE_DYE;
            }
        });

        // 玩家背包：右移20，主背包从 y=120 开始，快捷栏从 y=178 开始（间距4）
        int invStartX = 28;
        int invStartY = 120;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, invStartX + col * 18, invStartY + row * 18));
            }
        }
        int hotbarY = 178;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, invStartX + col * 18, hotbarY));
        }
    }

    public int getRed() {
        return propertyDelegate.get(0);
    }
    public int getGreen() {
        return propertyDelegate.get(1);
    }
    public int getBlue() {
        return propertyDelegate.get(2);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slotObj = this.slots.get(slot);
        if (slotObj != null && slotObj.hasStack()) {
            ItemStack originalStack = slotObj.getStack();
            newStack = originalStack.copy();
            if (slot < 12) {
                if (!this.insertItem(originalStack, 12, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, 12, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slotObj.setStack(ItemStack.EMPTY);
            } else {
                slotObj.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public Inventory getInventory() {
        return inventory;
    }
}