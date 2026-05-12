package net.mrqx.slashblade.maidpower.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.util.AttackHelper;
import net.minecraft.world.entity.LivingEntity;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AttackHelper.class)
public abstract class MixinAttackHelper {
    @Inject(method = "getRankBonus(Lnet/minecraft/world/entity/LivingEntity;)F", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectGetRankBonus(LivingEntity attacker, CallbackInfoReturnable<Float> cir) {
        if (attacker instanceof EntityMaid maid) {
            boolean hasTruePower = SlashBladeMaidBauble.TruePower.checkBauble(maid);
            IConcentrationRank.ConcentrationRanks rankBonus = maid.getData(CapabilityConcentrationRank.RANK_POINT)
                .getRank(attacker.getCommandSenderWorld().getGameTime());
            double rankDamageBonus = rankBonus.level / 2.0;
            if (IConcentrationRank.ConcentrationRanks.S.level <= rankBonus.level) {
                int refine = BladeStateAccess.of(maid.getMainHandItem())
                    .map(ISlashBladeState::getRefine).orElse(0);
                int expLevel = (int) Math.floor((double) maid.getExperience() / 120);
                
                rankDamageBonus = (float) Math.max(rankDamageBonus,
                    (hasTruePower ? refine : Math.min(expLevel, refine)) * SlashBladeConfig.REFINE_DAMAGE_MULTIPLIER.get());
            }
            
            cir.setReturnValue((float) rankDamageBonus);
        }
    }
}
