package net.fabricmc.fabric.api.transfer.v1.fluid;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public final class FluidVariant {
	private final Fluid fluid;
	private final Object components;

	private FluidVariant(Fluid fluid, Object components) {
		this.fluid = fluid;
		this.components = components;
	}

	public static FluidVariant of(Fluid fluid) {
		return new FluidVariant(fluid, null);
	}

	public static FluidVariant of(Fluid fluid, Object... components) {
		Object c = components == null || components.length == 0 ? null
				: (components.length == 1 ? components[0] : components);
		return new FluidVariant(fluid, c);
	}

	public static FluidVariant of(FluidStack stack) {
		return stack.isEmpty() ? new FluidVariant(net.minecraft.world.level.material.Fluids.EMPTY, null)
				: new FluidVariant(stack.getFluid(), null);
	}

	public Fluid getFluid() {
		return fluid;
	}

	public Object getComponents() {
		return components;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FluidVariant other)) return false;
		return fluid == other.fluid && java.util.Objects.equals(components, other.components);
	}

	@Override
	public int hashCode() {
		return fluid == null ? 0 : fluid.hashCode();
	}
}
