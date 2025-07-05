package net.mrqx.slashblade.maidpower.item;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDeathEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.slashblade.maidpower.event.MaidTickHandler;
import net.mrqx.slashblade.maidpower.event.api.MaidProgressComboEvent;

public class SlashBladeMaidBauble implements IMaidBauble {
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
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof ComboB) {
                    return true;
                }
            }
            return false;
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
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof ComboC) {
                    return true;
                }
            }
            return false;
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
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof RapidSlash) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class AirCombo extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof AirCombo) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class MirageBlade extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof MirageBlade) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class Trick extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof Trick) {
                    return true;
                }
            }
            return false;
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
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof Power) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class JudgementCut extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            if (SlashBladeMaidBauble.JustJudgementCut.checkBauble(maid)) {
                return true;
            }
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof JudgementCut) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class JustJudgementCut extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof JustJudgementCut) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class VoidSlash extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof VoidSlash) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class Guard extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof Guard) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class Health extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof Health) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class Exp extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            BaubleItemHandler handler = maid.getMaidBauble();

            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof Exp) {
                    return true;
                }
            }
            return false;
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
                if (data.getLong(MaidTickHandler.TRUE_POWER_RANK) > 0) {
                    data.putLong(MaidTickHandler.TRUE_POWER_RANK, data.getLong(MaidTickHandler.TRUE_POWER_RANK) - 300);
                    if (data.getLong(MaidTickHandler.TRUE_POWER_RANK) > 0) {
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

            int count = 0;
            boolean flag = false;
            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof SlashBladeMaidBauble) {
                    count++;
                    if (baubleIn instanceof TruePower) {
                        flag = true;
                    }
                }
            }
            return flag && count >= 9;
        }
    }
}
