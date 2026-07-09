package com.baran3575.vfa;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.server.packs.PackType;
import net.minecraft.resources.ResourceLocation;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;

public class VFAClient {
    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(VFAClient::onRegisterBlockColors);
        modEventBus.addListener(VFAClient::onRegisterItemColors);
        modEventBus.addListener(VFAClient::onRegisterRenderers);
        modEventBus.addListener(VFAClient::onRegisterReloadListeners);
        modEventBus.addListener(VFAClient::onRegisterKeyMappings);

        NeoForge.EVENT_BUS.addListener(VFAClient::onScreenInitPost);
        NeoForge.EVENT_BUS.addListener(VFAClient::onScreenRenderPost);

        NeoForge.EVENT_BUS.addListener(VFAClient::onClientTickPre);
        NeoForge.EVENT_BUS.addListener(VFAClient::onClientTickPost);
        NeoForge.EVENT_BUS.addListener(VFAClient::onLevelTickPre);
        NeoForge.EVENT_BUS.addListener(VFAClient::onLevelTickPost);

        NeoForge.EVENT_BUS.addListener(VFAClient::onClientPlayerLogin);
        NeoForge.EVENT_BUS.addListener(VFAClient::onClientPlayerLogout);

        // ponytail: fire Fabric HudRenderCallback from a real GUI layer so Jade's
        // overlay draws every frame regardless of which vanilla layer is active
        // (the old SUBTITLE_OVERLAY gating made it never render with subtitles off).
        modEventBus.addListener(VFAClient::onRegisterGuiLayers);

