package net.mrqx.slashblade.maidpower.util;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.mrqx.sbr_core.utils.MrqxSlayerStyleArts;
import net.mrqx.slashblade.maidpower.event.MaidGuardHandler;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MaidSlashBladeMovementUtils {
    public static final TriFunction<EntityMaid, Float, Double, Boolean> AIR_TRICK_CHECK = (maid, distance, reach) -> {
        if (distance > reach && !MaidGuardHandler.isGuarding(maid)) {
            return MrqxSlayerStyleArts.AIR_TRICK.apply(maid, true);
        }
        return false;
    };

    public static final Consumer<EntityMaid> TRICK_DOWN_CHECK = maid -> {
        if (maid.fallDistance > 2 && !MaidGuardHandler.isGuarding(maid)) {
            maid.fallDistance = 0;
            MrqxSlayerStyleArts.TRICK_DOWN.apply(maid, true);
        }
    };

    public static final Consumer<EntityMaid> TRY_TRICK_DODGE = maid -> {
        RandomSource random = maid.level().random;
        double oldX = maid.position().x;
        double oldY = maid.position().y;
        double oldZ = maid.position().z;
        if (!maid.level().isClientSide() && maid.isAlive()) {
            for (int i = 0; i < 16; ++i) {
                double x = maid.getX() + (random.nextDouble() - 0.5) * 16;
                double y = maid.getTarget() != null ? maid.getTarget().getY() : maid.getY();
                double z = maid.getZ() + (random.nextDouble() - 0.5) * 16;
                if (maid.randomTeleport(x, y, z, false) && MrqxSlayerStyleArts.TRICK_DODGE.apply(maid, true, false, maid.position())) {
                    maid.level().broadcastEntityEvent(maid, (byte) 46);
                    break;
                } else {
                    maid.teleportTo(oldX, oldY, oldZ);
                }
            }
        }
    };

    public static final BiConsumer<EntityMaid, LivingEntity> TRY_TRICK_TO_TARGET = (maid, target) -> {
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
    };
}
