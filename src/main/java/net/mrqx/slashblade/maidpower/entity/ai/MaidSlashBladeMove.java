package net.mrqx.slashblade.maidpower.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.OptionalBox;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.mrqx.sbr_core.utils.MrqxSlayerStyleArts;
import net.mrqx.slashblade.maidpower.event.MaidGuardHandler;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeAttackUtils;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeMovementUtils;

import java.util.Optional;
import java.util.function.Function;

public class MaidSlashBladeMove {
    public static final String TRICK_COOL_DOWN = "truePowerOfMaid.trickCooldown";

    public static BehaviorControl<Mob> create(Function<LivingEntity, Float> speedModifier) {
        return BehaviorBuilder.create(instance ->
                instance.group(
                        instance.registered(MemoryModuleType.WALK_TARGET),
                        instance.registered(MemoryModuleType.LOOK_TARGET),
                        instance.present(MemoryModuleType.ATTACK_TARGET),
                        instance.registered(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                ).apply(instance, (walkTargetAccessor, lookTargetAccessor, attackTargetAccessor, visibleEntitiesAccessor) ->
                        (level, mob, time) -> handleMove(instance, walkTargetAccessor, lookTargetAccessor, attackTargetAccessor, visibleEntitiesAccessor, mob, speedModifier)
                )
        );
    }

    public static BehaviorControl<Mob> create(float speedModifier) {
        return create(entity -> speedModifier);
    }

    private static boolean handleMove(
            BehaviorBuilder.Instance<Mob> instance,
            MemoryAccessor<OptionalBox.Mu, WalkTarget> walkTargetAccessor,
            MemoryAccessor<OptionalBox.Mu, PositionTracker> lookTargetAccessor,
            MemoryAccessor<IdF.Mu, LivingEntity> attackTargetAccessor,
            MemoryAccessor<OptionalBox.Mu, NearestVisibleLivingEntities> visibleEntitiesAccessor,
            Mob mob,
            Function<LivingEntity, Float> speedModifier
    ) {
        LivingEntity target = instance.get(attackTargetAccessor);
        Optional<NearestVisibleLivingEntities> visibleEntitiesOpt = instance.tryGet(visibleEntitiesAccessor);

        if (mob instanceof EntityMaid maid) {
            if (!MaidSlashBladeAttackUtils.isHoldingSlashBlade(maid) || maid.isMaidInSittingPose()) {
                return false;
            }

            float distance = maid.distanceTo(target);
            double reach = TargetSelector.getResolvedReach(maid);
            CompoundTag data = maid.getPersistentData();
            boolean hasTruePower = SlashBladeMaidBauble.TruePower.checkBauble(maid);
            if (MaidGuardHandler.isGuarding(maid) && !hasTruePower) {
                return false;
            }

            boolean canTrick = MaidSlashBladeMovementUtils.canTrick(maid);
            boolean canAirTrick = canTrick && SlashBladeMaidBauble.MirageBlade.checkBauble(maid);
            boolean hasTrick = false;

            boolean targetVisible = visibleEntitiesOpt.map(entities -> entities.contains(target)).orElse(false);

            if (targetVisible && distance > reach * reach) {
                if (!canAirTrick) {
                    walkTargetAccessor.erase();
                }
            } else {
                lookTargetAccessor.set(new EntityTracker(target, true));
                walkTargetAccessor.set(new WalkTarget(new EntityTracker(target, false), speedModifier.apply(mob) * (hasTruePower ? 2 : 1), 0));
            }

            if (canAirTrick && distance > reach) {
                if (!MaidSlashBladeMovementUtils.AIR_TRICK_CHECK.apply(maid, distance, reach)) {
                    MaidSlashBladeMovementUtils.TRY_TRICK_TO_TARGET.accept(maid, target);
                }
                hasTrick = true;
                data.putInt(TRICK_COOL_DOWN, 60);
                maid.level().broadcastEntityEvent(maid, (byte) 46);
            }

            if (canTrick && !hasTrick) {
                if (!maid.onGround() && maid.getY() - target.getY() > 5) {
                    MrqxSlayerStyleArts.TRICK_DOWN.apply(maid, true);
                    data.putInt(TRICK_COOL_DOWN, 60);
                    maid.level().broadcastEntityEvent(maid, (byte) 46);
                }
            }
            return true;
        }
        return false;
    }
}
