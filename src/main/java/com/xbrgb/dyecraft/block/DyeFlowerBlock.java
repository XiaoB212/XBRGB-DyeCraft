package com.xbrgb.dyecraft.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.function.Supplier;

public class DyeFlowerBlock extends CropBlock {
    public static final IntProperty AGE = IntProperty.of("age", 0, 3);
    private static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0)
    };

    private final Supplier<ItemConvertible> seedItem;
    private final Supplier<ItemConvertible> plantItem;

    public DyeFlowerBlock(Settings settings, Supplier<ItemConvertible> seedItem, Supplier<ItemConvertible> plantItem) {
        super(settings);
        this.seedItem = seedItem;
        this.plantItem = plantItem;
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0));
    }

    private int getSeedDropCount(Random random) {
        float chance = random.nextFloat();
        if (chance < 0.4f) return 1;
        else if (chance < 0.8f) return 2;
        else return 3;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(AGE)];
    }

    @Override
    protected ItemConvertible getSeedsItem() { return seedItem.get(); }
    public ItemConvertible getPlantItem() { return plantItem.get(); }

    @Override
    public IntProperty getAgeProperty() { return AGE; }
    @Override
    public int getMaxAge() { return 3; }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(AGE); }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            int age = this.getAge(state);
            if (age < this.getMaxAge()) {
                float f = getAvailableMoisture(this, world, pos);
                if (random.nextInt((int)(25.0F / f) + 1) == 0) {
                    world.setBlockState(pos, this.withAge(age + 1), 2);
                }
            }
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isOf(Blocks.FARMLAND);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack held = player.getStackInHand(player.getActiveHand());
        int age = state.get(AGE);

        // 骨粉催熟（仅未成熟可用）
        if (held.isOf(Items.BONE_MEAL)) {
            if (world.isClient) return ActionResult.SUCCESS;
            if (age < getMaxAge()) {
                if (world instanceof ServerWorld serverWorld) {
                    grow(serverWorld, world.random, pos, state);
                }
                held.decrement(1);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }

        // 成熟后不再响应任何右键交互，只能破坏收割
        return ActionResult.PASS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && !player.isCreative()) {
            int age = state.get(AGE);
            if (age == getMaxAge()) {
                int seedCount = getSeedDropCount(world.random);
                dropStack(world, pos, new ItemStack(getSeedsItem(), seedCount));
                dropStack(world, pos, new ItemStack(getPlantItem(), 1));
            } else {
                dropStack(world, pos, new ItemStack(getSeedsItem(), 1));
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) { return state.get(AGE) < getMaxAge(); }
    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) { return true; }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int age = state.get(AGE);
        if (age < getMaxAge()) {
            world.setBlockState(pos, state.with(AGE, age + 1), 2);
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    8, 0.3, 0.3, 0.3, 0.05);
        }
    }
}