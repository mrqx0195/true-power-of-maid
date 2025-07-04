package net.mrqx.slashblade.maidpower.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.mrqx.slashblade.maidpower.event.MaidGuardHandler;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeAttackUtils;
import net.mrqx.truepower.registry.TruePowerComboStateRegistry;

import java.util.Set;

public class MaidSlashBladeAttack {
    public static OneShot<Mob> create() {
        return BehaviorBuilder.create((mobInstance) ->
                mobInstance.group(mobInstance.registered(MemoryModuleType.LOOK_TARGET), mobInstance.present(MemoryModuleType.ATTACK_TARGET), mobInstance.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(mobInstance, (trackerMemoryAccessor, targetMemoryAccessor, livingEntitiesMemoryAccessor) -> (serverLevel, mob, l) -> {
                    LivingEntity target = mobInstance.get(targetMemoryAccessor);
                    if (mob instanceof EntityMaid maid
                        && maid.level().getGameTime() % 4 == 0
                        && MaidSlashBladeAttackUtils.isHoldingSlashBlade(maid)
                        && mobInstance.get(livingEntitiesMemoryAccessor).contains(target)
                        && !MaidGuardHandler.isGuarding(maid)) {
                        maid.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
                            state.setTargetEntityId(maid.getTarget());
                            if (maid.distanceTo(target) <= TargetSelector.getResolvedReach(maid)) {
                                trackerMemoryAccessor.set(new EntityTracker(target, true));
                                if (MaidSlashBladeAttackUtils.TRY_JUDGEMENT_CUT.apply(maid, state, target)) {
                                    return;
                                }
                                MaidSlashBladeAttackUtils.NORMAL_SLASHBLADE_ATTACK.accept(maid, state, target);
                            } else {
                                if (!maid.onGround() && maid.getY() - target.getY() > 5) {
                                    MaidSlashBladeAttackUtils.TRY_AERIAL_CLEAVE.apply(maid, state);
                                }
                                if (SlashBladeMaidBauble.RapidSlash.checkBauble(maid)) {
                                    trackerMemoryAccessor.set(new EntityTracker(target, true));
                                    MaidSlashBladeAttackUtils.RAPID_SLASH_ATTACK.accept(maid, state, target);
                                } else if (SlashBladeMaidBauble.JudgementCut.checkBauble(maid)) {
                                    trackerMemoryAccessor.set(new EntityTracker(target, true));
                                    MaidSlashBladeAttackUtils.TRY_JUDGEMENT_CUT.apply(maid, state, target);
                                }
                            }
                        });
                        return true;
                    }
                    return false;
                }));
    }

    public static final Set<ComboState> CHARGE_COMBO;
    public static final Set<ComboState> QUICK_CHARGE_COMBO;

    static {
        CHARGE_COMBO = Set.of(
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
        QUICK_CHARGE_COMBO = Set.of(
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
                TruePowerComboStateRegistry.VOID_SLASH_SHEATH.get()
        );
    }
}
