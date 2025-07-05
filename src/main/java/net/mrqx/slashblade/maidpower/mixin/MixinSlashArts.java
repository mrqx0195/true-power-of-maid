package net.mrqx.slashblade.maidpower.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.truepower.util.JustSlashArtManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlashArts.class)
public class MixinSlashArts {
    @Inject(method = "doArts(Lmods/flammpfeil/slashblade/slasharts/SlashArts$ArtsType;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/slasharts/SlashArts;getComboStateJust(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/resources/ResourceLocation;"),
            cancellable = true, remap = false)
    private void injectDoArts(SlashArts.ArtsType type, LivingEntity user, CallbackInfoReturnable<ResourceLocation> cir) {
        if (type == SlashArts.ArtsType.Jackpot && user instanceof EntityMaid maid) {
            int count = JustSlashArtManager.addJustCount(user);
            int maxCount = SlashBladeMaidBauble.JustJudgementCut.checkBauble(maid) ? (SlashBladeMaidBauble.TruePower.checkBauble(maid) ? 8 : 6) : 2;
            if (count > maxCount || JustSlashArtManager.getJustCooldown(user) > 0) {
                JustSlashArtManager.setJustCooldown(user, 240);
                cir.setReturnValue(ComboStateRegistry.NONE.getId());
            }
        }
    }
}
