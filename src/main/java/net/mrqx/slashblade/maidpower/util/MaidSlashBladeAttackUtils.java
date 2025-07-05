package net.mrqx.slashblade.maidpower.util;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.capability.inputstate.InputStateCapabilityProvider;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import mods.flammpfeil.slashblade.util.AttackManager;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.mrqx.slashblade.maidpower.entity.ai.MaidSlashBladeAttack;
import net.mrqx.slashblade.maidpower.event.ChargeActionHandler;
import net.mrqx.slashblade.maidpower.event.MaidProgressComboEvent;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.truepower.registry.TruePowerComboStateRegistry;
import net.mrqx.truepower.util.JustSlashArtManager;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class MaidSlashBladeAttackUtils {
    public static final BiFunction<EntityMaid, ISlashBladeState, Boolean> TRY_AERIAL_CLEAVE = (maid, state) -> {
        if (maid.onGround() || !SlashBladeMaidBauble.ComboC.checkBauble(maid)) {
            return false;
        }
        state.updateComboSeq(maid, ComboStateRegistry.AERIAL_CLEAVE.getId());
        return true;
    };

    public static final BiConsumer<EntityMaid, ISlashBladeState> GROUND_ATTACK = (maid, state) -> {
        if (maid.onGround()) {
            state.updateComboSeq(maid, ComboStateRegistry.COMBO_A1.getId());
        } else {
            TRY_AERIAL_CLEAVE.apply(maid, state);
        }
    };

    public static final BiConsumer<EntityMaid, ISlashBladeState> AIR_ATTACK = (maid, state) -> {
        if (!state.resolvCurrentComboState(maid).equals(ComboStateRegistry.UPPERSLASH.getId())) {
            if (maid.onGround()) {
                if (SlashBladeMaidBauble.RapidSlash.checkBauble(maid)) {
                    state.updateComboSeq(maid, ComboStateRegistry.RAPID_SLASH.getId());
                } else {
                    state.updateComboSeq(maid, ComboStateRegistry.UPPERSLASH.getId());
                }
            } else {
                state.updateComboSeq(maid, ComboStateRegistry.AERIAL_RAVE_A1.getId());
            }
        }
    };

    public static final TriConsumer<EntityMaid, ISlashBladeState, LivingEntity> RAPID_SLASH_ATTACK = (maid, state, target) -> {
        ResourceLocation currentLoc = state.resolvCurrentComboState(maid);
        ComboState current = ComboStateRegistry.REGISTRY.get().getValue(currentLoc);
        maid.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());
        if (current != null) {
            ResourceLocation next = current.getNext(maid);
            if (currentLoc.equals(ComboStateRegistry.NONE.getId()) || next.equals(ComboStateRegistry.NONE.getId())) {
                if (maid.onGround()) {
                    state.updateComboSeq(maid, ComboStateRegistry.RAPID_SLASH.getId());
                }
            }
        }
    };

    public static final TriConsumer<EntityMaid, ISlashBladeState, LivingEntity> JUDGEMENT_CUT = (maid, state, target) -> {
        ResourceLocation currentLoc = state.resolvCurrentComboState(maid);
        ComboState current = ComboStateRegistry.REGISTRY.get().getValue(currentLoc);
        maid.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());
        if (current != null && !ChargeActionHandler.isJudgementCut(currentLoc)) {
            state.doChargeAction(maid, SlashBladeMaidBauble.JustJudgementCut.checkBauble(maid) ? 10 : 50);
        }
    };

    public static final TriFunction<EntityMaid, ISlashBladeState, LivingEntity, Boolean> TRY_JUDGEMENT_CUT = (maid, state, target) -> {
        if (JustSlashArtManager.getJustCooldown(maid) > 0) {
            return false;
        }
        ResourceLocation currentLoc = state.resolvCurrentComboState(maid);
        ComboState current = ComboStateRegistry.REGISTRY.get().getValue(currentLoc);
        if (current != null) {
            ComboState next = ComboStateRegistry.REGISTRY.get().getValue(current.getNextOfTimeout(maid));
            boolean just = SlashBladeMaidBauble.JustJudgementCut.checkBauble(maid);
            if (just && MaidSlashBladeAttack.QUICK_CHARGE_COMBO.contains(current)) {
                MaidSlashBladeAttackUtils.JUDGEMENT_CUT.accept(maid, state, target);
                return true;
            } else if (MaidSlashBladeAttack.CHARGE_COMBO.contains(current)) {
                MaidSlashBladeAttackUtils.JUDGEMENT_CUT.accept(maid, state, target);
                return true;
            } else {
                return just && MaidSlashBladeAttack.QUICK_CHARGE_COMBO.contains(next);
            }
        }
        return false;
    };

    public static final TriConsumer<EntityMaid, ISlashBladeState, LivingEntity> VOID_SLASH = (maid, state, target) -> {
        MaidSlashBladeMovementUtils.TRY_TRICK_TO_TARGET.accept(maid, target);
        maid.getCapability(InputStateCapabilityProvider.INPUT_STATE).ifPresent(input ->
                input.getScheduler().schedule("maid_void_slash", 2, (livingEntity, queue, now) -> state.updateComboSeq(maid, TruePowerComboStateRegistry.VOID_SLASH.getId())));
    };

    public static final TriConsumer<EntityMaid, ISlashBladeState, LivingEntity> NORMAL_SLASHBLADE_ATTACK = (maid, state, target) -> {
        ResourceLocation currentLoc = state.resolvCurrentComboState(maid);
        ComboState current = ComboStateRegistry.REGISTRY.get().getValue(currentLoc);
        CompoundTag data = maid.getPersistentData();
        maid.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());
        if (current != null) {
            ResourceLocation nextLoc = current.getNext(maid);
            if (currentLoc.equals(ComboStateRegistry.NONE.getId()) || nextLoc.equals(ComboStateRegistry.NONE.getId())) {
                JustSlashArtManager.resetJustCount(maid);
                if (SlashBladeMaidBauble.VoidSlash.checkBauble(maid) && data.getInt(MaidSlashBladeAttackUtils.VOID_SLASH_COUNTER_KEY) <= 0) {
                    VOID_SLASH.accept(maid, state, target);
                    data.putInt(MaidSlashBladeAttackUtils.VOID_SLASH_COUNTER_KEY, 1000);
                } else {
                    if (SlashBladeMaidBauble.AirCombo.checkBauble(maid)) {
                        AIR_ATTACK.accept(maid, state);
                    } else {
                        GROUND_ATTACK.accept(maid, state);
                    }
                }
            } else if (current.equals(ComboStateRegistry.RAPID_SLASH.get()) && SlashBladeMaidBauble.AirCombo.checkBauble(maid)) {
                List<Entity> hits = AttackManager.areaAttack(maid, KnockBacks.toss.action, 0.44f, true, true, true);
                if (!hits.isEmpty()) {
                    state.updateComboSeq(maid, ComboStateRegistry.RISING_STAR.getId());
                    AdvancementHelper.grantCriterion(maid, AdvancementHelper.ADVANCEMENT_RISING_STAR);
                }
            } else {
                if (!nextLoc.equals(currentLoc)) {
                    MaidProgressComboEvent event = new MaidProgressComboEvent(maid, target, currentLoc, nextLoc);
                    MinecraftForge.EVENT_BUS.post(event);
                    if (!event.isCanceled()) {
                        state.progressCombo(maid);
                    }
                }
            }
        }
    };

    public static final String VOID_SLASH_COUNTER_KEY = "truePowerOfMaid.voidSlashCounter";
    public static final String SUPER_JUDGEMENT_CUT_COUNTER_KEY = "truePowerOfMaid.superJudgementCutCounter";

    public static boolean isHoldingSlashBlade(Mob pMob) {
        return pMob.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).isPresent();
    }
}
