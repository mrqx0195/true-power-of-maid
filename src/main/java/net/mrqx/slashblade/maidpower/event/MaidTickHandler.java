package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidTickEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.ability.ArrowReflector;
import mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.entity.IShootable;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.ModAttributes;
import mods.flammpfeil.slashblade.slasharts.JudgementCut;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.sbr_core.utils.SlashBladeAttackUtils;
import net.mrqx.sbr_core.utils.SlashBladeMovementUtils;
import net.mrqx.slashblade.maidpower.entity.EntityUnlimitedBladeWorks;
import net.mrqx.slashblade.maidpower.entity.ai.MaidMirageBladeBehavior;
import net.mrqx.slashblade.maidpower.entity.ai.MaidSlashBladeMove;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.slashblade.maidpower.task.TaskSlashBlade;
import net.mrqx.slashblade.maidpower.util.MaidItemUtils;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeMovementUtils;
import net.mrqx.truepower.util.JustSlashArtManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber
public class MaidTickHandler {
    public static final String TRUE_POWER_RANK = "truePowerOfMaid.truePowerRank";
    public static final String UNLIMITED_BLADE_WORKS_COOLDOWN_KEY = "truePowerOfMaid.unlimitedBladeWorks.cooldown";
    public static final String UNLIMITED_BLADE_WORKS_COUNTER_KEY = "truePowerOfMaid.unlimitedBladeWorks.counter";
    
