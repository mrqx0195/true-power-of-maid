package net.mrqx.slashblade.maidpower.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.mrqx.slashblade.maidpower.TruePowerOfMaid;
import net.mrqx.slashblade.maidpower.config.TruePowerOfMaidCommonConfig;
import net.mrqx.slashblade.maidpower.util.ItemTagHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TruePowerBaubleItem extends SlashBladeMaidBaubleItem {
    public static final String TRUE_POWER_SOULS_KEY = TruePowerOfMaid.MODID + "." + "truePowerSouls";

    public TruePowerBaubleItem() {
        super(new Properties().rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (!Screen.hasShiftDown() && !Screen.hasAltDown()) {
            tooltip.add(Component.translatable("item.true_power_of_maid.tooltips"));
            tooltip.add(Component.translatable("item.true_power_of_maid.soul_of_true_power.tooltips.alt"));
        } else if (Screen.hasShiftDown()) {
            int index = 1;
            while (true) {
                String key = this.getDescriptionId() + ".tooltips." + index;
                String translated = Component.translatable(key).getString();
                if (!translated.toLowerCase(Locale.ENGLISH).equals(key)) {
                    tooltip.add(index == 3 ? Component.translatable(key,
                            Component.literal(String.valueOf(TruePowerOfMaidCommonConfig.TRUE_POWER_MAX_SOUL_COUNT.get())).withStyle(ChatFormatting.GOLD)) : Component.translatable(key));
                    index++;
                } else {
                    return;
                }
            }
        } else if (Screen.hasAltDown()) {
            TruePowerBaubleItem.getSouls(stack).forEach(itemStack -> tooltip.add(itemStack.getDisplayName()));
        }
    }

    public static void addSoul(ItemStack itemStack, ItemStack soul) {
        ListTag listTag = ItemTagHelper.getList(itemStack, TRUE_POWER_SOULS_KEY, Tag.TAG_COMPOUND, false);
        listTag.add(soul.serializeNBT());
        if (listTag.size() > TruePowerOfMaidCommonConfig.TRUE_POWER_MAX_SOUL_COUNT.get()) {
            listTag.remove(0);
        }
        ItemTagHelper.setList(itemStack, TRUE_POWER_SOULS_KEY, listTag);
    }

    public static List<ItemStack> getSouls(ItemStack itemStack) {
        ListTag listTag = ItemTagHelper.getList(itemStack, TRUE_POWER_SOULS_KEY, Tag.TAG_COMPOUND, false);
        List<ItemStack> list = new ArrayList<>();
        listTag.forEach(tag -> {
            if (tag instanceof CompoundTag compoundTag) {
                list.add(ItemStack.of(compoundTag));
            }
        });
        return list;
    }
}
