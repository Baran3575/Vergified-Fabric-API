package com.baran3575.vfa;

public class VFAClientHelper {
    @SuppressWarnings("unchecked")
    public static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void registerClientChannel(
            net.neoforged.neoforge.network.registration.PayloadRegistrar registrar,
            net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> type,
            net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.RegistryFriendlyByteBuf, T> codec) {
        registrar.playToClient(type, codec, (payload, context) -> {
            net.fabricmc.fabric.api.networking.v1.ClientPlayNetworking.PlayPayloadHandler<T> handler = 
                (net.fabricmc.fabric.api.networking.v1.ClientPlayNetworking.PlayPayloadHandler<T>) 
                net.fabricmc.fabric.api.networking.v1.ClientPlayNetworking.getReceivers().get(type);
            if (handler != null) {
                context.enqueueWork(() -> handler.receive(payload, new net.fabricmc.fabric.api.networking.v1.ClientPlayNetworking.Context() {
                    @Override
                    public net.minecraft.client.player.LocalPlayer player() {
                        return (net.minecraft.client.player.LocalPlayer) context.player();
                    }
                }));
            }
        });
    }
}
