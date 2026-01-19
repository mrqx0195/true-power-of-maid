package net.mrqx.slashblade.maidpower.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.AttackManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.slashblade.maidpower.TruePowerOfMaid;
import net.mrqx.slashblade.maidpower.entity.EntityUnlimitedBladeWorks;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.slashblade.maidpower.util.MaidItemUtils;

import java.util.List;

@Mod.EventBusSubscriber
public class DoSlashHandler {
    public static final String LAST_DO_SLASH_TIME = "truePowerOfMaid.lastDoSlashTime";

    @SubscribeEvent
    public static void onDoSlashEvent(SlashBladeEvent.DoSlashEvent event) {
        if (event.getUser() instanceof EntityMaid maid) {
            CompoundTag data = maid.getPersistentData();
            data.putLong(LAST_DO_SLASH_TIME, maid.level().getGameTime());
            if (SlashBladeMaidBauble.UnlimitedBladeWorks.checkBauble(maid)) {
                List<ItemStack> bladeList = MaidItemUtils.getAllSlashBlade(maid);
                ItemStack blade = bladeList.get(maid.level().random.nextInt(bladeList.size()));

                EntityUnlimitedBladeWorks ubw = new EntityUnlimitedBladeWorks(TruePowerOfMaid.RegistryEvents.UnlimitedBladeWorks, maid.level());
                ubw.setOwnerUUID(maid.getUUID());
                ubw.setItemSlot(EquipmentSlot.MAINHAND, blade.copy());
                ubw.getAttributes().assignValues(maid.getAttributes());
                ubw.setPos(maid.position().add(maid.level().random.nextDouble(), maid.level().random.nextDouble(), maid.level().random.nextDouble()));
                ubw.setXRot(maid.getXRot());
                ubw.setYRot(maid.getYRot());
                ubw.getCapability(ConcentrationRankCapabilityProvider.RANK_POINT).ifPresent(rank ->
                        maid.getCapability(ConcentrationRankCapabilityProvider.RANK_POINT).ifPresent(maidRank -> {
                            rank.setRawRankPoint(maidRank.getRawRankPoint());
                            rank.setLastRankRise(maidRank.getLastRankRise());
                            rank.setLastUpdte(maidRank.getLastUpdate());
                        })
                );
                maid.level().addFreshEntity(ubw);

                AttackManager.doSlash(ubw, event.getRoll(), false, event.isCritical(), event.getDamage());
                blade.hurtAndBreak(1, maid, entityMaid ->
                        entityMaid.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }
        }
    }
}
