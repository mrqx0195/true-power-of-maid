package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.ability.Untouchable;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.sbr_core.utils.MrqxSlayerStyleArts;
import net.mrqx.slashblade.maidpower.entity.ai.MaidMirageBladeBehavior;
import net.mrqx.slashblade.maidpower.entity.ai.MaidSlashBladeMove;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeAttackUtils;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeMovementUtils;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber
public class MaidGuardHandler {
    public static final String GUARD_DAMAGE_COUNTER = "truePowerOfMaid.guardDamageCounter";
    public static final String GUARD_TOTAL_DAMAGE_COUNTER = "truePowerOfMaid.guardTotalDamageCounter";
    public static final String GUARD_ESCAPE_COUNTER = "truePowerOfMaid.guardEscapeCounter";
    public static final String PRE_ESCAPE_COUNTER = "truePowerOfMaid.preEscapeCounter";
    public static final String GUARD_COOL_DOWN = "truePowerOfMaid.guardCooldown";
    public static final String IS_PRE_ESCAPING = "truePowerOfMaid.isPreEscaping";
    public static final String GUARD_DAMAGE = "truePowerOfMaid.guardDamage";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingAttackEvent(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof EntityMaid maid)) {
            return;
        }
        maid.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE)
                .ifPresent(state -> handleGuardAttack(event, maid, state));
    }

    private static void handleGuardAttack(LivingAttackEvent event, EntityMaid maid, ISlashBladeState state) {
        LivingEntity attacker = null;
        if (event.getSource().getEntity() instanceof LivingEntity living) {
            attacker = living;
        } else if (event.getSource().getEntity() instanceof OwnableEntity ownable) {
            attacker = ownable.getOwner();
        }
        if (attacker != null) {
            trickToTarget(maid, attacker);
        }
        if (!SlashBladeMaidBauble.Guard.checkBauble(maid)) {
            return;
        }
        boolean hasTruePower = SlashBladeMaidBauble.TruePower.checkBauble(maid);
        CompoundTag data = maid.getPersistentData();
        if (!isGuarding(maid)) {
            data.putInt(GUARD_DAMAGE_COUNTER, data.getInt(GUARD_DAMAGE_COUNTER) + 150);
            data.putFloat(GUARD_TOTAL_DAMAGE_COUNTER, data.getFloat(GUARD_TOTAL_DAMAGE_COUNTER) + event.getAmount());
            boolean shouldGuard = data.getInt(GUARD_DAMAGE_COUNTER) > 500
                    || data.getFloat(GUARD_TOTAL_DAMAGE_COUNTER) > maid.getMaxHealth() * 0.2F;
            if (shouldGuard && data.getInt(GUARD_COOL_DOWN) <= 0) {
                data.putInt(GUARD_DAMAGE_COUNTER, 0);
                data.putFloat(GUARD_TOTAL_DAMAGE_COUNTER, 0);
                data.putInt(GUARD_ESCAPE_COUNTER, 100);
                data.putFloat(GUARD_DAMAGE, maid.getMaxHealth() * (hasTruePower ? 1 : 0.2F));
            }
        } else {
            boolean isProjectile = event.getSource().is(DamageTypeTags.IS_PROJECTILE)
                    || event.getSource().getDirectEntity() instanceof Projectile;
            if (!isProjectile) {
                int soulSpeedLevel = maid.getMainHandItem().getEnchantmentLevel(Enchantments.SOUL_SPEED);
                int justAcceptancePeriod = 3 + soulSpeedLevel;
                if (hasTruePower && maid.level().getGameTime() - data.getLong(DoSlashHandler.LAST_DO_SLASH_TIME) < justAcceptancePeriod) {
                    Untouchable.setUntouchable(maid, 10);
                } else {
                    float guardDamage = data.getFloat(GUARD_DAMAGE);
                    if (guardDamage >= event.getAmount()) {
                        data.putFloat(GUARD_DAMAGE, guardDamage - event.getAmount());
                        state.updateComboSeq(maid, ComboStateRegistry.COMBO_A1_END2.getId());
                    } else {
                        data.putFloat(GUARD_DAMAGE, 0);
                        data.putInt(GUARD_COOL_DOWN, 600);
                        state.updateComboSeq(maid, ComboStateRegistry.COMBO_A1.getId());
                        data.putInt(PRE_ESCAPE_COUNTER, 5);
                        data.putBoolean(IS_PRE_ESCAPING, true);
                    }
                    if (SlashBladeMaidBauble.Health.checkBauble(maid)) {
                        maid.heal((float) Math.min(maid.getMaxHealth() * 0.1, event.getAmount() * 0.1));
                    }
                }
            }
            event.setCanceled(true);
        }
    }

    public static void trickToTarget(@NotNull EntityMaid maid, @NotNull LivingEntity target) {
        if (maid.level() instanceof ServerLevel serverLevel) {
            maid.refreshBrain(serverLevel);
        }
        maid.setTarget(target);
        CompoundTag data = maid.getPersistentData();
        boolean canTrick = MaidSlashBladeMovementUtils.canTrick(maid);
        boolean canAirTrick = canTrick && SlashBladeMaidBauble.MirageBlade.checkBauble(maid);
        if (canAirTrick) {
            if (!MrqxSlayerStyleArts.AIR_TRICK.apply(maid, true)) {
                MaidSlashBladeMovementUtils.TRY_TRICK_TO_TARGET.accept(maid, target);
            }
            data.putInt(MaidSlashBladeMove.TRICK_COOL_DOWN, 60);
            maid.level().broadcastEntityEvent(maid, (byte) 46);
        } else if (canTrick) {
            MaidSlashBladeMovementUtils.TRY_TRICK_TO_TARGET.accept(maid, target);
            data.putInt(MaidSlashBladeMove.TRICK_COOL_DOWN, 60);
            maid.level().broadcastEntityEvent(maid, (byte) 46);
        }
    }

    public static boolean isGuarding(EntityMaid maid) {
        return maid.getPersistentData().getFloat(GUARD_DAMAGE) > 0
                && SlashBladeMaidBauble.Guard.checkBauble(maid);
    }

    public static void guardRefreshMaidTickCounter(EntityMaid maid) {
        CompoundTag data = maid.getPersistentData();
        data.putInt(MaidMirageBladeBehavior.HEAVY_RAIN_SWORD_COUNTER_KEY, 0);
        data.putInt(MaidMirageBladeBehavior.BLISTERING_SWORD_COUNTER_KEY, 0);
        data.putInt(MaidMirageBladeBehavior.SPIRAL_SWORD_COUNTER_KEY, 0);
        data.putInt(MaidMirageBladeBehavior.STORM_SWORD_COUNTER_KEY, 0);
        data.putInt(MaidMirageBladeBehavior.BASE_SUMMONED_SWORD_COUNTER_KEY, 0);
        data.putInt(MaidSlashBladeAttackUtils.VOID_SLASH_COUNTER_KEY, 0);
        data.putInt(MaidSlashBladeAttackUtils.SUPER_JUDGEMENT_CUT_COUNTER_KEY, 0);

        data.putInt(MaidSlashBladeMove.TRICK_COOL_DOWN, 0);

        data.putInt(GUARD_DAMAGE, 0);
        data.putInt(GUARD_DAMAGE_COUNTER, 0);
        data.putInt(GUARD_ESCAPE_COUNTER, 0);
        data.putInt(PRE_ESCAPE_COUNTER, 0);
        data.putInt(GUARD_COOL_DOWN, 0);
    }
}
