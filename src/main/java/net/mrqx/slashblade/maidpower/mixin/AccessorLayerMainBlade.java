package net.mrqx.slashblade.maidpower.mixin;

import jp.nyatla.nymmd.MmdMotionPlayerGL2;
import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LayerMainBlade.class)
public interface AccessorLayerMainBlade {
    @Accessor(value = "motionPlayer", remap = false)
    LazyOptional<MmdMotionPlayerGL2> getMotionPlayer();
}
