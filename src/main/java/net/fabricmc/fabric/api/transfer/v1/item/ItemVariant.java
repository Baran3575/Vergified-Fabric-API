package net.fabricmc.fabric.api.transfer.v1.item;

import net.minecraft.world.item.ItemStack;

public final class ItemVariant {
	private final ItemStack stack;

	private ItemVariant(ItemStack stack) {
		this.stack = stack;
	}

	public static ItemVariant of(ItemStack stack) {
		return new ItemVariant(stack);
	}

	public ItemStack toStack(int amount) {
		ItemStack copy = stack.copy();
		copy.setCount(amount);
		return copy;
	}

	public ItemStack toStack() {
		return stack.copy();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ItemVariant other)) return false;
		return ItemStack.isSameItemSameComponents(stack, other.stack);
	}

	@Override
	public int hashCode() {
		return stack.getItem().hashCode();
	}
}
