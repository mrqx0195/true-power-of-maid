package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidTickEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.ModAttributes;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.slashblade.maidpower.entity.ai.MaidMirageBladeBehavior;
import net.mrqx.slashblade.maidpower.entity.ai.MaidSlashBladeMove;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeAttackUtils;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeMovementUtils;
import net.mrqx.truepower.util.JustSlashArtManager;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class MaidTickHandler {
    @SubscribeEvent
    public static void onMaidTickEvent(MaidTickEvent event) {
        EntityMaid maid = event.getMaid();
        maid.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            maidTickCounter(maid);
            maidBonus(maid);
            maid.getMainHandItem().inventoryTick(maid.level(), maid, 0, true);
            boolean truePower = SlashBladeMaidBauble.TruePower.checkBauble(maid);
            if (truePower) {
                maid.getCapability(ConcentrationRankCapabilityProvider.RANK_POINT).ifPresent(rank -> rank.setRawRankPoint(Math.max(rank.getRankPoint(maid.level().getGameTime()), 2300)));
            }
            ComboState current = ComboStateRegistry.REGISTRY.get().getValue(state.resolvCurrentComboState(maid));
            CompoundTag data = maid.getPersistentData();
            boolean canTrick = SlashBladeMaidBauble.Trick.checkBauble(maid) && data.getInt(MaidSlashBladeMove.TRICK_COOL_DOWN) <= 0;
            Entity target = state.getTargetEntity(maid.level());
            boolean canAirTrick = canTrick && SlashBladeMaidBauble.MirageBlade.checkBauble(maid);
            if (target != null) {
                canAirTrick &= target.isAlive();
                if (target instanceof LivingEntity living) {
                    canAirTrick &= living.getHealth() > 0;
                    maid.setTarget(living);
                }
            }
            if (current != null && !current.equals(ComboStateRegistry.NONE.get())) {
                current.tickAction(maid);
            }
            if (canTrick && !canAirTrick) {
                MaidSlashBladeMovementUtils.TRICK_DOWN_CHECK.accept(maid);
            }
            if (MaidGuardHandler.isGuarding(maid)) {
                if (data.getInt(MaidGuardHandler.GUARD_ESCAPE_COUNTER) <= 0) {
                    MaidSlashBladeMovementUtils.TRY_TRICK_DODGE.accept(maid);
                    MaidGuardHandler.guardRefreshMaidTickCounter(maid);
                }
                state.setFallDecreaseRate(1);
            }
            if (data.getBoolean(MaidGuardHandler.IS_PRE_ESCAPING) && data.getInt(MaidGuardHandler.PRE_ESCAPE_COUNTER) <= 0) {
                MaidSlashBladeMovementUtils.TRY_TRICK_DODGE.accept(maid);
                data.putBoolean(MaidGuardHandler.IS_PRE_ESCAPING, false);
            }
            if (SlashBladeMaidBauble.Health.checkBauble(maid) && maid.getHealth() < maid.getMaxHealth()) {
                int favorabilityLevel = maid.getFavorabilityManager().getLevel();
                int soulCost = Math.max(0, 4 - favorabilityLevel);
                if (state.getProudSoulCount() >= soulCost) {
                    state.setProudSoulCount(state.getProudSoulCount() - soulCost);
                    if (truePower) {
                        maid.setHealth(Math.max(maid.getHealth() + favorabilityLevel, maid.getMaxHealth()));
                    } else {
                        maid.heal(favorabilityLevel * 0.5F);
                    }
                }
            }
            if (state.isBroken()) {
                if (SlashBladeMaidBauble.Exp.checkBauble(maid)) {
                    int favorabilityLevel = Math.max(1, maid.getFavorabilityManager().getLevel() + 1);
                    int expCost = Math.max(0, 4 - favorabilityLevel);
                    if (maid.getExperience() >= expCost) {
                        maid.setExperience(maid.getExperience() - expCost);
                        state.setDamage(state.getDamage() - favorabilityLevel);
                        if (state.getDamage() <= 0) {
                            state.setBroken(false);
                        }
                    }
                }
            }
            if (truePower && data.getInt(MaidSlashBladeAttackUtils.SUPER_JUDGEMENT_CUT_COUNTER_KEY) <= 0) {
                Map.Entry<Integer, ResourceLocation> currentLoc = state.resolvCurrentComboStateTicks(maid);
                ResourceLocation csLoc = state.getSlashArts().doArts(SlashArts.ArtsType.Super, maid);
                if (csLoc != ComboStateRegistry.NONE.getId() && !currentLoc.getValue().equals(csLoc)) {
                    state.updateComboSeq(maid, csLoc);
                    data.putInt(MaidSlashBladeAttackUtils.SUPER_JUDGEMENT_CUT_COUNTER_KEY, 2400);
                }
            }
        });
    }

    public static void maidTickCounter(EntityMaid maid) {
        CompoundTag data = maid.getPersistentData();
        int truePower = SlashBladeMaidBauble.TruePower.checkBauble(maid) ? 2 : 1;
        int favorabilityLevel = maid.getFavorabilityManager().getLevel() + 1;

        data.putInt(MaidMirageBladeBehavior.HEAVY_RAIN_SWORD_COUNTER_KEY, Math.max(0, data.getInt(MaidMirageBladeBehavior.HEAVY_RAIN_SWORD_COUNTER_KEY) - favorabilityLevel * truePower));
        data.putInt(MaidMirageBladeBehavior.BLISTERING_SWORD_COUNTER_KEY, Math.max(0, data.getInt(MaidMirageBladeBehavior.BLISTERING_SWORD_COUNTER_KEY) - favorabilityLevel * truePower));
        data.putInt(MaidMirageBladeBehavior.SPIRAL_SWORD_COUNTER_KEY, Math.max(0, data.getInt(MaidMirageBladeBehavior.SPIRAL_SWORD_COUNTER_KEY) - favorabilityLevel * truePower));
        data.putInt(MaidMirageBladeBehavior.STORM_SWORD_COUNTER_KEY, Math.max(0, data.getInt(MaidMirageBladeBehavior.STORM_SWORD_COUNTER_KEY) - favorabilityLevel * truePower));
        data.putInt(MaidMirageBladeBehavior.BASE_SUMMONED_SWORD_COUNTER_KEY, Math.max(0, data.getInt(MaidMirageBladeBehavior.BASE_SUMMONED_SWORD_COUNTER_KEY) - favorabilityLevel * truePower));
        data.putInt(MaidSlashBladeAttackUtils.VOID_SLASH_COUNTER_KEY, Math.max(0, data.getInt(MaidSlashBladeAttackUtils.VOID_SLASH_COUNTER_KEY) - favorabilityLevel * truePower));
        data.putInt(MaidSlashBladeAttackUtils.SUPER_JUDGEMENT_CUT_COUNTER_KEY, Math.max(0, data.getInt(MaidSlashBladeAttackUtils.SUPER_JUDGEMENT_CUT_COUNTER_KEY) - favorabilityLevel * truePower));

        data.putInt(MaidSlashBladeMove.TRICK_COOL_DOWN, Math.max(0, data.getInt(MaidSlashBladeMove.TRICK_COOL_DOWN) - truePower));
        data.putInt(MaidGuardHandler.GUARD_DAMAGE_COUNTER, Math.max(0, data.getInt(MaidGuardHandler.GUARD_DAMAGE_COUNTER) - truePower));
        data.putInt(MaidGuardHandler.GUARD_ESCAPE_COUNTER, Math.max(0, data.getInt(MaidGuardHandler.GUARD_ESCAPE_COUNTER) - truePower));
        data.putInt(MaidGuardHandler.PRE_ESCAPE_COUNTER, Math.max(0, data.getInt(MaidGuardHandler.PRE_ESCAPE_COUNTER) - truePower));
        data.putInt(MaidGuardHandler.GUARD_COOL_DOWN, Math.max(0, data.getInt(MaidGuardHandler.GUARD_COOL_DOWN) - truePower));

        long cooldown = JustSlashArtManager.getJustCooldown(maid);
        if (cooldown > 0) {
            cooldown -= truePower;
            JustSlashArtManager.setJustCooldown(maid, cooldown);
            if (cooldown == 0) {
                JustSlashArtManager.resetJustCount(maid);
            }
        }
    }

    public static void maidBonus(EntityMaid maid) {
        double radius = TargetSelector.getResolvedReach(maid) * 2;
        radius *= radius;
        if (SlashBladeMaidBauble.MirageBlade.checkBauble(maid) || SlashBladeMaidBauble.JudgementCut.checkBauble(maid)) {
            radius *= 3;
        }
        AttributeModifier followRangeBonus = new AttributeModifier(UUID.fromString("5a138a12-3f1a-40ab-98cf-9532bd9881ce"),
                "Maid SlashBlade Bonus", radius, AttributeModifier.Operation.ADDITION);
        AttributeInstance followRangeAttributeInstance = maid.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRangeAttributeInstance == null) {
            return;
        }
        followRangeAttributeInstance.removeModifier(followRangeBonus);
        followRangeAttributeInstance.addPermanentModifier(followRangeBonus);

        IConcentrationRank.ConcentrationRanks rankBonus = maid
                .getCapability(ConcentrationRankCapabilityProvider.RANK_POINT)
                .map(rp -> rp.getRank(maid.getCommandSenderWorld().getGameTime()))
                .orElse(IConcentrationRank.ConcentrationRanks.NONE);
        float rankDamageBonus = rankBonus.level / 2.0f;
        if (IConcentrationRank.ConcentrationRanks.S.level <= rankBonus.level) {
            int refine = maid.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).map(ISlashBladeState::getRefine).orElse(0);
            int level = (int) Math.floor((double) maid.getExperience() / 120);
            rankDamageBonus = (float) Math.max(rankDamageBonus, Math.min(level, refine) * SlashBladeConfig.REFINE_DAMAGE_MULTIPLIER.get());
        }

        AttributeModifier slashbladeDamageBonus = new AttributeModifier(UUID.fromString("5e800b9e-f7ba-4f48-a018-2acfe422dce6"),
                "Maid SlashBlade Bonus", rankDamageBonus, AttributeModifier.Operation.ADDITION);
        AttributeInstance slashbladeDamageAttributeInstance = maid.getAttribute(ModAttributes.SLASHBLADE_DAMAGE.get());
        if (slashbladeDamageAttributeInstance == null) {
            return;
        }
        slashbladeDamageAttributeInstance.removeModifier(slashbladeDamageBonus);
        slashbladeDamageAttributeInstance.addPermanentModifier(slashbladeDamageBonus);
    }
}
