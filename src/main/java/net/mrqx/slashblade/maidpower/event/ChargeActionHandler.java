package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.truepower.util.JustSlashArtManager;

@Mod.EventBusSubscriber
public class ChargeActionHandler {
    @SubscribeEvent
    public static void onChargeActionEvent(SlashBladeEvent.ChargeActionEvent event) {
        if (event.getEntityLiving() instanceof EntityMaid maid) {
            maid.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE)
                    .ifPresent(state -> onChargeAction(event, maid, state));
        }
    }

    private static void onChargeAction(SlashBladeEvent.ChargeActionEvent event, EntityMaid maid, ISlashBladeState state) {
        if (!SlashBladeMaidBauble.JudgementCut.checkBauble(maid) && !SlashBladeMaidBauble.JustJudgementCut.checkBauble(maid)) {
            event.setCanceled(true);
            return;
        }
        if (isJudgementCut(event.getComboState())) {
            int count = JustSlashArtManager.addJustCount(maid);
            int maxCount = SlashBladeMaidBauble.JustJudgementCut.checkBauble(maid) ? (SlashBladeMaidBauble.TruePower.checkBauble(maid) ? 5 : 3) : 1;
            if (count > maxCount) {
                JustSlashArtManager.setJustCooldown(maid, 240);
                event.setCanceled(true);
            }
            if (event.getType() == SlashArts.ArtsType.Jackpot) {
                AdvancementHelper.grantedIf(Enchantments.SOUL_SPEED, maid);
            }
        }
    }

    public static boolean isJudgementCut(ResourceLocation combo) {
        return combo.equals(ComboStateRegistry.JUDGEMENT_CUT.getId())
                || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SLASH.getId())
                || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SLASH_AIR.getId())
                || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST.getId())
                || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST2.getId())
                || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_END.getId());
    }
}
