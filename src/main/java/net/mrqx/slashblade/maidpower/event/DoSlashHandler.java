package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DoSlashHandler {
    public static final String LAST_DO_SLASH_TIME = "truePowerOfMaid.lastDoSlashTime";

    @SubscribeEvent
    public static void onDoSlashEvent(SlashBladeEvent.DoSlashEvent event) {
        if (event.getUser() instanceof EntityMaid maid) {
            CompoundTag data = maid.getPersistentData();
            data.putLong(LAST_DO_SLASH_TIME, maid.level().getGameTime());
        }
    }
}
