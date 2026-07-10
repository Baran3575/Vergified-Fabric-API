package net.fabricmc.fabric.api.lookup.v1.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

// ponytail: minimal Fabric BlockApiLookup so classes that reference it (e.g. TechReborn compat)
// link. find() returns null (no API provider registered) — safe no-op when the source mod is absent.
public final class BlockApiLookup<C, T> {
	private BlockApiLookup() {
	}

	public static <C, T> BlockApiLookup<C, T> get(Class<C> contextClass, Class<T> valueClass, ResourceLocation... ids) {
		return new BlockApiLookup<>();
	}

	public T find(Level level, BlockPos pos, C context) {
		return null;
	}

	public T find(Level level, BlockPos pos, Direction direction) {
		return null;
	}
}
