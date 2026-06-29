package net.fabricmc.fabric.api.client.networking.v1;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ClientPlayConnectionEvents {
    public static final Event<Init> INIT = EventFactory.createArrayBacked(Init.class,
        listeners -> (handler, client) -> {
            for (Init listener : listeners) {
                listener.onPlayInit(handler, client);
            }
        });

    public static final Event<Join> JOIN = EventFactory.createArrayBacked(Join.class,
        listeners -> (handler, sender, client) -> {
            for (Join listener : listeners) {
                listener.onPlayReady(handler, sender, client);
            }
        });

    public static final Event<Disconnect> DISCONNECT = EventFactory.createArrayBacked(Disconnect.class,
        listeners -> (handler, client) -> {
            for (Disconnect listener : listeners) {
                listener.onPlayDisconnect(handler, client);
            }
        });

    @FunctionalInterface
    public interface Init {
        void onPlayInit(ClientPacketListener handler, Minecraft client);
    }

    @FunctionalInterface
    public interface Join {
        void onPlayReady(ClientPacketListener handler, Object sender, Minecraft client);
    }

    @FunctionalInterface
    public interface Disconnect {
        void onPlayDisconnect(ClientPacketListener handler, Minecraft client);
    }
}
