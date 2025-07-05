package net.mrqx.slashblade.maidpower.item;

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
    public SlashBladeMaidBaubleItem() {
        super(new Item.Properties());
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        int i = 1;
        do {
            String s = this.getDescriptionId() + ".tooltips." + i;
            String s1 = Component.translatable(s).getString();
            if (!s1.toLowerCase(Locale.ROOT).equals(s)) {
                pTooltipComponents.add(Component.translatable(s));
                i++;
            } else {
                break;
            }
        } while (true);
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
