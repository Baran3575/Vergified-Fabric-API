package net.fabricmc.fabric.api.client.item.v1;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import java.util.List;

public final class ItemTooltipCallback {
    public static final Event<ItemTooltipListener> EVENT = EventFactory.createArrayBacked(ItemTooltipListener.class,
        listeners -> (stack, context, lines, flag) -> {
            for (ItemTooltipListener listener : listeners) {
                listener.onItemTooltip(stack, context, lines, flag);
            }
        });

    @FunctionalInterface
    public interface ItemTooltipListener {
        void onItemTooltip(ItemStack stack, Item.TooltipContext context, List<Component> lines, net.minecraft.world.item.TooltipFlag flag);
    }
}
