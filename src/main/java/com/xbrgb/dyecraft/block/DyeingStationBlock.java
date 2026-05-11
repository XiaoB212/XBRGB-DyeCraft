package com.xbrgb.dyecraft.block;

import com.xbrgb.dyecraft.blockentity.DyeingStationBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DyeingStationBlock extends Block implements BlockEntityProvider {
    public DyeingStationBlock(Settings settings) { super(settings); }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DyeingStationBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);
            if (factory != null) {
                player.openHandledScreen(factory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        return be instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory) be : null;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DyeingStationBlockEntity dyeStation) {
                ItemScatterer.spawn(world, pos, dyeStation);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean powered = world.isReceivingRedstonePower(pos);
            if (powered && world.getBlockEntity(pos) instanceof DyeingStationBlockEntity station) {
                station.redstoneDye();
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) { return true; }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof DyeingStationBlockEntity dyeStation ?
                dyeStation.getComparatorOutput() : 0;
    }
}