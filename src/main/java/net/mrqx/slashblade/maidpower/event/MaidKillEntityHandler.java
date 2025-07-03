package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class MaidKillEntityHandler {
    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof EntityMaid maid) {
            if (event.getEntity() instanceof OwnableEntity ownable) {
                maid.setTarget(ownable.getOwner());
                MaidGuardHandler.trickToTarget(maid, ownable.getOwner());
            }
        }
    }
}
