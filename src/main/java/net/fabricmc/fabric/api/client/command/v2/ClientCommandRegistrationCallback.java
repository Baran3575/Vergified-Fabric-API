package net.fabricmc.fabric.api.client.command.v2;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ClientCommandRegistrationCallback {
    void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context);

    Event<ClientCommandRegistrationCallback> EVENT = EventFactory.createArrayBacked(ClientCommandRegistrationCallback.class,
        listeners -> (dispatcher, context) -> {
            for (ClientCommandRegistrationCallback listener : listeners) {
                listener.register(dispatcher, context);
            }
        });
}
