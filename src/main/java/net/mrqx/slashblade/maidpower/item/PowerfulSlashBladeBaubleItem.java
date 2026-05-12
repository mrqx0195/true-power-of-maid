package net.mrqx.slashblade.maidpower.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.mrqx.slashblade.maidpower.config.TruePowerOfMaidCommonConfig;
import net.mrqx.slashblade.maidpower.init.MaidPowerItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PowerfulSlashBladeBaubleItem extends SlashBladeMaidBaubleItem {
    public PowerfulSlashBladeBaubleItem() {
        super(new Properties().rarity(Rarity.EPIC).component(MaidPowerItems.POWERFUL_SOULS_COMPONENT, new ArrayList<>()));
    }
    
    public static void addSoul(ItemStack itemStack, ItemStack soul) {
        List<ItemStack> listTag = PowerfulSlashBladeBaubleItem.getSouls(itemStack);
        listTag.add(soul);
        if (listTag.size() > TruePowerOfMaidCommonConfig.TRUE_POWER_MAX_SOUL_COUNT.get()) {
            listTag.removeFirst();
        }
        itemStack.set(MaidPowerItems.POWERFUL_SOULS_COMPONENT, listTag);
    }
    
    public static List<ItemStack> getSouls(ItemStack itemStack) {
        return new ArrayList<>(itemStack.getOrDefault(MaidPowerItems.POWERFUL_SOULS_COMPONENT, new ArrayList<>()));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown() && !Screen.hasAltDown()) {
            tooltipComponents.add(Component.translatable("item.true_power_of_maid.tooltips"));
            tooltipComponents.add(Component.translatable(this.getDescriptionId() + ".tooltips." + "alt"));
        } else if (Screen.hasShiftDown()) {
            int index = 1;
            while (true) {
                String key = this.getDescriptionId() + ".tooltips." + index;
                String translated = Component.translatable(key).getString();
                if (!translated.toLowerCase(Locale.ENGLISH).equals(key)) {
                    tooltipComponents.add(index == 3 ? Component.translatable(key,
                        Component.literal(String.valueOf(TruePowerOfMaidCommonConfig.TRUE_POWER_MAX_SOUL_COUNT.get())).withStyle(ChatFormatting.GOLD)) : Component.translatable(key));
                    index++;
                } else {
                    return;
                }
            }
        } else if (Screen.hasAltDown()) {
            HolderLookup.Provider registries = context.registries();
            if (registries != null) {
                PowerfulSlashBladeBaubleItem.getSouls(stack).forEach(itemStack -> tooltipComponents.add(itemStack.getDisplayName()));
            }
        }
    }
}
