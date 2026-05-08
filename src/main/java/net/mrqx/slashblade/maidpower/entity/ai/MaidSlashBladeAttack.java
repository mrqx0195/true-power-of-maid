package net.mrqx.slashblade.maidpower.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.OptionalBox;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.mrqx.sbr_core.utils.SlashBladeAttackUtils;
import net.mrqx.slashblade.maidpower.event.MaidGuardHandler;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;

public class MaidSlashBladeAttack {
    public static OneShot<Mob> create() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.LOOK_TARGET),
                instance.present(MemoryModuleType.ATTACK_TARGET),
                instance.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
            ).apply(instance, (lookTargetAccessor, attackTargetAccessor, visibleEntitiesAccessor) ->
                (level, mob, time) -> handleAttack(instance, lookTargetAccessor, attackTargetAccessor, visibleEntitiesAccessor, mob)
            )
        );
    }
    
    private static boolean handleAttack(
        BehaviorBuilder.Instance<Mob> instance,
        MemoryAccessor<OptionalBox.Mu, PositionTracker> lookTargetAccessor,
        MemoryAccessor<IdF.Mu, LivingEntity> attackTargetAccessor,
        MemoryAccessor<IdF.Mu, NearestVisibleLivingEntities> visibleEntitiesAccessor,
        Mob mob) {
        LivingEntity target = instance.get(attackTargetAccessor);
        if (!(mob instanceof EntityMaid maid)) {
            return false;
        }
        if (maid.level().getGameTime() % 4 != 0) {
            return false;
        }
        if (!SlashBladeAttackUtils.isHoldingSlashBlade(maid)) {
            return false;
        }
        NearestVisibleLivingEntities visibleEntities = instance.get(visibleEntitiesAccessor);
        boolean truePower = SlashBladeMaidBauble.TruePower.checkBauble(maid);
        if (!visibleEntities.contains(target)) {
            return false;
        }
        if (MaidGuardHandler.isGuarding(maid) && !truePower) {
            return false;
        }
        
        maid.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            state.setTargetEntityId(maid.getTarget());
            boolean canRapidSlash = SlashBladeMaidBauble.RapidSlash.checkBauble(maid);
            boolean preferAirAttack = SlashBladeMaidBauble.AirCombo.checkBauble(maid);
            boolean canVoidSlash = SlashBladeMaidBauble.VoidSlash.checkBauble(maid);
            boolean isJust = SlashBladeMaidBauble.JustJudgementCut.checkBauble(maid);
            if (maid.distanceTo(target) <= TargetSelector.getResolvedReach(maid)) {
                lookTargetAccessor.set(new EntityTracker(target, true));
                if (SlashBladeMaidBauble.JudgementCut.checkBauble(maid) && SlashBladeAttackUtils.trySlashArts(maid, state, target, isJust, truePower)) {
                    return;
                }
                SlashBladeAttackUtils.normalSlashBladeAttack(maid, state, target, canRapidSlash, preferAirAttack, canVoidSlash, truePower);
            } else {
                if (!maid.onGround() && maid.getY() - target.getY() > 5) {
                    SlashBladeAttackUtils.tryAerialCleave(maid, state);
                }
                if (canRapidSlash && SlashBladeAttackUtils.canInterruptCombo(maid, truePower) && !maid.isMaidInSittingPose()) {
                    lookTargetAccessor.set(new EntityTracker(target, true));
                    SlashBladeAttackUtils.rapidSlashAttack(maid, state, target);
                } else if (SlashBladeMaidBauble.JudgementCut.checkBauble(maid)) {
                    lookTargetAccessor.set(new EntityTracker(target, true));
                    SlashBladeAttackUtils.trySlashArts(maid, state, target, isJust, truePower);
                }
            }
        });
        return true;
    }
}
