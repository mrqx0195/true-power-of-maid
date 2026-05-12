package net.mrqx.slashblade.maidpower.item;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDamageEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDeathEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.BaubleItemHandler;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.util.AttackManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.mrqx.sbr_core.utils.SlashBladeAttackUtils;
import net.mrqx.slashblade.maidpower.TruePowerOfMaid;
import net.mrqx.slashblade.maidpower.config.TruePowerOfMaidCommonConfig;
import net.mrqx.slashblade.maidpower.entity.EntityUnlimitedBladeWorks;
import net.mrqx.slashblade.maidpower.event.MaidTickHandler;
import net.mrqx.slashblade.maidpower.event.api.MaidProgressComboEvent;
import net.mrqx.slashblade.maidpower.util.MaidItemUtils;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.List;

public class SlashBladeMaidBauble implements IMaidBauble {
    public interface IPowerfulSlashBladeBauble {
    }
    
    @EventBusSubscriber
    public static class UnawakenedSoul extends SlashBladeMaidBauble {
        @SubscribeEvent
        public static void onLivingDeathEvent(LivingDeathEvent event) {
            if (event.isCanceled()) {
                return;
            }
            if (event.getSource().getEntity() instanceof EntityMaid maid && maid.level() instanceof ServerLevel serverLevel) {
                BaubleItemHandler handler = maid.getMaidBauble();
                RandomSource random = serverLevel.getRandom();
                int exp = event.getEntity().getExperienceReward(serverLevel, maid);
                long exp4 = (long) exp * exp * exp;
                double chance = Math.min(1.0, exp4 / 100000.0);
                if (random.nextDouble() < chance) {
                    int i = random.nextInt(handler.getSlots());
                    IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                    if (baubleIn instanceof UnawakenedSoul && random.nextDouble() < chance) {
                        String item = TruePowerOfMaidCommonConfig.UNAWAKENED_SOUL_RANGE_MAP.get(random.nextDouble() * TruePowerOfMaidCommonConfig.unawakenedSoulTotalRange);
                        if (item != null) {
                            Item item1 = serverLevel.registryAccess().registryOrThrow(Registries.ITEM).get(ResourceLocation.tryParse(item));
                            if (item1 != null) {
                                handler.setStackInSlot(i, new ItemStack(item1));
                            }
                        }
                    }
                }
            }
        }
        
        public static boolean checkBauble(EntityMaid maid) {
            return MaidItemUtils.getBaubleCountForClass(maid, UnawakenedSoul.class) > 0;
        }
    }
    
    @EventBusSubscriber
    public static class ComboB extends SlashBladeMaidBauble {
        @SubscribeEvent
        public static void onMaidProgressComboEvent(MaidProgressComboEvent event) {
            if (checkBauble(event.getMaid())) {
                if ((event.getNextCombo().equals(ComboStateRegistry.COMBO_A4.getId())
                    || event.getNextCombo().equals(ComboStateRegistry.COMBO_A4_EX.getId())
                    || event.getNextCombo().equals(ComboStateRegistry.AERIAL_RAVE_A3.getId())
                )) {
                    event.setCanceled(true);
                }
            } else {
                if (event.getNextCombo().equals(ComboStateRegistry.COMBO_B1.getId()) || event.getNextCombo().equals(ComboStateRegistry.AERIAL_RAVE_B3.getId())) {
                    event.setCanceled(true);
                }
            }
        }
        
        public static boolean checkBauble(EntityMaid maid) {
            return MaidItemUtils.getBaubleCountForClass(maid, ComboB.class) > 0;
        }
    }
    
    @EventBusSubscriber
    public static class ComboC extends SlashBladeMaidBauble {
        @SubscribeEvent
        public static void onMaidProgressComboEvent(MaidProgressComboEvent event) {
            if (checkBauble(event.getMaid())) {
                if (event.getNextCombo().equals(ComboStateRegistry.COMBO_A3.getId())) {
                    event.setCanceled(true);
                }
            } else {
                if (event.getNextCombo().equals(ComboStateRegistry.COMBO_C.getId())) {
                    event.setCanceled(true);
                }
            }
        }
        
        public static boolean checkBauble(EntityMaid maid) {
            return MaidItemUtils.getBaubleCountForClass(maid, ComboC.class) > 0;
        }
    }
    
