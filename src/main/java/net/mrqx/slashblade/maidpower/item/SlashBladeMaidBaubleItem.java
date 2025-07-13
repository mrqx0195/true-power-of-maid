package net.mrqx.slashblade.maidpower.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class SlashBladeMaidBaubleItem extends Item {
    public SlashBladeMaidBaubleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("item.true_power_of_maid.tooltips"));
        } else {
            int index = 1;
            while (true) {
                String key = this.getDescriptionId() + ".tooltips." + index;
                String translated = Component.translatable(key).getString();
                if (!translated.toLowerCase(Locale.ENGLISH).equals(key)) {
                    tooltip.add(Component.translatable(key));
                    index++;
                } else {
                    return;
                }
            }
        }
    }
}
