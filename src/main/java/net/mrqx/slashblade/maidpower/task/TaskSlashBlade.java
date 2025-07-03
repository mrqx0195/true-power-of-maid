package net.mrqx.slashblade.maidpower.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IAttackTask;
import com.github.tartaricacid.touhoulittlemaid.api.task.IRangedAttackTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.util.SoundUtil;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.data.builtin.SlashBladeBuiltInRegistry;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.item.ItemStack;
import net.mrqx.slashblade.maidpower.TruePowerOfMaid;
import net.mrqx.slashblade.maidpower.entity.ai.MaidMirageBladeBehavior;
import net.mrqx.slashblade.maidpower.entity.ai.MaidSlashBladeAttack;
import net.mrqx.slashblade.maidpower.entity.ai.MaidSlashBladeMove;
import net.mrqx.slashblade.maidpower.item.MaidItems;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeAttackUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class TaskSlashBlade implements IAttackTask {
    public static final ResourceLocation UID = TruePowerOfMaid.prefix("slashblade_attack");

    @Override
    public @NotNull ResourceLocation getUid() {
        return UID;
    }

    @Override
    public @NotNull ItemStack getIcon() {
        if (Minecraft.getInstance().player != null) {
            Registry<SlashBladeDefinition> bladeRegistry = SlashBlade.getSlashBladeDefinitionRegistry(Minecraft.getInstance().player.level());
            if (bladeRegistry.containsKey(SlashBladeBuiltInRegistry.YAMATO)) {
                return Objects.requireNonNull(bladeRegistry.get(SlashBladeBuiltInRegistry.YAMATO)).getBlade();
            }
        }
        return SBItems.slashblade.getDefaultInstance();
    }

    @Override
    public @Nullable SoundEvent getAmbientSound(@NotNull EntityMaid maid) {
        return SoundUtil.attackSound(maid, InitSounds.MAID_ATTACK.get(), 0.5F);
    }

    @Override
    public @NotNull List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(@NotNull EntityMaid maid) {
        BehaviorControl<EntityMaid> supplementedTask = StartAttacking.create(MaidSlashBladeAttackUtils::isHoldingSlashBlade, IRangedAttackTask::findFirstValidAttackTarget);
        BehaviorControl<EntityMaid> findTargetTask = StopAttackingIfTargetInvalid.create((target) -> !MaidSlashBladeAttackUtils.isHoldingSlashBlade(maid) || farAway(target, maid));
        BehaviorControl<Mob> moveToTargetTask = MaidSlashBladeMove.create(1);
        BehaviorControl<Mob> attackTargetTask = MaidSlashBladeAttack.create();
        BehaviorControl<EntityMaid> mirageBladeTask = new MaidMirageBladeBehavior();
        return Lists.newArrayList(
                Pair.of(5, supplementedTask),
                Pair.of(5, findTargetTask),
                Pair.of(5, moveToTargetTask),
                Pair.of(5, attackTargetTask),
                Pair.of(5, mirageBladeTask)
        );
    }

    @Override
    public @NotNull List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createRideBrainTasks(@NotNull EntityMaid maid) {
        BehaviorControl<EntityMaid> supplementedTask = StartAttacking.create(MaidSlashBladeAttackUtils::isHoldingSlashBlade, IRangedAttackTask::findFirstValidAttackTarget);
        BehaviorControl<EntityMaid> findTargetTask = StopAttackingIfTargetInvalid.create((target) -> !MaidSlashBladeAttackUtils.isHoldingSlashBlade(maid) || farAway(target, maid));
        BehaviorControl<Mob> attackTargetTask = MaidSlashBladeAttack.create();
        BehaviorControl<EntityMaid> mirageBladeTask = new MaidMirageBladeBehavior();
        return Lists.newArrayList(
                Pair.of(5, supplementedTask),
                Pair.of(5, findTargetTask),
                Pair.of(5, attackTargetTask),
                Pair.of(5, mirageBladeTask)
        );
    }

    @Override
    public boolean isWeapon(@NotNull EntityMaid maid, ItemStack stack) {
        return stack.getCapability(ItemSlashBlade.BLADESTATE).isPresent();
    }

    public static boolean farAway(LivingEntity target, EntityMaid maid) {
        if (!target.isAlive()) {
            return true;
        } else {
            if (MaidItems.SlashBladeMaidBauble.MirageBlade.checkBauble(maid) && MaidItems.SlashBladeMaidBauble.Trick.checkBauble(maid)) {
                return false;
            }
            boolean enable = maid.isHomeModeEnable();
            double radius = TargetSelector.getResolvedReach(maid) * 2;
            radius *= radius;
            if (MaidItems.SlashBladeMaidBauble.MirageBlade.checkBauble(maid) || MaidItems.SlashBladeMaidBauble.JudgementCut.checkBauble(maid)) {
                radius *= 3;
            }
            if (!enable && maid.getOwner() != null) {
                return maid.getOwner().distanceTo(target) > radius;
            } else {
                return maid.distanceTo(target) > radius;
            }
        }
    }
}
