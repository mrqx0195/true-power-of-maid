package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.sbr_core.events.StunEvent;
import net.mrqx.slashblade.maidpower.item.MaidItems;

@Mod.EventBusSubscriber
public class StunHandler {
    @SubscribeEvent
    public static void onStunEvent(StunEvent event) {
        if (event.getEntity() instanceof EntityMaid maid && MaidItems.SlashBladeMaidBauble.TruePower.checkBauble(maid)) {
            event.setCanceled(true);
        }
    }
}
