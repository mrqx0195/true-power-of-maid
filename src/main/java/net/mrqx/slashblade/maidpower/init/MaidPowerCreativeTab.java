package net.mrqx.slashblade.maidpower.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.mrqx.slashblade.maidpower.TruePowerOfMaid;

public class MaidPowerCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TruePowerOfMaid.MODID);

    public static final RegistryObject<CreativeModeTab> TRUE_POWER_OF_MAID_GROUP = CREATIVE_MODE_TABS.register(TruePowerOfMaid.MODID, () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.true_power_of_maid"))
            .icon(() -> new ItemStack(MaidPowerItems.SOUL_OF_COMBO_B.get()))
            .displayItems((features, output) -> {
                output.accept(MaidPowerItems.SOUL_OF_COMBO_B.get());
                output.accept(MaidPowerItems.SOUL_OF_COMBO_C.get());
                output.accept(MaidPowerItems.SOUL_OF_RAPID_SLASH.get());
                output.accept(MaidPowerItems.SOUL_OF_AIR_COMBO.get());
                output.accept(MaidPowerItems.SOUL_OF_MIRAGE_BLADE.get());
                output.accept(MaidPowerItems.SOUL_OF_TRICK.get());
                output.accept(MaidPowerItems.SOUL_OF_POWER.get());
                output.accept(MaidPowerItems.SOUL_OF_JUDGEMENT_CUT.get());
                output.accept(MaidPowerItems.SOUL_OF_JUST_JUDGEMENT_CUT.get());
                output.accept(MaidPowerItems.SOUL_OF_VOID_SLASH.get());
                output.accept(MaidPowerItems.SOUL_OF_GUARD.get());
                output.accept(MaidPowerItems.SOUL_OF_HEALTH.get());
                output.accept(MaidPowerItems.SOUL_OF_EXP.get());
                output.accept(MaidPowerItems.SOUL_OF_TRUE_POWER.get());
            }).build());
}
