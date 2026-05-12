package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class DoSlashHandler {
    public static final String LAST_DO_SLASH_TIME = "truePowerOfMaid.lastDoSlashTime";
    
    @SubscribeEvent
    public static void onDoSlashEvent(SlashBladeEvent.DoSlashEvent event) {
        if (event.getUser() instanceof EntityMaid maid) {
            CompoundTag data = maid.getPersistentData();
            data.putLong(LAST_DO_SLASH_TIME, maid.level().getGameTime());
            if (SlashBladeMaidBauble.UnlimitedBladeWorks.checkBauble(maid)) {
                float roll = event.getRoll();
                boolean mute = false;
                boolean critical = event.isCritical();
                double damage = event.getDamage();
                Vec3 pos = maid.position().add(maid.level().random.nextDouble(), maid.level().random.nextDouble(), maid.level().random.nextDouble());
                float xRot = maid.getXRot();
                float yRot = maid.getYRot();
                
                SlashBladeMaidBauble.UnlimitedBladeWorks.ubwDoSlash(maid, pos, xRot, yRot, roll, mute, critical, damage);
            }
        }
    }
}
