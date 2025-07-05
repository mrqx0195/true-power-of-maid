package net.mrqx.slashblade.maidpower.event.api;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class MaidProgressComboEvent extends Event {
    private final EntityMaid maid;
    private final LivingEntity target;
    private final ResourceLocation current;
    private final ResourceLocation next;

    public MaidProgressComboEvent(EntityMaid maid, LivingEntity target, ResourceLocation current, ResourceLocation next) {
        this.maid = maid;
        this.target = target;
        this.current = current;
        this.next = next;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public ResourceLocation getCurrentCombo() {
        return current;
    }

    public ResourceLocation getNextCombo() {
        return next;
    }
}
