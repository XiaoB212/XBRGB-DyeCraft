package com.xbrgb.dyecraft.screen;

import com.xbrgb.dyecraft.block.ModBlocks;
import com.xbrgb.dyecraft.blockentity.DyeingStationBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DyeingStationMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;
    private final DyeingStationBlockEntity blockEntity;

    // 客户端构造器（用于注册菜单类型）
    public DyeingStationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(9), new SimpleContainerData(4), null);
    }

    // 服务端构造器
    public DyeingStationMenu(int containerId, Inventory playerInventory,
                             DyeingStationBlockEntity blockEntity, ContainerData data) {
        this(containerId, playerInventory, blockEntity.getInventory(), data, blockEntity);
    }

    // 内部统一构造器
    private DyeingStationMenu(int containerId, Inventory playerInventory, Container container,
                              ContainerData data, DyeingStationBlockEntity blockEntity) {
        super(ModMenuTypes.DYEING_STATION.get(), containerId);
        this.container = container;
        this.data = data;
        this.blockEntity = blockEntity;

        // 同步数据（R,G,B,默认颜色）
        addDataSlots(data);

        // ---------- 自定义槽位 ----------
        // 输入槽 0-2：可染色方块
        for (int col = 0; col < 3; col++) {
            this.addSlot(new Slot(container, col, 30 + col * 18, 23) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return isDyeable(stack);
                }
            });
        }

        // 输出槽 3-5：染色后物品（不可放入）
        for (int col = 0; col < 3; col++) {
            this.addSlot(new Slot(container, 3 + col, 30 + col * 18, 59) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        // 染料槽 6-8：红、绿、蓝染料
        this.addSlot(new Slot(container, 6, 30, 88) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.RED_DYE;
            }
        });
        this.addSlot(new Slot(container, 7, 50, 88) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.GREEN_DYE;
            }
        });
        this.addSlot(new Slot(container, 8, 70, 88) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.BLUE_DYE;
            }
        });

        // ---------- 玩家背包 ----------
        int invStartX = 28;
        int invStartY = 120;
        // 主库存 (3排 x 9列)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, invStartX + col * 18, invStartY + row * 18));
            }
        }
        // 快捷栏
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, invStartX + col * 18, 178));
        }
    }

    private boolean isDyeable(ItemStack stack) {
        return stack.getItem() == ModBlocks.DYEABLE_BLOCK.get().asItem() ||
                stack.getItem() == ModBlocks.DYEABLE_GLASS.get().asItem() ||
                stack.getItem() == ModBlocks.DYEABLE_CONCRETE.get().asItem() ||
                stack.getItem() == ModBlocks.DYEABLE_TERRACOTTA.get().asItem() ||
                stack.getItem() == ModBlocks.DYEABLE_WOOL.get().asItem();
    }

    // 给 Screen 读取颜色数据
    public int getRed()   { return data.get(0); }
    public int getGreen() { return data.get(1); }
    public int getBlue()  { return data.get(2); }
    public int getDefaultColor() { return data.get(3); }

    // 获取方块实体（网络包中用来执行染色）
    public DyeingStationBlockEntity getBlockEntity() {
        return blockEntity;
    }

    // Shift + 左键 快速移动
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();

            // 从自定义槽位移到玩家背包
            if (index < 9) {
                if (!this.moveItemStackTo(stackInSlot, 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // 从玩家背包移到自定义槽位
            else {
                if (isDyeable(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stackInSlot.getItem() == Items.RED_DYE) {
                    if (!this.moveItemStackTo(stackInSlot, 6, 7, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stackInSlot.getItem() == Items.GREEN_DYE) {
                    if (!this.moveItemStackTo(stackInSlot, 7, 8, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stackInSlot.getItem() == Items.BLUE_DYE) {
                    if (!this.moveItemStackTo(stackInSlot, 8, 9, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    // 玩家背包内部移动
                    if (index < this.slots.size() - 9) {
                        if (!this.moveItemStackTo(stackInSlot, this.slots.size() - 9, this.slots.size(), false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        if (!this.moveItemStackTo(stackInSlot, 9, this.slots.size() - 9, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}