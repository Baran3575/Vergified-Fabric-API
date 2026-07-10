package net.fabricmc.fabric.api.transfer.v1.storage;

public interface StorageView<T> {
	T getResource();

	long getAmount();

	long getCapacity();

	boolean isResourceBlank();
}
