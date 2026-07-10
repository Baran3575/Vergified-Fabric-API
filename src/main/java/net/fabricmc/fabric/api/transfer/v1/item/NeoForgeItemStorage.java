package net.fabricmc.fabric.api.transfer.v1.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

// ponytail: bridges NeoForge IItemHandler capability to Fabric's SlottedStorage<ItemVariant>.
class NeoForgeItemStorage implements ItemStorage, SlottedStorage<ItemVariant> {
	private final IItemHandler handler;

	NeoForgeItemStorage(IItemHandler handler) {
		this.handler = handler;
	}

	private static final class ItemSlot implements SingleSlotStorage<ItemVariant> {
		private final ItemVariant variant;
		private final long capacity;

		ItemSlot(ItemStack stack) {
			this.variant = ItemVariant.of(stack);
			this.capacity = stack.isEmpty() ? 0 : stack.getMaxStackSize();
		}

		@Override
		public ItemVariant getResource() {
			return variant;
		}

		@Override
		public long getAmount() {
			return variant == null ? 0 : variant.toStack().getCount();
		}

		@Override
		public long getCapacity() {
			return capacity;
		}

		@Override
		public boolean isResourceBlank() {
			return variant == null || variant.toStack().isEmpty();
		}
	}

	private List<StorageView<ItemVariant>> build() {
		List<StorageView<ItemVariant>> out = new ArrayList<>();
		int slots = handler.getSlots();
		for (int i = 0; i < slots; i++) {
			out.add(new ItemSlot(handler.getStackInSlot(i)));
		}
		return out;
	}

	@Override
	public int getSlotCount() {
		return handler.getSlots();
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		return new ItemSlot(handler.getStackInSlot(slot));
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return build().iterator();
	}

	@Override
	public Iterator<StorageView<ItemVariant>> nonEmptyIterator() {
		return build().stream().filter(v -> !v.isResourceBlank()).iterator();
	}

	@Override
	public long getVersion() {
		long hash = 0;
		for (StorageView<ItemVariant> v : build()) {
			hash = hash * 31 + (v.getResource() == null ? 0 : v.getResource().toStack().getItem().hashCode());
			hash = hash * 31 + v.getAmount();
		}
		return hash;
	}
}
