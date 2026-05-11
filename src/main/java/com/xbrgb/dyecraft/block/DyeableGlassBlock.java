package com.xbrgb.dyecraft.block;

import com.xbrgb.dyecraft.item.DyeableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DyeableGlassBlock extends DyeableBlock {
    public DyeableGlassBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && !player.isCreative()) {
            ItemStack tool = player.getMainHandItem();
            boolean hasSilkTouch = tool.getEnchantments().getLevel(level.registryAccess().holderOrThrow(Enchantments.SILK_TOUCH)) > 0;
            if (!hasSilkTouch) {
                level.removeBlock(pos, false);
                return state;
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}