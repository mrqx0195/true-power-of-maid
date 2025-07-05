package net.mrqx.slashblade.maidpower.util;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.mrqx.sbr_core.utils.MrqxSlayerStyleArts;
import net.mrqx.slashblade.maidpower.entity.ai.MaidSlashBladeMove;
import net.mrqx.slashblade.maidpower.event.MaidGuardHandler;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MaidSlashBladeMovementUtils {
    public static final TriFunction<EntityMaid, Float, Double, Boolean> AIR_TRICK_CHECK = MaidSlashBladeMovementUtils::airTrickCheck;
    public static final Consumer<EntityMaid> TRICK_DOWN_CHECK = MaidSlashBladeMovementUtils::trickDownCheck;
    public static final Consumer<EntityMaid> TRY_TRICK_DODGE = MaidSlashBladeMovementUtils::tryTrickDodge;
    public static final BiConsumer<EntityMaid, LivingEntity> TRY_TRICK_TO_TARGET = MaidSlashBladeMovementUtils::tryTrickToTarget;

    /**
     * 检查是否可发动瞬步类技能
     */
    public static boolean canTrick(EntityMaid maid) {
        CompoundTag data = maid.getPersistentData();
        return SlashBladeMaidBauble.Trick.checkBauble(maid)
                && data.getInt(MaidSlashBladeMove.TRICK_COOL_DOWN) <= 0
                && MaidSlashBladeAttackUtils.canInterruptCombo(maid);
    }

    /**
     * 尝试使用 隔空瞬步
     */
    private static Boolean airTrickCheck(EntityMaid maid, Float distance, Double reach) {
        if (distance > reach && !MaidGuardHandler.isGuarding(maid)) {
            return MrqxSlayerStyleArts.AIR_TRICK.apply(maid, true);
        }
        return false;
    }

    /**
     * 尝试使用 瞬步退行
     */
    private static void trickDownCheck(EntityMaid maid) {
        if (maid.fallDistance > 2 && !MaidGuardHandler.isGuarding(maid)) {
            maid.fallDistance = 0;
            MrqxSlayerStyleArts.TRICK_DOWN.apply(maid, true);
        }
    }

    /**
     * 尝试使用闪避
     */
    private static void tryTrickDodge(EntityMaid maid) {
        RandomSource random = maid.level().random;
        double oldX = maid.position().x;
        double oldY = maid.position().y;
        double oldZ = maid.position().z;
        if (!maid.level().isClientSide() && maid.isAlive()) {
            for (int i = 0; i < 16; ++i) {
                double x = maid.getX() + (random.nextDouble() - 0.5) * 16;
                double y = maid.getTarget() != null ? maid.getTarget().getY() : maid.getY();
                double z = maid.getZ() + (random.nextDouble() - 0.5) * 16;
                if (maid.randomTeleport(x, y, z, false)
                        && MrqxSlayerStyleArts.TRICK_DODGE.apply(maid, true, false, maid.position())) {
                    maid.level().broadcastEntityEvent(maid, (byte) 46);
                    break;
                } else {
                    maid.teleportTo(oldX, oldY, oldZ);
                }
            }
        }
    }

    /**
     * 尝试瞬移至目标
     */
    private static void tryTrickToTarget(EntityMaid maid, LivingEntity target) {
        RandomSource random = maid.level().random;
        double oldX = maid.position().x;
        double oldY = maid.position().y;
        double oldZ = maid.position().z;
        if (!maid.level().isClientSide() && maid.isAlive()) {
            for (int i = 0; i < 16; ++i) {
                double reach = TargetSelector.getResolvedReach(maid);
                reach *= reach;
                double x = target.getX() + (random.nextDouble() - 0.5) * reach * 0.8;
                double y = target.getY();
                double z = target.getZ() + (random.nextDouble() - 0.5) * reach * 0.8;
                if (maid.randomTeleport(x, y, z, false)
                        && MrqxSlayerStyleArts.TRICK_DODGE.apply(maid, true, false, maid.position())
                        && TargetSelector.getTargettableEntitiesWithinAABB(maid.level(), maid).contains(target)) {
                    maid.level().broadcastEntityEvent(maid, (byte) 46);
                    break;
                } else {
                    maid.teleportTo(oldX, oldY, oldZ);
                }
            }
        }
    }
}
