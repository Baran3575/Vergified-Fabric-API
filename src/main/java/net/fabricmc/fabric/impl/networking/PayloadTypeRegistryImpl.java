package net.fabricmc.fabric.impl.networking;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class PayloadTypeRegistryImpl implements PayloadTypeRegistry {
    public static final PayloadTypeRegistryImpl PLAY_C2S = new PayloadTypeRegistryImpl();
    public static final PayloadTypeRegistryImpl PLAY_S2C = new PayloadTypeRegistryImpl();

    private final Map<CustomPacketPayload.Type<?>, StreamCodec<? super RegistryFriendlyByteBuf, ?>> codecs = new HashMap<>();

    @Override
    public <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        codecs.put(type, codec);
    }

    public Map<CustomPacketPayload.Type<?>, StreamCodec<? super RegistryFriendlyByteBuf, ?>> getCodecs() {
        return codecs;
    }
}
