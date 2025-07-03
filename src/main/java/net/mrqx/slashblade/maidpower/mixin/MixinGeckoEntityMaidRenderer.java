package net.mrqx.slashblade.maidpower.mixin;

import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoMaidEntity;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.GeckoEntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoReplacedEntityRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.IGeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Mob;
import net.mrqx.slashblade.maidpower.client.renderer.GeoLayerMaidBladeRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GeckoEntityMaidRenderer.class)
public abstract class MixinGeckoEntityMaidRenderer<T extends Mob> extends GeoReplacedEntityRenderer<T, GeckoMaidEntity<T>> implements IGeoEntityRenderer<T> {
    private MixinGeckoEntityMaidRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)V", at = @At("RETURN"))
    private void injectInit(EntityRendererProvider.Context manager, CallbackInfo ci) {
        this.addLayer(new GeoLayerMaidBladeRenderer<>(this));
    }
}
