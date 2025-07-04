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
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), 1200);
    }

    @Override
    public boolean checkExtraStartConditions(@NotNull ServerLevel pLevel, EntityMaid pOwner) {
        Optional<LivingEntity> memory = pOwner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if (memory.isPresent()) {
            LivingEntity target = memory.get();
            return MaidSlashBladeAttackUtils.isHoldingSlashBlade(pOwner) && SlashBladeMaidBauble.MirageBlade.checkBauble(pOwner) && pOwner.canSee(target);
        }
        return false;
    }

    @Override
    public boolean canStillUse(@NotNull ServerLevel pLevel, EntityMaid pEntity, long pGameTime) {
        return pEntity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.checkExtraStartConditions(pLevel, pEntity);
    }

    @Override
    public void tick(@NotNull ServerLevel pLevel, @NotNull EntityMaid pOwner, long pGameTime) {
        pOwner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(target ->
                pOwner.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
                    if (MaidGuardHandler.isGuarding(pOwner)) {
                        return;
                    }
                    int powerLevel = (pOwner.getMainHandItem().getEnchantmentLevel(Enchantments.POWER_ARROWS) + pOwner.getFavorabilityManager().getLevel() + 1) * (SlashBladeMaidBauble.TruePower.checkBauble(pOwner) ? 2 : 1);
                    CompoundTag data = pOwner.getPersistentData();
                    switch (pOwner.getFavorabilityManager().getLevel()) {
                        case 3:
                            if (data.getInt(HEAVY_RAIN_SWORD_COUNTER_KEY) <= 0) {
                                int rank = pOwner.getCapability(CapabilityConcentrationRank.RANK_POINT).map(r -> r.getRank(pOwner.level().getGameTime()).level).orElse(0);
                                MrqxSummonedSwordArts.HEAVY_RAIN_SWORD.accept(pOwner, target, (double) powerLevel, (9 + Math.min(rank - 1, 0)) * 2);
                                data.putInt(HEAVY_RAIN_SWORD_COUNTER_KEY, 600);
                                break;
                            }
                        case 2:
                            if (data.getInt(BLISTERING_SWORD_COUNTER_KEY) <= 0) {
                                int rank = pOwner.getCapability(CapabilityConcentrationRank.RANK_POINT).map(r -> r.getRank(pOwner.level().getGameTime()).level).orElse(0);
                                MrqxSummonedSwordArts.BLISTERING_SWORD.accept(pOwner, target, (double) powerLevel, IConcentrationRank.ConcentrationRanks.S.level <= rank ? 8 : 6);
                                data.putInt(BLISTERING_SWORD_COUNTER_KEY, 400);
                                break;
                            }
                        case 1:
                            int rank = pOwner.getCapability(CapabilityConcentrationRank.RANK_POINT).map(r -> r.getRank(pOwner.level().getGameTime()).level).orElse(0);
                            int count = IConcentrationRank.ConcentrationRanks.S.level <= rank ? 8 : 6;
                            if (data.getInt(SPIRAL_SWORD_COUNTER_KEY) <= 0) {
                                MrqxSummonedSwordArts.SPIRAL_SWORD.accept(pOwner, (double) powerLevel, count);
                                data.putInt(SPIRAL_SWORD_COUNTER_KEY, 200);
                                break;
                            }
                            if (data.getInt(STORM_SWORD_COUNTER_KEY) <= 0) {
                                MrqxSummonedSwordArts.STORM_SWORD.accept(pOwner, target, (double) powerLevel, count);
                                data.putInt(STORM_SWORD_COUNTER_KEY, 200);
                                break;
                            }
                        case 0:
                            if (data.getInt(BASE_SUMMONED_SWORD_COUNTER_KEY) <= 0) {
                                MrqxSummonedSwordArts.BASE_SUMMONED_SWORD.accept(pOwner, target, (double) powerLevel);
                                data.putInt(BASE_SUMMONED_SWORD_COUNTER_KEY, 20);
                                break;
                            }
                    }
                })
        );
    }

}
