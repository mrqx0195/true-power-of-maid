package net.mrqx.slashblade.maidpower.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.mrqx.sbr_core.utils.SlashBladeAttackUtils;
import net.mrqx.truepower.registry.TruePowerComboStateRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SlashBladeAttackUtils.class)
public class MixinSlashBladeAttackUtils {
    @WrapOperation(method = "voidSlash(Lnet/minecraft/world/entity/LivingEntity;Lmods/flammpfeil/slashblade/capability/slashblade/ISlashBladeState;Lnet/minecraft/world/entity/LivingEntity;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lmods/flammpfeil/slashblade/capability/slashblade/ISlashBladeState;updateComboSeq(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/resources/ResourceLocation;)V",
            remap = false
        ),
        remap = false
    )
    private static void injectVoidSlash(ISlashBladeState instance, LivingEntity entity, ResourceLocation loc, Operation<Void> original) {
        if (entity instanceof EntityMaid) {
            loc = TruePowerComboStateRegistry.VOID_SLASH.getId();
        }
        original.call(instance, entity, loc);
    }
}
