package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.sbr_core.events.ComboStateRegistryEvent;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ComboModifyHandler {
    private static final ResourceLocation UPPER_SLASH_NAME = SlashBlade.prefix("upperslash_jump");

    @SubscribeEvent
    public static void onComboStateRegistryEvent(ComboStateRegistryEvent event) {
        ComboState.Builder builder = event.getBuilder();
        ComboState combo = event.getCombo();
        if (combo.getStartFrame() == ComboMovementModifiers.UPPER_SLASH.startFrame
                && combo.getEndFrame() == ComboMovementModifiers.UPPER_SLASH.endFrame
                && combo.getPriority() == ComboMovementModifiers.UPPER_SLASH.priority) {
            builder.addTickAction(ComboState.TimeLineTickAction.getBuilder().put(6, livingEntity -> {
                if (livingEntity instanceof EntityMaid maid && SlashBladeMaidBauble.AirCombo.checkBauble(maid)) {
                    maid.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
                        state.updateComboSeq(livingEntity, UPPER_SLASH_NAME);
                        AdvancementHelper.grantCriterion(livingEntity, AdvancementHelper.ADVANCEMENT_UPPERSLASH_JUMP);
                    });
                }
            }).build());
        }
    }

    private enum ComboMovementModifiers {
        UPPER_SLASH(1600, 1659, 90);

        public final int startFrame;
        public final int endFrame;
        public final int priority;

        ComboMovementModifiers(int startFrame, int endFrame, int priority) {
            this.startFrame = startFrame;
            this.endFrame = endFrame;
            this.priority = priority;
        }
    }
}
