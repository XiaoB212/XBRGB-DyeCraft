package com.xbrgb.dyecraft.blockentity;

import com.xbrgb.dyecraft.block.ModBlocks;
import com.xbrgb.dyecraft.item.DyeableBlockItem;
import com.xbrgb.dyecraft.screen.DyeingStationScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
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

public class DyeingStationBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory, SidedInventory {

    public enum UndoResult {
        SUCCESS,
        NOTHING_TO_UNDO,
        INVENTORY_CHANGED
    }

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(12, ItemStack.EMPTY);
    private final LinkedList<UndoSnapshot> undoStack = new LinkedList<>();
    private static final int MAX_UNDO = 5;
    private int red = 255, green = 255, blue = 255;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override public int get(int index) {
            return switch (index) { case 0 -> red; case 1 -> green; case 2 -> blue; default -> 0; };
        }
        @Override public void set(int index, int value) {
            switch (index) { case 0 -> red = value; case 1 -> green = value; case 2 -> blue = value; }
        }
        @Override public int size() { return 3; }
    };

    public DyeingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DYEING_STATION_BLOCK_ENTITY, pos, state);
    }

    /**
     * 完整快照：记录所有槽位的精确副本及消耗的染料组数
     */
    private static class UndoSnapshot {
        final DefaultedList<ItemStack> inventory;
        final int groupsNeeded;

        UndoSnapshot(DefaultedList<ItemStack> inv, int groupsNeeded) {
            this.inventory = DefaultedList.ofSize(inv.size(), ItemStack.EMPTY);
            for (int i = 0; i < inv.size(); i++) {
                this.inventory.set(i, inv.get(i).copy());
            }
            this.groupsNeeded = groupsNeeded;
        }
    }

    private void takeSnapshot(int groupsNeeded) {
        UndoSnapshot snap = new UndoSnapshot(inventory, groupsNeeded);
        undoStack.addLast(snap);
        if (undoStack.size() > MAX_UNDO) {
            undoStack.removeFirst();
        }
    }

    public UndoResult undoLastDye() {
        if (undoStack.isEmpty()) {
            return UndoResult.NOTHING_TO_UNDO;
        }
        UndoSnapshot snap = undoStack.getLast();

        // 检查 0-8 槽：类型和数量必须与快照完全相同（颜色可不同）
        for (int i = 0; i < 9; i++) {
            if (!ItemStack.areItemsEqual(inventory.get(i), snap.inventory.get(i)) ||
                    inventory.get(i).getCount() != snap.inventory.get(i).getCount()) {
                return UndoResult.INVENTORY_CHANGED;
            }
        }
        // 检查 9-11 染料槽：类型相同，且当前数量 == 快照数量 - 消耗组数
        for (int i = 9; i < 12; i++) {
            if (!ItemStack.areItemsEqual(inventory.get(i), snap.inventory.get(i)) ||
                    inventory.get(i).getCount() != snap.inventory.get(i).getCount() - snap.groupsNeeded) {
                return UndoResult.INVENTORY_CHANGED;
            }
        }

        // 完全符合预期，执行恢复
        undoStack.removeLast();
        for (int i = 0; i < inventory.size(); i++) {
            inventory.set(i, snap.inventory.get(i).copy());
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

    public void applyDye(int r, int g, int b) {
        this.red = r; this.green = g; this.blue = b;
        int color = 0xFF000000 | (r << 16) | (g << 8) | b;

        ItemStack redStack = inventory.get(9);
        ItemStack greenStack = inventory.get(10);
        ItemStack blueStack = inventory.get(11);

        int totalBlocks = 0;
        for (int i = 0; i < 9; i++) {
            if (!inventory.get(i).isEmpty()) totalBlocks += inventory.get(i).getCount();
        }
        if (totalBlocks == 0) return;

        int groupsNeeded = (totalBlocks + 15) / 16;
        if (redStack.getCount() < groupsNeeded || greenStack.getCount() < groupsNeeded || blueStack.getCount() < groupsNeeded) {
            return;
        }

        // 染色前保存快照
        takeSnapshot(groupsNeeded);

        boolean changed = false;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty() && isDyeable(stack)) {
                ItemStack result = stack.copy();
                DyeableBlockItem.setColorData(result, color);
                inventory.set(i, result);
                changed = true;
            }
        }
        if (changed) {
            redStack.decrement(groupsNeeded);
            greenStack.decrement(groupsNeeded);
            blueStack.decrement(groupsNeeded);
            markDirty();
        }
    }

    private boolean isDyeable(ItemStack stack) {
        Item item = stack.getItem();
        return item == ModBlocks.DYEABLE_BLOCK.asItem() ||
                item == ModBlocks.DYEABLE_GLASS.asItem() ||
                item == ModBlocks.DYEABLE_CONCRETE.asItem() ||
                item == ModBlocks.DYEABLE_TERRACOTTA.asItem() ||
                item == ModBlocks.DYEABLE_WOOL.asItem();
    }

    public int getComparatorOutput() {
        int filled = 0;
        for (int i = 0; i < 9; i++) if (!inventory.get(i).isEmpty()) filled++;
        return filled;
    }

    @Override public DefaultedList<ItemStack> getItems() { return inventory; }
    @Override public Text getDisplayName() { return Text.translatable("block.dyecraft.dyeing_station"); }
    @Nullable @Override public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new DyeingStationScreenHandler(syncId, inv, this, propertyDelegate);
    }

    @Override protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("Red", red); nbt.putInt("Green", green); nbt.putInt("Blue", blue);
    }
    @Override protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        red = nbt.getInt("Red"); green = nbt.getInt("Green"); blue = nbt.getInt("Blue");
    }

    @Override public int[] getAvailableSlots(Direction side) {
        return side == Direction.DOWN ? new int[]{0,1,2,3,4,5,6,7,8} : new int[]{9,10,11};
    }
    @Override public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir == Direction.DOWN) return false;
        return switch (slot) { case 9 -> stack.getItem() == Items.RED_DYE; case 10 -> stack.getItem() == Items.GREEN_DYE; case 11 -> stack.getItem() == Items.BLUE_DYE; default -> false; };
    }
    @Override public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot < 9;
    }
}