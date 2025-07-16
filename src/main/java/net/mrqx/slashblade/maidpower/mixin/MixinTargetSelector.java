package net.mrqx.slashblade.maidpower.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.stream.Stream;

@Mixin(TargetSelector.class)
public abstract class MixinTargetSelector {
    @Inject(method = "getTargettableEntitiesWithinAABB(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/phys/AABB;D)Ljava/util/List;",
            at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private static void injectGetTargettableEntities(Level world, LivingEntity attacker, AABB aabb, double reach, CallbackInfoReturnable<List<Entity>> cir, List<Entity> list1) {
        if (attacker instanceof EntityMaid maid) {
            list1.addAll(world.getEntitiesOfClass(LivingEntity.class, aabb.inflate(5), IForgeEntity::isMultipartEntity).stream()
                    .flatMap(e -> (e.isMultipartEntity()) ? Stream.of(e.getParts()) : Stream.of(e)).filter(t -> {
                        boolean result = false;
                        if (t instanceof LivingEntity living) {
                            result = maid.canAttack(living);
                        } else if (t instanceof PartEntity<?> part) {
                            if (part.getParent() instanceof LivingEntity living) {
                                result = maid.canAttack(living) && part.distanceToSqr(maid) < (reach * reach);
                            }
                        }
                        return result;
                    }).toList());

            list1.addAll(world.getEntitiesOfClass(LivingEntity.class, aabb).stream()
                    .flatMap(e -> (e.isMultipartEntity()) ? Stream.of(e.getParts()) : Stream.of(e)).filter(t -> {
                        boolean result = false;
                        if (t instanceof LivingEntity living) {
                            result = maid.canAttack(living);
                        } else if (t instanceof PartEntity<?> part) {
                            if (part.getParent() instanceof LivingEntity living) {
                                result = maid.canAttack(living) && part.distanceToSqr(maid) < (reach * reach);
                            }
                        }
                        return result;
                    }).toList());
            list1.removeIf(entity -> entity instanceof EntityMaid || entity instanceof Player);

            List<Entity> list = list1.stream().distinct().toList();
            list1.clear();
            list1.addAll(list);
        }
    }
}
