package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import mods.flammpfeil.slashblade.registry.ModAttributes;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributeModificationHandler {
    @SubscribeEvent
    public static void onEntityAttributeModificationEvent(EntityAttributeModificationEvent event) {
        event.add(InitEntities.MAID.get(), ModAttributes.SLASHBLADE_DAMAGE.get());
    }
}
