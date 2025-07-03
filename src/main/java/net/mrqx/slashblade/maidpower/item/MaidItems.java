package net.mrqx.slashblade.maidpower.item;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import com.github.tartaricacid.touhoulittlemaid.item.bauble.BaubleManager;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import net.mrqx.slashblade.maidpower.event.MaidProgressComboEvent;

public class MaidItems {
    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_combo_b")
    public static Item soulOfComboB;

    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_combo_c")
    public static Item soulOfComboC;

    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_rapid_slash")
    public static Item soulOfRapidSlash;

    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_air_combo")
    public static Item soulOfAirCombo;

    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_mirage_blade")
    public static Item soulOfMirageBlade;

    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_trick")
    public static Item soulOfTrick;

    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_power")
    public static Item soulOfPower;

    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_judgement_cut")
    public static Item soulOfJudgementCut;

    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_just_judgement_cut")
    public static Item soulOfJustJudgementCut;

    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_void_slash")
    public static Item soulOfVoidSlash;

    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_guard")
    public static Item soulOfGuard;

    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_health")
    public static Item soulOfHealth;

    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_exp")
    public static Item soulOfExp;

    @ObjectHolder(registryName = "minecraft:item", value = "true_power_of_maid:soul_of_true_power")
    public static Item soulOfTruePower;

    public static class SlashBladeMaidBauble implements IMaidBauble {
        public static void bindMaidBauble(BaubleManager manager) {
            manager.bind(soulOfComboB, new ComboB());
            manager.bind(soulOfComboC, new ComboC());
            manager.bind(soulOfRapidSlash, new RapidSlash());
            manager.bind(soulOfAirCombo, new AirCombo());
            manager.bind(soulOfMirageBlade, new MirageBlade());
            manager.bind(soulOfTrick, new Trick());
            manager.bind(soulOfPower, new Power());
            manager.bind(soulOfJudgementCut, new JudgementCut());
            manager.bind(soulOfJustJudgementCut, new JustJudgementCut());
            manager.bind(soulOfVoidSlash, new VoidSlash());
            manager.bind(soulOfGuard, new Guard());
            manager.bind(soulOfHealth, new Health());
            manager.bind(soulOfExp, new Exp());
            manager.bind(soulOfTruePower, new TruePower());
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
                if (MaidItems.SlashBladeMaidBauble.JustJudgementCut.checkBauble(maid)) {
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

            public static boolean checkBauble(EntityMaid maid) {
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
}
