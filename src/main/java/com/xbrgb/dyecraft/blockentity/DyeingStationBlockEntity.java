package com.xbrgb.dyecraft.blockentity;

import com.xbrgb.dyecraft.block.ModBlocks;
import com.xbrgb.dyecraft.item.DyeableBlockItem;
import com.xbrgb.dyecraft.screen.DyeingStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public class DyeingStationBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INVENTORY_SIZE = 9;
    private final SimpleContainer inventory = new SimpleContainer(INVENTORY_SIZE);
    private final LinkedList<UndoSnapshot> undoStack = new LinkedList<>();
    private static final int MAX_UNDO = 1;
    private int red = 255, green = 255, blue = 255;
    private int defaultColor = 0xFFFFFFFF;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> red;
                case 1 -> green;
                case 2 -> blue;
                case 3 -> defaultColor;
                default -> 0;
            };
        }
        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> red = value;
                case 1 -> green = value;
                case 2 -> blue = value;
                case 3 -> defaultColor = value;
            }
        }
        @Override
        public int getCount() { return 4; }
    };

    public DyeingStationBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DYEING_STATION.get(), pos, blockState);
        inventory.addListener(container -> setChanged());
    }

    // ---------- 快照类 ----------
    private static class UndoSnapshot {
        final ItemStack[] inventoryBefore;
        final int groupsNeeded;
        final ItemStack[] outputAfter;
        UndoSnapshot(ItemStack[] before, int groupsNeeded, ItemStack[] outputAfter) {
            this.inventoryBefore = new ItemStack[before.length];
            for (int i = 0; i < before.length; i++) this.inventoryBefore[i] = before[i].copy();
            this.groupsNeeded = groupsNeeded;
            this.outputAfter = new ItemStack[3];
            for (int i = 0; i < 3; i++) this.outputAfter[i] = outputAfter[i].copy();
        }
    }

    public enum UndoResult { SUCCESS, NOTHING_TO_UNDO, INVENTORY_MODIFIED }

    public UndoResult undoLastDye() {
        if (undoStack.isEmpty()) return UndoResult.NOTHING_TO_UNDO;
        UndoSnapshot snap = undoStack.getLast();
        // 染料检查
        for (int i = 6; i < 9; i++) {
            int expectedCount = snap.inventoryBefore[i].getCount() - snap.groupsNeeded;
            ItemStack current = inventory.getItem(i);
            if (expectedCount == 0) { if (!current.isEmpty()) return UndoResult.INVENTORY_MODIFIED; }
            else { if (current.getCount() != expectedCount || !ItemStack.isSameItem(current, snap.inventoryBefore[i])) return UndoResult.INVENTORY_MODIFIED; }
        }
        // 输入槽必须空
        for (int i = 0; i < 3; i++) if (!inventory.getItem(i).isEmpty()) return UndoResult.INVENTORY_MODIFIED;
        // 输出槽完整性
        for (int i = 0; i < 3; i++) {
            ItemStack currentOut = inventory.getItem(3 + i);
            ItemStack expectedOut = snap.outputAfter[i];
            if (!ItemStack.isSameItemSameComponents(currentOut, expectedOut) || currentOut.getCount() != expectedOut.getCount())
                return UndoResult.INVENTORY_MODIFIED;
        }
        undoStack.removeLast();
        for (int i = 0; i < inventory.getContainerSize(); i++) inventory.setItem(i, snap.inventoryBefore[i].copy());
        setChanged();
        return UndoResult.SUCCESS;
    }

    public void clearSnapshotStack() { undoStack.clear(); }

    // ---------- 染色 ----------
    public void applyDye(int r, int g, int b) {
        this.red = r; this.green = g; this.blue = b;
        int color = 0xFF000000 | (r << 16) | (g << 8) | b;
        ItemStack redStack = inventory.getItem(6), greenStack = inventory.getItem(7), blueStack = inventory.getItem(8);
        int totalBlocks = 0;
        for (int i = 0; i < 3; i++) if (!inventory.getItem(i).isEmpty()) totalBlocks += inventory.getItem(i).getCount();
        if (totalBlocks == 0) return;
        int groupsNeeded = (totalBlocks + 15) / 16;
        if (redStack.getCount() < groupsNeeded || greenStack.getCount() < groupsNeeded || blueStack.getCount() < groupsNeeded) return;

        ItemStack[] before = new ItemStack[INVENTORY_SIZE];
        for (int i = 0; i < INVENTORY_SIZE; i++) before[i] = inventory.getItem(i).copy();

        for (int i = 0; i < 3; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && isDyeable(stack)) {
                ItemStack copy = stack.copy();
                DyeableBlockItem.setColorData(copy, color);
                int remaining = copy.getCount();
                for (int out = 3; out < 6; out++) {
                    ItemStack outStack = inventory.getItem(out);
                    if (outStack.isEmpty()) { inventory.setItem(out, copy.split(remaining)); remaining = 0; break; }
                    else if (ItemStack.isSameItem(outStack, copy) && outStack.getCount() < outStack.getMaxStackSize()) {
                        int canMerge = Math.min(remaining, outStack.getMaxStackSize() - outStack.getCount());
                        if (canMerge > 0) { outStack.grow(canMerge); remaining -= canMerge; }
                    }
                    if (remaining <= 0) break;
                }
                stack.setCount(0);
            }
        }
        redStack.shrink(groupsNeeded); greenStack.shrink(groupsNeeded); blueStack.shrink(groupsNeeded);

        ItemStack[] outputAfter = new ItemStack[3];
        for (int i = 0; i < 3; i++) outputAfter[i] = inventory.getItem(3 + i).copy();

        undoStack.clear();
        undoStack.addLast(new UndoSnapshot(before, groupsNeeded, outputAfter));
        setChanged();
    }

    public void autoDye() {
        int r = (defaultColor >> 16) & 0xFF, g = (defaultColor >> 8) & 0xFF, b = defaultColor & 0xFF;
        applyDye(r, g, b);
    }

    private boolean isDyeable(ItemStack stack) {
        Item item = stack.getItem();
        return item == ModBlocks.DYEABLE_BLOCK.get().asItem() || item == ModBlocks.DYEABLE_GLASS.get().asItem() ||
               item == ModBlocks.DYEABLE_CONCRETE.get().asItem() || item == ModBlocks.DYEABLE_TERRACOTTA.get().asItem() ||
               item == ModBlocks.DYEABLE_WOOL.get().asItem();
    }

    public void triggerRedstone() {
        int total = 0;
        for (int i = 0; i < 3; i++) total += inventory.getItem(i).getCount();
        if (total == 0) return;
        int groupsNeeded = (total + 15) / 16;
        if (inventory.getItem(6).getCount() >= groupsNeeded && inventory.getItem(7).getCount() >= groupsNeeded && inventory.getItem(8).getCount() >= groupsNeeded)
            autoDye();
    }

    public int getComparatorOutput() { int filled = 0; for (int i = 0; i < 3; i++) if (!inventory.getItem(i).isEmpty()) filled++; return filled; }
    public SimpleContainer getInventory() { return inventory; }

    @Nullable @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DyeingStationMenu(containerId, playerInventory, this, dataAccess);
    }

    @Override public Component getDisplayName() { return Component.translatable("block.dyecraft.dyeing_station"); }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, inventory.getItems(), registries);
        red = tag.getInt("Red"); green = tag.getInt("Green"); blue = tag.getInt("Blue");
        defaultColor = tag.contains("DefaultColor") ? tag.getInt("DefaultColor") : 0xFFFFFFFF;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, inventory.getItems(), registries);
        tag.putInt("Red", red); tag.putInt("Green", green); tag.putInt("Blue", blue);
        tag.putInt("DefaultColor", defaultColor);
    }

    // ---------- 暴露 IItemHandler 给漏斗 ----------
    public static @Nullable IItemHandler getItemHandler(DyeingStationBlockEntity be, Direction side) {
        if (side == null) return null;
        return new DirectionalItemHandler(be.inventory, side, be);
    }

    private static class DirectionalItemHandler implements IItemHandler {
        private final SimpleContainer inventory;
        private final Direction side;
        private final DyeingStationBlockEntity be;

        DirectionalItemHandler(SimpleContainer inventory, Direction side, DyeingStationBlockEntity be) {
            this.inventory = inventory;
            this.side = side;
            this.be = be;
        }

        @Override
        public int getSlots() { return 9; }

        @Override
        public ItemStack getStackInSlot(int slot) { return inventory.getItem(slot); }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) return stack;
            if (side == Direction.UP) {
                if (slot < 0 || slot > 2) return stack;
                if (!be.isDyeable(stack)) return stack;
            } else if (side.getAxis().isHorizontal()) {
                if (slot == 6 && stack.getItem() != Items.RED_DYE) return stack;
                if (slot == 7 && stack.getItem() != Items.GREEN_DYE) return stack;
                if (slot == 8 && stack.getItem() != Items.BLUE_DYE) return stack;
                if (slot < 6 || slot > 8) return stack;
            } else {
                return stack; // 底部不允许输入
            }

            ItemStack existing = inventory.getItem(slot);
            int limit = stack.getMaxStackSize();
            if (!existing.isEmpty()) {
                if (!ItemStack.isSameItemSameComponents(stack, existing)) return stack;
                limit = existing.getMaxStackSize() - existing.getCount();
            }
            if (limit <= 0) return stack;
            int toInsert = Math.min(limit, stack.getCount());
            if (!simulate) {
                if (existing.isEmpty()) {
                    ItemStack placed = stack.copy();
                    placed.setCount(toInsert);
                    inventory.setItem(slot, placed);
                } else {
                    existing.grow(toInsert);
                }
                be.setChanged();
            }
            ItemStack result = stack.copy();
            result.shrink(toInsert);
            return result.isEmpty() ? ItemStack.EMPTY : result;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (side != Direction.DOWN) return ItemStack.EMPTY;
            if (slot < 3 || slot > 5) return ItemStack.EMPTY;
            ItemStack existing = inventory.getItem(slot);
            if (existing.isEmpty()) return ItemStack.EMPTY;
            int toExtract = Math.min(amount, existing.getCount());
            if (!simulate) {
                be.setChanged();
                return existing.split(toExtract);
            }
            ItemStack result = existing.copy();
            result.setCount(toExtract);
            return result;
        }

        @Override
        public int getSlotLimit(int slot) { return 64; }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (side == Direction.UP) return slot >= 0 && slot <= 2 && be.isDyeable(stack);
            if (side.getAxis().isHorizontal()) {
                if (slot == 6) return stack.getItem() == Items.RED_DYE;
                if (slot == 7) return stack.getItem() == Items.GREEN_DYE;
                if (slot == 8) return stack.getItem() == Items.BLUE_DYE;
            }
            return false;
        }
    }

    public void setDefaultColor(int color) {
        this.defaultColor = color;
        setChanged();
    }

    public int getDefaultColor() { return defaultColor; }
}