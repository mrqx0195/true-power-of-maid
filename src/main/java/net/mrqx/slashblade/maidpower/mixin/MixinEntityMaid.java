package net.mrqx.slashblade.maidpower.mixin;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMaid.class)
public abstract class MixinEntityMaid extends TamableAnimal implements CrossbowAttackMob, IMaid {
    private MixinEntityMaid(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "getMeleeAttackRangeSqr(Lnet/minecraft/world/entity/LivingEntity;)D", at = @At("HEAD"), cancellable = true)
    private void injectGetMeleeAttackRangeSqr(LivingEntity entity, CallbackInfoReturnable<Double> cir) {
        this.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            double reach = TargetSelector.getResolvedReach(this);
            cir.setReturnValue(reach * reach);
        });
    }
}
