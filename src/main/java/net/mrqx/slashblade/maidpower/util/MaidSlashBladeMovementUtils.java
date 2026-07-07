package net.mrqx.slashblade.maidpower.util;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.mrqx.sbr_core.utils.SlashBladeMovementUtils;
import net.mrqx.slashblade.maidpower.event.MaidGuardHandler;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;

public class MaidSlashBladeMovementUtils {
    public static boolean canTrick(EntityMaid maid) {
        return SlashBladeMaidBauble.Trick.checkBauble(maid)
            && !MaidGuardHandler.isGuarding(maid)
            && !maid.isMaidInSittingPose()
            && SlashBladeMovementUtils.canTrick(maid, SlashBladeMaidBauble.TruePower.checkBauble(maid));
    }
}
