package net.fabricmc.fabric.api.transfer.v1.fluid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

// ponytail: bridges NeoForge IFluidHandler capability to Fabric's Storage<FluidVariant>.
// Forge amounts are in millibuckets (1000/bucket); Fabric transfer uses 81000/bucket, hence *81.
class NeoForgeFluidStorage implements FluidStorage {
	private final IFluidHandler handler;

	NeoForgeFluidStorage(IFluidHandler handler) {
		this.handler = handler;
	}

	private List<StorageView<FluidVariant>> build() {
		List<StorageView<FluidVariant>> out = new ArrayList<>();
		int tanks = handler.getTanks();
		for (int i = 0; i < tanks; i++) {
			final FluidStack fs = handler.getFluidInTank(i);
			final long amount = (long) fs.getAmount() * 81L;
			final long capacity = (long) handler.getTankCapacity(i) * 81L;
			final FluidVariant variant = FluidVariant.of(fs);
			out.add(new StorageView<FluidVariant>() {
				@Override
				public FluidVariant getResource() {
					return variant;
				}

				@Override
				public long getAmount() {
					return amount;
				}

				@Override
				public long getCapacity() {
					return capacity;
				}

				@Override
				public boolean isResourceBlank() {
					return variant == null || variant.getFluid() == Fluids.EMPTY;
				}
			});
		}
		return out;
	}

	@Override
	public Iterator<StorageView<FluidVariant>> iterator() {
		return build().iterator();
	}

	@Override
	public Iterator<StorageView<FluidVariant>> nonEmptyIterator() {
		return build().stream().filter(v -> !v.isResourceBlank()).iterator();
	}

	@Override
	public long getVersion() {
		long hash = 0;
		for (StorageView<FluidVariant> v : build()) {
			hash = hash * 31 + (v.getResource() == null ? 0 : v.getResource().getFluid().hashCode());
			hash = hash * 31 + v.getAmount();
		}
		return hash;
	}
}
