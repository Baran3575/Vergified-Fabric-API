package net.fabricmc.fabric.api.transfer.v1.item;

import net.fabricmc.fabric.api.transfer.v1.storage.SidedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public interface ItemStorage extends Storage<ItemVariant> {
	SidedStorage<ItemVariant> SIDED = new SidedStorage<>() {
		@Override
		public ItemStorage find(Level level, BlockPos pos, Direction side) {
			IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side);
			return handler == null ? null : new NeoForgeItemStorage(handler);
		}
	};
}
