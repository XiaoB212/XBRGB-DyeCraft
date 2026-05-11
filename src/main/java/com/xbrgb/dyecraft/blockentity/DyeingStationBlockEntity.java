package com.xbrgb.dyecraft.blockentity;

import com.xbrgb.dyecraft.block.ModBlocks;
import com.xbrgb.dyecraft.item.DyeableBlockItem;
import com.xbrgb.dyecraft.screen.DyeingStationScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public class DyeingStationBlockEntity extends BlockEntity
        implements NamedScreenHandlerFactory, ImplementedInventory, SidedInventory {

    public static final int INVENTORY_SIZE = 9;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private final LinkedList<UndoSnapshot> undoStack = new LinkedList<>();
    private static final int MAX_UNDO = 1;

    private int red = 255, green = 255, blue = 255;
    private int presetRed = 255, presetGreen = 255, presetBlue = 255;   // 恢复为三个独立预设颜色

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> red;
                case 1 -> green;
                case 2 -> blue;
                case 3 -> presetRed;
                case 4 -> presetGreen;
                case 5 -> presetBlue;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            switch (index) {
                case 0 -> red = value;
                case 1 -> green = value;
                case 2 -> blue = value;
                case 3 -> presetRed = value;
                case 4 -> presetGreen = value;
                case 5 -> presetBlue = value;
            }
        }
        @Override public int size() { return 6; }
    };

    public DyeingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DYEING_STATION_BLOCK_ENTITY, pos, state);
    }

    // ---------- 快照 ----------
    private static class UndoSnapshot {
        final DefaultedList<ItemStack> inventoryBefore;
        final int groupsNeeded;
        final ItemStack[] outputAfter;

        UndoSnapshot(DefaultedList<ItemStack> before, int groups, ItemStack[] output) {
            this.inventoryBefore = DefaultedList.ofSize(before.size(), ItemStack.EMPTY);
            for (int i = 0; i < before.size(); i++) {
                this.inventoryBefore.set(i, before.get(i).copy());
            }
            this.groupsNeeded = groups;
            this.outputAfter = new ItemStack[3];
            for (int i = 0; i < 3; i++) {
                this.outputAfter[i] = output[i].copy();
            }
        }
    }

    public enum UndoResult { SUCCESS, NOTHING_TO_UNDO, INVENTORY_CHANGED }

    public UndoResult undoLastDye() {
        if (undoStack.isEmpty()) return UndoResult.NOTHING_TO_UNDO;
        UndoSnapshot snap = undoStack.getLast();

        // 1. 检查染料槽 (6-8) 消耗是否正确
        for (int i = 6; i < 9; i++) {
            int expectedCount = snap.inventoryBefore.get(i).getCount() - snap.groupsNeeded;
            ItemStack current = inventory.get(i);
            if (expectedCount == 0) {
                if (!current.isEmpty()) return UndoResult.INVENTORY_CHANGED;
            } else {
                if (current.getCount() != expectedCount ||
                    !ItemStack.areItemsEqual(current, snap.inventoryBefore.get(i))) {
                    return UndoResult.INVENTORY_CHANGED;
                }
            }
        }

        // 2. 输入槽 (0-2) 必须为空
        for (int i = 0; i < 3; i++) {
            if (!inventory.get(i).isEmpty()) return UndoResult.INVENTORY_CHANGED;
        }

        // 3. 输出槽 (3-5) 必须与染色后产物完全一致
        for (int i = 0; i < 3; i++) {
            ItemStack currentOut = inventory.get(3 + i);
            ItemStack expectedOut = snap.outputAfter[i];
            if (!ItemStack.areEqual(currentOut, expectedOut)) {
                return UndoResult.INVENTORY_CHANGED;
            }
        }

        // 所有检查通过，恢复整个库存到染色前
        undoStack.removeLast();
        for (int i = 0; i < inventory.size(); i++) {
            inventory.set(i, snap.inventoryBefore.get(i).copy());
        }
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
        return UndoResult.SUCCESS;
    }

    public void clearSnapshotStack() {
        undoStack.clear();
    }

    // ---------- 染色 ----------
    public void applyDye(int r, int g, int b) {
        this.red = r; this.green = g; this.blue = b;
        int color = 0xFF000000 | (r << 16) | (g << 8) | b;

        ItemStack redStack = inventory.get(6);
        ItemStack greenStack = inventory.get(7);
        ItemStack blueStack = inventory.get(8);

        int totalBlocks = 0;
        for (int i = 0; i < 3; i++) {
            if (!inventory.get(i).isEmpty()) totalBlocks += inventory.get(i).getCount();
        }
        if (totalBlocks == 0) return;

        int groupsNeeded = (totalBlocks + 15) / 16;
        if (redStack.getCount() < groupsNeeded ||
            greenStack.getCount() < groupsNeeded ||
            blueStack.getCount() < groupsNeeded) {
            return;
        }

        // 1. 保存染色前完整快照
        DefaultedList<ItemStack> before = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            before.set(i, inventory.get(i).copy());
        }

        // 2. 执行染色
        for (int i = 0; i < 3; i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty() && isDyeable(stack)) {
                ItemStack copy = stack.copy();
                DyeableBlockItem.setColorData(copy, color);
                int remaining = copy.getCount();
                for (int out = 3; out < 6; out++) {
                    ItemStack outStack = inventory.get(out);
                    if (outStack.isEmpty()) {
                        inventory.set(out, copy.split(remaining));
                        remaining = 0;
                        break;
                    } else if (ItemStack.areItemsEqual(outStack, copy) &&
                               outStack.getCount() < outStack.getMaxCount()) {
                        int canMerge = Math.min(remaining, outStack.getMaxCount() - outStack.getCount());
                        if (canMerge > 0) {
                            outStack.increment(canMerge);
                            remaining -= canMerge;
                        }
                    }
                    if (remaining <= 0) break;
                }
                stack.setCount(0);
            }
        }
        redStack.decrement(groupsNeeded);
        greenStack.decrement(groupsNeeded);
        blueStack.decrement(groupsNeeded);

        // 3. 记录染色后输出槽产物
        ItemStack[] outputAfter = new ItemStack[3];
        for (int i = 0; i < 3; i++) {
            outputAfter[i] = inventory.get(3 + i).copy();
        }

        // 4. 清除旧快照，保存新快照
        undoStack.clear();
        undoStack.addLast(new UndoSnapshot(before, groupsNeeded, outputAfter));
        markDirty();
    }

    public void redstoneDye() {
        applyDye(presetRed, presetGreen, presetBlue);
    }

    private boolean isDyeable(ItemStack stack) {
        Item item = stack.getItem();
        return item == ModBlocks.DYEABLE_BLOCK.asItem() ||
               item == ModBlocks.DYEABLE_GLASS.asItem() ||
               item == ModBlocks.DYEABLE_CONCRETE.asItem() ||
               item == ModBlocks.DYEABLE_TERRACOTTA.asItem() ||
               item == ModBlocks.DYEABLE_WOOL.asItem();
    }

    // 设置预设颜色
    public void setPresetColor(int r, int g, int b) {
        this.presetRed = r;
        this.presetGreen = g;
        this.presetBlue = b;
        markDirty();
    }

    public int getComparatorOutput() {
        int filled = 0;
        for (int i = 0; i < 3; i++) if (!inventory.get(i).isEmpty()) filled++;
        return filled;
    }

    // ---------- 接口实现 ----------
    @Override public DefaultedList<ItemStack> getItems() { return inventory; }
    @Override public Text getDisplayName() { return Text.translatable("block.dyecraft.dyeing_station"); }

    @Nullable @Override public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new DyeingStationScreenHandler(syncId, inv, this, propertyDelegate);
    }

    @Override protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup reg) {
        super.writeNbt(nbt, reg);
        Inventories.writeNbt(nbt, inventory, reg);
        nbt.putInt("Red", red); nbt.putInt("Green", green); nbt.putInt("Blue", blue);
        nbt.putInt("PresetRed", presetRed); nbt.putInt("PresetGreen", presetGreen); nbt.putInt("PresetBlue", presetBlue);
    }

    @Override protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup reg) {
        super.readNbt(nbt, reg);
        Inventories.readNbt(nbt, inventory, reg);
        red = nbt.getInt("Red"); green = nbt.getInt("Green"); blue = nbt.getInt("Blue");
        presetRed = nbt.getInt("PresetRed"); presetGreen = nbt.getInt("PresetGreen"); presetBlue = nbt.getInt("PresetBlue");
    }

    // ---------- 漏斗自动化 ----------
    @Override public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) return new int[]{0, 1, 2};
        if (side == Direction.DOWN) return new int[]{3, 4, 5};
        return new int[]{6, 7, 8};
    }

    @Override public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir == Direction.DOWN) return false;
        if (dir == Direction.UP && slot >= 0 && slot <= 2) return isDyeable(stack);
        if (dir != null && dir.getAxis().isHorizontal() && slot >= 6 && slot <= 8) {
            return switch (slot) {
                case 6 -> stack.getItem() == Items.RED_DYE;
                case 7 -> stack.getItem() == Items.GREEN_DYE;
                case 8 -> stack.getItem() == Items.BLUE_DYE;
                default -> false;
            };
        }
        return false;
    }

    @Override public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot >= 3 && slot <= 5;
    }
}