package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.network.protocol.Packet;

public interface PacketSender {
	void sendPacket(Packet<?> packet);

	boolean isClient();
}
