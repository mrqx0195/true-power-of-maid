package net.mrqx.slashblade.maidpower.mixin;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Mob;
import net.mrqx.slashblade.maidpower.client.renderer.LayerMaidBladeRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityMaidRenderer.class)
public abstract class MixinEntityMaidRenderer extends MobRenderer<Mob, BedrockModel<Mob>> {
    private MixinEntityMaidRenderer(EntityRendererProvider.Context pContext, BedrockModel<Mob> pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)V", at = @At("RETURN"))
    private void injectInit(EntityRendererProvider.Context manager, CallbackInfo ci) {
        this.addLayer(new LayerMaidBladeRenderer(this));
    }
}
