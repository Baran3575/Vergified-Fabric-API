package net.fabricmc.fabric.api.transfer.v1.client.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;

public final class FluidVariantRendering {
	private static final FluidVariantRenderHandler DEFAULT = new FluidVariantRenderHandler() {
		@Nullable
		@Override
		public TextureAtlasSprite[] getSprites(FluidVariant variant) {
			Fluid fluid = variant.getFluid();
			if (fluid == null || fluid == Fluids.EMPTY) return null;
			ResourceLocation rl = IClientFluidTypeExtensions.of(fluid).getStillTexture(null);
			if (rl == null) return null;
			TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(rl);
			return new TextureAtlasSprite[]{sprite};
		}

		@Override
		public int getColor(FluidVariant variant, @Nullable Level level, @Nullable FluidState state) {
			Fluid fluid = variant.getFluid();
			if (fluid == null) return 0xFFFFFF;
			return IClientFluidTypeExtensions.of(fluid).getTintColor(new net.neoforged.neoforge.fluids.FluidStack(fluid, 1));
		}
	};

	public static FluidVariantRenderHandler getHandlerOrDefault(Fluid fluid) {
		return DEFAULT;
	}

	private FluidVariantRendering() {
	}
}
