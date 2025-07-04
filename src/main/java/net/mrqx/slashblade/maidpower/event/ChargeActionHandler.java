package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.sbr_core.events.ChargeActionEvent;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;

@Mod.EventBusSubscriber
public class ChargeActionHandler {
    @SubscribeEvent
    public static void onChargeActionEvent(ChargeActionEvent event) {
        if (event.getEntityLiving() instanceof EntityMaid maid) {
            maid.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
                if (!SlashBladeMaidBauble.JudgementCut.checkBauble(maid) && !SlashBladeMaidBauble.JustJudgementCut.checkBauble(maid)) {
                    event.setCanceled(true);
                } else if (isJudgementCut(event.getComboState())) {
                    ResourceLocation saLoc = SlashArtsRegistry.JUDGEMENT_CUT.get().doArts(event.getType(), maid);
                    if (saLoc != ComboStateRegistry.NONE.getId()) {
                        if (event.getType() == SlashArts.ArtsType.Jackpot) {
                            AdvancementHelper.grantedIf(Enchantments.SOUL_SPEED, maid);
                        }
                        state.updateComboSeq(maid, saLoc);
                    }
                }
            });
        }
    }

    public static boolean isJudgementCut(ResourceLocation combo) {
        return combo.equals(ComboStateRegistry.JUDGEMENT_CUT.getId())
               || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SLASH.getId())
               || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SLASH_AIR.getId())
               || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST.getId())
               || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST2.getId())
               || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SHEATH.getId())
               || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SHEATH_AIR.getId())
               || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_SHEATH_JUST.getId())
               || combo.equals(ComboStateRegistry.JUDGEMENT_CUT_END.getId());
    }
}
