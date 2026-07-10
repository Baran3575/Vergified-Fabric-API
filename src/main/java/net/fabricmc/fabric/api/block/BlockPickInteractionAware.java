package net.fabricmc.fabric.api.block;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;

public interface BlockPickInteractionAware {
	ItemStack getPickedStack(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult);
}
