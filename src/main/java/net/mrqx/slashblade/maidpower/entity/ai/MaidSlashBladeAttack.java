package net.mrqx.slashblade.maidpower.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.OptionalBox;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
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
import net.mrqx.slashblade.maidpower.event.MaidGuardHandler;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeAttackUtils;
import net.mrqx.truepower.registry.TruePowerComboStateRegistry;

import java.util.Set;

public class MaidSlashBladeAttack {
    /**
     * 可以后接次元斩的 Combo
     */
    public static final Set<ComboState> CHARGE_COMBO = Set.of(
            ComboStateRegistry.COMBO_C.get(),
            ComboStateRegistry.COMBO_A4.get(),
            ComboStateRegistry.COMBO_A5.get(),
            ComboStateRegistry.COMBO_B7.get(),
            ComboStateRegistry.COMBO_B_END2.get(),
            ComboStateRegistry.AERIAL_RAVE_A3.get(),
            ComboStateRegistry.AERIAL_RAVE_B4.get(),
            ComboStateRegistry.UPPERSLASH.get(),
            ComboStateRegistry.UPPERSLASH_JUMP.get(),
            ComboStateRegistry.AERIAL_CLEAVE_LANDING.get(),
            ComboStateRegistry.RAPID_SLASH_END.get(),
            ComboStateRegistry.RISING_STAR.get(),
            TruePowerComboStateRegistry.VOID_SLASH_SHEATH.get()
    );

    /**
     * 【女仆之荣耀 - 真正的力量】生效时，可以打断发完美次元斩的 Combo
     */
    public static final Set<ComboState> TRUE_POWER_CHARGE_COMBO = Set.of(
            ComboStateRegistry.JUDGEMENT_CUT_SLASH.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH_AIR.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST2.get(),
            ComboStateRegistry.JUDGEMENT_CUT_END.get()

    );

    /**
     * 可以后接完美次元斩的 Combo
     */
    public static final Set<ComboState> QUICK_CHARGE_COMBO = Set.of(
            ComboStateRegistry.COMBO_A1_END.get(),
            ComboStateRegistry.COMBO_A2_END.get(),
            ComboStateRegistry.COMBO_C_END.get(),
            ComboStateRegistry.COMBO_A3_END3.get(),
            ComboStateRegistry.COMBO_A4_END.get(),
            ComboStateRegistry.COMBO_A4_EX_END2.get(),
            ComboStateRegistry.COMBO_A5_END.get(),
            ComboStateRegistry.COMBO_B7_END3.get(),
            ComboStateRegistry.COMBO_B_END3.get(),
            ComboStateRegistry.AERIAL_RAVE_A1_END.get(),
            ComboStateRegistry.AERIAL_RAVE_A2_END2.get(),
            ComboStateRegistry.AERIAL_RAVE_A3_END.get(),
            ComboStateRegistry.AERIAL_RAVE_B3_END.get(),
            ComboStateRegistry.AERIAL_RAVE_B4_END.get(),
            ComboStateRegistry.UPPERSLASH_END.get(),
            ComboStateRegistry.UPPERSLASH_JUMP_END.get(),
            ComboStateRegistry.AERIAL_CLEAVE_END.get(),
            ComboStateRegistry.RAPID_SLASH_QUICK.get(),
            ComboStateRegistry.RAPID_SLASH_END2.get(),
            ComboStateRegistry.RISING_STAR_END.get(),
            TruePowerComboStateRegistry.VOID_SLASH_SHEATH.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SHEATH.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SHEATH_AIR.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SHEATH_JUST.get()
    );

    /**
     * 不应被打断的 Combo
     */
    public static final Set<ComboState> UNINTERRUPTIBLE_COMBO = Set.of(
            ComboStateRegistry.COMBO_A1.get(),
            ComboStateRegistry.COMBO_A2.get(),
            ComboStateRegistry.COMBO_C.get(),
            ComboStateRegistry.COMBO_A3.get(),
            ComboStateRegistry.COMBO_A4.get(),
            ComboStateRegistry.COMBO_A4_EX.get(),
            ComboStateRegistry.COMBO_A5.get(),
            ComboStateRegistry.COMBO_B1.get(),
            ComboStateRegistry.COMBO_B7.get(),
            ComboStateRegistry.COMBO_B_END2.get(),
            ComboStateRegistry.AERIAL_RAVE_A1.get(),
            ComboStateRegistry.AERIAL_RAVE_A2.get(),
            ComboStateRegistry.AERIAL_RAVE_A3.get(),
            ComboStateRegistry.AERIAL_RAVE_B3.get(),
            ComboStateRegistry.AERIAL_RAVE_B4.get(),
            ComboStateRegistry.UPPERSLASH.get(),
            ComboStateRegistry.UPPERSLASH_JUMP.get(),
            ComboStateRegistry.AERIAL_CLEAVE.get(),
            ComboStateRegistry.RISING_STAR.get(),
            ComboStateRegistry.JUDGEMENT_CUT.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH_AIR.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST2.get(),
            ComboStateRegistry.JUDGEMENT_CUT_END.get(),
            TruePowerComboStateRegistry.VOID_SLASH.get()
    );

    /**
     * 【女仆之荣耀 - 真正的力量】生效时，不应被打断的 Combo
     */
    public static final Set<ComboState> TRUE_POWER_UNINTERRUPTIBLE_COMBO = Set.of(
            ComboStateRegistry.COMBO_C.get(),
            ComboStateRegistry.COMBO_A4_EX.get(),
            ComboStateRegistry.COMBO_A5.get(),
            ComboStateRegistry.COMBO_B7.get(),
            ComboStateRegistry.COMBO_B_END2.get(),
            ComboStateRegistry.AERIAL_RAVE_B3.get(),
            ComboStateRegistry.UPPERSLASH.get(),
            ComboStateRegistry.AERIAL_CLEAVE.get(),
            ComboStateRegistry.RISING_STAR.get(),
            ComboStateRegistry.JUDGEMENT_CUT.get(),
            ComboStateRegistry.JUDGEMENT_CUT_END.get(),
            TruePowerComboStateRegistry.VOID_SLASH.get()
    );

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
        if (!MaidSlashBladeAttackUtils.isHoldingSlashBlade(maid)) {
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
            if (maid.distanceTo(target) <= TargetSelector.getResolvedReach(maid)) {
                lookTargetAccessor.set(new EntityTracker(target, true));
                if (MaidSlashBladeAttackUtils.TRY_JUDGEMENT_CUT.apply(maid, state, target)) {
                    return;
                }
                MaidSlashBladeAttackUtils.NORMAL_SLASHBLADE_ATTACK.accept(maid, state, target);
            } else {
                if (!maid.onGround() && maid.getY() - target.getY() > 5) {
                    MaidSlashBladeAttackUtils.TRY_AERIAL_CLEAVE.apply(maid, state);
                }
                if (SlashBladeMaidBauble.RapidSlash.checkBauble(maid) && MaidSlashBladeAttackUtils.canInterruptCombo(maid)) {
                    lookTargetAccessor.set(new EntityTracker(target, true));
                    MaidSlashBladeAttackUtils.RAPID_SLASH_ATTACK.accept(maid, state, target);
                } else if (SlashBladeMaidBauble.JudgementCut.checkBauble(maid)) {
                    lookTargetAccessor.set(new EntityTracker(target, true));
                    MaidSlashBladeAttackUtils.TRY_JUDGEMENT_CUT.apply(maid, state, target);
                }
            }
        });
        return true;
    }
}
