package net.fabricmc.fabric.api.event.lifecycle.v1;

import java.util.function.BiConsumer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.RegistryAccess;

public final class CommonLifecycleEvents {
	public static final Event<BiConsumer<RegistryAccess, Boolean>> TAGS_LOADED = EventFactory.createArrayBacked(
			BiConsumer.class,
			listeners -> (registryAccess, sync) -> {
				for (BiConsumer<RegistryAccess, Boolean> listener : listeners) {
					listener.accept(registryAccess, sync);
				}
			});

	private CommonLifecycleEvents() {
	}
}
