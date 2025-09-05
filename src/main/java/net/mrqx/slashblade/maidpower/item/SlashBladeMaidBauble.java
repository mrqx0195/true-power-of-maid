package net.mrqx.slashblade.maidpower.item;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDeathEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
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
import net.mrqx.slashblade.maidpower.LittleMaidImpl;
import net.mrqx.slashblade.maidpower.config.TruePowerOfMaidCommonConfig;
import net.mrqx.slashblade.maidpower.event.MaidTickHandler;
import net.mrqx.slashblade.maidpower.event.api.MaidProgressComboEvent;

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
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.UNAWAKENED_SOUL_BAUBLE) >= 0;
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
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.COMBO_B_BAUBLE) >= 0;
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
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.COMBO_C_BAUBLE) >= 0;
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
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.RAPID_SLASH_BAUBLE) >= 0;
        }
    }

    public static class AirCombo extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.AIR_COMBO_BAUBLE) >= 0;
        }
    }

    public static class MirageBlade extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.MIRAGE_BLADE_BAUBLE) >= 0;
        }
    }

    public static class Trick extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.TRICK_BAUBLE) >= 0;
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
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.POWER_BAUBLE) >= 0;
        }
    }

    public static class JudgementCut extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            if (SlashBladeMaidBauble.JustJudgementCut.checkBauble(maid)) {
                return true;
            }
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.JUDGEMENT_CUT_BAUBLE) >= 0;
        }
    }

    public static class JustJudgementCut extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.JUST_JUDGEMENT_CUT_BAUBLE) >= 0;
        }
    }

    public static class VoidSlash extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.VOID_SLASH_BAUBLE) >= 0;
        }
    }

    public static class Guard extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.GUARD_BAUBLE) >= 0;
        }
    }

    public static class Health extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.HEALTH_BAUBLE) >= 0;
        }
    }

    public static class Exp extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return ItemsUtil.getBaubleSlotInMaid(maid, LittleMaidImpl.EXP_BAUBLE) >= 0;
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
