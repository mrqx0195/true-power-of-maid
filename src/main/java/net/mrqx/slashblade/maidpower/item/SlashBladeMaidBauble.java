package net.mrqx.slashblade.maidpower.item;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDeathEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.github.tartaricacid.touhoulittlemaid.item.bauble.BaubleManager;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.slashblade.maidpower.config.TruePowerOfMaidCommonConfig;
import net.mrqx.slashblade.maidpower.event.MaidTickHandler;
import net.mrqx.slashblade.maidpower.event.api.MaidProgressComboEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SlashBladeMaidBauble implements IMaidBauble {
    @Mod.EventBusSubscriber
    public static class UnawakenedSoul extends SlashBladeMaidBauble {
        @SubscribeEvent
        public static void onLivingDeathEvent(LivingDeathEvent event) {
            if (event.isCanceled()) {
                return;
            }
            if (event.getSource().getEntity() instanceof EntityMaid maid) {
                BaubleItemHandler handler = maid.getMaidBauble();
                RandomSource random = maid.level().getRandom();
                int exp = event.getEntity().getExperienceReward();
                long exp4 = (long) exp * exp * exp;
                double chance = Math.min(1.0, exp4 / 100000.0);
                if (random.nextDouble() < chance) {
                    int i = random.nextInt(handler.getSlots());
                    IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                    if (baubleIn instanceof UnawakenedSoul && random.nextDouble() < chance) {
                        String item = TruePowerOfMaidCommonConfig.UNAWAKENED_SOUL_RANGE_MAP.get(random.nextDouble() * TruePowerOfMaidCommonConfig.unawakenedSoulTotalRange);
                        if (item != null) {
                            Item item1 = maid.level().registryAccess().registryOrThrow(Registries.ITEM).get(new ResourceLocation(item));
                            if (item1 != null) {
                                handler.setStackInSlot(i, new ItemStack(item1));
                            }
                        }
                    }
                }
            }
        }

        public static boolean checkBauble(EntityMaid maid) {
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, UnawakenedSoul.class) > 0;
        }
    }

    @Mod.EventBusSubscriber
    public static class ComboB extends SlashBladeMaidBauble {
        @SubscribeEvent
        public static void onMaidProgressComboEvent(MaidProgressComboEvent event) {
            if (checkBauble(event.getMaid())) {
                if (event.getCurrentCombo().equals(ComboStateRegistry.COMBO_A3.getId()) || event.getCurrentCombo().equals(ComboStateRegistry.AERIAL_RAVE_A2.getId())) {
                    event.setCanceled(true);
                }
            } else {
                if (event.getNextCombo().equals(ComboStateRegistry.COMBO_B1.getId()) || event.getNextCombo().equals(ComboStateRegistry.AERIAL_RAVE_B3.getId())) {
                    event.setCanceled(true);
                }
            }
        }

        public static boolean checkBauble(EntityMaid maid) {
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, ComboB.class) > 0;
        }
    }

    @Mod.EventBusSubscriber
    public static class ComboC extends SlashBladeMaidBauble {
        @SubscribeEvent
        public static void onMaidProgressComboEvent(MaidProgressComboEvent event) {
            if (checkBauble(event.getMaid())) {
                if (event.getCurrentCombo().equals(ComboStateRegistry.COMBO_A2.getId())) {
                    event.setCanceled(true);
                }
            } else {
                if (event.getNextCombo().equals(ComboStateRegistry.COMBO_C.getId())) {
                    event.setCanceled(true);
                }
            }
        }

        public static boolean checkBauble(EntityMaid maid) {
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, ComboC.class) > 0;
        }
    }

    @Mod.EventBusSubscriber
    public static class RapidSlash extends SlashBladeMaidBauble {
        @SubscribeEvent
        public static void onMaidProgressComboEvent(MaidProgressComboEvent event) {
            if (!checkBauble(event.getMaid()) && event.getNextCombo().equals(ComboStateRegistry.RAPID_SLASH_QUICK.getId())) {
                event.setCanceled(true);
            }
        }

        public static boolean checkBauble(EntityMaid maid) {
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, RapidSlash.class) > 0;
        }
    }

    public static class AirCombo extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, AirCombo.class) > 0;
        }
    }

    public static class MirageBlade extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, MirageBlade.class) > 0;
        }
    }

    public static class Trick extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, Trick.class) > 0;
        }
    }

    @Mod.EventBusSubscriber
    public static class Power extends SlashBladeMaidBauble {
        @SubscribeEvent
        public static void onPowerBladeEvent(SlashBladeEvent.PowerBladeEvent event) {
            if (event.getUser() instanceof EntityMaid maid && checkBauble(maid) && maid.getFavorabilityManager().getLevel() >= 3) {
                event.setPowered(true);
            }
        }

        public static boolean checkBauble(EntityMaid maid) {
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, Power.class) > 0;
        }
    }

    public static class JudgementCut extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            if (SlashBladeMaidBauble.JustJudgementCut.checkBauble(maid)) {
                return true;
            }
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, JudgementCut.class) > 0;
        }
    }

    public static class JustJudgementCut extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, JustJudgementCut.class) > 0;
        }
    }

    public static class VoidSlash extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, VoidSlash.class) > 0;
        }
    }

    public static class Guard extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, Guard.class) > 0;
        }
    }

    public static class Health extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, Health.class) > 0;
        }
    }

    public static class Exp extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return SlashBladeMaidBauble.getBaubleCountForClass(maid, Exp.class) > 0;
        }
    }

    @Mod.EventBusSubscriber
    public static class TruePower extends SlashBladeMaidBauble {
        @SubscribeEvent
        public static void onPowerBladeEvent(SlashBladeEvent.PowerBladeEvent event) {
            if (event.getUser() instanceof EntityMaid maid && checkBauble(maid)) {
                event.setPowered(true);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public void onMaidDeathEvent(MaidDeathEvent event) {
            if (checkBauble(event.getMaid())) {
                CompoundTag data = event.getMaid().getPersistentData();
                if (data.getLong(MaidTickHandler.TRUE_POWER_RANK) >= 0) {
                    data.putLong(MaidTickHandler.TRUE_POWER_RANK, data.getLong(MaidTickHandler.TRUE_POWER_RANK) - 300);
                    if (data.getLong(MaidTickHandler.TRUE_POWER_RANK) >= 0) {
                        event.setCanceled(true);
                        event.getMaid().setHealth(event.getMaid().getMaxHealth());
                    }
                }
            }
        }

        public static boolean checkBauble(EntityMaid maid) {
            if (maid.getFavorabilityManager().getLevel() < 3) {
                return false;
            }
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof TruePower) {
                    return true;
                }
            }
            return false;
        }
    }

    public static int getBaubleCountForClass(EntityMaid maid, Class<?> clazz) {
        BaubleItemHandler handler = maid.getMaidBauble();

        AtomicInteger count = new AtomicInteger(0);
        AtomicBoolean truePowerFlag = new AtomicBoolean(false);
        for (int i = 0; i < handler.getSlots(); ++i) {
            IMaidBauble baubleIn = handler.getBaubleInSlot(i);
            if (clazz.isInstance(baubleIn)) {
                count.getAndIncrement();
            }
            if (baubleIn instanceof TruePower && !truePowerFlag.get()) {
                count.set(0);
                truePowerFlag.set(true);
                ItemStack itemStack = handler.getStackInSlot(i);
                TruePowerBaubleItem.getSouls(itemStack).forEach(itemStack1 -> {
                    if (clazz.isInstance(BaubleManager.getBauble(itemStack1))) {
                        count.getAndIncrement();
                    }
                });
                return count.get();
            }
        }
        return count.get();
    }
}
