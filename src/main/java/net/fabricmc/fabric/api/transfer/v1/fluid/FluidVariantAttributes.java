package net.fabricmc.fabric.api.transfer.v1.fluid;

import net.minecraft.network.chat.Component;

public final class FluidVariantAttributes {
	public static Component getName(FluidVariant variant) {
		Fluid fluid = variant.getFluid();
		if (fluid == null || fluid == net.minecraft.world.level.material.Fluids.EMPTY) {
			return Component.translatable("block.air");
		}
		return Component.translatable(fluid.getDescriptionId());
	}

	private FluidVariantAttributes() {
	}
}
