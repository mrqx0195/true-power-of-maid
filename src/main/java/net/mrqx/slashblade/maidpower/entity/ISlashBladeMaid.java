package net.mrqx.slashblade.maidpower.entity;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.slashblade.maidpower.event.api.MaidProgressComboEvent;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ISlashBladeMaid extends ISlashBladeEntity {
    @Override
    default boolean useUpperSlashJump() {
        return this instanceof EntityMaid maid ? SlashBladeMaidBauble.AirCombo.checkBauble(maid) : ISlashBladeEntity.super.useUpperSlashJump();
    }
    
    @Override
    default void setCurrentAnimation(@Nullable VanillaConvertedVmdAnimation currentAnimation) {
    }
    
    @Override
    default @Nullable VanillaConvertedVmdAnimation getCurrentAnimation() {
        return null;
    }
    
    @Override
    default Set<Class<? extends Entity>> getAttackableEntities() {
        return Set.of(LivingEntity.class);
    }
    
    @Override
    default boolean canUseCombo(ResourceLocation combo) {
        if (this instanceof EntityMaid maid) {
            MaidProgressComboEvent event = new MaidProgressComboEvent(maid, null, maid.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE)
                .map(ISlashBladeState::getComboSeq).orElse(ComboStateRegistry.NONE.getId()), combo);
            MinecraftForge.EVENT_BUS.post(event);
            return !event.isCanceled();
        }
        return true;
    }
    
    @Override
    default boolean canProgressCombo(LivingEntity target, ResourceLocation current, ResourceLocation next) {
        if (this instanceof EntityMaid maid) {
            MaidProgressComboEvent event = new MaidProgressComboEvent(maid, null, current, next);
            MinecraftForge.EVENT_BUS.post(event);
            return !event.isCanceled();
        }
        return true;
    }
}
