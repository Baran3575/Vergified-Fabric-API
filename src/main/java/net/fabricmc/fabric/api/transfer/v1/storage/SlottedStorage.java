package net.fabricmc.fabric.api.transfer.v1.storage;

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;

public interface SlottedStorage<T> extends Storage<T> {
	int getSlotCount();

	SingleSlotStorage<T> getSlot(int slot);
}
