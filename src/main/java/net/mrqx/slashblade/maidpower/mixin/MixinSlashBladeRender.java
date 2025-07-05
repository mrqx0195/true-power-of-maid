package net.mrqx.slashblade.maidpower.mixin;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.compat.slashblade.SlashBladeRender;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.ILocationModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlashBladeRender.class)
public abstract class MixinSlashBladeRender {
    @Inject(method = "renderMaidMainhandSlashBlade(Lnet/minecraft/world/entity/Mob;Lcom/github/tartaricacid/touhoulittlemaid/client/model/bedrock/BedrockModel;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ItemStack;F)V",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectRenderMaidMainhandSlashBlade(Mob maid, BedrockModel<Mob> model, PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, ItemStack stack, float partialTicks, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "renderMaidMainhandSlashBlade(Lnet/minecraft/world/entity/LivingEntity;Lcom/github/tartaricacid/touhoulittlemaid/geckolib3/geo/animated/ILocationModel;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ItemStack;F)V",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectRenderMaidMainhandSlashBlade(LivingEntity maid, ILocationModel model, PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, ItemStack stack, float partialTicks, CallbackInfo ci) {
        ci.cancel();
    }
}
