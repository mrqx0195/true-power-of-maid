package net.mrqx.slashblade.maidpower.event.api;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

@Cancelable
public class MaidProgressComboEvent extends Event {
    private final EntityMaid maid;
    @Nullable
    private final LivingEntity target;
    @Nullable
    private final ResourceLocation current;
    private final ResourceLocation next;
    
    public MaidProgressComboEvent(EntityMaid maid, @Nullable LivingEntity target, @Nullable ResourceLocation current, ResourceLocation next) {
        this.maid = maid;
        this.target = target;
        this.current = current;
        this.next = next;
    }
    
    public EntityMaid getMaid() {
        return maid;
    }
    
    @Nullable
    public LivingEntity getTarget() {
        return target;
    }
    
    @Nullable
    public ResourceLocation getCurrentCombo() {
        return current;
    }
    
    public ResourceLocation getNextCombo() {
        return next;
    }
}
