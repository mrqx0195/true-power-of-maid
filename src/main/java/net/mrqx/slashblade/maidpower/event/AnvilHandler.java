package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.item.bauble.BaubleManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.slashblade.maidpower.init.MaidPowerItems;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.slashblade.maidpower.item.TruePowerBaubleItem;

@Mod.EventBusSubscriber
public class AnvilHandler {
    @SubscribeEvent
    public static void onAnvilUpdateEvent(AnvilUpdateEvent event) {
        if (!event.getOutput().isEmpty()) {
            return;
        }
        ItemStack base = event.getLeft();
        ItemStack material = event.getRight();
        if (base.isEmpty() || material.isEmpty()) {
            return;
        }

        if (base.is(MaidPowerItems.SOUL_OF_TRUE_POWER.get())) {
            IMaidBauble bauble = BaubleManager.getBauble(material);
            if (bauble instanceof SlashBladeMaidBauble && !(bauble instanceof SlashBladeMaidBauble.TruePower)) {
                ItemStack output = base.copy();

                TruePowerBaubleItem.addSoul(output, material);

                event.setMaterialCost(1);
                event.setCost(30);
                event.setOutput(output);
            }
        }
    }
}