        NeoForge.EVENT_BUS.addListener(VFAClient::onClientEntityJoin);
        NeoForge.EVENT_BUS.addListener(VFAClient::onClientEntityLeave);
        NeoForge.EVENT_BUS.addListener(VFAClient::onItemTooltip);
        NeoForge.EVENT_BUS.addListener(VFAClient::onRegisterClientCommands);
    }

    private static void onClientPlayerLogin(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingIn event) {
        net.minecraft.client.multiplayer.ClientPacketListener connection = net.minecraft.client.Minecraft.getInstance().getConnection();
        if (connection != null) {
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.INIT.invoker().onPlayInit(
                connection,
                net.minecraft.client.Minecraft.getInstance()
            );
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.JOIN.invoker().onPlayReady(
                connection,
                new net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PacketSender() {
                    @Override
                    public <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void sendPacket(net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> type, T payload) {
                        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(payload);
                    }
                },
                net.minecraft.client.Minecraft.getInstance()
            );
        }
    }

    private static void onClientPlayerLogout(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
        net.minecraft.client.multiplayer.ClientPacketListener connection = net.minecraft.client.Minecraft.getInstance().getConnection();
        if (connection != null) {
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.invoker().onPlayDisconnect(
                connection,
                net.minecraft.client.Minecraft.getInstance()
            );
        }
    }

    private static boolean clientStartedFired = false;

    private static void onClientTickPre(net.neoforged.neoforge.client.event.ClientTickEvent.Pre event) {
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick(net.minecraft.client.Minecraft.getInstance());
        if (!clientStartedFired) {
            clientStartedFired = true;
            net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STARTED.invoker().onClientStarted(net.minecraft.client.Minecraft.getInstance());
        }
    }

    private static void onClientTickPost(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick(net.minecraft.client.Minecraft.getInstance());
    }

    private static void onLevelTickPre(net.neoforged.neoforge.event.tick.LevelTickEvent.Pre event) {
        if (event.getLevel().isClientSide() && event.getLevel() instanceof net.minecraft.client.multiplayer.ClientLevel cl) {
            net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_WORLD_TICK.invoker().onStartTick(cl);
        }
    }

    private static void onLevelTickPost(net.neoforged.neoforge.event.tick.LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide() && event.getLevel() instanceof net.minecraft.client.multiplayer.ClientLevel cl) {
            net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_WORLD_TICK.invoker().onEndTick(cl);
        }
    }

    private static void onRegisterBlockColors(net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Block event) {
        net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistryImpl<Block, BlockColor> blockRegistry =
            (net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistryImpl<Block, BlockColor>) net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.BLOCK;
        for (net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistryImpl.Registration<Block, BlockColor> reg : blockRegistry.getPending()) {
            event.register(reg.provider, reg.objects);
        }
    }

    private static void onRegisterItemColors(net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item event) {
        net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistryImpl<Item, ItemColor> itemRegistry =
            (net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistryImpl<Item, ItemColor>) net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM;
        for (net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistryImpl.Registration<Item, ItemColor> reg : itemRegistry.getPending()) {
            event.register(reg.provider, reg.objects);
        }
    }

    private static void onRegisterRenderers(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        for (net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.Registration<?> reg : net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.getPending()) {
            registerBlockEntityHelper(event, reg);
        }
        for (net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.Registration<?> reg : net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.getPending()) {
            registerEntityHelper(event, reg);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> void registerBlockEntityHelper(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event, net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.Registration<T> reg) {
        event.registerBlockEntityRenderer(reg.type, reg.factory);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> void registerEntityHelper(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event, net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.Registration<T> reg) {
        event.registerEntityRenderer(reg.type, (net.minecraft.client.renderer.entity.EntityRendererProvider<T>) reg.provider);
    }

    private static void onRegisterReloadListeners(net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent event) {
        net.fabricmc.fabric.api.resource.ResourceManagerHelper clientHelper = net.fabricmc.fabric.api.resource.ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
        for (net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener listener : clientHelper.getListeners()) {
            event.registerReloadListener(listener);
        }
    }

    private static void onRegisterKeyMappings(net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent event) {
        for (net.minecraft.client.KeyMapping keyMapping : net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.getPending()) {
            event.register(keyMapping);
        }
    }

    private static void onScreenInitPost(net.neoforged.neoforge.client.event.ScreenEvent.Init.Post event) {
        net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_INIT.invoker().afterInit(
            net.minecraft.client.Minecraft.getInstance(),
            event.getScreen(),
            event.getScreen().width,
            event.getScreen().height
        );
    }

    private static void onScreenRenderPost(net.neoforged.neoforge.client.event.ScreenEvent.Render.Post event) {
        net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_RENDER.invoker().afterRender(
            event.getScreen(),
            event.getGuiGraphics(),
            event.getMouseX(),
            event.getMouseY(),
            0.0f
        );
    }

    private static void onRegisterGuiLayers(net.neoforged.neoforge.client.event.RegisterGuiLayersEvent event) {
        event.registerAbove(
            VanillaGuiLayers.HUD,
            ResourceLocation.fromNamespaceAndPath("vfa", "hud_layer"),
            (guiGraphics, deltaTracker) -> {
                net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.invoker().onHudRender(
                    guiGraphics,
                    deltaTracker.getRealtimeDeltaTicks()
                );
            });
    }

    private static void onClientEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents.ENTITY_LOAD.invoker()
                .onEntityJoin(event.getEntity(), event.getLevel());
        }
    }

    private static void onClientEntityLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents.ENTITY_UNLOAD.invoker()
                .onEntityLeave(event.getEntity(), event.getLevel());
        }
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.invoker().onItemTooltip(
            event.getItemStack(),
            event.getContext(),
            event.getToolTip(),
            event.getFlags()
        );
    }

    // ponytail: Fabric's client command dispatcher is <FabricClientCommandSource>; NeoForge's is
    // <ClientCommandSourceStack>. We can't bridge the types, so Jade's /jade command tree is built
    // into a standalone dispatcher (harmless) but not wired to execution yet. Core Jade (overlay,
    // keybinds, networking) is unaffected. Full command execution = later iteration.
    private static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        com.mojang.brigadier.CommandDispatcher<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource> dispatcher =
            new com.mojang.brigadier.CommandDispatcher<>();
        net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback.EVENT.invoker()
            .register(dispatcher, event.getBuildContext());
    }
}
