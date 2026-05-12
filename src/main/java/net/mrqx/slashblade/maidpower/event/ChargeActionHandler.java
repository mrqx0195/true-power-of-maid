package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.truepower.util.JustSlashArtManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class ChargeActionHandler {
    @SubscribeEvent
    public static void onPerformSlashArtEvent(SlashBladeEvent.PerformSlashArtEvent event) {
        if (event.getEntityLiving() instanceof EntityMaid maid) {
            BladeStateAccess.of(maid.getMainHandItem())
                .ifPresent(state -> onPerformSlashArt(event, maid, state));
        }
    }
    
    private static void onPerformSlashArt(SlashBladeEvent.PerformSlashArtEvent event, EntityMaid maid, ISlashBladeState state) {
        if (!SlashBladeMaidBauble.JudgementCut.checkBauble(maid) || JustSlashArtManager.getJustCooldown(maid) > 0) {
            event.setCanceled(true);
            return;
        }
        if (isJudgementCut(event.getComboState())) {
            int count = JustSlashArtManager.addJustCount(maid);
            int maxCount = SlashBladeMaidBauble.JustJudgementCut.checkBauble(maid) ? (SlashBladeMaidBauble.TruePower.checkBauble(maid) ? 5 : 3) : 1;
            if (count > maxCount) {
                JustSlashArtManager.setJustCooldown(maid, 240);
                JustSlashArtManager.resetJustCount(maid);
                event.setCanceled(true);
            }
            if (event.getType() == SlashArts.ArtsType.Jackpot) {
                AdvancementHelper.grantedIf(maid.registryAccess().holderOrThrow(Enchantments.SOUL_SPEED).value(), maid);
            }
        } else if (SlashBladeMaidBauble.UnlimitedBladeWorks.checkBauble(maid)) {
            JustSlashArtManager.setJustCooldown(maid, 240);
            if (event.getType() == SlashArts.ArtsType.Jackpot) {
                AdvancementHelper.grantedIf(maid.registryAccess().holderOrThrow(Enchantments.SOUL_SPEED).value(), maid);
            }
        } else {
            event.setCanceled(true);
        }
    }
    
    public static boolean isJudgementCut(ResourceLocation combo) {
        return combo.equals(ComboStateRegistry.JUDGEMENT_CUT.getId())
            || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SHEATH_JUST.getId())
            || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SLASH.getId())
            || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SLASH_AIR.getId())
            || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST.getId())
            || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST2.getId())
            || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_END.getId());
    }
}
