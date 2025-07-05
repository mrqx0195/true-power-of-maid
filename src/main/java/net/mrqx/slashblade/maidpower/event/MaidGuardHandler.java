package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.sbr_core.utils.MrqxSlayerStyleArts;
import net.mrqx.slashblade.maidpower.entity.ai.MaidMirageBladeBehavior;
import net.mrqx.slashblade.maidpower.entity.ai.MaidSlashBladeMove;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeAttackUtils;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeMovementUtils;

@Mod.EventBusSubscriber
public class MaidGuardHandler {
    public static final String GUARD_DAMAGE_COUNTER = "truePowerOfMaid.guardDamageCounter";
    public static final String GUARD_TOTAL_DAMAGE_COUNTER = "truePowerOfMaid.guardTotalDamageCounter";

    public static final String GUARD_ESCAPE_COUNTER = "truePowerOfMaid.guardEscapeCounter";
    public static final String PRE_ESCAPE_COUNTER = "truePowerOfMaid.preEscapeCounter";
    public static final String GUARD_COOL_DOWN = "truePowerOfMaid.guardCooldown";

    public static final String IS_PRE_ESCAPING = "truePowerOfMaid.isPreEscaping";

    public static final String GUARD_DAMAGE = "truePowerOfMaid.guardDamage";

    @SubscribeEvent
    public static void onLivingAttackEvent(LivingAttackEvent event) {
        if (event.getEntity() instanceof EntityMaid maid) {
            maid.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
                if (!SlashBladeMaidBauble.Guard.checkBauble(maid)) {
                    return;
                }
                if (event.getSource().getEntity() instanceof LivingEntity living) {
                    trickToTarget(maid, living);
                } else if (event.getSource().getEntity() instanceof OwnableEntity ownable) {
                    trickToTarget(maid, ownable.getOwner());
                }
                CompoundTag data = maid.getPersistentData();
                if (!isGuarding(maid)) {
                    data.putInt(GUARD_DAMAGE_COUNTER, data.getInt(GUARD_DAMAGE_COUNTER) + 150);
                    data.putFloat(GUARD_TOTAL_DAMAGE_COUNTER, data.getFloat(GUARD_TOTAL_DAMAGE_COUNTER) + event.getAmount());
                    boolean shouldGuard = data.getInt(GUARD_DAMAGE_COUNTER) > 500 || data.getFloat(GUARD_TOTAL_DAMAGE_COUNTER) > maid.getMaxHealth() * 0.2F;
                    if (shouldGuard && data.getInt(GUARD_COOL_DOWN) <= 0) {
                        data.putInt(GUARD_DAMAGE_COUNTER, 0);
                        data.putFloat(GUARD_TOTAL_DAMAGE_COUNTER, 0);
                        data.putInt(GUARD_ESCAPE_COUNTER, 100);
                        data.putFloat(GUARD_DAMAGE, maid.getMaxHealth() * 0.2F);
                    }
                } else {
                    float guardDamage = data.getFloat(GUARD_DAMAGE);
                    if (guardDamage >= event.getAmount()) {
                        data.putFloat(GUARD_DAMAGE, data.getFloat(GUARD_DAMAGE) - event.getAmount());
                        state.updateComboSeq(maid, ComboStateRegistry.COMBO_A1_END2.getId());
                    } else {
                        data.putFloat(GUARD_DAMAGE, 0);
                        data.putInt(GUARD_COOL_DOWN, 300);
                        state.updateComboSeq(maid, ComboStateRegistry.COMBO_A1.getId());
                        data.putInt(PRE_ESCAPE_COUNTER, 5);
                        data.putBoolean(IS_PRE_ESCAPING, true);
                    }
                    event.setCanceled(true);
                }
            });
        }
    }

    public static void trickToTarget(EntityMaid maid, LivingEntity target) {
        if (maid.level() instanceof ServerLevel serverLevel) {
            maid.refreshBrain(serverLevel);
        }
        maid.setTarget(target);
        CompoundTag data = maid.getPersistentData();
        boolean canTrick = SlashBladeMaidBauble.Trick.checkBauble(maid) && data.getInt(MaidSlashBladeMove.TRICK_COOL_DOWN) <= 0;
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
        return maid.getPersistentData().getFloat(GUARD_DAMAGE) > 0 && SlashBladeMaidBauble.Guard.checkBauble(maid);
    }

    public static void guardRefreshMaidTickCounter(EntityMaid maid) {
        CompoundTag data = maid.getPersistentData();
        data.putInt(MaidMirageBladeBehavior.HEAVY_RAIN_SWORD_COUNTER_KEY, 0);
        data.putInt(MaidMirageBladeBehavior.BLISTERING_SWORD_COUNTER_KEY, 0);
        data.putInt(MaidMirageBladeBehavior.SPIRAL_SWORD_COUNTER_KEY, 0);
        data.putInt(MaidMirageBladeBehavior.STORM_SWORD_COUNTER_KEY, 0);
        data.putInt(MaidMirageBladeBehavior.BASE_SUMMONED_SWORD_COUNTER_KEY, 0);
        data.putInt(MaidSlashBladeAttackUtils.VOID_SLASH_COUNTER_KEY, 0);

        data.putInt(MaidGuardHandler.GUARD_DAMAGE_COUNTER, 0);
        data.putInt(MaidGuardHandler.GUARD_ESCAPE_COUNTER, 0);
        data.putInt(MaidGuardHandler.PRE_ESCAPE_COUNTER, 0);
        data.putInt(MaidGuardHandler.GUARD_COOL_DOWN, 0);
    }
}
