package net.mrqx.slashblade.maidpower.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.mrqx.sbr_core.utils.MrqxSlayerStyleArts;
import net.mrqx.slashblade.maidpower.event.MaidGuardHandler;
import net.mrqx.slashblade.maidpower.item.MaidItems;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeMovementUtils;

import java.util.Optional;
import java.util.function.Function;

public class MaidSlashBladeMove {
    public static BehaviorControl<Mob> create(Function<LivingEntity, Float> pSpeedModifier) {
        return BehaviorBuilder.create((mobInstance) -> mobInstance.group(mobInstance.registered(MemoryModuleType.WALK_TARGET), mobInstance.registered(MemoryModuleType.LOOK_TARGET), mobInstance.present(MemoryModuleType.ATTACK_TARGET), mobInstance.registered(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(mobInstance, (walkTargetMemoryAccessor, positionTrackerMemoryAccessor, livingEntityMemoryAccessor, visibleLivingEntitiesMemoryAccessor) -> (level, mob, l) -> {
            LivingEntity target = mobInstance.get(livingEntityMemoryAccessor);
            Optional<NearestVisibleLivingEntities> optional = mobInstance.tryGet(visibleLivingEntitiesMemoryAccessor);
            if (mob instanceof EntityMaid maid
                    && !MaidGuardHandler.isGuarding(maid)) {
                float distance = maid.distanceTo(target);
                double reach = TargetSelector.getResolvedReach(maid);
                CompoundTag data = maid.getPersistentData();
                boolean canTrick = MaidItems.SlashBladeMaidBauble.Trick.checkBauble(maid) && data.getInt(MaidSlashBladeMove.TRICK_COOL_DOWN) <= 0;
                boolean canAirTrick = canTrick && MaidItems.SlashBladeMaidBauble.MirageBlade.checkBauble(maid);
                boolean hasTrick = false;
                if (optional.isPresent() && optional.get().contains(target) && distance > reach * reach) {
                    if (!canAirTrick) {
                        walkTargetMemoryAccessor.erase();
                    }
                } else {
                    positionTrackerMemoryAccessor.set(new EntityTracker(target, true));
                    walkTargetMemoryAccessor.set(new WalkTarget(new EntityTracker(target, false), pSpeedModifier.apply(mob), 0));
                }
                if (canAirTrick && distance > reach) {
                    if (!MaidSlashBladeMovementUtils.AIR_TRICK_CHECK.apply(maid, distance, reach)) {
                        MaidSlashBladeMovementUtils.TRY_TRICK_TO_TARGET.accept(maid, target);
                    }
                    hasTrick = true;
                    data.putInt(MaidSlashBladeMove.TRICK_COOL_DOWN, 60);
                    maid.level().broadcastEntityEvent(maid, (byte) 46);
                }
                if (canTrick && !hasTrick) {
                    if (!maid.onGround() && maid.getY() - target.getY() > 5) {
                        MrqxSlayerStyleArts.TRICK_DOWN.apply(maid, true);
                        data.putInt(MaidSlashBladeMove.TRICK_COOL_DOWN, 60);
                        maid.level().broadcastEntityEvent(maid, (byte) 46);
                    }
                }
            }
            return true;
        }));
    }

    public static BehaviorControl<Mob> create(float pSpeedModifier) {
        return create((livingEntity) -> pSpeedModifier);
    }

    public static final String TRICK_COOL_DOWN = "truePowerOfMaid.trickCooldown";
}
