package com.baran3575.vfa;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(VergifiedFabricAPI.MODID)
public class VergifiedFabricAPI {
    public static final String MODID = "vfa";
    public static final Logger LOGGER = LoggerFactory.getLogger(VergifiedFabricAPI.class);

    public VergifiedFabricAPI(IEventBus modEventBus) {
        LOGGER.info("[VFA] Vergified Fabric API (VFA) is initializing!");

        // Register event listeners on Forge Bus
        NeoForge.EVENT_BUS.register(this);
        
        // Payload and Creative Mode Tab contents event listeners on Mod Bus
        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(this::onBuildCreativeModeTabContents);
        
        if (FMLEnvironment.dist.isClient()) {
            VFAClient.init(modEventBus);
        }
    }

    // Packet payload registration logic
    private void registerPayloads(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) {
        LOGGER.info("[VFA] Registering Fabric custom payload networking channels to NeoForge...");
        net.neoforged.neoforge.network.registration.PayloadRegistrar registrar = event.registrar(MODID);

        // Register Server Receivers (C2S)
        net.fabricmc.fabric.impl.networking.PayloadTypeRegistryImpl playC2S = net.fabricmc.fabric.impl.networking.PayloadTypeRegistryImpl.PLAY_C2S;
        for (java.util.Map.Entry<net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<?>, net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.FriendlyByteBuf, ?>> entry : playC2S.getCodecs().entrySet()) {
            registerServerChannel(registrar, (net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type) entry.getKey(), (net.minecraft.network.codec.StreamCodec) entry.getValue());
        }

        // Register Client Receivers (S2C)
        net.fabricmc.fabric.impl.networking.PayloadTypeRegistryImpl playS2C = net.fabricmc.fabric.impl.networking.PayloadTypeRegistryImpl.PLAY_S2C;
        for (java.util.Map.Entry<net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<?>, net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.FriendlyByteBuf, ?>> entry : playS2C.getCodecs().entrySet()) {
            registerClientChannel(registrar, (net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type) entry.getKey(), (net.minecraft.network.codec.StreamCodec) entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void registerServerChannel(net.neoforged.neoforge.network.registration.PayloadRegistrar registrar, net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> type, net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.RegistryFriendlyByteBuf, T> codec) {
        registrar.playToServer(type, codec, (payload, context) -> {
            net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler<T> handler = (net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler<T>) net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.getReceivers().get(type);
            if (handler != null) {
                context.enqueueWork(() -> handler.receive(payload, new net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context() {
                    @Override
                    public net.minecraft.server.level.ServerPlayer player() {
                        return (net.minecraft.server.level.ServerPlayer) context.player();
                    }
                }));
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void registerClientChannel(net.neoforged.neoforge.network.registration.PayloadRegistrar registrar, net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> type, net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.RegistryFriendlyByteBuf, T> codec) {
        registrar.playToClient(type, codec, (payload, context) -> {
            net.fabricmc.fabric.api.networking.v1.ClientPlayNetworking.PlayPayloadHandler<T> handler = (net.fabricmc.fabric.api.networking.v1.ClientPlayNetworking.PlayPayloadHandler<T>) net.fabricmc.fabric.api.networking.v1.ClientPlayNetworking.getReceivers().get(type);
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

    public void onBuildCreativeModeTabContents(net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent event) {
        net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.Event fabricEvent = 
            net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.getEvents().get(event.getTabKey());
        if (fabricEvent != null) {
            net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.Entries entries = event::accept;
            for (java.util.function.Consumer<net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.Entries> listener : fabricEvent.getListeners()) {
                listener.accept(entries);
            }
        }
    }

    // ─── Fabric Server Events bridge ──────────────────────────────────────────
    @net.neoforged.bus.api.SubscribeEvent
    public void onServerAboutToStart(net.neoforged.neoforge.event.server.ServerAboutToStartEvent event) {
        for (net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting handler : net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTING.getHandlers()) {
            handler.onServerStarting(event.getServer());
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onServerStarted(net.neoforged.neoforge.event.server.ServerStartedEvent event) {
        for (net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted handler : net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.getHandlers()) {
            handler.onServerStarted(event.getServer());
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onServerStopping(net.neoforged.neoforge.event.server.ServerStoppingEvent event) {
        for (net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopping handler : net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPING.getHandlers()) {
            handler.onServerStopping(event.getServer());
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onServerStopped(net.neoforged.neoforge.event.server.ServerStoppedEvent event) {
        for (net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped handler : net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPED.getHandlers()) {
            handler.onServerStopped(event.getServer());
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onAddReloadListener(net.neoforged.neoforge.event.AddReloadListenerEvent event) {
        net.fabricmc.fabric.api.resource.ResourceManagerHelper serverHelper = net.fabricmc.fabric.api.resource.ResourceManagerHelper.get(net.minecraft.server.packs.PackType.SERVER_DATA);
        for (net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener listener : serverHelper.getListeners()) {
            event.addListener(listener);
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onServerTickStart(net.neoforged.neoforge.event.tick.ServerTickEvent.Pre event) {
        for (net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.StartTick h : net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_SERVER_TICK.getHandlers()) {
            try { h.onStartTick(event.getServer()); } catch (Exception e) { LOGGER.warn("[VFA] START_SERVER_TICK handler error", e); }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onServerTickEnd(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
        for (net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndTick h : net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.getHandlers()) {
            try { h.onEndTick(event.getServer()); } catch (Exception e) { LOGGER.warn("[VFA] END_SERVER_TICK handler error", e); }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onLevelTickStart(net.neoforged.neoforge.event.tick.LevelTickEvent.Pre event) {
        if (event.getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
            for (net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.StartWorldTick h : net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_WORLD_TICK.getHandlers()) {
                try { h.onStartTick(sl); } catch (Exception e) { LOGGER.warn("[VFA] START_WORLD_TICK handler error", e); }
            }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onLevelTickEnd(net.neoforged.neoforge.event.tick.LevelTickEvent.Post event) {
        if (event.getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
            for (net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndWorldTick h : net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.getHandlers()) {
                try { h.onEndTick(sl); } catch (Exception e) { LOGGER.warn("[VFA] END_WORLD_TICK handler error", e); }
            }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onLevelLoad(net.neoforged.neoforge.event.level.LevelEvent.Load event) {
        if (event.getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
            net.minecraft.server.MinecraftServer srv = sl.getServer();
            for (net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.Load h : net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.LOAD.getHandlers()) {
                try { h.onWorldLoad(srv, sl); } catch (Exception e) { LOGGER.warn("[VFA] WORLD_LOAD handler error", e); }
            }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onLevelUnload(net.neoforged.neoforge.event.level.LevelEvent.Unload event) {
        if (event.getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
            net.minecraft.server.MinecraftServer srv = sl.getServer();
            for (net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.Unload h : net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.UNLOAD.getHandlers()) {
                try { h.onWorldUnload(srv, sl); } catch (Exception e) { LOGGER.warn("[VFA] WORLD_UNLOAD handler error", e); }
            }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onEntityJoinLevel(net.neoforged.neoforge.event.entity.EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
            for (net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.EntityLoad h : net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.getHandlers()) {
                try { h.onLoad(event.getEntity(), sl); } catch (Exception e) { LOGGER.warn("[VFA] ENTITY_LOAD handler error", e); }
            }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onEntityLeaveLevel(net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent event) {
        if (event.getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
            for (net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.EntityUnload h : net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_UNLOAD.getHandlers()) {
                try { h.onUnload(event.getEntity(), sl); } catch (Exception e) { LOGGER.warn("[VFA] ENTITY_UNLOAD handler error", e); }
            }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onPlayerJoin(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer sp && sp.getServer() != null) {
            for (net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join h : net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.getHandlers()) {
                try { h.onPlayReady(sp.connection, sp.getServer()); } catch (Exception e) { LOGGER.warn("[VFA] PLAY_JOIN handler error", e); }
            }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onPlayerLeave(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer sp && sp.getServer() != null) {
            for (net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Disconnect h : net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.getHandlers()) {
                try { h.onPlayDisconnect(sp.connection, sp.getServer()); } catch (Exception e) { LOGGER.warn("[VFA] PLAY_DISCONNECT handler error", e); }
            }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onPlayerRightClickBlock(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        for (net.fabricmc.fabric.api.event.player.UseBlockCallback.Event h : net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.getHandlers()) {
            try {
                net.minecraft.world.InteractionResult result = h.interact(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
                if (result != net.minecraft.world.InteractionResult.PASS) {
                    event.setCanceled(true);
                    event.setUseBlock(net.neoforged.neoforge.common.util.TriState.FALSE);
                    break;
                }
            } catch (Exception e) { LOGGER.warn("[VFA] USE_BLOCK handler error", e); }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onPlayerRightClickItem(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        for (net.fabricmc.fabric.api.event.player.UseItemCallback.Event h : net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.getHandlers()) {
            try {
                net.minecraft.world.InteractionResultHolder<net.minecraft.world.item.ItemStack> result =
                    h.interact(event.getEntity(), event.getLevel(), event.getHand());
                if (result.getResult() != net.minecraft.world.InteractionResult.PASS) {
                    event.setCanceled(true);
                    break;
                }
            } catch (Exception e) { LOGGER.warn("[VFA] USE_ITEM handler error", e); }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onPlayerAttackEntity(net.neoforged.neoforge.event.entity.player.AttackEntityEvent event) {
        for (net.fabricmc.fabric.api.event.player.AttackEntityCallback.Event h : net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.getHandlers()) {
            try {
                net.minecraft.world.InteractionResult result = h.interact(
                    event.getEntity(), event.getEntity().level(),
                    net.minecraft.world.InteractionHand.MAIN_HAND, event.getTarget(), java.util.Optional.empty());
                if (result == net.minecraft.world.InteractionResult.FAIL) {
                    event.setCanceled(true);
                    break;
                }
            } catch (Exception e) { LOGGER.warn("[VFA] ATTACK_ENTITY handler error", e); }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onRegisterCommands(net.neoforged.neoforge.event.RegisterCommandsEvent event) {
        for (net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.Event h : net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.getHandlers()) {
            try { h.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()); }
            catch (Exception e) { LOGGER.warn("[VFA] COMMAND_REGISTRATION handler error", e); }
        }
    }
}
