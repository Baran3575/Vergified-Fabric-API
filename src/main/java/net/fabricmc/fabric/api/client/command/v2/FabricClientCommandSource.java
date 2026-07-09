package net.fabricmc.fabric.api.client.command.v2;

import net.minecraft.network.chat.Component;

public interface FabricClientCommandSource {
    void sendFeedback(Component component);

    void sendError(Component component);
}
