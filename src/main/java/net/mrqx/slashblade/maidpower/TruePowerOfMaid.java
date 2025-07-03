package net.mrqx.slashblade.maidpower;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.github.tartaricacid.touhoulittlemaid.item.bauble.BaubleManager;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.mrqx.slashblade.maidpower.item.MaidItems;
import net.mrqx.slashblade.maidpower.task.TaskSlashBlade;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(TruePowerOfMaid.MODID)
public class TruePowerOfMaid {
    public static final String MODID = "true_power_of_maid";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(MODID, path);
    }

    public TruePowerOfMaid() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        TruePowerOfMaidCreativeGroup.CREATIVE_MODE_TABS.register(modEventBus);
    }

    @LittleMaidExtension
    @SuppressWarnings("unused")
    public static class LittleMaid implements ILittleMaid {
        @Override
        public void addMaidTask(TaskManager manager) {
            manager.add(new TaskSlashBlade());
        }

        @Override
        public void bindMaidBauble(BaubleManager manager) {
            MaidItems.SlashBladeMaidBauble.bindMaidBauble(manager);
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void register(RegisterEvent event) {
            event.register(ForgeRegistries.Keys.ITEMS, helper -> {
                helper.register(prefix("soul_of_combo_b"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });

                helper.register(prefix("soul_of_combo_c"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });

                helper.register(prefix("soul_of_rapid_slash"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });

                helper.register(prefix("soul_of_air_combo"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });

                helper.register(prefix("soul_of_mirage_blade"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });

                helper.register(prefix("soul_of_trick"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });

                helper.register(prefix("soul_of_power"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });

                helper.register(prefix("soul_of_judgement_cut"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });

                helper.register(prefix("soul_of_just_judgement_cut"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });

                helper.register(prefix("soul_of_void_slash"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });

                helper.register(prefix("soul_of_guard"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });

                helper.register(prefix("soul_of_health"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });

                helper.register(prefix("soul_of_exp"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });

                helper.register(prefix("soul_of_true_power"), new Item((new Item.Properties())) {
                    @Override
                    public boolean isFoil(@NotNull ItemStack stack) {
                        return true;
                    }
                });
            });
        }
    }
}
