package net.fabricmc.fabric.api.client.event.lifecycle.v1;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ClientEntityEvents {
    public static final Event<EntityJoin> ENTITY_LOAD = EventFactory.createArrayBacked(EntityJoin.class,
        listeners -> (entity, world) -> {
            for (EntityJoin listener : listeners) {
                listener.onEntityJoin(entity, world);
            }
        });

    public static final Event<EntityLeave> ENTITY_UNLOAD = EventFactory.createArrayBacked(EntityLeave.class,
        listeners -> (entity, world) -> {
            for (EntityLeave listener : listeners) {
                listener.onEntityLeave(entity, world);
            }
        });

    @FunctionalInterface
    public interface EntityJoin {
        void onEntityJoin(Entity entity, Level world);
    }

    @FunctionalInterface
    public interface EntityLeave {
        void onEntityLeave(Entity entity, Level world);
    }
}
