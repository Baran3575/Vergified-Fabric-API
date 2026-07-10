package net.fabricmc.fabric.api.entity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

public interface EntityPickInteractionAware {
	ItemStack getPickedStack(Player player, BlockHitResult hitResult);
}
