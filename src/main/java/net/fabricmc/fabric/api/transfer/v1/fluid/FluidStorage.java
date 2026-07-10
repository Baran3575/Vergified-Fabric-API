package net.fabricmc.fabric.api.transfer.v1.fluid;

import net.fabricmc.fabric.api.transfer.v1.storage.SidedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface FluidStorage extends Storage<FluidVariant> {
	SidedStorage<FluidVariant> SIDED = new SidedStorage<>() {
		@Override
		public FluidStorage find(Level level, BlockPos pos, Direction side) {
			IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side);
			return handler == null ? null : new NeoForgeFluidStorage(handler);
		}
	};
}
