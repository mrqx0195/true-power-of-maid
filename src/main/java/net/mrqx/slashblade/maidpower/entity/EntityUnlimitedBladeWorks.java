package net.mrqx.slashblade.maidpower.entity;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;

import javax.annotation.Nullable;
import java.util.*;

public class EntityUnlimitedBladeWorks extends LivingEntity implements OwnableEntity, ISlashBladeMaid {
    private static final EntityDataAccessor<Optional<UUID>> OWNER_ID = SynchedEntityData.defineId(EntityUnlimitedBladeWorks.class, EntityDataSerializers.OPTIONAL_UUID);
    private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
    
    public EntityUnlimitedBladeWorks(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return EntityMaid.createAttributes();
    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder synchedEntityDataBuilder) {
        super.defineSynchedData(synchedEntityDataBuilder);
        synchedEntityDataBuilder.define(OWNER_ID, Optional.empty());
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("OwnerUUID")) {
            this.setOwnerUUID(NbtUtils.loadUUID(Objects.requireNonNull(compound.get("OwnerUUID"))));
        }
        
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.entityData.get(OWNER_ID).ifPresent((uuid) -> compound.putUUID("OwnerUUID", uuid));
    }
    
    @Override
    public Iterable<ItemStack> getHandSlots() {
        return this.handItems;
    }
    
    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }
    
    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return switch (slot.getType()) {
            case HAND -> this.handItems.get(slot.getIndex());
            case HUMANOID_ARMOR -> this.armorItems.get(slot.getIndex());
            default -> ItemStack.EMPTY;
        };
    }
    
    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        this.verifyEquippedItem(stack);
        switch (slot.getType()) {
            case HAND:
                this.onEquipItem(slot, this.handItems.set(slot.getIndex(), stack), stack);
                break;
            case HUMANOID_ARMOR:
                this.onEquipItem(slot, this.armorItems.set(slot.getIndex(), stack), stack);
                break;
            default:
                break;
        }
        
    }
    
    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
    
    @Override
    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_ID).orElse(null);
    }
    
    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(OWNER_ID, Optional.ofNullable(uuid));
    }
    
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }
    
    @Override
    public void tick() {
        super.tick();
        this.getMainHandItem().inventoryTick(this.level(), this, 0, true);
        this.setHealth(this.getHealth() - 1);
        if (this.getHealth() <= 0) {
            this.discard();
        }
    }
    
    @Override
    public boolean useUpperSlashJump() {
        return this.getOwner() instanceof ISlashBladeEntity slashBladeEntity
            ? slashBladeEntity.useUpperSlashJump()
            : ISlashBladeMaid.super.useUpperSlashJump();
    }
    
    @Override
    public void setCurrentAnimation(@Nullable VanillaConvertedVmdAnimation currentAnimation) {
        if (this.getOwner() instanceof ISlashBladeEntity slashBladeEntity) {
            slashBladeEntity.setCurrentAnimation(currentAnimation);
        } else {
            ISlashBladeMaid.super.setCurrentAnimation(currentAnimation);
        }
    }
    
    @Override
    public @Nullable VanillaConvertedVmdAnimation getCurrentAnimation() {
        return this.getOwner() instanceof ISlashBladeEntity slashBladeEntity
            ? slashBladeEntity.getCurrentAnimation()
            : ISlashBladeMaid.super.getCurrentAnimation();
    }
    
    @Override
    public Set<Class<? extends Entity>> getAttackableEntities() {
        return this.getOwner() instanceof ISlashBladeEntity slashBladeEntity
            ? slashBladeEntity.getAttackableEntities()
            : ISlashBladeMaid.super.getAttackableEntities();
    }
    
    @Override
    public boolean canUseCombo(ResourceLocation combo) {
        return this.getOwner() instanceof ISlashBladeEntity slashBladeEntity
            ? slashBladeEntity.canUseCombo(combo)
            : ISlashBladeMaid.super.canUseCombo(combo);
    }
    
    @Override
    public boolean canProgressCombo(LivingEntity target, ResourceLocation current, ResourceLocation next) {
        return this.getOwner() instanceof ISlashBladeEntity slashBladeEntity
            ? slashBladeEntity.canProgressCombo(target, current, next)
            : ISlashBladeMaid.super.canProgressCombo(target, current, next);
    }
    
    @Override
    public void hitEffect(LivingEntity enemy) {
        if (this.getOwner() instanceof ISlashBladeEntity slashBladeEntity) {
            slashBladeEntity.hitEffect(enemy);
        } else {
            ISlashBladeMaid.super.hitEffect(enemy);
        }
    }
    
    @Override
    public List<Entity> processTargetList(Level world, LivingEntity attacker, AABB aabb, double reach, List<Entity> originalTargetList) {
        return this.getOwner() instanceof ISlashBladeEntity slashBladeEntity ? slashBladeEntity.processTargetList(world, attacker, aabb, reach, originalTargetList)
            : ISlashBladeMaid.super.processTargetList(world, attacker, aabb, reach, originalTargetList);
    }
}
