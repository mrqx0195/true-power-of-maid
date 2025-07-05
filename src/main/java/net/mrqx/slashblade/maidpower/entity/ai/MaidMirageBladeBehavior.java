package net.mrqx.slashblade.maidpower.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.enchantment.Enchantments;
import net.mrqx.sbr_core.utils.MrqxSummonedSwordArts;
import net.mrqx.slashblade.maidpower.event.MaidGuardHandler;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeAttackUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MaidMirageBladeBehavior extends Behavior<EntityMaid> {
    public static final String BASE_SUMMONED_SWORD_COUNTER_KEY = "truePowerOfMaid.baseSummonedSwordCounter";
    public static final String SPIRAL_SWORD_COUNTER_KEY = "truePowerOfMaid.spiralSwordCounter";
    public static final String STORM_SWORD_COUNTER_KEY = "truePowerOfMaid.stormSwordCounter";
    public static final String BLISTERING_SWORD_COUNTER_KEY = "truePowerOfMaid.blisteringSwordCounter";
    public static final String HEAVY_RAIN_SWORD_COUNTER_KEY = "truePowerOfMaid.heavyRainSwordCounter";

    public MaidMirageBladeBehavior() {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT
        ), 1200);
    }

    @Override
    public boolean checkExtraStartConditions(@NotNull ServerLevel level, EntityMaid maid) {
        Optional<LivingEntity> targetOpt = maid.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if (targetOpt.isEmpty()) {
            return false;
        }
        LivingEntity target = targetOpt.get();
        return MaidSlashBladeAttackUtils.isHoldingSlashBlade(maid)
               && SlashBladeMaidBauble.MirageBlade.checkBauble(maid)
               && maid.canSee(target);
    }

    @Override
    public boolean canStillUse(@NotNull ServerLevel level, EntityMaid maid, long gameTime) {
        return maid.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)
               && checkExtraStartConditions(level, maid);
    }

    @Override
    public void tick(@NotNull ServerLevel level, @NotNull EntityMaid maid, long gameTime) {
        LivingEntity target = maid.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        if (target == null) {
            return;
        }
        if (!maid.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).isPresent()) {
            return;
        }
        if (MaidGuardHandler.isGuarding(maid)) {
            return;
        }

        int favorLevel = maid.getFavorabilityManager().getLevel();
        int enchantPower = maid.getMainHandItem().getEnchantmentLevel(Enchantments.POWER_ARROWS);
        int powerLevel = (enchantPower + favorLevel + 1) * (SlashBladeMaidBauble.TruePower.checkBauble(maid) ? 2 : 1);
        CompoundTag data = maid.getPersistentData();

        switch (favorLevel) {
            case 3:
                if (data.getInt(HEAVY_RAIN_SWORD_COUNTER_KEY) <= 0) {
                    int rank = maid.getCapability(CapabilityConcentrationRank.RANK_POINT)
                            .map(r -> r.getRank(maid.level().getGameTime()).level).orElse(0);
                    MrqxSummonedSwordArts.HEAVY_RAIN_SWORD.accept(
                            maid, target, (double) powerLevel, (9 + Math.min(rank - 1, 0)) * 2);
                    data.putInt(HEAVY_RAIN_SWORD_COUNTER_KEY, 600);
                    break;
                }
            case 2:
                if (data.getInt(BLISTERING_SWORD_COUNTER_KEY) <= 0) {
                    int rank = maid.getCapability(CapabilityConcentrationRank.RANK_POINT)
                            .map(r -> r.getRank(maid.level().getGameTime()).level).orElse(0);
                    int count = IConcentrationRank.ConcentrationRanks.S.level <= rank ? 8 : 6;
                    MrqxSummonedSwordArts.BLISTERING_SWORD.accept(
                            maid, target, (double) powerLevel, count);
                    data.putInt(BLISTERING_SWORD_COUNTER_KEY, 400);
                    break;
                }
            case 1:
                int rank = maid.getCapability(CapabilityConcentrationRank.RANK_POINT)
                        .map(r -> r.getRank(maid.level().getGameTime()).level).orElse(0);
                int count = IConcentrationRank.ConcentrationRanks.S.level <= rank ? 8 : 6;
                if (data.getInt(SPIRAL_SWORD_COUNTER_KEY) <= 0) {
                    MrqxSummonedSwordArts.SPIRAL_SWORD.accept(maid, (double) powerLevel, count);
                    data.putInt(SPIRAL_SWORD_COUNTER_KEY, 200);
                    break;
                }
                if (data.getInt(STORM_SWORD_COUNTER_KEY) <= 0) {
                    MrqxSummonedSwordArts.STORM_SWORD.accept(maid, target, (double) powerLevel, count);
                    data.putInt(STORM_SWORD_COUNTER_KEY, 200);
                    break;
                }
            case 0:
                if (data.getInt(BASE_SUMMONED_SWORD_COUNTER_KEY) <= 0) {
                    MrqxSummonedSwordArts.BASE_SUMMONED_SWORD.accept(maid, target, (double) powerLevel);
                    data.putInt(BASE_SUMMONED_SWORD_COUNTER_KEY, 20);
                    break;
                }
            default:
                break;
        }
    }
}
