package net.fabricmc.fabric.api.transfer.v1.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface SidedStorage<T> {
	Storage<T> find(Level level, BlockPos pos, Direction side);
}
