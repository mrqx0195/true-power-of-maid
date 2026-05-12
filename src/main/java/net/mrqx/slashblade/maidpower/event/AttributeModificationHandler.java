package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import mods.flammpfeil.slashblade.registry.ModAttributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@EventBusSubscriber
public class AttributeModificationHandler {
    @SubscribeEvent
    public static void onEntityAttributeModificationEvent(EntityAttributeModificationEvent event) {
        event.add(InitEntities.MAID.get(), ModAttributes.SLASHBLADE_DAMAGE);
    }
}
