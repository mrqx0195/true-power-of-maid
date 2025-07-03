package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidPickupEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.slashblade.maidpower.item.MaidItems;

@Mod.EventBusSubscriber
public class MaidPickupHandler {
    @SubscribeEvent
    public static void onMaidPickupExperience(MaidPickupEvent.ExperienceResult event) {
        event.getMaid().getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            if (MaidItems.SlashBladeMaidBauble.Exp.checkBauble(event.getMaid())) {
                state.setDamage(state.getDamage() - event.getExperienceOrb().getValue());
            }
        });
    }

    @SubscribeEvent
    public static void onMaidPickupPowerPoint(MaidPickupEvent.PowerPointResult event) {
        event.getMaid().getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            if (MaidItems.SlashBladeMaidBauble.Exp.checkBauble(event.getMaid())) {
                state.setDamage(state.getDamage() - event.getPowerPoint().getValue());
            }
        });
    }
}
