package net.fabricmc.fabric.api.client.event.lifecycle.v1;

import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ClientLifecycleEvents {
    public static final Event<ClientStarted> CLIENT_STARTED = EventFactory.createArrayBacked(ClientStarted.class,
        listeners -> client -> {
            for (ClientStarted listener : listeners) {
                listener.onClientStarted(client);
            }
        });

    public static final Event<ClientStopping> CLIENT_STOPPING = EventFactory.createArrayBacked(ClientStopping.class,
        listeners -> client -> {
            for (ClientStopping listener : listeners) {
                listener.onClientStopping(client);
            }
        });

    @FunctionalInterface
    public interface ClientStarted {
        void onClientStarted(Minecraft client);
    }

    @FunctionalInterface
    public interface ClientStopping {
        void onClientStopping(Minecraft client);
    }
}
