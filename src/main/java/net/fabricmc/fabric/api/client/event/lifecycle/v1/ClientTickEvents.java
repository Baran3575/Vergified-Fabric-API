package net.fabricmc.fabric.api.client.event.lifecycle.v1;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ClientTickEvents {
    public static final Event<StartTick> START_CLIENT_TICK = EventFactory.createArrayBacked(StartTick.class,
        listeners -> client -> {
            for (StartTick listener : listeners) {
                listener.onStartTick(client);
            }
        });

    public static final Event<EndTick> END_CLIENT_TICK = EventFactory.createArrayBacked(EndTick.class,
        listeners -> client -> {
            for (EndTick listener : listeners) {
                listener.onEndTick(client);
            }
        });

    public static final Event<StartWorldTick> START_WORLD_TICK = EventFactory.createArrayBacked(StartWorldTick.class,
        listeners -> world -> {
            for (StartWorldTick listener : listeners) {
                listener.onStartTick(world);
            }
        });

    public static final Event<EndWorldTick> END_WORLD_TICK = EventFactory.createArrayBacked(EndWorldTick.class,
        listeners -> world -> {
            for (EndWorldTick listener : listeners) {
                listener.onEndTick(world);
            }
        });

    @FunctionalInterface
    public interface StartTick {
        void onStartTick(Minecraft client);
    }

    @FunctionalInterface
    public interface EndTick {
        void onEndTick(Minecraft client);
    }

    @FunctionalInterface
    public interface StartWorldTick {
        void onStartTick(ClientLevel world);
    }

    @FunctionalInterface
    public interface EndWorldTick {
        void onEndTick(ClientLevel world);
    }
}
