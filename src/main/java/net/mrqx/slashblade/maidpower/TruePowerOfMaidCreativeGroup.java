package net.mrqx.slashblade.maidpower;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.mrqx.slashblade.maidpower.item.MaidItems;

public class TruePowerOfMaidCreativeGroup {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TruePowerOfMaid.MODID);

    private static final CreativeModeTab TRUE_POWER_OF_MAID = CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.true_power_of_maid")).icon(() -> new ItemStack(MaidItems.soulOfComboB))
            .displayItems((features, output) -> {
                output.accept(MaidItems.soulOfComboB);
                output.accept(MaidItems.soulOfComboC);
                output.accept(MaidItems.soulOfRapidSlash);
                output.accept(MaidItems.soulOfAirCombo);
                output.accept(MaidItems.soulOfMirageBlade);
                output.accept(MaidItems.soulOfTrick);
                output.accept(MaidItems.soulOfPower);
                output.accept(MaidItems.soulOfJudgementCut);
                output.accept(MaidItems.soulOfJustJudgementCut);
                output.accept(MaidItems.soulOfVoidSlash);
                output.accept(MaidItems.soulOfGuard);
                output.accept(MaidItems.soulOfHealth);
                output.accept(MaidItems.soulOfExp);
                output.accept(MaidItems.soulOfTruePower);
            })
            .build();

    public static final RegistryObject<CreativeModeTab> TRUE_POWER_OF_MAID_GROUP = CREATIVE_MODE_TABS.register(TruePowerOfMaid.MODID, () -> TRUE_POWER_OF_MAID);
}
