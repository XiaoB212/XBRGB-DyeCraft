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
        this(syncId, playerInventory, new SimpleInventory(9), new ArrayPropertyDelegate(6));
    }

    public DyeingStationScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate delegate) {
        super(ModScreenHandlers.DYEING_STATION_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.propertyDelegate = delegate;
        addProperties(delegate);
        inventory.onOpen(playerInventory.player);

        // 输入行 (0-2) y=23
        for (int col = 0; col < 3; col++)
            this.addSlot(new Slot(inventory, col, 30 + col * 18, 23) {
                @Override public boolean canInsert(ItemStack stack) { return isDyeable(stack); }
            });

        // 输出行 (3-5) y=59
        for (int col = 0; col < 3; col++)
            this.addSlot(new Slot(inventory, 3 + col, 30 + col * 18, 59) {
                @Override public boolean canInsert(ItemStack stack) { return false; }
            });

        // 染料槽 (6-8)
        this.addSlot(new Slot(inventory, 6, 30, 88) { @Override public boolean canInsert(ItemStack stack) { return stack.getItem() == Items.RED_DYE; } });
        this.addSlot(new Slot(inventory, 7, 50, 88) { @Override public boolean canInsert(ItemStack stack) { return stack.getItem() == Items.GREEN_DYE; } });
        this.addSlot(new Slot(inventory, 8, 70, 88) { @Override public boolean canInsert(ItemStack stack) { return stack.getItem() == Items.BLUE_DYE; } });

        // 玩家背包
        int invX = 28, invY = 120;
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, invX + col * 18, invY + row * 18));
        for (int col = 0; col < 9; col++)
            this.addSlot(new Slot(playerInventory, col, invX + col * 18, 178));
    }

    private boolean isDyeable(ItemStack stack) {
        return stack.getItem() == ModBlocks.DYEABLE_BLOCK.asItem() ||
                stack.getItem() == ModBlocks.DYEABLE_GLASS.asItem() ||
                stack.getItem() == ModBlocks.DYEABLE_CONCRETE.asItem() ||
                stack.getItem() == ModBlocks.DYEABLE_TERRACOTTA.asItem() ||
                stack.getItem() == ModBlocks.DYEABLE_WOOL.asItem();
    }

    public int getRed() { return propertyDelegate.get(0); }
    public int getGreen() { return propertyDelegate.get(1); }
    public int getBlue() { return propertyDelegate.get(2); }
    public int getPresetRed() { return propertyDelegate.get(3); }
    public int getPresetGreen() { return propertyDelegate.get(4); }
    public int getPresetBlue() { return propertyDelegate.get(5); }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slotObj = this.slots.get(slot);
        if (slotObj != null && slotObj.hasStack()) {
            ItemStack original = slotObj.getStack();
            newStack = original.copy();
            if (slot < 6) {
                if (!this.insertItem(original, 6, this.slots.size(), true)) return ItemStack.EMPTY;
            } else if (slot < 9) {
                if (!this.insertItem(original, 0, 6, true) && !this.insertItem(original, 6, this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else {
                if (isDyeable(original)) {
                    if (!this.insertItem(original, 0, 3, false)) return ItemStack.EMPTY;
                } else if (original.getItem() == Items.RED_DYE || original.getItem() == Items.GREEN_DYE || original.getItem() == Items.BLUE_DYE) {
                    if (!this.insertItem(original, 6, 9, false)) return ItemStack.EMPTY;
                } else {
                    if (slot >= 36) {
                        if (!this.insertItem(original, 9, 36, false)) return ItemStack.EMPTY;
                    } else {
                        if (!this.insertItem(original, 36, this.slots.size(), false)) return ItemStack.EMPTY;
                    }
                }
            }
            if (original.isEmpty()) slotObj.setStack(ItemStack.EMPTY);
            else slotObj.markDirty();
        }
        return newStack;
    }

    @Override public boolean canUse(PlayerEntity player) { return inventory.canPlayerUse(player); }
    public Inventory getInventory() { return inventory; }
}