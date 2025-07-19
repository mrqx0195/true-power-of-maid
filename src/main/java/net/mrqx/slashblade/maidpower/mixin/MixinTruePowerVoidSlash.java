package net.mrqx.slashblade.maidpower.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityPowerPoint;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.entity.EntitySlashEffect;
import mods.flammpfeil.slashblade.entity.Projectile;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.mrqx.slashblade.maidpower.task.TaskSlashBlade;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(targets = "net.mrqx.truepower.util.TruePowerAttackManager$1")
public abstract class MixinTruePowerVoidSlash extends EntitySlashEffect {
    public MixinTruePowerVoidSlash(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        if (this.getOwner() instanceof EntityMaid maid) {
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
                    UUID.fromString("eaf33b07-3105-4676-866d-7a64640706b5"),
                    "Maid VoidSlash Transient Bonus", bonus, AttributeModifier.Operation.MULTIPLY_TOTAL);

            entityReachAttributeInstance.addTransientModifier(entityReachBonus);
            super.tick();
            entityReachAttributeInstance.removeModifier(entityReachBonus);
        } else {
            super.tick();
        }
    }

    @Inject(method = "tryDespawn()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/mrqx/truepower/util/TruePowerAttackManager$1;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"
            ), remap = false)
    private void injectTryDespawn(CallbackInfo ci) {
        if (this.getOwner() instanceof EntityMaid maid && this.level() instanceof ServerLevel serverLevel) {
            List<Entity> list = new ArrayList<>();
            list.addAll(serverLevel.getEntitiesOfClass(ItemEntity.class, maid.getBoundingBox().inflate(TaskSlashBlade.getRadius(maid))));
            list.addAll(serverLevel.getEntitiesOfClass(ExperienceOrb.class, maid.getBoundingBox().inflate(TaskSlashBlade.getRadius(maid))));
            list.addAll(serverLevel.getEntitiesOfClass(EntityPowerPoint.class, maid.getBoundingBox().inflate(TaskSlashBlade.getRadius(maid))));
            list.forEach(entity -> entity.teleportTo(serverLevel, maid.getX(), maid.getY(), maid.getZ(), RelativeMovement.ALL, entity.getYHeadRot(), entity.getXRot()));
        }
    }
}
