package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.truepower.entity.EntityBlastSummonedSword;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber
public class EntityJoinLevelHandler {
    @SubscribeEvent
    public static void onEntityJoinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof EntityBlastSummonedSword summonedSword) {
            if (summonedSword.getOwner() instanceof EntityMaid maid) {
                if (!SlashBladeMaidBauble.MirageBlade.checkBauble(maid)) {
                    EntityBlastSummonedSword.getPreSummonSwordList(maid).clear();
                    EntityBlastSummonedSword.getPreBlastSwordList(maid).clear();
                    event.setCanceled(true);
                }
            }
        }
    }
}
