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

package com.xbrgb.dyecraft.block;

import com.xbrgb.dyecraft.blockentity.DyeableBlockEntity;
import com.xbrgb.dyecraft.item.DyeableBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DyeableBlock extends Block implements BlockEntityProvider {

    public DyeableBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DyeableBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof DyeableBlockEntity dyeable) {
            NbtCompound nbt = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
            if (nbt != null && nbt.contains("Color")) {
                dyeable.setColor(nbt.getInt("Color"));
            } else {
                dyeable.setColor(0xFFFFFFFF);
            }
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof DyeableBlockEntity dyeable && !world.isClient && !player.isCreative()) {
            // 使用 createDefaultStack 创建基础物品（已包含白色、移除 BLOCK_STATE）
            ItemStack stack = DyeableBlockItem.createDefaultStack(this);
            // 覆盖为方块当前的颜色
            DyeableBlockItem.setColorData(stack, dyeable.getColor());
            dropStack(world, pos, stack);
        }
        return super.onBreak(world, pos, state, player);
    }
}