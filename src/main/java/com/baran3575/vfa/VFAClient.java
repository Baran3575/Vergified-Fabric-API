package com.baran3575.vfa;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.server.packs.PackType;

public class VFAClient {
    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(VFAClient::onRegisterBlockColors);
        modEventBus.addListener(VFAClient::onRegisterItemColors);
        modEventBus.addListener(VFAClient::onRegisterRenderers);
        modEventBus.addListener(VFAClient::onRegisterReloadListeners);
        modEventBus.addListener(VFAClient::onRegisterKeyMappings);

        NeoForge.EVENT_BUS.addListener(VFAClient::onScreenInitPost);
        NeoForge.EVENT_BUS.addListener(VFAClient::onScreenRenderPost);
        NeoForge.EVENT_BUS.addListener(VFAClient::onHudRenderPost);
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

    private static void onHudRenderPost(net.neoforged.neoforge.client.event.RenderGuiEvent.Post event) {
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.invoker().onHudRender(
            event.getGuiGraphics(),
            0.0f
        );
    }
}