    @SubscribeEvent
    public static void onMaidTickEvent(MaidTickEvent event) {
        EntityMaid maid = event.getMaid();
        maid.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE)
            .ifPresent(state -> handleMaidTick(maid, state));
    }
    
    private static void handleMaidTick(EntityMaid maid, ISlashBladeState state) {
        boolean hasTruePower = SlashBladeMaidBauble.TruePower.checkBauble(maid);
        boolean hasUnlimitedBladeWorks = SlashBladeMaidBauble.UnlimitedBladeWorks.checkBauble(maid);
        CompoundTag data = maid.getPersistentData();
        
        maidTickCounter(maid, hasTruePower, hasUnlimitedBladeWorks);
        maidBonus(maid, hasTruePower, hasUnlimitedBladeWorks);
        maid.getMainHandItem().inventoryTick(maid.level(), maid, 0, true);
        
        maid.getCapability(ConcentrationRankCapabilityProvider.RANK_POINT)
            .ifPresent(rank -> {
                if (hasTruePower) {
                    long rankPoint = Math.min(Math.max(rank.getRankPoint(maid.level().getGameTime()), data.getLong(TRUE_POWER_RANK)), rank.getMaxCapacity());
                    rank.setRawRankPoint(rankPoint);
                    rank.setLastUpdte(maid.level().getGameTime());
                    data.putLong(TRUE_POWER_RANK, rankPoint);
                }
                if (hasUnlimitedBladeWorks) {
                    long rankPoint = Math.min(Math.max(rank.getRankPoint(maid.level().getGameTime()), 5 * rank.getUnitCapacity() + 3), rank.getMaxCapacity());
                    rank.setRawRankPoint(rankPoint);
                    rank.setLastUpdte(maid.level().getGameTime());
                }
            });
        
        boolean canTrick = MaidSlashBladeMovementUtils.canTrick(maid);
        Entity target = state.getTargetEntity(maid.level());
        boolean canAirTrick = canTrick && SlashBladeMaidBauble.MirageBlade.checkBauble(maid);
        
        if (target != null) {
            canAirTrick &= target.isAlive();
            if (target instanceof LivingEntity living) {
                canAirTrick &= living.getHealth() > 0;
                maid.setTarget(living);
            }
        }
        
        if (canTrick && !canAirTrick) {
            SlashBladeMovementUtils.trickDownCheck(maid);
        }
        if (MaidGuardHandler.isGuarding(maid)) {
            if (data.getInt(MaidGuardHandler.GUARD_ESCAPE_COUNTER) <= 0) {
                SlashBladeMovementUtils.tryTrickDodge(maid, null);
                MaidGuardHandler.guardRefreshMaidTickCounter(maid);
            }
            state.setFallDecreaseRate(1);
        }
        if (data.getBoolean(MaidGuardHandler.IS_PRE_ESCAPING) && data.getInt(MaidGuardHandler.PRE_ESCAPE_COUNTER) <= 0) {
            SlashBladeMovementUtils.tryTrickDodge(maid, null);
            data.putBoolean(MaidGuardHandler.IS_PRE_ESCAPING, false);
        }
        handleHealthAndExp(maid, state, hasTruePower, hasUnlimitedBladeWorks);
        if (hasTruePower) {
            handleSuperJudgementCut(maid, state, data);
        }
        if (hasUnlimitedBladeWorks) {
            handleUnlimitedBladeWorks(maid, data);
        }
    }
    
    private static void handleHealthAndExp(EntityMaid maid, ISlashBladeState state, boolean hasTruePower, boolean hasUnlimitedBladeWorks) {
        int favorabilityLevel = Math.max(1, maid.getFavorabilityManager().getLevel() + 1);
        boolean flag = hasTruePower || hasUnlimitedBladeWorks;
        int cost = Math.max(1, Math.max(0, 4 - favorabilityLevel) / (flag ? 2 : 1));
        if (SlashBladeMaidBauble.Health.checkBauble(maid) && maid.getHealth() < maid.getMaxHealth()) {
            boolean isGuarding = MaidGuardHandler.isGuarding(maid);
            if (!flag && !isGuarding && maid.level().getGameTime() % 10 != 0) {
                return;
            }
            if (state.getProudSoulCount() >= cost) {
                state.setProudSoulCount(state.getProudSoulCount() - cost);
                if (flag) {
                    maid.setHealth(Math.max(maid.getHealth() + favorabilityLevel * (isGuarding ? 2 : 1), maid.getMaxHealth()));
                } else {
                    maid.heal(favorabilityLevel * 0.5F);
                }
            }
        }
        if (state.isBroken() && SlashBladeMaidBauble.Exp.checkBauble(maid)) {
            if (maid.getExperience() >= cost) {
                maid.setExperience(maid.getExperience() - cost);
                state.setDamage(state.getDamage() - favorabilityLevel);
                if (state.getDamage() <= 0) {
                    state.setBroken(false);
                }
            }
        }
        if (hasUnlimitedBladeWorks && SlashBladeMaidBauble.Exp.checkBauble(maid)) {
            MaidItemUtils.getAllSlashBlade(maid).forEach(stack ->
                stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(bladeState -> {
                    if (maid.getExperience() >= cost && stack.getDamageValue() > 0) {
                        maid.setExperience(maid.getExperience() - cost);
                        bladeState.setDamage(bladeState.getDamage() - favorabilityLevel);
                        if (bladeState.getDamage() <= 0) {
                            bladeState.setBroken(false);
                        }
                    }
                })
            );
        }
    }
    
    private static void handleSuperJudgementCut(EntityMaid maid, ISlashBladeState state, CompoundTag data) {
        if (data.getInt(SlashBladeAttackUtils.SUPER_JUDGEMENT_CUT_COUNTER_KEY) <= 0) {
            Map.Entry<Integer, ResourceLocation> currentLoc = state.resolvCurrentComboStateTicks(maid);
            ResourceLocation csLoc = state.getSlashArts().doArts(SlashArts.ArtsType.Super, maid);
            if (!Objects.equals(csLoc, ComboStateRegistry.NONE.getId()) && !currentLoc.getValue().equals(csLoc)) {
                data.putInt(SlashBladeAttackUtils.SUPER_JUDGEMENT_CUT_COUNTER_KEY, 2400);
                
                AttributeInstance entityReachAttributeInstance = maid.getAttribute(ForgeMod.ENTITY_REACH.get());
                if (entityReachAttributeInstance == null) {
                    return;
                }
                
                double radius = TaskSlashBlade.getRadius(maid);
                int rank = maid.getCapability(ConcentrationRankCapabilityProvider.RANK_POINT)
                    .map(cr -> cr.getRank(maid.level().getGameTime()))
                    .orElse(IConcentrationRank.ConcentrationRanks.NONE).level;
                double bonus = radius / Math.max(TargetSelector.getResolvedReach(maid), 1) * rank / 7;
                
                AttributeModifier entityReachBonus = new AttributeModifier(
                    UUID.fromString("a7333e5f-d97e-465c-98c7-281a82396d6b"),
                    "Maid SuperJudgementCut Transient Bonus", bonus, AttributeModifier.Operation.MULTIPLY_TOTAL);
                
                entityReachAttributeInstance.addTransientModifier(entityReachBonus);
                JudgementCut.doJudgementCutSuper(maid);
                
                AABB aabb = maid.getBoundingBox().inflate(48.0F);
                double reach = TargetSelector.getResolvedReach(maid) + 32.0;
                
                maid.level().getEntitiesOfClass(Projectile.class, aabb).stream()
                    .filter(e -> {
                        Entity owner = (e instanceof IShootable iShootable) ?
                            iShootable.getShooter() : e.getOwner();
                        if (owner != null) {
                            return !owner.equals(maid) &&
                                !owner.equals(maid.getOwner()) &&
                                ((owner instanceof OwnableEntity ownable) && Objects.equals(ownable.getOwner(), maid.getOwner()));
                        } else {
                            return true;
                        }
                    })
                    .filter(e -> (e.distanceToSqr(maid) < (reach * reach)))
                    .forEach(e -> ArrowReflector.doReflect(e, maid));
                
                
                entityReachAttributeInstance.removeModifier(entityReachBonus);
            }
        }
    }
    
    private static void handleUnlimitedBladeWorks(EntityMaid maid, CompoundTag data) {
        if (data.getInt(UNLIMITED_BLADE_WORKS_COOLDOWN_KEY) <= 0 && data.getInt(UNLIMITED_BLADE_WORKS_COUNTER_KEY) <= 0) {
            data.putInt(UNLIMITED_BLADE_WORKS_COUNTER_KEY, 400);
        }
        if (data.getInt(UNLIMITED_BLADE_WORKS_COUNTER_KEY) > 0
//                && maid.tickCount % 2 == 0
        ) {
            AttributeInstance entityReachAttributeInstance = maid.getAttribute(ForgeMod.ENTITY_REACH.get());
            if (entityReachAttributeInstance == null) {
                return;
            }
            
            double radius = TaskSlashBlade.getRadius(maid);
            int rank = maid.getCapability(ConcentrationRankCapabilityProvider.RANK_POINT)
                .map(cr -> cr.getRank(maid.level().getGameTime()))
                .orElse(IConcentrationRank.ConcentrationRanks.NONE).level;
            double bonus = radius / Math.max(TargetSelector.getResolvedReach(maid), 1) * rank / 7;
            
            AttributeModifier entityReachBonus = new AttributeModifier(
                UUID.fromString("a7333e5f-d97e-465c-98c7-281a82396d6b"),
                "Maid UnlimitedBladeWorks Transient Bonus", bonus, AttributeModifier.Operation.MULTIPLY_TOTAL);
            
            entityReachAttributeInstance.addTransientModifier(entityReachBonus);
            
            Level level = maid.level();
            List<Entity> founds = TargetSelector.getTargettableEntitiesWithinAABB(level, maid, maid.getBoundingBox().inflate(48.0F),
                TargetSelector.getResolvedReach(maid) + (double) 32.0F);
            
            List<LivingEntity> livingEntities = founds.stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .toList();
            
            if (!livingEntities.isEmpty()) {
                double totalWeight = livingEntities.stream()
                    .mapToDouble(LivingEntity::getMaxHealth)
                    .sum();
                
                double random = level.random.nextDouble() * totalWeight;
                double cumulative = 0.0;
                LivingEntity selected = null;
                for (LivingEntity entity : livingEntities) {
                    cumulative += entity.getMaxHealth();
                    if (random <= cumulative) {
                        selected = entity;
                        break;
                    }
                }
                
                if (selected != null) {
                    data.putInt(UNLIMITED_BLADE_WORKS_COUNTER_KEY, data.getInt(UNLIMITED_BLADE_WORKS_COUNTER_KEY) - 1);
                    selected.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 10));
                    
                    float roll = level.random.nextFloat() * 360 - 180;
                    boolean mute = false;
                    boolean critical = true;
                    double damage = 4;
                    Vec3 pos = selected.position().add(level.random.nextDouble(), level.random.nextDouble(), level.random.nextDouble());
                    float xRot = level.random.nextFloat() * 180 - 90;
                    float yRot = level.random.nextFloat() * 360 - 180;
                    
                    SlashBladeMaidBauble.UnlimitedBladeWorks.ubwDoSlash(maid, pos, xRot, yRot, roll, mute, critical, damage);
                    
                    if (data.getInt(UNLIMITED_BLADE_WORKS_COUNTER_KEY) % 20 == 0) {
                        List<ItemStack> bladeList = MaidItemUtils.getAllSlashBladeUnbroken(maid);
                        if (!bladeList.isEmpty()) {
                            ItemStack blade = bladeList.get(maid.level().random.nextInt(bladeList.size()));
                            
                            EntityUnlimitedBladeWorks ubw = SlashBladeMaidBauble.UnlimitedBladeWorks.getEntityUnlimitedBladeWorks(maid, pos, xRot, yRot, blade);
                            LivingEntity target = selected;
                            ubw.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE)
                                .ifPresent(state -> SlashBladeAttackUtils.doSlashArts(ubw, state, target, critical));
                            blade.hurtAndBreak(1, maid, entityMaid ->
                                entityMaid.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                        }
                    }
                    if (data.getInt(UNLIMITED_BLADE_WORKS_COUNTER_KEY) <= 0) {
                        data.putInt(UNLIMITED_BLADE_WORKS_COOLDOWN_KEY, 2400);
                    }
                }
            }
            
            entityReachAttributeInstance.removeModifier(entityReachBonus);
        }
    }
    
    public static void maidTickCounter(EntityMaid maid, boolean hasTruePower, boolean hasUnlimitedBladeWorks) {
        CompoundTag data = maid.getPersistentData();
        int power = (hasTruePower || hasUnlimitedBladeWorks) ? 2 : 1;
        int favorabilityLevel = maid.getFavorabilityManager().getLevel() + 1;
        int decrement = favorabilityLevel * power;
        
        data.putInt(MaidMirageBladeBehavior.HEAVY_RAIN_SWORD_COUNTER_KEY,
            Math.max(0, data.getInt(MaidMirageBladeBehavior.HEAVY_RAIN_SWORD_COUNTER_KEY) - decrement));
        data.putInt(MaidMirageBladeBehavior.BLISTERING_SWORD_COUNTER_KEY,
            Math.max(0, data.getInt(MaidMirageBladeBehavior.BLISTERING_SWORD_COUNTER_KEY) - decrement));
        data.putInt(MaidMirageBladeBehavior.SPIRAL_SWORD_COUNTER_KEY,
            Math.max(0, data.getInt(MaidMirageBladeBehavior.SPIRAL_SWORD_COUNTER_KEY) - decrement));
        data.putInt(MaidMirageBladeBehavior.STORM_SWORD_COUNTER_KEY,
            Math.max(0, data.getInt(MaidMirageBladeBehavior.STORM_SWORD_COUNTER_KEY) - decrement));
        data.putInt(MaidMirageBladeBehavior.BASE_SUMMONED_SWORD_COUNTER_KEY,
            Math.max(0, data.getInt(MaidMirageBladeBehavior.BASE_SUMMONED_SWORD_COUNTER_KEY) - decrement));
        data.putInt(SlashBladeAttackUtils.VOID_SLASH_COUNTER_KEY,
            Math.max(0, data.getInt(SlashBladeAttackUtils.VOID_SLASH_COUNTER_KEY) - decrement));
        data.putInt(SlashBladeAttackUtils.SUPER_JUDGEMENT_CUT_COUNTER_KEY,
            Math.max(0, data.getInt(SlashBladeAttackUtils.SUPER_JUDGEMENT_CUT_COUNTER_KEY) - decrement));
        data.putInt(UNLIMITED_BLADE_WORKS_COOLDOWN_KEY,
            Math.max(0, data.getInt(UNLIMITED_BLADE_WORKS_COOLDOWN_KEY) - decrement));
        
        data.putInt(MaidSlashBladeMove.TRICK_COOL_DOWN,
            Math.max(0, data.getInt(MaidSlashBladeMove.TRICK_COOL_DOWN) - power));
        data.putInt(MaidGuardHandler.GUARD_DAMAGE_COUNTER,
            Math.max(0, data.getInt(MaidGuardHandler.GUARD_DAMAGE_COUNTER) - power));
        data.putInt(MaidGuardHandler.GUARD_ESCAPE_COUNTER,
            Math.max(0, data.getInt(MaidGuardHandler.GUARD_ESCAPE_COUNTER) - power));
        data.putInt(MaidGuardHandler.PRE_ESCAPE_COUNTER,
            Math.max(0, data.getInt(MaidGuardHandler.PRE_ESCAPE_COUNTER) - power));
        data.putInt(MaidGuardHandler.GUARD_COOL_DOWN,
            Math.max(0, data.getInt(MaidGuardHandler.GUARD_COOL_DOWN) - power));
        
        if (!data.contains(TRUE_POWER_RANK)) {
            data.putLong(TRUE_POWER_RANK, 2300);
        }
        data.putLong(TRUE_POWER_RANK, Math.min(2400, data.getLong(TRUE_POWER_RANK) + decrement));
        
        long cooldown = JustSlashArtManager.getJustCooldown(maid);
        if (cooldown >= 0) {
            cooldown -= power;
            JustSlashArtManager.setJustCooldown(maid, cooldown);
            if (cooldown <= 0) {
                JustSlashArtManager.resetJustCount(maid);
            }
        }
    }
    
    public static void maidBonus(EntityMaid maid, boolean hasTruePower, boolean hasUnlimitedBladeWorks) {
        double radius = TaskSlashBlade.getRadius(maid);
        AttributeModifier followRangeBonus = new AttributeModifier(
            UUID.fromString("5a138a12-3f1a-40ab-98cf-9532bd9881ce"),
            "Maid SlashBlade Radius Bonus", radius, AttributeModifier.Operation.ADDITION);
        AttributeInstance followRangeAttributeInstance = maid.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRangeAttributeInstance == null) {
            return;
        }
        followRangeAttributeInstance.removeModifier(followRangeBonus);
        followRangeAttributeInstance.addPermanentModifier(followRangeBonus);
        
        AttributeModifier slashBladeDamageBonus = new AttributeModifier(
            UUID.fromString("b70ee5b2-c9c8-45a9-a959-9db875d2c56e"),
            "Maid SlashBlade Unawakened Soul Bonus", MaidItemUtils.getBaubleCountForClass(maid, SlashBladeMaidBauble.UnawakenedSoul.class) * 0.1,
            AttributeModifier.Operation.MULTIPLY_TOTAL);
        AttributeInstance slashBladeDamageInstance = maid.getAttribute(ModAttributes.SLASHBLADE_DAMAGE.get());
        if (slashBladeDamageInstance == null) {
            return;
        }
        slashBladeDamageInstance.removeModifier(slashBladeDamageBonus);
        slashBladeDamageInstance.addPermanentModifier(slashBladeDamageBonus);
        
        AttributeModifier entityReachBonus = new AttributeModifier(
            UUID.fromString("5dd047e5-bb60-4ebf-93ba-34a1ece10128"),
            "Maid SlashBlade True Power Bonus", (hasTruePower || hasUnlimitedBladeWorks) ? 2.5 : 0.5, AttributeModifier.Operation.ADDITION);
        AttributeInstance entityReachAttributeInstance = maid.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (entityReachAttributeInstance == null) {
            return;
        }
        entityReachAttributeInstance.removeModifier(entityReachBonus);
        entityReachAttributeInstance.addPermanentModifier(entityReachBonus);
    }
}
