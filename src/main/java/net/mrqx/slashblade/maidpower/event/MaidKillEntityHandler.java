package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.item.ItemStack;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class MaidKillEntityHandler {
    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof EntityMaid maid && maid.level() instanceof ServerLevel serverLevel) {
            ItemStack stack = maid.getMainHandItem();
            BladeStateAccess.of(stack).ifPresent(state -> {
                IConcentrationRank.ConcentrationRanks rankBonus = maid.getData(CapabilityConcentrationRank.RANK_POINT).getRank(maid.level().getGameTime());
                int souls = (int) Math.floor(event.getEntity().getExperienceReward(serverLevel, maid) * (1 + rankBonus.level * 0.1) * (SlashBladeMaidBauble.UnawakenedSoul.checkBauble(maid) ? 1 : 0.8));
                state.setProudSoulCount(state.getProudSoulCount() + Math.min(SlashBladeConfig.MAX_PROUD_SOUL_GOT.get(), souls));
            });
            if (event.getEntity() instanceof OwnableEntity ownable && ownable.getOwner() != null) {
                maid.setTarget(ownable.getOwner());
                MaidGuardHandler.trickToTarget(maid, ownable.getOwner());
            }
        }
    }
}
