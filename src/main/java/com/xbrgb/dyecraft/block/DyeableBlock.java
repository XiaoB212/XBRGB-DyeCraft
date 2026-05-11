package com.xbrgb.dyecraft.block;

import com.xbrgb.dyecraft.blockentity.DyeableBlockEntity;
import com.xbrgb.dyecraft.item.DyeableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DyeableBlock extends Block implements EntityBlock {
    public DyeableBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DyeableBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DyeableBlockEntity dyeable) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                CompoundTag tag = customData.copyTag();
                if (tag.contains("Color")) {
                    dyeable.setColor(tag.getInt("Color"));
                    return;
                }
            }
            dyeable.setColor(0xFFFFFFFF);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && !player.isCreative()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DyeableBlockEntity dyeable) {
                ItemStack stack = new ItemStack(this);
                DyeableBlockItem.setColorData(stack, dyeable.getColor());
                popResource(level, pos, stack);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}