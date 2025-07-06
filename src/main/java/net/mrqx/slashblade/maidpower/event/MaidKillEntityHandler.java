package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;

@Mod.EventBusSubscriber
public class MaidKillEntityHandler {
    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof EntityMaid maid) {
            ItemStack stack = maid.getMainHandItem();
            if (!stack.isEmpty()) {
                if (stack.getCapability(ItemSlashBlade.BLADESTATE).isPresent()) {
                    IConcentrationRank.ConcentrationRanks rankBonus = maid.getCapability(ConcentrationRankCapabilityProvider.RANK_POINT).map((rp) -> rp.getRank(maid.level().getGameTime())).orElse(IConcentrationRank.ConcentrationRanks.NONE);
                    int souls = (int) Math.floor(event.getEntity().getExperienceReward() * (1 + rankBonus.level * 0.1) * (SlashBladeMaidBauble.UnawakenedSoul.checkBauble(maid) ? 1 : 0.8));
                    stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> state.setProudSoulCount(state.getProudSoulCount() + Math.min(SlashBladeConfig.MAX_PROUD_SOUL_GOT.get(), souls)));
                }
            }
            if (event.getEntity() instanceof OwnableEntity ownable) {
                maid.setTarget(ownable.getOwner());
                MaidGuardHandler.trickToTarget(maid, ownable.getOwner());
            }
        }
    }
}
