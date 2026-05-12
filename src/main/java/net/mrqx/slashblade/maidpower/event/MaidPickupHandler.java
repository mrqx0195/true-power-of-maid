package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidPickupEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class MaidPickupHandler {
    @SubscribeEvent
    public static void onMaidPickupExperience(MaidPickupEvent.ExperienceResult event) {
        EntityMaid maid = event.getMaid();
        BladeStateAccess.of(maid.getMainHandItem()).ifPresent(state -> {
            if (SlashBladeMaidBauble.Exp.checkBauble(maid)) {
                state.setDamage(state.getDamage() - event.getExperienceOrb().getValue());
            }
        });
    }
    
    @SubscribeEvent
    public static void onMaidPickupPowerPoint(MaidPickupEvent.PowerPointResult event) {
        EntityMaid maid = event.getMaid();
        BladeStateAccess.of(maid.getMainHandItem()).ifPresent(state -> {
            if (SlashBladeMaidBauble.Exp.checkBauble(maid)) {
                state.setDamage(state.getDamage() - event.getPowerPoint().getValue());
            }
        });
    }
}
