package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.slashblade.maidpower.item.MaidItems;
import net.mrqx.truepower.entity.EntityBlastSummonedSword;

@Mod.EventBusSubscriber
public class EntityJoinLevelHandler {
    @SubscribeEvent
    public static void onEntityJoinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof EntityBlastSummonedSword summonedSword) {
            if (summonedSword.getOwner() instanceof EntityMaid maid) {
                if (!MaidItems.SlashBladeMaidBauble.MirageBlade.checkBauble(maid)) {
                    EntityBlastSummonedSword.getPreSummonSwordList(maid).clear();
                    EntityBlastSummonedSword.getPreBlastSwordList(maid).clear();
                    event.setCanceled(true);
                }
            }
        }
    }
}
