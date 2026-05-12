package net.mrqx.slashblade.maidpower.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityPowerPoint;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.mrqx.slashblade.maidpower.TruePowerOfMaid;
import net.mrqx.slashblade.maidpower.task.TaskSlashBlade;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(targets = "net.mrqx.truepower.util.TruePowerAttackManager$1")
public abstract class MixinTruePowerVoidSlash extends EntitySlashEffect {
    public MixinTruePowerVoidSlash(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }
    
    @Override
    public void tick() {
        if (this.getOwner() instanceof EntityMaid maid) {
            AttributeInstance entityReachAttributeInstance = maid.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
            if (entityReachAttributeInstance == null) {
                return;
            }
            
            double radius = TaskSlashBlade.getRadius(maid);
            int rank = maid.getData(CapabilityConcentrationRank.RANK_POINT).getRank(maid.level().getGameTime()).level;
            double bonus = radius / Math.max(TargetSelector.getResolvedReach(maid), 1) * rank / 7;
            
            AttributeModifier entityReachBonus = new AttributeModifier(
                TruePowerOfMaid.prefix("maid_void_slash_transient_bonus"), bonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            
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