    @EventBusSubscriber
    public static class RapidSlash extends SlashBladeMaidBauble {
        @SubscribeEvent
        public static void onMaidProgressComboEvent(MaidProgressComboEvent event) {
            if (!checkBauble(event.getMaid()) && (event.getNextCombo().equals(ComboStateRegistry.RAPID_SLASH.getId()) || event.getNextCombo().equals(ComboStateRegistry.RAPID_SLASH_QUICK.getId()))) {
                event.setCanceled(true);
            }
        }
        
        public static boolean checkBauble(EntityMaid maid) {
            return MaidItemUtils.getBaubleCountForClass(maid, RapidSlash.class) > 0;
        }
    }
    
    public static class AirCombo extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return MaidItemUtils.getBaubleCountForClass(maid, AirCombo.class) > 0;
        }
    }
    
    public static class MirageBlade extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return MaidItemUtils.getBaubleCountForClass(maid, MirageBlade.class) > 0;
        }
    }
    
    public static class Trick extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return MaidItemUtils.getBaubleCountForClass(maid, Trick.class) > 0;
        }
    }
    
    @EventBusSubscriber
    public static class Power extends SlashBladeMaidBauble {
        @SubscribeEvent
        public static void onPowerBladeEvent(SlashBladeEvent.PowerBladeEvent event) {
            if (event.getUser() instanceof EntityMaid maid && checkBauble(maid) && maid.getFavorabilityManager().getLevel() >= 3) {
                event.setPowered(true);
            }
        }
        
        public static boolean checkBauble(EntityMaid maid) {
            return MaidItemUtils.getBaubleCountForClass(maid, Power.class) > 0;
        }
    }
    
    public static class JudgementCut extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            if (SlashBladeMaidBauble.JustJudgementCut.checkBauble(maid)) {
                return true;
            }
            return MaidItemUtils.getBaubleCountForClass(maid, JudgementCut.class) > 0;
        }
    }
    
    public static class JustJudgementCut extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return MaidItemUtils.getBaubleCountForClass(maid, JustJudgementCut.class) > 0;
        }
    }
    
    public static class VoidSlash extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return MaidItemUtils.getBaubleCountForClass(maid, VoidSlash.class) > 0;
        }
    }
    
    public static class Guard extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return MaidItemUtils.getBaubleCountForClass(maid, Guard.class) > 0;
        }
    }
    
    public static class Health extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return MaidItemUtils.getBaubleCountForClass(maid, Health.class) > 0;
        }
    }
    
    public static class Exp extends SlashBladeMaidBauble {
        public static boolean checkBauble(EntityMaid maid) {
            return MaidItemUtils.getBaubleCountForClass(maid, Exp.class) > 0;
        }
    }
    
    @EventBusSubscriber
    public static class TruePower extends SlashBladeMaidBauble implements IPowerfulSlashBladeBauble {
        @SubscribeEvent
        public static void onPowerBladeEvent(SlashBladeEvent.PowerBladeEvent event) {
            if (event.getUser() instanceof EntityMaid maid && checkBauble(maid)) {
                event.setPowered(true);
            }
        }
        
        public static boolean checkBauble(EntityMaid maid) {
            if (maid.getFavorabilityManager().getLevel() < 3) {
                return false;
            }
            BaubleItemHandler handler = maid.getMaidBauble();
            
            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof IPowerfulSlashBladeBauble) {
                    return baubleIn instanceof TruePower;
                }
            }
            return false;
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onMaidDeathEvent(MaidDeathEvent event) {
            if (checkBauble(event.getMaid()) && SlashBladeAttackUtils.isHoldingSlashBlade(event.getMaid())) {
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
    }
    
    @EventBusSubscriber
    public static class UnlimitedBladeWorks extends SlashBladeMaidBauble implements IPowerfulSlashBladeBauble {
        @SubscribeEvent
        public static void onPowerBladeEvent(SlashBladeEvent.PowerBladeEvent event) {
            if (event.getUser() instanceof EntityMaid maid && checkBauble(maid)) {
                event.setPowered(true);
            }
        }
        
        @SubscribeEvent
        public static void onMaidDamageEvent(MaidDamageEvent event) {
            if (checkBauble(event.getMaid()) && SlashBladeAttackUtils.isHoldingSlashBlade(event.getMaid())) {
                MaidItemUtils.getAllSlashBladeUnbroken(event.getMaid()).forEach(itemStack ->
                    BladeStateAccess.of(itemStack).ifPresent(state -> {
                        if (event.getAmount() > 0 && !event.getMaid().getMainHandItem().equals(itemStack)) {
                            int damage = itemStack.getMaxDamage() - Math.min(itemStack.getDamageValue(), itemStack.getMaxDamage());
                            damage = Math.min(damage - 1, (int) Math.ceil(event.getAmount()));
                            if (damage > 0) {
                                event.setAmount(event.getAmount() - damage);
                                itemStack.hurtAndBreak(damage, event.getMaid(), EquipmentSlot.MAINHAND);
                            }
                        }
                    })
                );
            }
        }
        
        @SubscribeEvent
        public static void onMaidDeathEvent(MaidDeathEvent event) {
            if (checkBauble(event.getMaid()) && SlashBladeAttackUtils.isHoldingSlashBlade(event.getMaid())) {
                ItemStack[] blade = {event.getMaid().getMainHandItem()};
                MaidItemUtils.getAllSlashBladeUnbroken(event.getMaid()).forEach(itemStack ->
                    BladeStateAccess.of(itemStack).ifPresent(state -> {
                        ItemStack stack = blade[0];
                        if (!event.getMaid().getMainHandItem().equals(itemStack)) {
                            if (event.getMaid().getMainHandItem().equals(stack)) {
                                blade[0] = itemStack;
                                return;
                            }
                            if ((stack.getMaxDamage() - stack.getDamageValue()) > (itemStack.getMaxDamage() - itemStack.getDamageValue())) {
                                blade[0] = itemStack;
                            }
                        }
                    })
                );
                BladeStateAccess.of(blade[0]).ifPresent(state -> {
                    if (!state.isBroken()) {
                        state.setBroken(true);
                        event.setCanceled(true);
                        event.getMaid().setHealth(event.getMaid().getMaxHealth());
                    }
                });
            }
        }
        
        public static boolean checkBauble(EntityMaid maid) {
            if (maid.getFavorabilityManager().getLevel() < 3) {
                return false;
            }
            BaubleItemHandler handler = maid.getMaidBauble();
            
            for (int i = 0; i < handler.getSlots(); ++i) {
                IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                if (baubleIn instanceof IPowerfulSlashBladeBauble) {
                    return baubleIn instanceof UnlimitedBladeWorks;
                }
            }
            return false;
        }
        
        public static void ubwDoSlash(EntityMaid maid, Vec3 pos, float xRot, float yRot, float roll, boolean mute, boolean critical, double damage) {
            List<ItemStack> bladeList = MaidItemUtils.getAllSlashBladeUnbroken(maid);
            if (!bladeList.isEmpty()) {
                ItemStack blade = bladeList.get(maid.level().random.nextInt(bladeList.size()));
                EntityUnlimitedBladeWorks ubw = getEntityUnlimitedBladeWorks(maid, pos, xRot, yRot, blade);
                AttackManager.doSlash(ubw, roll, mute, critical, damage);
                blade.hurtAndBreak(1, maid, EquipmentSlot.MAINHAND);
            }
        }
        
        public static EntityUnlimitedBladeWorks getEntityUnlimitedBladeWorks(EntityMaid maid, Vec3 pos, float xRot, float yRot, ItemStack blade) {
            EntityUnlimitedBladeWorks ubw = new EntityUnlimitedBladeWorks(TruePowerOfMaid.RegistryEvents.UnlimitedBladeWorks, maid.level());
            ubw.setOwnerUUID(maid.getUUID());
            ubw.setItemSlot(EquipmentSlot.MAINHAND, blade.copy());
            ubw.getAttributes().assignAllValues(maid.getAttributes());
            ubw.setPos(pos);
            ubw.setXRot(xRot);
            ubw.setYRot(yRot);
            IConcentrationRank ubwRank = ubw.getData(CapabilityConcentrationRank.RANK_POINT);
            IConcentrationRank maidRank = maid.getData(CapabilityConcentrationRank.RANK_POINT);
            ubwRank.setRawRankPoint(maidRank.getRawRankPoint());
            ubwRank.setLastRankRise(maidRank.getLastRankRise());
            ubwRank.setLastUpdte(maidRank.getLastUpdate());
            ubw.setHealth(ubw.getMaxHealth());
            ubw.setNoGravity(true);
            ubw.noPhysics = true;
            maid.level().addFreshEntity(ubw);
            return ubw;
        }
    }
}
