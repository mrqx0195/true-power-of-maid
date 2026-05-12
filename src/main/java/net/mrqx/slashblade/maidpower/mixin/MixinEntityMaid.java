package net.mrqx.slashblade.maidpower.mixin;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.level.Level;
import net.mrqx.slashblade.maidpower.entity.ISlashBladeMaid;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMaid.class)
public abstract class MixinEntityMaid extends TamableAnimal implements CrossbowAttackMob, IMaid, ISlashBladeMaid {
    private MixinEntityMaid(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    
    @Inject(method = "isWithinMeleeAttackRange(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void injectGetMeleeAttackRangeSqr(LivingEntity target, CallbackInfoReturnable<Double> cir) {
        BladeStateAccess.of(this.getMainHandItem()).ifPresent(state -> {
            double reach = TargetSelector.getResolvedReach(this);
            if (SlashBladeMaidBauble.UnlimitedBladeWorks.checkBauble((EntityMaid) (Object) this)) {
                reach *= 2;
            }
            cir.setReturnValue(reach * reach);
        });
    }
}
