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

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DyeableGlassBlock extends DyeableBlock {
    public DyeableGlassBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && !player.isCreative()) {
            ItemStack tool = player.getMainHandStack();
            RegistryEntry<Enchantment> silkTouch = world.getRegistryManager()
                    .get(RegistryKeys.ENCHANTMENT)
                    .getEntry(Enchantments.SILK_TOUCH)
                    .orElse(null);
            boolean hasSilkTouch = silkTouch != null && EnchantmentHelper.getLevel(silkTouch, tool) > 0;
            if (!hasSilkTouch) {
                world.removeBlock(pos, false);
                return state;
            }
        }
        return super.onBreak(world, pos, state, player);
    }
}