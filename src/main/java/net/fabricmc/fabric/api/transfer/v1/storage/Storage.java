package net.fabricmc.fabric.api.transfer.v1.storage;

import java.util.Iterator;

public interface Storage<T> extends Iterable<StorageView<T>> {
	@Override
	Iterator<StorageView<T>> iterator();

	Iterator<StorageView<T>> nonEmptyIterator();

	long getVersion();
}
