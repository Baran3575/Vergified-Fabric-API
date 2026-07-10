package net.fabricmc.fabric.api.transfer.v1.client.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public interface FluidVariantRenderHandler {
	@Nullable
	TextureAtlasSprite[] getSprites(FluidVariant variant);

	int getColor(FluidVariant variant, @Nullable Level level, @Nullable FluidState state);
}
