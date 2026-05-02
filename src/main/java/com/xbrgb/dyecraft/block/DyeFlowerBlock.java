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

    /**
     * 根据概率生成种子掉落数量
     * 40% 掉落 1 个，40% 掉落 2 个，20% 掉落 3 个
     */
    private int getSeedDropCount(Random random) {
        float chance = random.nextFloat();
        if (chance < 0.4f) {
            return 1;
        } else if (chance < 0.8f) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(AGE)];
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return seedItem.get();
    }

    public ItemConvertible getPlantItem() {
        return plantItem.get();
    }

    @Override
    public IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            int age = this.getAge(state);
            if (age < this.getMaxAge()) {
                float f = getAvailableMoisture(this, world, pos);
                if (random.nextInt((int) (25.0F / f) + 1) == 0) {
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

        // 成熟作物右键收获
        if (age == getMaxAge()) {
            if (!world.isClient) {
                int seedCount = getSeedDropCount(world.random);
                dropStack(world, pos, new ItemStack(getSeedsItem(), seedCount));
                dropStack(world, pos, new ItemStack(getPlantItem(), 1));
                world.setBlockState(pos, state.with(AGE, 0), 2);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && !player.isCreative()) {
            int age = state.get(AGE);
            if (age == getMaxAge()) {
                // 成熟：按概率掉落 1-3 个种子 + 1 个植物
                int seedCount = getSeedDropCount(world.random);
                dropStack(world, pos, new ItemStack(getSeedsItem(), seedCount));
                dropStack(world, pos, new ItemStack(getPlantItem(), 1));
            } else {
                // 未成熟：只掉落 1 个种子
                dropStack(world, pos, new ItemStack(getSeedsItem(), 1));
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return state.get(AGE) < getMaxAge();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int age = state.get(AGE);
        if (age < getMaxAge()) {
            world.setBlockState(pos, state.with(AGE, age + 1), 2);
            // 骨粉粒子效果
            world.spawnParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    8,
                    0.3, 0.3, 0.3,
                    0.05
            );
        }
    }
}