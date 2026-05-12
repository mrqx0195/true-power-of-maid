package net.mrqx.slashblade.maidpower.util;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.github.tartaricacid.touhoulittlemaid.item.bauble.BaubleManager;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.item.ItemStack;
import net.mrqx.slashblade.maidpower.item.PowerfulSlashBladeBaubleItem;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MaidItemUtils {
    public static int getBaubleCountForClass(EntityMaid maid, Class<?> clazz) {
        BaubleItemHandler handler = maid.getMaidBauble();
        
        AtomicInteger count = new AtomicInteger(0);
        AtomicBoolean powerfulBaubleFlag = new AtomicBoolean(false);
        for (int i = 0; i < handler.getSlots(); ++i) {
            IMaidBauble baubleIn = handler.getBaubleInSlot(i);
            if (clazz.isInstance(baubleIn)) {
                count.getAndIncrement();
            }
            if (baubleIn instanceof SlashBladeMaidBauble.IPowerfulSlashBladeBauble && !powerfulBaubleFlag.get()) {
                count.set(0);
                powerfulBaubleFlag.set(true);
                ItemStack itemStack = handler.getStackInSlot(i);
                PowerfulSlashBladeBaubleItem.getSouls(itemStack).forEach(itemStack1 -> {
                    if (clazz.isInstance(BaubleManager.getBauble(itemStack1))) {
                        count.getAndIncrement();
                    }
                });
                return count.get();
            }
        }
        return count.get();
    }
    
    public static List<ItemStack> getAllSlashBlade(EntityMaid maid) {
        ItemStackHandler maidInv = maid.getMaidInv();
        List<ItemStack> bladeList = new ArrayList<>();
        for (int i = 0; i < maidInv.getSlots(); i++) {
            ItemStack stack = maidInv.getStackInSlot(i);
            if (BladeStateAccess.of(stack).isPresent()) {
                bladeList.add(stack);
            }
        }
        return bladeList;
    }
    
    public static List<ItemStack> getAllSlashBladeUnbroken(EntityMaid maid) {
        List<ItemStack> list = getAllSlashBlade(maid);
        list.removeIf(itemStack -> BladeStateAccess.of(itemStack).map(ISlashBladeState::isBroken).orElse(false));
        return list;
    }
}
