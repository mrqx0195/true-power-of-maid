package net.mrqx.slashblade.maidpower.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlashBladeMaidBaubleItem extends Item {
    public SlashBladeMaidBaubleItem() {
        super(new Item.Properties());
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }
}
