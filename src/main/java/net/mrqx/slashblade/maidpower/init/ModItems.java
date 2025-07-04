package net.mrqx.slashblade.maidpower.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mrqx.slashblade.maidpower.TruePowerOfMaid;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBaubleItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TruePowerOfMaid.MODID);

    public static final RegistryObject<Item> SOUL_OF_COMBO_B = ITEMS.register("soul_of_combo_b", SlashBladeMaidBaubleItem::new);
    public static final RegistryObject<Item> SOUL_OF_COMBO_C = ITEMS.register("soul_of_combo_c", SlashBladeMaidBaubleItem::new);
    public static final RegistryObject<Item> SOUL_OF_RAPID_SLASH = ITEMS.register("soul_of_rapid_slash", SlashBladeMaidBaubleItem::new);
    public static final RegistryObject<Item> SOUL_OF_AIR_COMBO = ITEMS.register("soul_of_air_combo", SlashBladeMaidBaubleItem::new);
    public static final RegistryObject<Item> SOUL_OF_MIRAGE_BLADE = ITEMS.register("soul_of_mirage_blade", SlashBladeMaidBaubleItem::new);
    public static final RegistryObject<Item> SOUL_OF_TRICK = ITEMS.register("soul_of_trick", SlashBladeMaidBaubleItem::new);
    public static final RegistryObject<Item> SOUL_OF_POWER = ITEMS.register("soul_of_power", SlashBladeMaidBaubleItem::new);
    public static final RegistryObject<Item> SOUL_OF_JUDGEMENT_CUT = ITEMS.register("soul_of_judgement_cut", SlashBladeMaidBaubleItem::new);
    public static final RegistryObject<Item> SOUL_OF_JUST_JUDGEMENT_CUT = ITEMS.register("soul_of_just_judgement_cut", SlashBladeMaidBaubleItem::new);
    public static final RegistryObject<Item> SOUL_OF_VOID_SLASH = ITEMS.register("soul_of_void_slash", SlashBladeMaidBaubleItem::new);
    public static final RegistryObject<Item> SOUL_OF_GUARD = ITEMS.register("soul_of_guard", SlashBladeMaidBaubleItem::new);
    public static final RegistryObject<Item> SOUL_OF_HEALTH = ITEMS.register("soul_of_health", SlashBladeMaidBaubleItem::new);
    public static final RegistryObject<Item> SOUL_OF_EXP = ITEMS.register("soul_of_exp", SlashBladeMaidBaubleItem::new);
    public static final RegistryObject<Item> SOUL_OF_TRUE_POWER = ITEMS.register("soul_of_true_power", SlashBladeMaidBaubleItem::new);
}
